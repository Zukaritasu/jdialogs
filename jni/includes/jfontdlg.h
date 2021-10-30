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
