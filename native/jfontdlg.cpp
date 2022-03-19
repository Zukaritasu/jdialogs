// Copyright (C) 2021-2022 Zukaritasu
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

#include "jfontdlg.hpp"

#include <strsafe.h>
#ifdef _DEBUG
#include <cstdio>	
#endif // _DEBUG

JNIFUNCTION(jboolean)
Java_org_zuky_dialogs_FontDialog_showDialog(JNIPARAMS, 
											jstring name, 
											jint style, 
											jint size, 
											jint flags, 
											jint color, 
											jlong hwndParent)
{
	LOGFONT logFont{};

	CHOOSEFONT choose{};
	choose.lStructSize = sizeof(CHOOSEFONT);
	choose.Flags       = flags;
	choose.lpLogFont   = &logFont;
	choose.hwndOwner   = (HWND)hwndParent;

	// Sample text color is set 
	if (flags & CF_EFFECTS)
		choose.rgbColors = RGBATORGB(color);
	if ((flags & CF_INITTOLOGFONTSTRUCT) && name != NULL)
	{
		const jchar* fontnm = env->GetStringChars(name, NULL);
		CHECK_NULL(fontnm);
		
		// The field 'lfFaceName' has a limit of 32 characters including
		// the null character so the StringCbCopy function copies as many
		// characters as needed although it would be wrong to specify a
		// font name with a length that exceeds the limit 
		StringCbCopy(logFont.lfFaceName, sizeof(logFont.lfFaceName), 
					(const wchar_t*)fontnm);
		env->ReleaseStringChars(name, fontnm);

		logFont.lfHeight         = size * (-1);
		logFont.lfWeight         = ((style & 1) != 0) ? FW_BOLD : FW_NORMAL;
		logFont.lfItalic         = ((style & 2) != 0) ? 0xff : 0x0;
		logFont.lfCharSet        = DEFAULT_CHARSET;
		logFont.lfOutPrecision   = OUT_TT_PRECIS;
		logFont.lfQuality        = PROOF_QUALITY;
		logFont.lfPitchAndFamily = FF_ROMAN;
	}

	if (ChooseFont(&choose))
	{
#ifdef _DEBUG
		fprintf(stdout, "Height: %d\n", logFont.lfHeight);
		fprintf(stdout, "Width: %d\n", logFont.lfWidth);
#endif // _DEBUG
		// The java.awt.Font class only supports the BOLD and ITALIC styles
		style = ((logFont.lfWeight == FW_BOLD) ? 1 : 0) | 
		        ((logFont.lfItalic == 0xff)    ? 2 : 0);

		jclass classFont = env->FindClass("java/awt/Font");
		CHECK_NULL(classFont);
		// Font(String name, int style, int size)
		jmethodID method = env->GetMethodID(classFont, "<init>", "(Ljava/lang/String;II)V");
		CHECK_NULL(method);
		
		jstring faceName = env->NewString((const jchar*)logFont.lfFaceName, 
								lstrlen(logFont.lfFaceName));
		CHECK_NULL(faceName);
		// The Font class object is created with the name of the font,
		// the style and the size 
		jobject fontObj  = env->NewObject(classFont, method, faceName, 
								style, logFont.lfHeight * (-1));
		CHECK_NULL(fontObj);

		jclass clazz     = env->GetObjectClass(obj);
		CHECK_NULL(clazz);
		
		jfieldID ffont   = env->GetFieldID(clazz, "font", "Ljava/awt/Font;");
		CHECK_NULL(ffont);
		
		env->SetObjectField(obj, ffont, fontObj);
		
		// If effects are enabled, the selected color is save
		if ((flags & CF_EFFECTS)) 
		{
			jfieldID fcolor = env->GetFieldID(clazz, "color", "I");
			CHECK_NULL(fcolor);
			env->SetIntField(obj, fcolor, RGBTORGBA(choose.rgbColors));
		}

		env->DeleteLocalRef(clazz);
		env->DeleteLocalRef(classFont);
		env->DeleteLocalRef(fontObj);
		env->DeleteLocalRef(faceName);

		return JNI_TRUE;
	}

	ShowError(env);
	return JNI_FALSE;
}
