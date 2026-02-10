// Copyright (C) 2021-2026 Zukaritasu
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
#include <vector>
#include <string>

struct OleScope {
	HRESULT hr;
	OleScope() { hr = OleInitialize(NULL); }
	~OleScope() { if (SUCCEEDED(hr)) OleUninitialize(); }
};


static HRESULT ShellItemArrayToStringArray(JNIEnv* env, jobjectArray& outArray, IShellItemArray* items) {
	DWORD count = 0;
	HRESULT hr = items->GetCount(&count);

	if (FAILED(hr) || count == 0) {
		return hr;
	}

	jclass clsString = env->FindClass("java/lang/String");
	if (!clsString) return E_FAIL;

	outArray = env->NewObjectArray(static_cast<jsize>(count), clsString, nullptr);
	if (!outArray) {
		env->DeleteLocalRef(clsString);
		return E_OUTOFMEMORY;
	}

	for (DWORD i = 0; i < count; ++i) {
		IShellItem* item = nullptr;
		hr = items->GetItemAt(i, &item);
		if (FAILED(hr)) break;

		PWSTR sysPath = nullptr;
		hr = item->GetDisplayName(SIGDN_FILESYSPATH, &sysPath);
		item->Release();

		if (SUCCEEDED(hr)) {
			jstring jElement = env->NewString(reinterpret_cast<const jchar*>(sysPath),
				static_cast<jsize>(wcslen(sysPath)));

			if (jElement) {
				env->SetObjectArrayElement(outArray, static_cast<jsize>(i), jElement);
				env->DeleteLocalRef(jElement);
			}

			CoTaskMemFree(sysPath);
		}
	}

	env->DeleteLocalRef(clsString);

	if (FAILED(hr) && outArray) {
		env->DeleteLocalRef(outArray);
		outArray = nullptr;
	}

	return hr;
}


class NativeFileDialog {
private:
	IFileDialog* pfd = nullptr;
	HRESULT lastHr = S_OK;

public:
	NativeFileDialog(bool isOpenMode) {
		lastHr = CoCreateInstance(isOpenMode ? CLSID_FileOpenDialog : CLSID_FileSaveDialog,
			nullptr, CLSCTX_ALL,
			isOpenMode ? IID_IFileOpenDialog : IID_IFileSaveDialog,
			reinterpret_cast<void**>(&pfd));
	}

	~NativeFileDialog() {
		if (pfd) pfd->Release();
	}

	bool isValid() const { return SUCCEEDED(lastHr) && pfd != nullptr; }
	HRESULT getLastHResult() const { return lastHr; }

	void SetTitle(JNIEnv* env, jstring title) {
		if (!title) return;
		const jchar* raw = env->GetStringChars(title, nullptr);
		pfd->SetTitle(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(title, raw);
	}

	void SetOkButtonLabel(JNIEnv* env, jstring label) {
		if (!label) return;
		const jchar* raw = env->GetStringChars(label, nullptr);
		pfd->SetOkButtonLabel(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(label, raw);
	}

	void SetOptions(jint options) {
		pfd->SetOptions(static_cast<FILEOPENDIALOGOPTIONS>(options));
	}

	void SetFileName(JNIEnv* env, jstring fileName) {
		if (!fileName) return;
		const jchar* raw = env->GetStringChars(fileName, nullptr);
		pfd->SetFileName(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(fileName, raw);
	}
	
	void SetFileNameLabel(JNIEnv* env, jstring fileNamelabel) {
		if (!fileNamelabel) return;
		const jchar* raw = env->GetStringChars(fileNamelabel, nullptr);
		pfd->SetFileNameLabel(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(fileNamelabel, raw);
	}

	void SetDefaultExtension(JNIEnv* env, jstring ext) {
		if (!ext) return;
		const jchar* raw = env->GetStringChars(ext, nullptr);
		pfd->SetDefaultExtension(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(ext, raw);
	}

	void SetRootFolder(JNIEnv* env, jstring rootPath) {
		if (!rootPath) return;
		const jchar* raw = env->GetStringChars(rootPath, nullptr);
		LPITEMIDLIST pidl = ILCreateFromPath(reinterpret_cast<LPCWSTR>(raw));
		env->ReleaseStringChars(rootPath, raw);

		if (pidl) {
			IShellItem* pItem = nullptr;
			if (SUCCEEDED(SHCreateItemFromIDList(pidl, IID_IShellItem, (void**)&pItem))) {
				pfd->SetFolder(pItem);
				pItem->Release();
			}
			ILFree(pidl);
		}
	}

	HRESULT ApplyFilters(JNIEnv* env, jobjectArray filterArray, jint defaultIndex) {
		if (!filterArray) return S_OK;
		jsize len = env->GetArrayLength(filterArray);
		if (len == 0 || (len % 2) != 0) return E_INVALIDARG;

		UINT count = len / 2;
		std::vector<COMDLG_FILTERSPEC> specs(count);
		std::vector<std::wstring> buffer;

		// CRITICAL: Pre-allocate memory to prevent vector reallocation.
		// If the vector reallocates, the pointers returned by .c_str() and stored 
		// in 'specs' will be invalidated (especially due to Small String Optimization),
		// leading to memory corruption or crashes when the Windows API accesses them.
		buffer.reserve(static_cast<size_t>(count) * 2);

		for (UINT i = 0; i < count; ++i) {
			jstring jDesc = (jstring)env->GetObjectArrayElement(filterArray, i * 2);
			jstring jSpec = (jstring)env->GetObjectArrayElement(filterArray, (i * 2) + 1);

			auto getString = [&](jstring s) {
				if (!s) return std::wstring();
				const jchar* raw = env->GetStringChars(s, nullptr);
				if (!raw) return std::wstring();
				std::wstring ws(reinterpret_cast<const wchar_t*>(raw));
				env->ReleaseStringChars(s, raw);
				return ws;
			};

			buffer.push_back(getString(jDesc));
			specs[i].pszName = buffer.back().c_str();
			buffer.push_back(getString(jSpec));
			specs[i].pszSpec = buffer.back().c_str();

			env->DeleteLocalRef(jDesc);
			env->DeleteLocalRef(jSpec);
		}

		HRESULT hr = pfd->SetFileTypes(count, specs.data());
		if (SUCCEEDED(hr)) pfd->SetFileTypeIndex(defaultIndex);
		return hr;
	}

	HRESULT Show(HWND parent) {
		return pfd->Show(parent);
	}

	void SyncResultsToJava(JNIEnv* env, jobject javaObj, bool mode, jint options) {
		jclass clazz = env->GetObjectClass(javaObj);

		if (mode && (options & FOS_ALLOWMULTISELECT)) {
			IFileOpenDialog* pOpenDlg = static_cast<IFileOpenDialog*>(pfd);
			IShellItemArray* pItems = nullptr;
			if (SUCCEEDED(pOpenDlg->GetResults(&pItems))) {
				jobjectArray jArray = nullptr;
				if (SUCCEEDED(ShellItemArrayToStringArray(env, jArray, pItems))) {
					jfieldID fid = env->GetFieldID(clazz, "selectedFiles", "[Ljava/lang/String;");
					env->SetObjectField(javaObj, fid, jArray);
					env->DeleteLocalRef(jArray);
				}
				pItems->Release();
			}
		}
		else {
			IShellItem* pItem = nullptr;
			if (SUCCEEDED(pfd->GetResult(&pItem))) {
				PWSTR path = nullptr;
				if (SUCCEEDED(pItem->GetDisplayName(SIGDN_FILESYSPATH, &path))) {
					jstring jPath = env->NewString(reinterpret_cast<const jchar*>(path), (jsize)wcslen(path));
					jfieldID fid = env->GetFieldID(clazz, "absolutePath", "Ljava/lang/String;");
					env->SetObjectField(javaObj, fid, jPath);
					env->DeleteLocalRef(jPath);
					CoTaskMemFree(path);
				}
				pItem->Release();
			}
		}

		UINT uIdx;
		if (SUCCEEDED(pfd->GetFileTypeIndex(&uIdx))) {
			jfieldID fid = env->GetFieldID(clazz, "filterIndex", "I");
			env->SetIntField(javaObj, fid, (jint)uIdx);
		}
	}
};

extern "C" JNIEXPORT jboolean JNICALL
Java_com_zukadev_dialogs_FileDialog_showDialog(JNIEnv* env, jobject obj,
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
	OleScope ole;
	if (FAILED(ole.hr)) return JNI_FALSE;

	NativeFileDialog dialog(mode);
	if (!dialog.isValid()) {
		ShowError(env, dialog.getLastHResult());
		return JNI_FALSE;
	}

	dialog.SetTitle(env, title);
	dialog.SetOkButtonLabel(env, okButtonLabel);
	dialog.SetRootFolder(env, root);
	dialog.SetFileName(env, fileName);
	dialog.SetFileNameLabel(env, fileNameLabel);
	dialog.SetOptions(options);

	if (!(options & FOS_PICKFOLDERS)) {
		dialog.SetDefaultExtension(env, defaultExt);
		dialog.ApplyFilters(env, filter, filterIndex);
	}

	HRESULT hr = dialog.Show((HWND)hwndParent);

	if (SUCCEEDED(hr)) {
		dialog.SyncResultsToJava(env, obj, mode, options);
	}

	if (FAILED(hr) && hr != HRESULT_FROM_WIN32(ERROR_CANCELLED)) {
		ShowError(env, hr);
	}

	return SUCCEEDED(hr);
}
