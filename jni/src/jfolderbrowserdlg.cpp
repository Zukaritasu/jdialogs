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

#include "jfolderbrowserdlg.h"



JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FolderBrowserDialog_showDialog(
	JNIPARAMS, jstring root, jstring description, jint flags, jlong hwndParent)
{
	TCHAR dirname[MAX_PATH]{};
	LPITEMIDLIST _item_ = nullptr;
	
	BROWSEINFO browse{};
	browse.lpszTitle      = (LPCWSTR)env->GetStringChars(description, nullptr);
	browse.pszDisplayName = dirname;
	browse.ulFlags        = flags;
	browse.hwndOwner      = (HWND)hwndParent;

	const jchar* _root_ = env->GetStringChars(root, nullptr);
	if (_root_ != nullptr) {
		browse.pidlRoot = _item_ = ILCreateFromPath((PCWSTR)_root_);
		env->ReleaseStringChars(root, _root_);
	}

	LPITEMIDLIST itemIdList = SHBrowseForFolder(&browse);
	env->ReleaseStringChars(description, (jpcchar)browse.lpszTitle);
	ILFree(_item_);
	if (itemIdList == nullptr) {
		ShowError(env);
		return JNI_FALSE;
	}

	TCHAR directory[MAX_PATH]{};
	SHGetPathFromIDList(itemIdList, directory);
	CoTaskMemFree(itemIdList);

	jclass clazz = env->GetObjectClass(obj);

	jstring displayName  = env->NewString((jpcchar)dirname, lstrlen(dirname));
	env->SetObjectField(obj,
		env->GetFieldID(clazz, "displayName", STRING_PATH), displayName);
	env->DeleteLocalRef(displayName);

	jstring absolutePath = env->NewString((jpcchar)directory, lstrlen(directory));
	env->SetObjectField(obj,
		env->GetFieldID(clazz, "absolutePath", STRING_PATH), absolutePath);
	env->DeleteLocalRef(absolutePath);

	env->DeleteLocalRef(clazz);
	return JNI_TRUE;
}
