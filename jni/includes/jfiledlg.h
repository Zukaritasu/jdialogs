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

	/* Cuadro de dialogo Selector de Archivos */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showDialog
						  (JNIPARAMS, 
							jboolean mode,         /* Abrir o guardar */
							jstring fileNameLabel, /* El texto de la etiqueta "Nombre de archivo" */
							jstring okButtonLabel, /* El texto del boton OK */
							jstring root,          /* La carpeta donde se iniciara la busqueda */
							jstring fileName,      /* El nombre de archivo que se mostrara en la caja de texto */
							jstring title,         /* El titulo del cuadro de dialogo */
							jstring defaultExt,    /* La extension de archivo por defecto */
							jint options,          /* Las opciones del cuadro de dialogo */
							jobjectArray filter,   /* El filtro de archivos */
							jint filterIndex,      /* El indice del filtro que se seleccionara */
							jlong hwndParent       /* La ventana padre */
						  );

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JFILEDLG_H

