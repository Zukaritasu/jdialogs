//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jcolordlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jcolordlg.h"



JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showDialog
(JNIPARAMS, jint flags, jlong hwndParent)
{
	/* los colores personalizados */
	COLORREF customColors[NUM_CUSTOM_COLORS]{};

	jclass clazz      = GET_OBJECT_R0(clazz, env->GetObjectClass(obj));
	jintArray colors  = GET_OBJECT_R0(colors, reinterpret_cast<jintArray>(env->GetObjectField(obj,
		                env->GetFieldID(clazz, "custom", "[I"))));

	/* se verifica que el arreglo de enteros tenga la longitud adecuada 
	   a la señalada por la constante NUM_CUSTOM_COLORS. Si ese no es
	   el caso se lanza una excepcion de tipo RuntimeException */
	if (env->GetArrayLength(colors) != NUM_CUSTOM_COLORS) {
		env->ThrowNew(
			env->FindClass("java/lang/RuntimeException"), 
			"El arreglo de enteros 'custom' tiene un longitud diferente a NUM_CUSTOM_COLORS");
		return JNI_FALSE;
	}

	jint * custColors = env->GetIntArrayElements(colors, nullptr);

	/* se elimina el canal alpha del color */
	for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
		customColors[i] = RGBATORGB(custColors[i]);

	/* se inicializa la estructura CHOOSECOLOR */
	CHOOSECOLOR cChooser{};
	cChooser.lStructSize  = sizeof(CHOOSECOLOR);
	cChooser.Flags        = flags;
	cChooser.hInstance    = (HWND)GetModuleHandle(nullptr);
	cChooser.hwndOwner    = (HWND)hwndParent;
	cChooser.lpCustColors = customColors;

	/* se muestra el cuadro de dialogo y se obtiene el 
	   resultado de la operacion */
	jboolean result = ChooseColor(&cChooser);
	if (result)
	{
		/* se implementa el canal alpha en el color */
		for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
			custColors[i] = RGBTORGBA(customColors[i]);

		env->SetIntField(obj,
			env->GetFieldID(clazz, "color", "I"), RGBTORGBA(cChooser.rgbResult));
		env->SetIntArrayRegion(colors, 0, NUM_CUSTOM_COLORS, custColors);
	}

	/* se liberan los recursor locales */
	env->DeleteLocalRef(clazz);
	env->DeleteLocalRef(colors);

	return result;
}
