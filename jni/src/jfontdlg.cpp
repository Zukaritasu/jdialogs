//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfontdlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jfontdlg.h"

/*
** 
**
*/
void InitializeFont(JNIEnv* env, LOGFONT& font, jstring name, jint style, jint size)
{
	if (name != nullptr) {
		/* Se inicializa la estructura LOGFONT con los valores pasados
		   por parametro y algunos valores predeterminados */
		lstrcpy(font.lfFaceName, GetStringChars(env, name));
		font.lfHeight         = size * (-1);
		font.lfWeight         = ((style & 1) != 0) ? FW_BOLD : FW_NORMAL;
		font.lfItalic         = ((style & 2) != 0) ? 0xff : 0x0;
		font.lfCharSet        = DEFAULT_CHARSET;
		font.lfOutPrecision   = OUT_TT_PRECIS;
		font.lfQuality        = PROOF_QUALITY;
		font.lfPitchAndFamily = FF_ROMAN;
	}
}

/*
** En este bloque de codigo solo se implementa las funciones basicas
** permitidas por el cuadro de dialogo para tener compatibilidad con
** la clase java.awt.Font.
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showDialog
	(JNIPARAMS, jstring name, jint style, jint size, jint flags, jint color, jlong hwndParent)
{
	/* Fuente de inicializacion. La bandera CF_INITTOLOGFONTSTRUCT debe estar
	presente, de lo contrario el cuadro de dialogo se mostrara sin ninguna
	fuente seleccionada */
	LOGFONT logFont{};

	/* Se inicializa la estructura CHOOSEFONT */
	CHOOSEFONT choose{};
	choose.lStructSize = sizeof(choose);
	choose.Flags       = flags;
	choose.hInstance   = GetModuleHandle(nullptr);
	choose.lpLogFont   = &logFont;
	choose.hwndOwner   = (HWND)hwndParent;

	/* Se asigna el color del texto de muestra, para ello debe
	estar presente la bandera CF_EFFECTS. La macro RGBATORGB
	remueve el canal alpha para solo obtener el color RGB (24 bits)
	sin transparencia */
	if ((flags & CF_EFFECTS) != 0)
		choose.rgbColors = RGBATORGB(color);
	/* Se inicializa la estructura LOGFONT */
	if ((flags & CF_INITTOLOGFONTSTRUCT) != 0)
		InitializeFont(env, logFont, name, style, size);

	if (ChooseFont(&choose))
	{
		/* Se establece el estilo de la fuente. La clase Font solo
		tiene 2 tipos de grosor de fuente los cuales son NORMAL
		y BOLD que son los grosores mas comunes */
		style = ((logFont.lfWeight == FW_BOLD) ? 1 : 0) | 
		        ((logFont.lfItalic == 0xff)    ? 2 : 0);

		jclass clazzFont = GET_OBJECT_R0(clazzFont, env->FindClass("java/awt/Font"));
		jmethodID method = env->GetMethodID(clazzFont, "<init>", "(Ljava/lang/String;II)V");
		jstring faceName = GET_OBJECT_R0(faceName, NEW_STRING(logFont.lfFaceName));

		/* Se crea el objeto de la clase Font con los valores que
		selecciono el usuario en el cuadro de dialogo.
		Se llama el constructor Font(String, int, int) para dicho proposito */
		jobject font     = GET_OBJECT_R0(font, env->NewObject(clazzFont, method, faceName,
			               style, logFont.lfHeight * (-1)));

		jclass clazz     = GET_OBJECT_R0(clazz, env->GetObjectClass(obj));

		env->SetObjectField(obj, env->GetFieldID(clazz, "font", "Ljava/awt/Font;"), font);
		if ((flags & CF_EFFECTS) != 0)
		{
			/* Si los efectos estaban habilitados se le asigna el
			color del texto de muestra a la variable global 'color' de
			tipo int. No se toma en cuenta si el usuario selecciono un
			color distinto al origina */
			env->SetIntField(obj,
				env->GetFieldID(clazz, "color", "I"), RGBTORGBA(choose.rgbColors));
		}

		/* Se liberan los recursor */
		env->DeleteLocalRef(clazz);
		env->DeleteLocalRef(clazzFont);
		env->DeleteLocalRef(font);
		env->DeleteLocalRef(faceName);

		return JNI_TRUE;
	}
	return JNI_FALSE;
}
