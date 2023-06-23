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

#include "jfolderbrowserdlg.hpp"
#include <ShlObj.h>


JNIFUNCTION(jboolean) 
Java_org_zuka_dialogs_FolderBrowserDialog_showDialog(JNIPARAMS, 
													 jstring root, 
													 jstring description, 
													 jint flags, 
													 jlong hwndParent
													 )
{
	// Buffer where the name of the selected folder is saved
	wchar_t dirname[MAX_PATH]{};
	
	BROWSEINFO browse{};
	
	if (description != NULL)
	{
		browse.lpszTitle  = (const wchar_t*)env->GetStringChars(description, NULL);
		CHECK_NULL(browse.lpszTitle);
	}
	
	browse.pszDisplayName = dirname;
	browse.ulFlags        = flags;
	browse.hwndOwner      = (HWND)hwndParent;

	// the 'root' variable can be null or not, if it is null or
	// the path does not exist, the default path will be used 
	if (root != NULL)
	{
		const jchar* dirroot = env->GetStringChars(root, NULL);
		CHECK_NULL(dirroot);
		
		// Possibly null if the path entered is invalid
		browse.pidlRoot = ILCreateFromPath((const wchar_t*)dirroot);
		env->ReleaseStringChars(root, dirroot);
	}

	LPITEMIDLIST itemIdList = SHBrowseForFolder(&browse);
	
	if (browse.lpszTitle != NULL)
		env->ReleaseStringChars(description, (const jchar*)browse.lpszTitle);
	if (browse.pidlRoot != NULL)
		ILFree(const_cast<LPITEMIDLIST>(browse.pidlRoot));
	
	if (itemIdList == NULL) 
	{
		ShowError(env); // A possible error is displayed
		return JNI_FALSE;
	}

	// Buffer where the path of the selected folder is saved
	wchar_t sel_dir[MAX_PATH]{};
	if (!SHGetPathFromIDList(itemIdList, sel_dir))
		return JNI_FALSE;
	CoTaskMemFree(itemIdList);

	jclass clazz = env->GetObjectClass(obj);
	CHECK_NULL(clazz);
	
	// Assigns a value of type string to the specified variable.
	// In case of error it returns false
	auto SetStringField = [&](const wchar_t* value, const char* fieldnm)
	{
		auto string = env->NewString((const jchar*)value, lstrlen(value));
		if (string != NULL)
		{
			jfieldID field = env->GetFieldID(clazz, fieldnm, STRING_PATH);
			if (field != NULL) 
			{
				env->SetObjectField(obj, field, string);
				env->DeleteLocalRef(string);
				return JNI_TRUE;
			}
		}
		// out of memory or the variable does not exist 
		return JNI_FALSE;
	};

	if (!SetStringField(dirname, "displayName") || 
		!SetStringField(sel_dir, "absolutePath"))
		return JNI_FALSE;
		
	env->DeleteLocalRef(clazz);
	return JNI_TRUE;
}
