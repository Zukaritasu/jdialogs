//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jcolordlg.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once

#ifndef JCOLORDLG_H
#define JCOLORDLG_H

#include "defines.h"
#include <ColorDlg.h>

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Cuadro de dialogo Selector de Color */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showDialog
		(JNIPARAMS, jint flags, jlong hwndParent);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JCOLORDLG_H
