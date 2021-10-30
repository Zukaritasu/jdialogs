//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo defines.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once

#ifndef DEFINES_H
#define DEFINES_H

#include <windows.h>
#include <commdlg.h>
#include <ColorDlg.h>
#include <shlobj.h>
#include <shellapi.h>

#include "jni.h"

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE MACROS
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#define JNIFUNCTION(type) JNIEXPORT type JNICALL
#define JNIPARAMS JNIEnv* env, jobject obj

#define RGBATORGB(rgba) \
        RGB(((rgba >> 16) & 0xff), ((rgba >> 8) & 0xff), (rgba & 0xff))

#define RGBTORGBA(rgb) \
        (jint)((255 << 24) | ((GetRValue(rgb) & 0xff) << 16) | \
                             ((GetGValue(rgb) & 0xff) <<  8) | \
                             ((GetBValue(rgb) & 0xff) <<  0))

#define GET_OBJECT_R0(nm, val) val; if (nm## == nullptr) return JNI_FALSE;

#ifdef UNICODE
#define NEW_STRING(str) \
		env->NewString(reinterpret_cast<const jchar*>(str), lstrlen(str))
#else
#define NEW_STRING(str) \
		env->NewStringUTF(str, lstrlen(str))
#endif // UNICODE

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE CONSTANTES
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#define STRING_PATH         "Ljava/lang/String;"

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE FUNCIONES
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

inline LPCTSTR GetStringChars(JNIEnv* env, jstring text)
{
	if (text) {
#ifdef UNICODE
		const jchar* string = env->GetStringChars(text, nullptr);
#else
		const char* string  = env->GetStringUTFChars(text, nullptr);
#endif // !UNICODE
		return reinterpret_cast<LPCTSTR>(string);
	}
	return nullptr;
}

#endif // !DEFINES_H
