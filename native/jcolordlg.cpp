// Copyright (C) 2021-2023 Zukaritasu
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <https://www.gnu.org/licenses/>.

#include "jcolordlg.hpp"



JNIFUNCTION(jboolean) 
Java_com_zukadev_dialogs_ColorDialog_showDialog(JNIPARAMS, jint flags, jlong hwndParent)
{
	jclass clazz = env->GetObjectClass(obj);
	CHECK_NULL(clazz);
	
	jfieldID fcustom = env->GetFieldID(clazz, "custom", "[I");
	CHECK_NULL(fcustom);
	
	jintArray custom = static_cast<jintArray>(env->GetObjectField(obj, fcustom));
	CHECK_NULL(custom);
	
	// In a certain case that the following condition is met, an
	// error message is displayed. This error is for developer only 
	if (env->GetArrayLength(custom) != NUM_CUSTOM_COLORS)
	{
		env->FatalError("The 'custom' array has a different \
		length than NUM_CUSTOM_COLORS");
		
		return JNI_FALSE;
	}
	
	jint* custom_colors = env->GetIntArrayElements(custom, NULL);
	CHECK_NULL(custom_colors);
	
	COLORREF buf_colors[NUM_CUSTOM_COLORS];
	// The RGBATORGB macro removes the alpha channel which is not supported 
	for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
	{
		buf_colors[i] = RGBATORGB(custom_colors[i]);
	}

	CHOOSECOLOR cChooser{};
	cChooser.lStructSize  = sizeof(CHOOSECOLOR);
	cChooser.Flags        = flags;
	cChooser.hwndOwner    = (HWND)hwndParent;
	cChooser.lpCustColors = buf_colors;

	// In case an error has occurred in the ChooseColor function,
	// the error code is evaluated at the end of this function 
	jboolean result = (jboolean)ChooseColor(&cChooser);
	if (result) 
	{
		// The RGBTORGBA macro adds the alpha channel to 255 
		for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
		{
			custom_colors[i] = RGBTORGBA(buf_colors[i]);
		}

		jfieldID fcolor = env->GetFieldID(clazz, "color", "I");
		CHECK_NULL(fcolor);
		// The last color selected 
		env->SetIntField(obj, fcolor, RGBTORGBA(cChooser.rgbResult));
		env->SetIntArrayRegion(custom, 0, NUM_CUSTOM_COLORS, custom_colors);
	}
	
	env->ReleaseIntArrayElements(custom, custom_colors, 0);
	env->DeleteLocalRef(custom);
	env->DeleteLocalRef(clazz);
	
	ShowError(env);
	return result;
}
