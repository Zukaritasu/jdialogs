//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Nombre de archivo jfiledlg.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jfiledlg.h"


//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#define SET_TEXT(mtd, text) \
		if (text != nullptr && FAILED(hr = dialog->mtd(GetStringChars(env, text)))) { \
			goto _FinishDlg_; \
		}

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

HRESULT ShellItemArrayToStringArray(JNIEnv* env, jobjectArray& array, IShellItemArray* items)
{
	HRESULT hr  = S_OK; /* Resultado de la funcion */
	DWORD count = 0;    /* Cantidad de elementos del array IShellItemArray */
	IShellItem* item;   /* Aqui se guarda un elemento del array IShellItemArray */
	LPTSTR path;        /* Aqui se guarda la ruta absoluta del archivo o carpeta seleccionada */
	jclass clazz;       /* La clase que representa el tipo de array que se creara */
	jstring element;    /* Un nuevo elemento que se agregara en el array */

	if (SUCCEEDED(hr = items->GetCount(&count)) && count > 0) {
		if ((clazz = env->FindClass("java/lang/String")) == nullptr || 
			(array = env->NewObjectArray((jsize)count, clazz, nullptr)) == nullptr) {
			hr = E_OUTOFMEMORY; /* memoria insuficiente */
			goto _finish_;
		}

		for (int i = 0, j = 0; i < (int)count; i++) {
			if (FAILED(hr = items->GetItemAt(i, &item)))
				goto _finish_;
			if (FAILED(item->GetDisplayName(SIGDN_FILESYSPATH, &path))) {
				item->Release();
				goto _finish_;
			}

			/* Se crea un nuevo String usando 'path'. En el caso que el nuevo
			elemento sea null se le asigna E_OUTOFMEMORY a la variable local
			'hr' y se sale del ciclo */
			if ((element = NEW_STRING(path)) != nullptr) {
				env->SetObjectArrayElement(array, j++, element);
				env->DeleteLocalRef(element);
			}

			CoTaskMemFree(path);
			item->Release();
			if (element == nullptr) {
				hr = E_OUTOFMEMORY;
				goto _finish_;
			}
		}

		/* Al llegar a este punto se puede decir que el la funcion termino con
		exito o con algun error especificado por 'hr'. Si ocurrio algun error
		se liberan los recursos */
	_finish_:
		if (clazz != nullptr)
			env->DeleteLocalRef(clazz);
		if (FAILED(hr) && array != nullptr)
			env->DeleteLocalRef(array);
	}

	return hr;
}

HRESULT SetFilter(IFileDialog* dialog, JNIEnv* env, jobjectArray filter, jint index)
{
	HRESULT hr = S_OK; /* Resultado de la funcion */
	int half;          /* La mitad de la longitud del array */
	if (filter != nullptr && (half = env->GetArrayLength(filter)) > 0 && (half % 2) == 0) {
		COMDLG_FILTERSPEC* filters = new COMDLG_FILTERSPEC[half /= 2];
		if (filters == nullptr) {
			return E_OUTOFMEMORY; /* memoria insuficiente */
		}

		/* Se establece la descripcion del filtro y las extensiones en
		cada elemento de 'filters'. No se toma en cuenta si GetObjectArrayElement
		retorna un objeto que es null */
		for (jsize i = 0, j = 0; i < half; i++) {
			filters[i].pszName = GetStringChars(env, 
				(jstring)env->GetObjectArrayElement(filter, j++));
			filters[i].pszSpec = GetStringChars(env,
				(jstring)env->GetObjectArrayElement(filter, j++));
		}

		/* Se asignan los filtros y el indice del filtro que se usara en
		el cuadro de dialogo. Si el metodo SetFileTypes o SetFileTypeIndex
		retornan un resultado indicando un error, ese sera el resultado
		que retorna esta funcion */
		if (SUCCEEDED(hr = dialog->SetFileTypes(half, filters))) {
			hr = dialog->SetFileTypeIndex(index);
		}

		/* Se elimina el filtro */
		delete[] filters;
	}
	return hr;
}

JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showDialog
	(JNIPARAMS, jboolean mode, jstring fileNameLabel, jstring okButtonLabel, jstring root, 
		jstring fileName, jstring title, jstring defaultExt, jint options, jobjectArray filter, 
		jint filterIndex, jlong hwndParent)
{
	IFileDialog* dialog;    /* Objeto de la interfaz IFileDialog */
	IShellItem* item;       /* Objeto de la interfaz IShellItem */
	HRESULT hr;             /* Resultado de la funcion */
	LPTSTR text = nullptr;  /* En esta variable se guarda la ruta absoluta del archivo o carpeta seleccionada */
	jclass clazz = nullptr; /* La clase del objeto de org.zuky.dialogs.FileDialog */
	
	if (mode) {
		/* Se crea el objeto para abrir un archivo */
		hr = CoCreateInstance(CLSID_FileOpenDialog, nullptr, CLSCTX_ALL,
				IID_IFileOpenDialog, reinterpret_cast<void**>(&dialog));
	} else {
		/* Se crea el objeto para guardar un archivo */
		hr = CoCreateInstance(CLSID_FileSaveDialog, nullptr, CLSCTX_ALL,
				IID_IFileSaveDialog, reinterpret_cast<void**>(&dialog));
	}

	if (FAILED(hr)) {
		return JNI_FALSE;
	}

	SET_TEXT(SetOkButtonLabel, okButtonLabel);
	SET_TEXT(SetTitle, title);
	SET_TEXT(SetFileNameLabel, fileNameLabel);

	/* Se asigna la ruta donde el cuadro de dialogo iniciara la busqueda */
	if (root != nullptr) {
		LPITEMIDLIST itemList = ILCreateFromPath(GetStringChars(env, root));
		if (itemList != nullptr) {
			if (SUCCEEDED(SHCreateItemFromIDList(itemList, IID_IShellItem,
					reinterpret_cast<void**>(&item)))) {
				hr = dialog->SetFolder(item);
				item->Release();
			}
			ILFree(itemList);

			/* En el caso que el metodo dialog->SetFolder(item) retorna
			un resultado indicando que ocurruio un error, en ese caso es
			liberado el cuadro de dialogo y se retorna JNI_FALSE */
			if (FAILED(hr)) {
				goto _FinishDlg_;
			}
		}
	}

	if (FAILED(hr = dialog->SetOptions(options))) {
		goto _FinishDlg_;
	}

	/* En el caso que no se requiera seleccionar carpetas se establecen
	las opciones de busqueda */
	if (!(options & FOS_PICKFOLDERS)) {
		SET_TEXT(SetDefaultExtension, defaultExt);
		SET_TEXT(SetFileName, fileName);

		if (FAILED(SetFilter(dialog, env, filter, filterIndex))) {
			goto _FinishDlg_;
		}
	}
	
	/* Se muestra el cuadro de dialogo */
	if (FAILED(dialog->Show((HWND)hwndParent))) {
		goto _FinishDlg_;
	}
	
	if ((clazz = env->GetObjectClass(obj)) == nullptr) {
		hr = E_OUTOFMEMORY;
		goto _FinishDlg_;
	}

	if (mode && (options & FOS_ALLOWMULTISELECT) != 0) {
		IShellItemArray* items = nullptr;
		jobjectArray array     = nullptr;
		if (FAILED(hr = reinterpret_cast<IFileOpenDialog*>(dialog)->GetResults(&items))) {
			goto _FinishDlg_;
		}

		if (SUCCEEDED(hr = ShellItemArrayToStringArray(env, array, items))) {
			env->SetObjectField(obj, 
				env->GetFieldID(clazz, "selectedFiles", "[Ljava/lang/String;"), array);
			env->DeleteLocalRef(array);
		}

		items->Release();

		if (FAILED(hr)) {
			goto _FinishDlg_;
		}
	}

	if ((!mode || !(options & FOS_ALLOWMULTISELECT)) && SUCCEEDED(hr = dialog->GetResult(&item))) {
		jstring absolutePath = nullptr;
		if (SUCCEEDED(hr = item->GetDisplayName(SIGDN_FILESYSPATH, &text)) && 
			(absolutePath = NEW_STRING(text)) != nullptr) {
			env->SetObjectField(obj,
				env->GetFieldID(clazz, "absolutePath", "Ljava/lang/String;"), absolutePath);
			env->DeleteLocalRef(absolutePath);
		}

		item->Release();

		if (FAILED(hr)) {
			goto _FinishDlg_;
		}

		CoTaskMemFree(text);

		if (absolutePath == nullptr) {
			hr = E_OUTOFMEMORY;
			goto _FinishDlg_;
		}
	}

	UINT uFilterIndex;
	if (SUCCEEDED(hr = dialog->GetFileTypeIndex(&uFilterIndex))) {
		env->SetIntField(obj, 
			env->GetFieldID(clazz, "filterIndex", "I"), (jint)uFilterIndex);
	}

_FinishDlg_:
	if (clazz != nullptr)
		env->DeleteLocalRef(clazz);
	dialog->Release();

	return SUCCEEDED(hr);
}
