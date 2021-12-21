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

#pragma once

#ifndef DEFINES_H
#define DEFINES_H

#include <windows.h>
#include <commdlg.h>
#include <shlobj.h>
#include <shellapi.h>

#include <stdio.h>

#include "jni.h"

typedef const jchar* jpcchar;

#define JNIFUNCTION(type) JNIEXPORT type JNICALL
#define JNIPARAMS JNIEnv* env, jobject obj

#define RGBATORGB(rgba) \
        RGB(((rgba >> 16) & 0xff), ((rgba >> 8) & 0xff), (rgba & 0xff))

#define RGBTORGBA(rgb) \
        (jint)((255 << 24) | ((GetRValue(rgb) & 0xff) << 16) | \
                             ((GetGValue(rgb) & 0xff) <<  8) | \
                             ((GetBValue(rgb) & 0xff) <<  0))

#define STRING_PATH "Ljava/lang/String;"

inline void ShowError(JNIEnv* env, unsigned long code = 0L)
{
	unsigned long error = (code == 0L) ? GetLastError() : code;
	if (error != 0L) {
		char* message = nullptr;
		FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER, nullptr, error, 0, (char*)&message, 0, nullptr);
		if (message != nullptr) {
			if (env == nullptr) {
				fprintf(stderr, message);
			} else {
				env->ThrowNew(env->FindClass("org/zuky/dialogs/WindowsException"), message);
			}
			LocalFree(message);
		}
	}
}

#endif // !DEFINES_H
