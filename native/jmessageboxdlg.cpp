// Copyright (C) 2021-2022 Zukaritasu
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

JNIFUNCTION(jint)
Java_org_zuky_dialogs_MessageBox_showMessage(JNIPARAMS, 
											 jlong hwndParent, 
											 jstring msg, 
											 jstring caption, 
											 jint type
											)
{
	
	const jchar* message = msg ? env->GetStringChars(msg, NULL) : NULL;
	const jchar* title = caption ? env->GetStringChars(caption, NULL) : NULL;
	
	int result  = MessageBox((HWND)hwndParent, (const wchar_t*)message, (const wchar_t*)title, type);
	
	if (msg) env->ReleaseStringChars(msg, message);
	if (caption) env->ReleaseStringChars(caption, title);
	return result;
}