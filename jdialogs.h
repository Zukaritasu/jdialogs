//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo jdialogs.h
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#pragma once
#ifndef JDIALOGS_H
#define JDIALOGS_H

#include "stddef.h"

JNIFUNCTIONS_BEGIN

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showColorDialog
		(JNIPARAMS, jobject dialog, jint flags, jlong hwndParent);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_DirectoryDialog_showDirectoryDialog
		(JNIPARAMS, jobject dialog, jstring root, jstring title, jint flags, jlong hwndParent);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showFontDialog
		(JNIPARAMS, jobject dialog, jstring name, jint style, jint size, jint flags, 
			jint color, jlong hwndParent);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showFileDialog
		(JNIPARAMS, jobject dialog, jboolean mode, jstring filter, jstring customFilter,
			jint maxCusFilter, jint filterIndex, jstring fileTitle, jstring initialDir,
			jstring title, jint flags, jint fileOffSet, jint fileExtension, jstring defExtension,
			jint flagsEx, jint maxFile, jlong hwndParent);

	JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
		(JNIPARAMS, jlong hwndParent, jstring msg, jstring caption, jint type);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
		(JNIPARAMS, jlong handle);

	JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_setDialogTitle
		(JNIPARAMS, jstring title, jlong handle);

JNIFUNCTIONS_END

#endif // !JDIALOGS_H