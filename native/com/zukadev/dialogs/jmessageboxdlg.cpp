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

#include "jmessageboxdlg.hpp"

JNIEXPORT jint JNICALL
Java_com_zukadev_dialogs_MessageBox_showMessage(

	JNIEnv *env,
	jobject,
	jlong hwndParent,
	jstring msg,
	jstring caption,
	jint type)
{
	const jchar* message = msg ? env->GetStringChars(msg, nullptr) : nullptr;
	const jchar* title = caption ? env->GetStringChars(caption, nullptr) : nullptr;

	static const wchar_t empty[] = L"";
	const wchar_t* msgPtr = message ? reinterpret_cast<const wchar_t*>(message) : empty;
	const wchar_t* titlePtr = title ? reinterpret_cast<const wchar_t*>(title) : empty;

	int result = MessageBoxW((HWND)hwndParent, msgPtr, titlePtr, type);

	if (msg && message)
		env->ReleaseStringChars(msg, message);
	if (caption && title)
		env->ReleaseStringChars(caption, title);
	return result;
}