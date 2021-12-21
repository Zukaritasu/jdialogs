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
