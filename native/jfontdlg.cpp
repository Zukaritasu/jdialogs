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
#include <cmath>
#ifdef _DEBUG
#include <cstdio>	
#endif // _DEBUG

static BOOL InitializeLogFont(JNIEnv* env, LOGFONT& logf, HDC hdc,
							  jstring name, jint style, jint size)
{
	const jchar* font_name = env->GetStringChars(name, NULL);
	if (font_name != NULL)
	{
		// Copy font name, max 32 characters including the null character
		StringCbCopy(logf.lfFaceName, sizeof(logf.lfFaceName), 
					(const wchar_t*)font_name);
		env->ReleaseStringChars(name, font_name);
		
		logf.lfHeight         = -MulDiv(size, GetDeviceCaps(hdc, LOGPIXELSY), 72);
		
		logf.lfWeight         = (style & 1) ? FW_BOLD : FW_NORMAL;
		logf.lfItalic         = (style & 2) ? 0xff : 0x0;
		
		// Set default values
		
		logf.lfCharSet        = DEFAULT_CHARSET;
		logf.lfOutPrecision   = OUT_TT_PRECIS;
		logf.lfQuality        = PROOF_QUALITY;
		logf.lfPitchAndFamily = FF_ROMAN;
		
		return TRUE;
	}
	return FALSE;
}

JNIFUNCTION(jboolean)
Java_org_zuky_dialogs_FontDialog_showDialog(JNIPARAMS, 
											jstring name, 
											jint style, 
											jint size, 
											jint flags, 
											jint color, 
											jlong hwndParent)
{
	LOGFONT logf{};

	CHOOSEFONT choose{};
	choose.lStructSize = sizeof(CHOOSEFONT);
	choose.Flags       = flags;
	choose.lpLogFont   = &logf;
	choose.hwndOwner   = (HWND)hwndParent;
	
	static HDC hdc;
	if (hdc == NULL)
	{
		hdc = CreateDC(TEXT("DISPLAY"), NULL, NULL, NULL);
		if (hdc == NULL)
		{
			ShowError(env);
			return JNI_FALSE;
		}
	}

	// Sample text color is set 
	if (flags & CF_EFFECTS)
		choose.rgbColors = RGBATORGB(color);
	if ((flags & CF_INITTOLOGFONTSTRUCT) && name != NULL && 
		!InitializeLogFont(env, logf, hdc, name, style, size))
	{
		return JNI_FALSE; // cause by GetStringChars
	}

	if (ChooseFont(&choose))
	{
		// The java.awt.Font class only supports the BOLD and ITALIC styles
		style = ((logf.lfWeight == FW_BOLD) ? 1 : 0) | 
		        ((logf.lfItalic == 0xff   ) ? 2 : 0); // 0 or 255

		jclass classFont = env->FindClass("java/awt/Font");
		CHECK_NULL(classFont);
		// Font(String name, int style, int size)
		jmethodID method = env->GetMethodID(classFont, "<init>", "(Ljava/lang/String;II)V");
		CHECK_NULL(method);
		
		jstring faceName = env->NewString((const jchar*)logf.lfFaceName, 
								lstrlen(logf.lfFaceName));
		CHECK_NULL(faceName);
		
		float result = (logf.lfHeight * 72) / (GetDeviceCaps(hdc, LOGPIXELSY) * 1.0F);
		size = lroundf(result);
		// The Font class object is created with the name of the font,
		// the style and the size 
		jobject fontObj  = env->NewObject(classFont, method, faceName, style, size);
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
		
		// All delete local references
		env->DeleteLocalRef(clazz);
		env->DeleteLocalRef(classFont);
		env->DeleteLocalRef(fontObj);
		env->DeleteLocalRef(faceName);
		
		return JNI_TRUE;
	}
	
	ShowError(env);
	
	return JNI_FALSE;
}
