//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfolderbrowserdlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jfolderbrowserdlg.h"

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FolderBrowserDialog_showDialog(
	JNIPARAMS, jstring root, jstring description, jint flags, jlong hwndParent)
{
	/* En este arreglo se guarda el nombre de la carpeta 
	   cuando es seleccionanda */
	TCHAR pDisplayName[MAX_PATH]{};

	/* Se inicializa la estrutura BROWSEINFO y ademas se
	   asigna la carpeta donde iniciara la navegacion y
	   el titulo del cuadro de dialogo */
	BROWSEINFO browse{};
	browse.lpszTitle      = GetStringChars(env, description);
	browse.pszDisplayName = pDisplayName;
	browse.ulFlags        = flags;
	browse.pidlRoot       = ILCreateFromPath(GetStringChars(env, root));
	browse.hwndOwner      = (HWND)hwndParent;

	/* Si la funcion SHBrowseForFolder retorna un puntero nullptr,
	   esto quiere decir que el usuario cancelo la operacion o
	   que ocurrio un error. En el caso que el resultado sea distinto de
	   nullptr se obtiene la ruta absoluta de la carpeta y se continua */
	LPITEMIDLIST itemIdList = SHBrowseForFolder(&browse);
	if (itemIdList == nullptr) {
		return JNI_FALSE;
	}

	/* La ruta absoluta de la carpeta */
	TCHAR pAbsolutePath[MAX_PATH]{};
	SHGetPathFromIDList(itemIdList, pAbsolutePath);

	jstring displayName  = GET_OBJECT_R0(displayName, NEW_STRING(pDisplayName));
	jstring absolutePath = GET_OBJECT_R0(absolutePath, NEW_STRING(pAbsolutePath));
	jclass clazz         = GET_OBJECT_R0(clazz, env->GetObjectClass(obj));

	/* Es establecido en la variable global 'displayName' el
	   nombre de la carpeta que se muestra en la caja de texto
	   y en la variable global 'absolutePath' la ruta absoluta
	   de la carpeta. En el caso que tambien se requiera seleccionar
	   archivo tambien se sigue el mismo procedimiento */
	env->SetObjectField(obj, 
		env->GetFieldID(clazz, "displayName", STRING_PATH), displayName);
	env->SetObjectField(obj,
		env->GetFieldID(clazz, "absolutePath", STRING_PATH), absolutePath);

	/* Se liberan los recursor */
	ILFree(itemIdList);
	env->DeleteLocalRef(clazz);
	env->DeleteLocalRef(displayName);
	env->DeleteLocalRef(absolutePath);
	return JNI_TRUE;
}
