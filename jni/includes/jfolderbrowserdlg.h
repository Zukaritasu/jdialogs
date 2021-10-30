//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfolderbrowserdlg.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once
#ifndef JFOLDERBROWSERDLG_H
#define JFOLDERBROWSERDLG_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Cuadro de dialogo Selector de Carpetas */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FolderBrowserDialog_showDialog
		        			(JNIPARAMS, 
								jstring root,        /* La carpeta donde se iniciara la busqueda */
								jstring description, /* Una pequena descripcion de lo que debe hacer el usuario */
								jint flags,          /* Las banderas del cuadro de dialogo */
								jlong hwndParent     /* La ventana padre del cuadro de dialogo */
							);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JFOLDERBROWSERDLG_H
