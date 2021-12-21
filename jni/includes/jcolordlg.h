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

#ifndef JCOLORDLG_H
#define JCOLORDLG_H

#include "defines.h"
#include <ColorDlg.h>

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

	/* Cuadro de dialogo Selector de Color */
	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showDialog
							(JNIPARAMS, 
								jint flags,      /* Las banderas del cuadro de dialogo */
								jlong hwndParent /* Ventana padre */
							);

#ifdef __cplusplus
}
#endif // __cplusplus
#endif // !JCOLORDLG_H
