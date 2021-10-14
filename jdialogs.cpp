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
	return IsWindow((HWND)hwnd);
}

BOOL APIENTRY DllMain(HMODULE hModule, DWORD  reason, LPVOID lpReserved)
{
	switch (reason)
	{
	case DLL_PROCESS_ATTACH:
	{
		if (!SUCCEEDED(CoInitialize(nullptr)) || !SUCCEEDED(OleInitialize(nullptr))) // Error
		{
			MessageBox(nullptr, 
				TEXT("Initialize COM Library or OLE Library error"), 
				TEXT("Error"), 
				MB_ICONERROR);
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