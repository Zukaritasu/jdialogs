// Copyright (C) 2021-2023 Zukaritasu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

#include "jfiledlg.hpp"

#include <cstdlib>
#include <ShlObj.h>

HRESULT ShellItemArrayToStringArray(JNIEnv* env, jobjectArray& array, 
									IShellItemArray* items)
{
	HRESULT hr;
	DWORD count = 0;
	
	if (SUCCEEDED(hr = items->GetCount(&count)) && count > 0)
	{
		jclass cls_string = env->FindClass("java/lang/String");
		CHECK_NULL_OOM(cls_string);
		
		array = env->NewObjectArray(count, cls_string, NULL);
		CHECK_NULL_OOM(array);
		env->DeleteLocalRef(cls_string);
		
		for (int i = 0, j = 0; i < (int)count; i++)
		{
			IShellItem* item;
			if (FAILED(hr = items->GetItemAt(i, &item)))
				break;
			
			LPWSTR syspath;
			hr = item->GetDisplayName(SIGDN_FILESYSPATH, &syspath);
			
			item->Release();
			if (FAILED(hr)) break;
			
			jstring element = env->NewString((const jchar*)syspath, lstrlen(syspath));
			CoTaskMemFree(syspath);
			CHECK_NULL_OOM(E_OUTOFMEMORY);
			
			env->SetObjectArrayElement(array, j++, element);
			env->DeleteLocalRef(element);
		}
		
		if (FAILED(hr)) env->DeleteLocalRef(array);
	}
	return hr;
}

HRESULT SetFilter(IFileDialog* dialog, JNIEnv* env, jobjectArray filter, jint index)
{
	HRESULT hr = S_OK;
	int count;
	// The filter is an array that has as its first element the description
	// of the extension and the second element the extension or the group
	// of extensions and so on with the following elements, this means that
	// the length of the array must be divisible by 2, otherwise it is means
	// an error in the java code 
	if (filter && (count = env->GetArrayLength(filter)) > 0 && (count % 2) == 0) 
	{
		// La mitad de 'count' que especifica la cantidad de filtros
		unsigned half = count / 2;
		COMDLG_FILTERSPEC* filters = (COMDLG_FILTERSPEC*)
									 malloc(sizeof(COMDLG_FILTERSPEC) * half);
		if (filters == NULL)
		{
			ShowOutOfMemory(env, "malloc function");
			return E_OUTOFMEMORY;
		}
		
		// Obtiene el elemento por el indice en el arreglo 'filter' y
		// returna una copia de la cadena
		auto GetElement = [&](unsigned i) -> wchar_t*
		{
			// En el arreglo no pueden existir elementos nulos
			jobject element = env->GetObjectArrayElement(filter, i);
			if (element != NULL)
			{
				wchar_t* str_copy = NULL;
				const jchar* str_chars = env->GetStringChars((jstring)element, NULL);
				if (str_chars != NULL)
				{
					str_copy = _wcsdup((const wchar_t*)str_chars);
					if (str_copy == NULL)
						ShowOutOfMemory(env, "_wcsdup function");
					env->ReleaseStringChars((jstring)element, str_chars);
				}
				env->DeleteLocalRef(element);
				return str_copy;
			}
			return nullptr;
		};
		
		unsigned i = 0, j = 0;
		for (; i < half; i++) 
		{
			// Description and extensions
			if ((filters[i].pszName = GetElement(j++)) == NULL || 
				(filters[i].pszSpec = GetElement(j++)) == NULL)
			{
				j--;
				hr = E_OUTOFMEMORY;
				break;
			}
		}

		if (SUCCEEDED(hr) && SUCCEEDED(hr = dialog->SetFileTypes(half, filters)))
			hr = dialog->SetFileTypeIndex(index);
		
		for (i = 0; i < j; i++)
		{
			free(((void**)filters)[i]);
		}
		free(filters);
	}
	return hr;
}

#define SETTEXT(func, text) \
	if (text != nullptr) { \
		hr = func((LPCWSTR)(aux = env->GetStringChars(text, nullptr))); \
		env->ReleaseStringChars(text, aux); \
		if (FAILED(hr)) goto finish; \
	}
		
#define CHECK_NULL_RELEASE(obj) \
	if (obj == NULL) { dialog->Release(); return JNI_FALSE; }

JNIFUNCTION(jboolean) 
Java_com_zukadev_dialogs_FileDialog_showDialog(JNIPARAMS, 
											jboolean mode,
											jstring fileNameLabel,
											jstring okButtonLabel,
											jstring root,
											jstring fileName,
											jstring title,
											jstring defaultExt,
											jint options,
											jobjectArray filter,
											jint filterIndex,
											jlong hwndParent
											)
{
	IFileDialog* dialog;
	IShellItem* item;
	HRESULT hr;
	jclass clazz = NULL;
	const jchar* aux = NULL;
	
	// An instance is created depending on the value of 'mode'.
	// The OleInitialize or CoInitialize function must be
	// called before the CoCreateInstance function is called. 
	if (mode)
		hr = CoCreateInstance(CLSID_FileOpenDialog, NULL, CLSCTX_ALL,
				IID_IFileOpenDialog, reinterpret_cast<void**>(&dialog));
	else
		hr = CoCreateInstance(CLSID_FileSaveDialog, NULL, CLSCTX_ALL,
				IID_IFileSaveDialog, reinterpret_cast<void**>(&dialog));

	if (FAILED(hr)) 
	{
		ShowError(env, hr);
		return JNI_FALSE;
	}

	SETTEXT(dialog->SetOkButtonLabel, okButtonLabel);
	SETTEXT(dialog->SetFileNameLabel, fileNameLabel);
	SETTEXT(dialog->SetTitle, title);
	
	// root can be null and if it is it means that the default
	// path will be used 
	if (root) 
	{
		const jchar* aux = env->GetStringChars(root, NULL);
		// The path may be invalid, in which case the function
		// returns null and the default path is used. 
		LPITEMIDLIST itemList = ILCreateFromPath((const wchar_t*)aux);
		env->ReleaseStringChars(root, aux);
		if (itemList != NULL)
		{
			hr = SHCreateItemFromIDList(itemList, IID_IShellItem, reinterpret_cast<void**>(&item));
			ILFree(itemList);
			
			if (FAILED(hr)) goto finish;
			
			hr = dialog->SetFolder(item);
			item->Release();
			
			if (FAILED(hr)) goto finish;
		}
	}

	if (FAILED(hr = dialog->SetOptions(options)))
		goto finish;

	// If the option to select one or more folders is disabled then
	// the corresponding values are assigned for the file selection 
	if (!(options & FOS_PICKFOLDERS)) 
	{
		SETTEXT(dialog->SetDefaultExtension, defaultExt);
		SETTEXT(dialog->SetFileName, fileName);
		
		if (FAILED(SetFilter(dialog, env, filter, filterIndex)))
		{
			dialog->Release();
			return JNI_FALSE;
		}
	}
	
	if (FAILED(hr = dialog->Show((HWND)hwndParent)))
		goto finish;

	clazz = env->GetObjectClass(obj);
	CHECK_NULL_RELEASE(clazz);

	// The selected files are retrieved and assigned in the
	// global variable 'selectedFiles' 
	if (mode && (options & FOS_ALLOWMULTISELECT)) 
	{
		IShellItemArray* items = NULL;
		jobjectArray array = NULL;
		
		if (FAILED(hr = reinterpret_cast<IFileOpenDialog*>(dialog)->GetResults(&items)))
			goto finish;
		
		hr = ShellItemArrayToStringArray(env, array, items);
		items->Release();
		
		// Possible out of memory in the jvm from the ShellItemArrayToStringArray function
		if (FAILED(hr)) 
		{
			if (hr == E_OUTOFMEMORY)
			{
				dialog->Release();
				return JNI_FALSE;
			}
			goto finish;
		}
		
		jfieldID field_selfiles = env->GetFieldID(clazz, "selectedFiles", "[Ljava/lang/String;");
		CHECK_NULL_RELEASE(field_selfiles);
		
		env->SetObjectField(obj, field_selfiles, array);
		env->DeleteLocalRef(array);
	}

	// In the event that an attempt is made to save a file or that
	// selected a file to open it, the 'GetResult' method is used 
	if ((!mode || !(options & FOS_ALLOWMULTISELECT)) && SUCCEEDED(hr = dialog->GetResult(&item))) 
	{
		LPWSTR text = NULL;
		// Absolute path of the file or folder
		hr = item->GetDisplayName(SIGDN_FILESYSPATH, &text);
		item->Release();
		
		if (FAILED(hr)) goto finish;
		
		jstring absolutePath = env->NewString((const jchar*)text, lstrlen(text));
		CoTaskMemFree(text);
		CHECK_NULL_RELEASE(absolutePath);
		
		// The absolute path of the selected file or folder Is
		// set in the global variable 'absolutePath' 
		jfieldID field_abpath = env->GetFieldID(clazz, "absolutePath", "Ljava/lang/String;");
		CHECK_NULL_RELEASE(field_abpath);
		env->SetObjectField(obj, field_abpath, absolutePath);
		env->DeleteLocalRef(absolutePath);
	}

	UINT uFilterIndex;
	// The index of the filter selected by the user is obtained.
	// It does not work if in the open option you try to select folders
	// or in the case that a filter has not been specified 
	if (SUCCEEDED(hr = dialog->GetFileTypeIndex(&uFilterIndex))) 
	{
		jfieldID filter_index = env->GetFieldID(clazz, "filterIndex", "I");
		CHECK_NULL_RELEASE(filter_index);
		env->SetIntField(obj, filter_index, (jint)uFilterIndex);
	}

finish:
	if (clazz != NULL) 
		env->DeleteLocalRef(clazz);
	dialog->Release();
	// The possible error occurred in this function is shown.
	// The errors that occur in the jvm are not shown, the jvm itself
	// is responsible for showing them, including the out of memory

	// The user closed the window by cancelling the operation
	if (hr != S_OK && hr != HRESULT_FROM_WIN32(ERROR_CANCELLED))
		ShowError(env, hr);

	return SUCCEEDED(hr);
}
