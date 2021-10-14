//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jdialogs.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once

#ifndef JDIALOGS_H
#define JDIALOGS_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
		(JNIPARAMS, jlong hwndParent, jstring msg, jstring caption, jint type);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
		(JNIPARAMS, jlong hwnd);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JDIALOGS_H
