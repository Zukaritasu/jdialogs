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

#include "jcolordlg.h"



JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showDialog
	(JNIPARAMS, jint flags, jlong hwndParent)
{
	COLORREF customColors[NUM_CUSTOM_COLORS]{};

	jclass clazz     = env->GetObjectClass(obj);
	jintArray colors = (jintArray)(env->GetObjectField(obj, env->GetFieldID(clazz, "custom", "[I")));
	jint* custColors = env->GetIntArrayElements(colors, nullptr);

	for (int i = 0; i < NUM_CUSTOM_COLORS; i++) {
		customColors[i] = RGBATORGB(custColors[i]);
	}

	CHOOSECOLOR cChooser{};
	cChooser.lStructSize  = sizeof(CHOOSECOLOR);
	cChooser.Flags        = flags;
	cChooser.hInstance    = (HWND)GetModuleHandle(nullptr);
	cChooser.hwndOwner    = (HWND)hwndParent;
	cChooser.lpCustColors = customColors;

	jboolean result = (jboolean)ChooseColor(&cChooser);
	if (result) {
		for (int i = 0; i < NUM_CUSTOM_COLORS; i++) {
			custColors[i] = RGBTORGBA(customColors[i]);
		}

		env->SetIntField(obj,
			env->GetFieldID(clazz, "color", "I"), RGBTORGBA(cChooser.rgbResult));
		env->SetIntArrayRegion(colors, 0, NUM_CUSTOM_COLORS, custColors);
	}
	
	env->DeleteLocalRef(clazz);
	env->DeleteLocalRef(colors);
	
	ShowError(env);
	return result;
}
