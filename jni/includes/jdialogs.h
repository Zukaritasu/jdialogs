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

#ifndef JDIALOGS_H
#define JDIALOGS_H

#include "defines.h"

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Muestra un mensaje */
	JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
		(JNIPARAMS, 
			jlong hwndParent, /* La ventana padre */
			jstring msg,      /* El mensaje a mostrar */
			jstring caption,  /* El titulo del cuadro de dialogo */
			jint type         /* El tipo de cuadro de dialogo */
		);

	/* Retorna true si el identificador es valido */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
		(JNIPARAMS, 
			jlong hwnd /* Identificador de la ventana */
		);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JDIALOGS_H
