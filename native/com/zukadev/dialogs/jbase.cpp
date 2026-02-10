
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

#include "jbase.hpp"

#include <cstdio> // fprintf and sprintf

// Use LocalFree(LPVOID)
static char *GetFormatMessage(DWORD code)
{
	char *message = nullptr;
	FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER,
				   nullptr, code, 0, (char *)&message, 0, nullptr);
	return message;
}

static void ShowWin32Exception(JNIEnv *env, DWORD code)
{
	jclass clazz = env->FindClass("com/zukadev/dialogs/WindowsException");
	if (clazz)
	{
		// WindowsException(int errorCode)
		jmethodID const_ = env->GetMethodID(clazz, "<init>", "(I)V");
		if (const_)
		{
			jthrowable exception = (jthrowable)env->NewObject(clazz, const_, (jint)code);
			if (exception)
			{
				env->Throw(exception);
				env->DeleteLocalRef(exception);
			}
		}
		env->DeleteLocalRef(clazz);
	}
}

void ShowError(JNIEnv *env, DWORD code)
{
	DWORD error_code = (code == 0) ? GetLastError() : code;
	if (error_code != 0)
	{
		if (env)
		{
			switch (error_code)
			{
			case E_OUTOFMEMORY:
				ShowOutOfMemory(env);
				break;
			default:
				ShowWin32Exception(env, error_code);
				break;
			}
		}
		else
		{
			char *message = GetFormatMessage(error_code);
			if (!message)
				fprintf(stderr, "Unknown error: 0x%X\n", error_code);
			else
			{
				fprintf(stderr, "%s\n", message);
				LocalFree(message);
			}
		}
	}
}

void ShowOutOfMemory(JNIEnv *env, const char *comment)
{
	ThrowNew(env, "java/lang/OutOfMemoryError", comment);
}

void ThrowNew(JNIEnv *env, const char *class_name, const char *comment)
{
	jclass exception = env->FindClass(class_name);
	if (exception)
	{
		env->ThrowNew(exception, comment);
		env->DeleteLocalRef(exception);
	}
}

JNIEXPORT jstring JNICALL
Java_com_zukadev_dialogs_WindowsException_getFormatMessage(JNIEnv* env, jobject, jint code)
{
	char *message = GetFormatMessage(code);
	if (message)
	{
		jstring format_message = env->NewStringUTF(message);
		LocalFree(message);
		return format_message;
	}
	else
	{
		char unknown_error[48];
		snprintf(unknown_error, sizeof(unknown_error), "Unknown error: 0x%X", (unsigned)code);
		return env->NewStringUTF(unknown_error);
	}
}

JNIEXPORT jlong JNICALL
Java_com_zukadev_dialogs_CommonDialog_getHWnd0(JNIEnv *env, jobject, jobject window)
{
	// TODO The function may return null due to access to private classes
	jclass clazz = env->FindClass("sun/awt/windows/WToolkit");
	if (!clazz) return 0L;
	// return WComponentPeer object
	jmethodID targetToPeer = env->GetStaticMethodID(clazz, "targetToPeer", "(Ljava/lang/Object;)Ljava/lang/Object;");
	if (!targetToPeer)
	{
		env->DeleteLocalRef(clazz);
		return 0L;
	}
	
	jobject peer = env->CallStaticObjectMethod(clazz, targetToPeer, window);
	env->DeleteLocalRef(clazz);

	if (peer)
	{
		jclass class_peer = env->FindClass("sun/awt/windows/WComponentPeer");
		jmethodID getHWnd = class_peer ? env->GetMethodID(class_peer, "getHWnd", "()J") : nullptr;
		jlong hwnd = (getHWnd && class_peer) ? env->CallLongMethod(peer, getHWnd) : 0L;
		if (class_peer) env->DeleteLocalRef(class_peer);
		env->DeleteLocalRef(peer);
		return hwnd;
	}
	return 0L;
}
