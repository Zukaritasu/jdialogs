//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfiledlg.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once

#ifndef JFILEDLG_H
#define JFILEDLG_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

#define ERROR_FILTER "El arreglo de String 'filter' no tiene una longitud divisible entre 2"

	/* Cuadro de dialogo Selector de Archivos */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showDialog
		(JNIPARAMS, jboolean mode, jstring fileNameLabel, jstring okButtonLabel, jstring root,
			jstring fileName, jstring title, jstring defaultExt, jint options, jobjectArray filter,
			jint filterIndex, jlong hwndParent);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JFILEDLG_H

