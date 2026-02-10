// Copyright (C) 2021-2026 Zukaritasu
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
#include <memory>
#ifdef _DEBUG
#include <cstdio>
#endif // _DEBUG

static BOOL InitializeLogFont(JNIEnv *env, LOGFONT &logf, HDC hdc,
							  jstring name, jint style, jint size)
{
	const jchar *font_name = env->GetStringChars(name, nullptr);
	if (!font_name)
		return FALSE;

	// Copy font name, max 32 characters including the null character
	StringCbCopy(logf.lfFaceName, sizeof(logf.lfFaceName),
				 (const wchar_t *)font_name);
	env->ReleaseStringChars(name, font_name);

	logf.lfHeight = -MulDiv(size, GetDeviceCaps(hdc, LOGPIXELSY), 72);

	logf.lfWeight = (style & 1) ? FW_BOLD : FW_NORMAL;
	logf.lfItalic = (style & 2) ? 0xff : 0x0;

	// Set default values

	logf.lfCharSet = DEFAULT_CHARSET;
	logf.lfOutPrecision = OUT_TT_PRECIS;
	logf.lfQuality = PROOF_QUALITY;
	logf.lfPitchAndFamily = FF_ROMAN;

	return TRUE;
}

JNIEXPORT jboolean JNICALL
Java_com_zukadev_dialogs_FontDialog_showDialog(
	JNIEnv *env,
	jobject obj,
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
	choose.Flags = flags;
	choose.lpLogFont = &logf;
	choose.hwndOwner = reinterpret_cast<HWND>(hwndParent);

	HDC hdc = CreateDC(TEXT("DISPLAY"), nullptr, nullptr, nullptr);
	if (!hdc)
	{
		ShowError(env);
		return JNI_FALSE;
	}

	const auto func = [](HDC h)
	{ DeleteDC(h); };

	std::unique_ptr<HDC__, decltype(func)> hdcPtr(hdc, func);

	// Sample text color is set
	if (flags & CF_EFFECTS)
		choose.rgbColors = RGBATORGB(color);
	if ((flags & CF_INITTOLOGFONTSTRUCT) && name &&
		!InitializeLogFont(env, logf, hdc, name, style, size))
	{
		return JNI_FALSE; // cause by GetStringChars
	}

	if (ChooseFont(&choose))
	{
		// The java.awt.Font class only supports the BOLD and ITALIC styles
		style = ((logf.lfWeight == FW_BOLD) ? 1 : 0) |
				((logf.lfItalic == 0xff) ? 2 : 0); // 0 or 255

		jclass classFont = env->FindClass("java/awt/Font");
		jmethodID method = env->GetMethodID(classFont, "<init>", "(Ljava/lang/String;II)V");
		jstring faceName = env->NewString((const jchar *)logf.lfFaceName,
										  lstrlen(logf.lfFaceName));

		if (faceName)
		{
			float result = (logf.lfHeight * 72) / (GetDeviceCaps(hdc, LOGPIXELSY) * 1.0F);
			size = lroundf(result);
			// The Font class object is created with the name of the font,
			// the style and the size
			jobject fontObj = env->NewObject(classFont, method, faceName, style, size);
			if (fontObj)
			{
				jclass clazz = env->GetObjectClass(obj);
				jfieldID ffont = env->GetFieldID(clazz, "font", "Ljava/awt/Font;");

				env->SetObjectField(obj, ffont, fontObj);

				// If effects are enabled, the selected color is save
				if ((flags & CF_EFFECTS))
				{
					jfieldID fcolor = env->GetFieldID(clazz, "color", "I");
					env->SetIntField(obj, fcolor, RGBTORGBA(choose.rgbColors));
				}

				return JNI_TRUE;
			}
		}
	}

	ShowError(env);

	return JNI_FALSE;
}
