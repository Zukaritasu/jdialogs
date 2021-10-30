//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jdialogs.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jdialogs.h"


JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
	(JNIPARAMS, jlong hwndParent, jstring msg, jstring caption, jint type)
{
	return MessageBox((HWND)hwndParent, GetStringChars(env, msg), 
		   GetStringChars(env, caption), type);
}

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
	(JNIPARAMS, jlong hwnd)
{
	return (jboolean)IsWindow((HWND)hwnd);
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD  reason, LPVOID lpReserved)
{
	switch (reason)
	{
	case DLL_PROCESS_ATTACH:
	{
		HRESULT hr;
		if (!SUCCEEDED(hr = CoInitialize(nullptr)) || !SUCCEEDED(hr = OleInitialize(nullptr))) // Error
		{
			TCHAR* message = NULL;
			DWORD n = FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_ALLOCATE_BUFFER, 
				                    NULL, hr, 0, (LPTSTR)&message, 0, NULL);
			if (n > 0 && message != NULL)
			{
				MessageBox(NULL, message, NULL, MB_ICONERROR);
				LocalFree(message);
			}
			
			return FALSE;
		}
		break;
	}
	break;
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		CoUninitialize();
		OleUninitialize();
		break;
	}
	return TRUE;
}