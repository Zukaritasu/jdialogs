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

#include "jdialogs.h"


JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
	(JNIPARAMS, jlong hwndParent, jstring msg, jstring caption, jint type)
{
	const jchar* _msg_ = env->GetStringChars(msg, nullptr);
	const jchar* _caption_ = env->GetStringChars(caption, nullptr);
	int result = MessageBox((HWND)hwndParent, (LPCWSTR)_msg_, (LPCWSTR)_caption_, type);
	env->ReleaseStringChars(msg, _msg_);
	env->ReleaseStringChars(caption, _caption_);
	return result;
}

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
	(JNIPARAMS, jlong hwnd)
{
	return (jboolean)IsWindow((HWND)hwnd);
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD reason, LPVOID lpReserved)
{
	switch (reason) {
		case DLL_PROCESS_DETACH:
			OleUninitialize();
			break;
		case DLL_PROCESS_ATTACH: {
			HRESULT hr;
			if (FAILED(hr = OleInitialize(nullptr))) {
				ShowError(nullptr, hr);
				return FALSE;
			}
			break;
		}
	}
	return TRUE;
}