//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfontdlg.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once
#ifndef JFONTDLG_H
#define JFONTDLG_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Cuadro de dialogo Selector de Fuentes */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showDialog
		(JNIPARAMS, jstring name, jint style, jint size, jint flags,
			jint color, jlong hwndParent);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JFONTDLG_H
