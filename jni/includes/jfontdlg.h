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
#ifndef JFONTDLG_H
#define JFONTDLG_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Cuadro de dialogo Selector de Fuentes */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showDialog
		(JNIPARAMS, 
			jstring name,    /* El nombre de la fuente */
			jint style,      /* Los estilos de la fuente NORMAL, BOLD, ITALIC (Soportado por Font) */
			jint size,       /* Tamano de la fuente */
			jint flags,      /* Las banderas del cuadro de dialogo */
			jint color,      /* El color inicial del texto de muestra */
			jlong hwndParent /* Identificador de la ventana padre */
		);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JFONTDLG_H
