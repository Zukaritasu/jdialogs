//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jcolordlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jcolordlg.h"



JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showDialog
	(JNIPARAMS, jint flags, jlong hwndParent)
{
	/* Los colores personalizados */
	COLORREF customColors[NUM_CUSTOM_COLORS]{};

	jclass clazz     = GET_OBJECT_R0(clazz, env->GetObjectClass(obj));
	jintArray colors = GET_OBJECT_R0(colors, reinterpret_cast<jintArray>(env->GetObjectField(obj,
		               env->GetFieldID(clazz, "custom", "[I"))));

	/* Se verifica que el arreglo de enteros tenga la longitud
	   adecuada a la senalada por la constante NUM_CUSTOM_COLORS.
	   Si ese no es el caso se lanza una excepcion de tipo RuntimeException */
	if (env->GetArrayLength(colors) != NUM_CUSTOM_COLORS) {
		env->ThrowNew(
			env->FindClass("java/lang/RuntimeException"), 
			"El arreglo de enteros 'custom' tiene una longitud diferente a NUM_CUSTOM_COLORS");
		return JNI_FALSE;
	}

	jint* custColors = env->GetIntArrayElements(colors, nullptr);

	/* Se elimina el canal alpha del color */
	for (int i = 0; i < NUM_CUSTOM_COLORS; i++) {
		customColors[i] = RGBATORGB(custColors[i]);
	}

	/* Se inicializa la estructura CHOOSECOLOR */
	CHOOSECOLOR cChooser{};
	cChooser.lStructSize  = sizeof(CHOOSECOLOR);
	cChooser.Flags        = flags;
	cChooser.hInstance    = (HWND)GetModuleHandle(nullptr);
	cChooser.hwndOwner    = (HWND)hwndParent;
	cChooser.lpCustColors = customColors;

	/* Se muestra el cuadro de dialogo y se obtiene el 
	   resultado de la operacion. La funcion puede retornar
	   false indicando que el usuario cancelo la operacion 
	   o que ocurrio un error inesperado, en todo caso el 
	   error es ignorado */
	jboolean result = (jboolean)ChooseColor(&cChooser);
	if (result)
	{
		/* Se implementa el canal alpha en el color */
		for (int i = 0; i < NUM_CUSTOM_COLORS; i++) {
			custColors[i] = RGBTORGBA(customColors[i]);
		}

		env->SetIntField(obj,
			env->GetFieldID(clazz, "color", "I"), RGBTORGBA(cChooser.rgbResult));
		env->SetIntArrayRegion(colors, 0, NUM_CUSTOM_COLORS, custColors);
	}

	/* Se liberan los recursor utilizados */
	env->DeleteLocalRef(clazz);
	env->DeleteLocalRef(colors);

	return result;
}
