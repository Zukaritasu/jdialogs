//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo stddef.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once
#ifndef STDDEF_H
#define STDDEF_H

#include <windows.h>
#include <commdlg.h>
#include <ColorDlg.h>
#include <shlobj.h>
#include <shellapi.h>
#include <jni.h>

#define JNIFUNCTION(type) JNIEXPORT type JNICALL
#define JNIPARAMS JNIEnv* env, jobject obj

#ifdef __cplusplus
#define JNIFUNCTIONS_BEGIN extern "C" {
#define JNIFUNCTIONS_END }
#else
#define JNIFUNCTIONS_BEGIN
#define JNIFUNCTIONS_END
#endif // __cplusplus

#define STRING_PATH         "Ljava/lang/String;"
#ifdef UNICODE
#define CUSTOMDATAPROC L"customData"
#else
#define CUSTOMDATAPROC "customData"
#endif // !UNICODE

inline LPCTSTR GetStringChars(JNIEnv* env, jstring text)
{	
	if (text) {
#ifdef __cplusplus
#ifdef UNICODE
		const wchar_t* string = (wchar_t*)env->GetStringChars(text, NULL);
		env->ReleaseStringChars(text, NULL);
#else
		const char* string = env->GetStringUTFChars(text, NULL);
		env->ReleaseStringUTFChars(text, NULL);
#endif // !UNICODE
#else
#ifdef UNICODE
		const wchar_t* string = (wchar_t*)(*env)->GetStringChars(text, NULL);
		(*env)->ReleaseStringChars(text, NULL);
#else
		const char* string = (*env)->GetStringUTFChars(text, NULL);
		(*env)->ReleaseStringUTFChars(text, NULL);
#endif // !UNICODE
#endif // __cplusplus
		return string;
	}
	return NULL;
}

#endif // !STDDEF_H