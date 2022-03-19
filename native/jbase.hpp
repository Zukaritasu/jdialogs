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

#pragma once

#include <Windows.h>

#include "jni.h"

#pragma warning(disable: 4996) // ignore deprecated

#define JNIFUNCTION(type) extern "C" JNIEXPORT type JNICALL
// The default parameters of each native function
#define JNIPARAMS JNIEnv* env, jobject obj

#define RGBATORGB(rgba) \
        RGB(((rgba >> 16) & 0xff), ((rgba >> 8) & 0xff), (rgba & 0xff))

#define RGBTORGBA(rgb) \
        (jint)((255 << 24) | ((GetRValue(rgb) & 0xff) << 16) | \
                             ((GetGValue(rgb) & 0xff) <<  8) | \
                             ((GetBValue(rgb) & 0xff) <<  0))

#define STRING_PATH "Ljava/lang/String;"
// Check jobject, jmethodID, jfieldID, etc... 
#define CHECK_NULL(obj) if (obj == NULL) return JNI_FALSE;
#define CHECK_NULL_OOM(obj) if (obj == NULL) return E_OUTOFMEMORY;

// It shows an error occurred on the part of Windows.
// Exceptions that occur in the jvm are displayed by the jvm
// itself. Exceptions or errors can be displayed by console
// or from a call to the JNIEnv::Throw or JNIEnv::ThrowNew method. 
void ShowError(JNIEnv* env, unsigned long code = 0);
void ShowOutOfMemory(JNIEnv* env, const char* comment = NULL);
void ThrowNew(JNIEnv* env, const char* class_name, const char* comment = NULL);

JNIFUNCTION(jstring)
Java_org_zuky_dialogs_WindowsException_getFormatMessage(JNIPARAMS, jint code);
