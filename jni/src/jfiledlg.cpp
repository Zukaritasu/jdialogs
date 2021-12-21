/*
** Copyright (C) 2021 Zukaritasu
**
** This program is free software: you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation, either version 3 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

#include "jfiledlg.h"


typedef HRESULT(STDMETHODCALLTYPE SETTEXTMETHOD)(LPCWSTR);

HRESULT ShellItemArrayToStringArray(JNIEnv* env, jobjectArray& array, IShellItemArray* items)
{
	HRESULT     hr  = S_OK;
	DWORD       count = 0;
	IShellItem* item;
	LPWSTR      path;
	jclass      clazz;
	jstring     element;

	if (SUCCEEDED(hr = items->GetCount(&count)) && count > 0) {
		if ((clazz = env->FindClass("java/lang/String")) == nullptr || 
			(array = env->NewObjectArray((jsize)count, clazz, nullptr)) == nullptr) {
			hr = E_OUTOFMEMORY;
			goto error;
		}

		for (int i = 0, j = 0; i < (int)count; i++) {
			if (FAILED(hr = items->GetItemAt(i, &item)))
				break;
			if (FAILED(item->GetDisplayName(SIGDN_FILESYSPATH, &path))) {
				item->Release();
				break;
			}

			if ((element = env->NewString((jpcchar)path, lstrlen(path))) != nullptr) {
				env->SetObjectArrayElement(array, j++, element);
				env->DeleteLocalRef(element);
			}

			CoTaskMemFree(path);
			item->Release();
			if (element == nullptr)
				break;
		}

	error:
		if (clazz != nullptr)
			env->DeleteLocalRef(clazz);
		if (FAILED(hr) && array != nullptr)
			env->DeleteLocalRef(array);
	}

	return hr;
}

HRESULT SetFilter(IFileDialog* dialog, JNIEnv* env, jobjectArray filter, jint index)
{
	HRESULT hr = S_OK;
	int count, half;
	if (filter != nullptr && (count = env->GetArrayLength(filter)) > 0 && (count % 2) == 0) {
		COMDLG_FILTERSPEC* filters = new COMDLG_FILTERSPEC[half = count / 2];
		if (filters == nullptr) {
			return E_OUTOFMEMORY;
		}
		
		for (jsize i = 0, j = 0; i < half; i++) {
			filters[i].pszName = (LPCWSTR)env->GetStringChars(
				(jstring)env->GetObjectArrayElement(filter, j++), nullptr);
			filters[i].pszSpec = (LPCWSTR)env->GetStringChars(
				(jstring)env->GetObjectArrayElement(filter, j++), nullptr);
		}

		if (SUCCEEDED(hr = dialog->SetFileTypes(half, filters))) {
			hr = dialog->SetFileTypeIndex(index);
		}

		for (jsize i = 0; i < count; i++) {
			env->ReleaseStringChars((jstring)env->GetObjectArrayElement(filter, i), 
				(jpcchar)((LPCWSTR*)filters)[i]);
		}

		delete[] filters;
	}
	return hr;
}

#define RESULT_ERROR(e) \
		if (FAILED(e)) { \
			goto _FinishDlg_; \
		}

#define SETTEXT(func, text) \
		if (text != nullptr) { \
			hr = func((LPCWSTR)(aux = env->GetStringChars(text, nullptr))); \
			env->ReleaseStringChars(text, aux); \
			RESULT_ERROR(hr); \
		}

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showDialog
	(JNIPARAMS, jboolean mode, jstring fileNameLabel, jstring okButtonLabel, jstring root, 
		jstring fileName, jstring title, jstring defaultExt, jint options, jobjectArray filter, 
		jint filterIndex, jlong hwndParent)
{
	IFileDialog* dialog;
	IShellItem* item;
	HRESULT hr;
	LPWSTR text = nullptr;
	jclass clazz = nullptr;
	const jchar* aux;

	if (mode) {
		hr = CoCreateInstance(CLSID_FileOpenDialog, nullptr, CLSCTX_ALL,
				IID_IFileOpenDialog, reinterpret_cast<void**>(&dialog));
	} else {
		hr = CoCreateInstance(CLSID_FileSaveDialog, nullptr, CLSCTX_ALL,
				IID_IFileSaveDialog, reinterpret_cast<void**>(&dialog));
	}

	if (FAILED(hr)) {
		ShowError(env, hr);
		return JNI_FALSE;
	}

	auto method = [&](SETTEXTMETHOD settext, jstring text) {
		aux = env->GetStringChars(text, nullptr);
		HRESULT result = settext((LPCWSTR)aux);
		env->ReleaseStringChars(text, aux);
		return result;
	};

	SETTEXT(dialog->SetOkButtonLabel, okButtonLabel);
	SETTEXT(dialog->SetTitle, title);
	SETTEXT(dialog->SetFileNameLabel, fileNameLabel);
	
	if (root != nullptr) {
		LPITEMIDLIST itemList = ILCreateFromPath((PCWSTR)(aux = env->GetStringChars(root, nullptr)));
		env->ReleaseStringChars(root, aux);
		if (itemList != nullptr) {
			if (SUCCEEDED(SHCreateItemFromIDList(itemList, IID_IShellItem, 
					reinterpret_cast<void**>(&item)))) {
				hr = dialog->SetFolder(item);
				item->Release();
			}
			ILFree(itemList);
			RESULT_ERROR(hr);
		}
	}

	RESULT_ERROR(hr = dialog->SetOptions(options));

	if (!(options & FOS_PICKFOLDERS)) {
		SETTEXT(dialog->SetDefaultExtension, defaultExt);
		SETTEXT(dialog->SetFileName, fileName);
		if (FAILED(SetFilter(dialog, env, filter, filterIndex))) {
			goto _FinishDlg_;
		}
	}
	
	RESULT_ERROR(hr = dialog->Show((HWND)hwndParent));

	clazz = env->GetObjectClass(obj);

	if (mode && (options & FOS_ALLOWMULTISELECT) != 0) {
		IShellItemArray* items = nullptr;
		jobjectArray array = nullptr;
		
		RESULT_ERROR(hr = hr = reinterpret_cast<IFileOpenDialog*>(dialog)->GetResults(&items));
		if (SUCCEEDED(hr = ShellItemArrayToStringArray(env, array, items))) {
			env->SetObjectField(obj, 
				env->GetFieldID(clazz, "selectedFiles", "[Ljava/lang/String;"), array);
			env->DeleteLocalRef(array);
		}

		items->Release();

		RESULT_ERROR(hr);
	}

	if ((!mode || !(options & FOS_ALLOWMULTISELECT)) && SUCCEEDED(hr = dialog->GetResult(&item))) {
		jstring absolutePath = nullptr;
		if (SUCCEEDED(hr = item->GetDisplayName(SIGDN_FILESYSPATH, &text)) && 
			(absolutePath = env->NewString((jpcchar)text, lstrlen(text))) != nullptr) {
			env->SetObjectField(obj,
				env->GetFieldID(clazz, "absolutePath", "Ljava/lang/String;"), absolutePath);
			env->DeleteLocalRef(absolutePath);
		}

		item->Release();

		RESULT_ERROR(hr);
		CoTaskMemFree(text);

		if (absolutePath == nullptr) {
			goto _FinishDlg_;
		}
	}

	UINT uFilterIndex;
	if (SUCCEEDED(hr = dialog->GetFileTypeIndex(&uFilterIndex))) {
		env->SetIntField(obj, 
			env->GetFieldID(clazz, "filterIndex", "I"), (jint)uFilterIndex);
	}

_FinishDlg_:
	if (clazz != nullptr)
		env->DeleteLocalRef(clazz);
	dialog->Release();
	
	ShowError(env, hr);

	return SUCCEEDED(hr);
}
