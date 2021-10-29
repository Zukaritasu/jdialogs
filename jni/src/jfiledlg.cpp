//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfiledlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jfiledlg.h"


//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#define SET_TEXT(mtd, text) \
		if (text != nullptr && !SUCCEEDED(dialog->mtd(GetStringChars(env, text)))) { \
			dialog->Release(); \
			return JNI_FALSE; \
		} \

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showDialog
(JNIPARAMS, jboolean mode, jstring fileNameLabel, jstring okButtonLabel, jstring root, 
	jstring fileName, jstring title, jstring defaultExt, jint options, jobjectArray filter, 
	jint filterIndex, jlong hwndParent)
{
	IFileDialog* dialog; /* Objeto de la interfaz IFileDialog */
	IShellItem* item;    /* Objeto de la interfaz IShellItem */
	HRESULT hr;          /* Resultado de la funcion */
	LPTSTR text;         /* En esta variable se guarda el texto */

	if (mode) {
		/* Se crea el objeto para abrir un archivo */
		IFileOpenDialog* foDialog;
		hr = CoCreateInstance(CLSID_FileOpenDialog, nullptr, CLSCTX_ALL,
			 IID_IFileOpenDialog, reinterpret_cast<void**>(&foDialog));
		/* Si ocurre un error al crear el objeto se retorna JNI_FALSE */
		if (!SUCCEEDED(hr)) return JNI_FALSE;
		dialog = foDialog;
	} else {
		/* Se crea el objeto para guardar un archivo */
		IFileSaveDialog* fsDialog;
		hr = CoCreateInstance(CLSID_FileSaveDialog, nullptr, CLSCTX_ALL,
			 IID_IFileSaveDialog, reinterpret_cast<void**>(&fsDialog));

		/* Si ocurre un error al crear el objeto se retorna JNI_FALSE */
		if (!SUCCEEDED(hr)) return JNI_FALSE;
		dialog = fsDialog;
	}

	/* Se asigna el texto al boton OK */
	SET_TEXT(SetOkButtonLabel, okButtonLabel);
	/* Se asigna el titulo al cuadro de dialogo */
	SET_TEXT(SetTitle, title);
	/* Se asigna el texto a la etiqueta del nombre de archivo */
	SET_TEXT(SetFileNameLabel, fileNameLabel);
	/* Se asigna la ruta donde el cuadro de dialogo iniciara la busqueda */
	if (root != nullptr) {
		LPITEMIDLIST itemList = ILCreateFromPath(GetStringChars(env, root));
		if (itemList != nullptr) {
			if (SUCCEEDED(SHCreateItemFromIDList(itemList, IID_IShellItem,
				reinterpret_cast<void**>(&item))) && SUCCEEDED(hr = dialog->SetFolder(item))) {
				item->Release();
			}
			ILFree(itemList);

			/* En el caso que el metodo dialog->SetFolder(item) retorna
			un resultado indicando que ocurruio un error, en ese caso es
			liberado el cuadro de dialogo y se retorna JNI_FALSE */
			if (FAILED(hr)) {
				dialog->Release();
				return JNI_FALSE;
			}
		}
	}

	/* Se establecen las opciones */
	dialog->SetOptions(options);

	/* En el caso que no se requiera seleccionar carpetas se establecen
	   las opciones de busqueda */
	if (!(options & FOS_PICKFOLDERS)) {
		/* Se asigna la extension por defecto */
		SET_TEXT(SetDefaultExtension, defaultExt);
		/* Se asigna el nombre de archivo por defecto */
		SET_TEXT(SetFileName, fileName);
		/* Se asigna el filtro de busqueda */
		if (filter != nullptr) {
			jsize length = env->GetArrayLength(filter);
			
			if (length > 0)
			{
				/* La longitud del arreglo debe ser divisible entre 2, si no lo
				es quiere decir que el filtro se encuentra incompleto por ende
				no se puede continuar */
				if ((length % 2) != 0) {
					env->ThrowNew(env->FindClass("java/lang/RuntimeException"), ERROR_FILTER);
					return JNI_FALSE;
				}

				int middle = (length / 2);

				COMDLG_FILTERSPEC * dlgFilter = new COMDLG_FILTERSPEC[middle];
				if (dlgFilter == nullptr) {
					return JNI_FALSE; /* memoria insuficiente */
				}

				for (jsize i = 0, j = 0; i < middle; i++) {
					dlgFilter[i] = {
						GetStringChars(env, (jstring)env->GetObjectArrayElement(filter, j++)),
						GetStringChars(env, (jstring)env->GetObjectArrayElement(filter, j++))
					};
				}
				if (SUCCEEDED(dialog->SetFileTypes(middle, dlgFilter))) {
					dialog->SetFileTypeIndex(filterIndex);
				}
			}
		}
	}
	
	/* Se muestra el cuadro de dialogo */
	if (!SUCCEEDED(dialog->Show((HWND)hwndParent))) {
		dialog->Release();
		return JNI_FALSE;
	}
		
	jclass clazz = GET_OBJECT_R0(clazz, env->GetObjectClass(obj));

	if (mode) {
		if ((options & FOS_ALLOWMULTISELECT) != 0) {
			IShellItemArray* items;
			hr = reinterpret_cast<IFileOpenDialog*>(dialog)->GetResults(&items);
			if (SUCCEEDED(hr)) {
				DWORD count = 0;
				if (SUCCEEDED(items->GetCount(&count))) {
					jclass arrayClass     = GET_OBJECT_R0(arrayClass, env->FindClass("java/lang/String"));
					jobjectArray objArray = GET_OBJECT_R0(objArray,
						                    env->NewObjectArray((jsize)count, arrayClass, nullptr));
					                        env->DeleteLocalRef(arrayClass);
					for (int i = 0, j = 0; i < (int)count; i++) {
						if (SUCCEEDED(items->GetItemAt(i, &item)) && 
							SUCCEEDED(item->GetDisplayName(SIGDN_FILESYSPATH, &text))) {
							jstring element = GET_OBJECT_R0(element, NEW_STRING(text));
							env->SetObjectArrayElement(objArray, j++, element);
							env->DeleteLocalRef(element);
							item->Release();
						}
					}
					/* selectedFiles */
					env->SetObjectField(obj, 
						env->GetFieldID(clazz, "selectedFiles", "[Ljava/lang/String;"), objArray);
					env->DeleteLocalRef(objArray);
				}
				items->Release();
			}
		}
	}

	if ((!mode || !(options & FOS_ALLOWMULTISELECT)) && SUCCEEDED(dialog->GetResult(&item))) {
		if (SUCCEEDED(item->GetDisplayName(SIGDN_FILESYSPATH, &text))) {
			jstring absolutePath = GET_OBJECT_R0(absolutePath, NEW_STRING(text));
			/* absolutePath */ 
			env->SetObjectField(obj,
				env->GetFieldID(clazz, "absolutePath", "Ljava/lang/String;"), absolutePath);
			env->DeleteLocalRef(absolutePath);
			item->Release();
		}
	}

	UINT uFilterIndex;
	if (SUCCEEDED(dialog->GetFileTypeIndex(&uFilterIndex))) {
		/* filterIndex */
		env->SetIntField(obj, 
			env->GetFieldID(clazz, "filterIndex", "I"), (jint)uFilterIndex);
	}

	env->DeleteLocalRef(clazz);
	dialog->Release();

	return JNI_TRUE;
}
