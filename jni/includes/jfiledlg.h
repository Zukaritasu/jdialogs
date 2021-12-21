/*
** Copyright (C) 2021 Zukaritasu
**
** This program is free software: you can redistribute it and/or modify
** it under the terms of the GNU General Public License as published by
** the Free Software Foundation, either version 3 of the License, or
** (at your option) any later version.
**
** This program is distributed in the hope that it will be useful,
** but WITHOUT ANY WARRANTY; without even the implied warranty of
** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
** GNU General Public License for more details.
**
** You should have received a copy of the GNU General Public License
** along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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

