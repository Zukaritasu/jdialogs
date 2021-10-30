//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jdialogs.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

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
