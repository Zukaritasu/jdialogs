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

#include "jbase.hpp"

// Use LocalFree(LPVOID)
static char* GetFormatMessage(DWORD code)
{
	char* message = NULL;
	FormatMessageA(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER,
				   NULL, code, 0, (char*)&message, 0, NULL);
	return message;
}

static void ShowWin32Exception(JNIEnv* env, DWORD code)
{
	jclass clazz = env->FindClass("org/zuky/dialogs/WindowsException");
	if (clazz != NULL)
	{
		jmethodID const_ = env->GetMethodID(clazz, "<init>", "(I)V");
		if (const_ != NULL)
		{
			jthrowable exception = (jthrowable)env->NewObject(clazz, const_, (jint)code);
			if (exception != NULL)
			{
				env->Throw(exception);
				env->DeleteLocalRef(exception);
				env->DeleteLocalRef(clazz);
			}
		}
	}
}

void ShowError(JNIEnv* env, DWORD code)
{
	DWORD error_code = (code == 0) ? GetLastError() : code;
	if (error_code != 0)
	{
		if (env)
		{
			switch (code)
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
			char* message = GetFormatMessage(error_code);
			if (message != NULL)
				fprintf(stderr, "Unknown error: 0x%X\n", error_code);
			else
			{
				fprintf(stderr, "%s\n", message);
				LocalFree(message);
			}
		}
	}
}

void ShowOutOfMemory(JNIEnv* env, const char* comment)
{
	jclass out_of_memory = env->FindClass("java/lang/OutOfMemoryError");
	if (out_of_memory != NULL)
	{
		env->ThrowNew(out_of_memory, comment);
		env->DeleteLocalRef(out_of_memory);
	}
}

JNIFUNCTION(jstring) 
Java_org_zuky_dialogs_WindowsException_getFormatMessage(JNIPARAMS, jint code)
{
	char* message = GetFormatMessage(code);
#ifdef _DEBUG
		fprintf(stdout, "Code: %d\n", code);
#endif // _DEBUG
	if (message != NULL)
	{
		jstring format_message = env->NewStringUTF(message);
		LocalFree(message);
		return format_message;
	}
	else
	{
		char unknown_code[48];
		sprintf(unknown_code, "Unknown error: 0x%X", (int)code);
		return env->NewStringUTF(unknown_code);
	}
}
