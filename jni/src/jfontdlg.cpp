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

#include "jfontdlg.h"


JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showDialog
	(JNIPARAMS, jstring name, jint style, jint size, jint flags, jint color, jlong hwndParent)
{
	LOGFONT _font_{};

	CHOOSEFONT choose{};
	choose.lStructSize = sizeof(CHOOSEFONT);
	choose.Flags       = flags;
	choose.hInstance   = GetModuleHandle(nullptr);
	choose.lpLogFont   = &_font_;
	choose.hwndOwner   = (HWND)hwndParent;

	if ((flags & CF_EFFECTS) != 0)
		choose.rgbColors = RGBATORGB(color);
	if ((flags & CF_INITTOLOGFONTSTRUCT) != 0 && name != nullptr)
	{
		const jchar* name_f = env->GetStringChars(name, nullptr);
		lstrcpy(_font_.lfFaceName, (const wchar_t*)name_f);
		env->ReleaseStringChars(name, name_f);

		_font_.lfHeight         = size * (-1);
		_font_.lfWeight         = ((style & 1) != 0) ? FW_BOLD : FW_NORMAL;
		_font_.lfItalic         = ((style & 2) != 0) ? 0xff : 0x0;
		_font_.lfCharSet        = DEFAULT_CHARSET;
		_font_.lfOutPrecision   = OUT_TT_PRECIS;
		_font_.lfQuality        = PROOF_QUALITY;
		_font_.lfPitchAndFamily = FF_ROMAN;
	}

	if (ChooseFont(&choose))
	{
		style = ((_font_.lfWeight == FW_BOLD) ? 1 : 0) | 
		        ((_font_.lfItalic == 0xff)    ? 2 : 0);

		jclass class_f   = 
			env->FindClass("java/awt/Font");
		jmethodID method = 
			env->GetMethodID(class_f, "<init>", "(Ljava/lang/String;II)V");
		jstring faceName = 
			env->NewString((jpcchar)_font_.lfFaceName, lstrlen(_font_.lfFaceName));
		jobject font     = 
			env->NewObject(class_f, method, faceName, style, _font_.lfHeight * (-1));

		jclass clazz = env->GetObjectClass(obj);
		env->SetObjectField(obj, env->GetFieldID(clazz, "font", "Ljava/awt/Font;"), font);
		if ((flags & CF_EFFECTS) != 0) 
		{
			env->SetIntField(obj,
				env->GetFieldID(clazz, "color", "I"), RGBTORGBA(choose.rgbColors));
		}

		env->DeleteLocalRef(clazz);
		env->DeleteLocalRef(class_f);
		env->DeleteLocalRef(font);
		env->DeleteLocalRef(faceName);

		return JNI_TRUE;
	}
	ShowError(env);
	return JNI_FALSE;
}
