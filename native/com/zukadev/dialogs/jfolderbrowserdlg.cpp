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

#include "jfolderbrowserdlg.hpp"
#include <ShlObj.h>

JNIEXPORT jboolean JNICALL
Java_com_zukadev_dialogs_FolderBrowserDialog_showDialog(
	JNIEnv *env,
	jobject obj,
	jstring root,
	jstring description,
	jint flags,
	jlong hwndParent)
{
	// Buffer where the name of the selected folder is saved
	wchar_t dirname[MAX_PATH]{};

	BROWSEINFO browse{};

	if (description)
	{
		browse.lpszTitle = reinterpret_cast<const wchar_t *>(env->GetStringChars(description, nullptr));
		if (!browse.lpszTitle)
		{
			return JNI_FALSE;
		}
	}

	browse.pszDisplayName = dirname;
	browse.ulFlags = flags;
	browse.hwndOwner = reinterpret_cast<HWND>(hwndParent);

	// the 'root' variable can be null or not, if it is null or
	// the path does not exist, the default path will be used
	if (root)
	{
		const jchar *dirroot = env->GetStringChars(root, nullptr);
		if (!dirroot)
		{
			if (browse.lpszTitle)
				env->ReleaseStringChars(description, (const jchar *)browse.lpszTitle);
			return JNI_FALSE;
		}

		// Possibly null if the path entered is invalid
		browse.pidlRoot = ILCreateFromPath((const wchar_t *)dirroot);
		env->ReleaseStringChars(root, dirroot);
	}

	LPITEMIDLIST itemIdList = SHBrowseForFolder(&browse);

	if (browse.lpszTitle)
	{
		env->ReleaseStringChars(description, (const jchar *)browse.lpszTitle);
	}

	if (browse.pidlRoot)
	{
		ILFree(const_cast<LPITEMIDLIST>(browse.pidlRoot));
	}

	if (!itemIdList)
	{
		ShowError(env); // A possible error is displayed
		return JNI_FALSE;
	}

	// Buffer where the path of the selected folder is saved
	wchar_t sel_dir[MAX_PATH]{};
	if (!SHGetPathFromIDList(itemIdList, sel_dir))
	{
		CoTaskMemFree(itemIdList);
		return JNI_FALSE;
	}

	CoTaskMemFree(itemIdList);

	jclass clazz = env->GetObjectClass(obj);

	// Assigns a value of type string to the specified variable.
	// In case of error it returns false
	auto SetStringField = [&](const wchar_t *value, const char *fieldnm)
	{
		auto new_string = env->NewString((const jchar *)value, lstrlen(value));
		if (new_string)
		{
			jfieldID field = env->GetFieldID(clazz, fieldnm, STRING_PATH);

			env->SetObjectField(obj, field, new_string);
			env->DeleteLocalRef(new_string);
			return JNI_TRUE;
		}
		// out of memory or the variable does not exist
		return JNI_FALSE;
	};

	if (!SetStringField(dirname, "displayName") ||
		!SetStringField(sel_dir, "absolutePath"))
		return JNI_FALSE;

	return JNI_TRUE;
}
