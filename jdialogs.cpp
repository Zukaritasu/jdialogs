//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo jdialogs.cpp
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#include "jdialogs.h"

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE PREPROCESADORES
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

#define RGBATORGB(rgba) \
        RGB(((rgba >> 16) & 0xff), ((rgba >> 8) & 0xff), (rgba & 0xff))

#define RGBTORGBA(rgb) \
        (jint)((255 << 24) | ((GetRValue(rgb) & 0xff) << 16) | \
                             ((GetGValue(rgb) & 0xff) <<  8) | \
                             ((GetBValue(rgb) & 0xff) <<  0))

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE VARIABLES
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

HINSTANCE hInstance;

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE ESTRUCTURAS
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

typedef struct tagLPARAMDATA
{
	JNIEnv * env;
	jobject dialog;
	jmethodID proc;
}LPARAMDATA, *LPLPARAMDATA;

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// FUNCIONES UTILITATIAS
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

/*
**
** [ En desuso ]
*/
void SetByteDataElements(JNIEnv * env, jbyteArray bArray, HGLOBAL data, int sizeOf)
{
	LPVOID lockData = GlobalLock(data);
	env->SetByteArrayRegion(bArray, 0, sizeOf, (const jbyte*)lockData);
	GlobalUnlock(data);
}

/*
**
**
*/
void GetHookProcData(JNIEnv * env, jclass clazz, jobject dialog, LPLPARAMDATA data)
{
	data->dialog = dialog;
	data->env    = env;
	data->proc   = env->GetMethodID(clazz, "hookProc", "(JIJJ)J");
}

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// DECLARACION DE FUNCIONES PARA LOS CUADROS DE DIALOGO
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

jlong CallMethodHookProc(HWND hwnd, UINT msg, LPARAM lparam, LPARAM data);

/*
**
**
*/
UINT_PTR CALLBACK ColorHookProc(HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam) {
	if (msg == WM_INITDIALOG)
		SetProp(hwnd, CUSTOMDATAPROC, (HANDLE)((LPCHOOSECOLOR)lparam)->lCustData);
	return (UINT_PTR)CallMethodHookProc(hwnd, msg, wparam, lparam);
}

/*
**
**
*/
UINT_PTR CALLBACK OpenOrSaveFileHookProc(HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam) {
	if (msg == WM_INITDIALOG)
		SetProp(hwnd, CUSTOMDATAPROC, (HANDLE)((LPOPENFILENAME)lparam)->lCustData);
	return (UINT_PTR)CallMethodHookProc(hwnd, msg, wparam, lparam);
}

/*
**
**
*/
UINT_PTR CALLBACK FontHookProc(HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam) {
	if (msg == WM_INITDIALOG)
		SetProp(hwnd, CUSTOMDATAPROC, (HANDLE)((LPCHOOSEFONT)lparam)->lCustData);
	return (UINT_PTR)CallMethodHookProc(hwnd, msg, wparam, lparam);
}

/*
**
**
*/
UINT_PTR CALLBACK PrintOrSetupHookProc(HWND hwnd, UINT msg, WPARAM wparam, LPARAM lparam) {
	if (msg == WM_INITDIALOG)
		SetProp(hwnd, CUSTOMDATAPROC, (HANDLE)((LPPRINTDLG)lparam)->lCustData);
	return (UINT_PTR)CallMethodHookProc(hwnd, msg, wparam, lparam);
}

/*
**
**
*/
int CALLBACK BrowserHookProc(HWND hwnd, UINT msg, LPARAM lparam, LPARAM data) {
	if (msg == BFFM_INITIALIZED)
		SetProp(hwnd, CUSTOMDATAPROC, (HANDLE)data);
	return (int)CallMethodHookProc(hwnd, msg, lparam, data);
}

//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_ColorDialog_showColorDialog
	(JNIPARAMS, jobject dialog, jint flags, jlong hwndParent)
{
	COLORREF customColors[NUM_CUSTOM_COLORS]{};
	
	jclass clazz      = env->GetObjectClass(dialog);
	jfieldID fcolor   = env->GetFieldID(clazz, "color", "I");
	jfieldID fcustom  = env->GetFieldID(clazz, "custom", "[I");
	jintArray colors  = (jintArray)env->GetObjectField(dialog, fcustom);
	jint * custColors = env->GetIntArrayElements(colors, NULL);

	for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
		customColors[i] = RGBATORGB(custColors[i]);
	
	CHOOSECOLOR cChooser{};
	cChooser.lStructSize  = sizeof(cChooser);
	cChooser.Flags        = flags;
	cChooser.hInstance    = (HWND)hInstance;
	cChooser.hwndOwner    = (HWND)hwndParent;
	cChooser.lpCustColors = customColors;
	
	if ((flags & CC_ENABLEHOOK) != 0)
	{
		LPARAMDATA data{};
		GetHookProcData(env, clazz, dialog, &data);

		cChooser.lpfnHook  = ColorHookProc;
		cChooser.lCustData = (LPARAM)&data;
	}

	jboolean result = ChooseColor(&cChooser);

	if (result)
	{
		for (int i = 0; i < NUM_CUSTOM_COLORS; i++)
			custColors[i] = RGBTORGBA(customColors[i]);
		env->SetIntField(dialog, fcolor, (jint)cChooser.rgbResult);
		env->SetIntArrayRegion(colors, 0, NUM_CUSTOM_COLORS, custColors);
	}

	env->DeleteLocalRef(clazz);
	env->DeleteLocalRef(colors);

	return result;
}

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_DirectoryDialog_showDirectoryDialog
	(JNIPARAMS, jobject dialog, jstring root, jstring title, jint flags, 
		jlong hwndParent)
{
	TCHAR displayName[MAX_PATH]{};
	jclass clazz = env->GetObjectClass(dialog);

	LPARAMDATA data{};
	GetHookProcData(env, clazz, dialog, &data);

	BROWSEINFO browse{};
	browse.lpszTitle      = GetStringChars(env, title);
	browse.pszDisplayName = displayName;
	browse.ulFlags        = flags;
	browse.pidlRoot       = ILCreateFromPath(GetStringChars(env, root));
	browse.hwndOwner      = (HWND)hwndParent;
	browse.lpfn           = BrowserHookProc;
	browse.lParam         = (LPARAM)&data;
	
	LPITEMIDLIST itemIdList = SHBrowseForFolder(&browse);
	if (itemIdList)
	{
		jfieldID fimageIndex   = env->GetFieldID(clazz, "imageIndex", "I");
		jfieldID fabsolutePath = env->GetFieldID(clazz, "absolutePath", STRING_PATH);
		jfieldID fdisplayName  = env->GetFieldID(clazz, "displayName", STRING_PATH);

		TCHAR absolutePath[MAX_PATH]{};
		SHGetPathFromIDList(itemIdList, absolutePath);

		env->SetIntField(dialog, fimageIndex, browse.iImage);
#ifdef UNICODE
		env->SetObjectField(dialog, fdisplayName,
			env->NewString((const jchar*)displayName, lstrlen(displayName)));
		env->SetObjectField(dialog, fabsolutePath,
			env->NewString((const jchar*)absolutePath, lstrlen(absolutePath)));
#else
		env->SetObjectField(dialog, fdisplayName,
			env->NewStringUTF(displayName, lstrlen(displayName)));
		env->SetObjectField(dialog, fabsolutePath,
			env->NewStringUTF(absolutePath, lstrlen(absolutePath)));
#endif
	}
	env->DeleteLocalRef(clazz);

	return itemIdList != NULL;
	return FALSE;
}

/*
**
**
*/
void InitializeFont(JNIEnv * env, LPLOGFONT font, jstring name, jint style, jint size)
{
	if (name != NULL)
	{
		lstrcpy(font->lfFaceName, GetStringChars(env, name));
		
		font->lfHeight         = size * (-1);
		font->lfWeight         = ((style & 1) != 0) ? FW_BOLD : FW_NORMAL;
		font->lfItalic         = ((style & 2) != 0) ? 0xff : 0x0;
		font->lfCharSet        = DEFAULT_CHARSET;
		font->lfOutPrecision   = OUT_TT_PRECIS;
		font->lfQuality        = PROOF_QUALITY;
		font->lfPitchAndFamily = FF_ROMAN;
	}
}

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FontDialog_showFontDialog
	(JNIPARAMS, jobject dialog, jstring name, jint style, jint size, jint flags, 
		jint color, jlong hwndParent)
{
	jclass clazz = env->GetObjectClass(dialog);
	LOGFONT logFont{};
	CHOOSEFONT choose{};
	choose.lStructSize = sizeof(choose);
	choose.Flags       = flags;
	choose.hInstance   = hInstance;
	choose.lpLogFont   = &logFont;
	choose.hwndOwner   = (HWND)hwndParent;

	if ((flags & CF_EFFECTS) != 0)
		choose.rgbColors = RGBATORGB(color);
	if ((flags & CF_INITTOLOGFONTSTRUCT) != 0)
		InitializeFont(env, &logFont, name, style, size);
	if ((flags & CF_ENABLEHOOK) != 0)
	{
		LPARAMDATA data{};
		GetHookProcData(env, clazz, dialog, &data);

		choose.lpfnHook  = FontHookProc;
		choose.lCustData = (LPARAM)&data;
	}
	
	jboolean result = ChooseFont(&choose);
	if (result)
	{
		style = size = 0;

		style |= (logFont.lfWeight == FW_BOLD) ? 1 : 0;
		style |= (logFont.lfItalic ==    0xff) ? 2 : 0;
		size = logFont.lfHeight * (-1);

		jclass clazzFont = env->FindClass("java/awt/Font");
		jmethodID method = env->GetMethodID(clazzFont, "<init>", "(Ljava/lang/String;II)V");
		jfieldID fFont   = env->GetFieldID(clazz, "font", "Ljava/awt/Font;");
		jfieldID fType   = env->GetFieldID(clazz, "fontType", "I");
		jobject font     = env->NewObject(clazzFont, method,
#ifdef UNICODE
		env->NewString((const jchar*)logFont.lfFaceName, lstrlen(logFont.lfFaceName))
#else
		env->NewStringUTF(logFont.lfFaceName, lstrlen(logFont.lfFaceName))
#endif // UNICODE
			, style, size);
		if ((flags & CF_EFFECTS) != 0)
		{
			env->SetIntField(dialog, 
				env->GetFieldID(clazz, "color", "I"), RGBTORGBA(choose.rgbColors));
		}

		env->SetIntField(dialog, fType, choose.nFontType);
		env->SetObjectField(dialog, fFont, font);
		env->DeleteLocalRef(clazzFont);
		env->DeleteLocalRef(font);
	}
	env->DeleteLocalRef(clazz);

	return result;
}

/*
**
**
*/
int LengthFile(LPCTSTR file, int length)
{
	int count = 0;
	TCHAR token = 0;
	for (int i = 0; i < length; i++)
	{
		if (i == 0 && file[0] == 0)
			break;
		if (token == 0 && file[i] == 0)
		{
			--count;
			break;
		}
		token = file[i];
		++count;
	}
	return count;
}

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_FileDialog_showFileDialog
	(JNIPARAMS, jobject dialog, jboolean mode, jstring filter, jstring customFilter, 
		jint maxCusFilter, jint filterIndex, jstring strFileName, jstring initialDir, 
		jstring title, jint flags, jint fileOffSet, jint fileExtension, jstring defExtension, 
		jint flagsEx, jint maxFile, jlong hwndParent)
{
	jclass clazz = env->GetObjectClass(dialog);
	TCHAR fileName[MAX_PATH]{};
	TCHAR * file = new TCHAR[maxFile]{};

	OPENFILENAME ofn{};
	ofn.lStructSize     = sizeof(ofn);
	ofn.Flags           = flags;
	ofn.FlagsEx         = flagsEx;
	ofn.hInstance       = hInstance;
	ofn.lpstrDefExt     = GetStringChars(env, defExtension);
	ofn.lpstrFile       = file;
	ofn.lpstrFileTitle  = fileName;
	ofn.lpstrFilter     = GetStringChars(env, filter);
	ofn.lpstrInitialDir = GetStringChars(env, initialDir);
	ofn.lpstrTitle      = GetStringChars(env, title);
	ofn.nFileExtension  = (WORD)fileExtension;
	ofn.nFileOffset     = (WORD)fileOffSet;
	ofn.nFilterIndex    = filterIndex;
	ofn.nMaxFile        = maxFile;
	ofn.nMaxFileTitle   = MAX_PATH;
	ofn.hwndOwner       = (HWND)hwndParent;

	if (strFileName)
		lstrcpy(file, GetStringChars(env, strFileName));
	if ((flags & OFN_ENABLEHOOK) != 0)
	{
		LPARAMDATA data{};
		GetHookProcData(env, clazz, dialog, &data);

		ofn.lpfnHook = OpenOrSaveFileHookProc;
		ofn.lCustData = (LPARAM)&data;
	}

	jboolean result = mode ? GetOpenFileName(&ofn) : GetSaveFileName(&ofn);
	if (result)
	{
#ifdef UNICODE
		env->SetObjectField(dialog, 
			env->GetFieldID(clazz, "file", STRING_PATH),
			env->NewString((const jchar*)file, LengthFile(file, maxFile)));
		env->SetObjectField(dialog, 
			env->GetFieldID(clazz, "filename", STRING_PATH),
			env->NewString((const jchar*)fileName, lstrlen(fileName)));
#else
		env->SetObjectField(dialog, 
			env->GetFieldID(clazz, "file", STRING_PATH),
			env->NewStringUTF(file, LengthFile(file, maxFile)));
		env->SetObjectField(dialog, 
			env->GetFieldID(clazz, "filename", STRING_PATH),
			env->NewStringUTF(fileName, lstrlen(fileName)));
#endif
	}
	env->DeleteLocalRef(clazz);

	return result;
}

/*
**
**
*/
JNIFUNCTION(jint) Java_org_zuky_dialogs_MessageBox_showMessage
	(JNIPARAMS, jlong hwndParent, jstring msg, jstring caption, jint type)
{
	return MessageBox((HWND)hwndParent, GetStringChars(env, msg),
		GetStringChars(env, caption), type);
}

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_validateHandle
	(JNIPARAMS, jlong handle)
{
	return handle != 0 && IsWindow((HWND)handle);
}

/*
**
**
*/
JNIFUNCTION(jboolean) Java_org_zuky_dialogs_CommonDialog_setDialogTitle
	(JNIPARAMS, jstring title, jlong handle)
{
	return SetWindowText((HWND)handle, GetStringChars(env, title));
}

/*
**
**
*/
jlong CallMethodHookProc(HWND hwnd, UINT msg, LPARAM lparam, LPARAM data)
{
	LPLPARAMDATA lpData = (LPLPARAMDATA)GetProp(hwnd, CUSTOMDATAPROC);
	if (lpData)
	{
		return lpData->env->CallLongMethod(lpData->dialog, lpData->proc, 
			(jlong)hwnd, (jint)msg, (jlong)lparam, (jlong)data);
	}
	return 0;
}

/*
**
**
*/
BOOL APIENTRY DllMain(HMODULE hModule, DWORD  ul_reason_for_call, LPVOID lpReserved)
{
	switch (ul_reason_for_call)
	{
	case DLL_PROCESS_ATTACH:
		{
			hInstance = hModule;

			if (!SUCCEEDED(CoInitialize(NULL))) // Error
			{
				TCHAR message[1024]{};
				FormatMessage(FORMAT_MESSAGE_FROM_SYSTEM, NULL, 
							GetLastError(), 
							GetUserDefaultUILanguage(), message, ARRAYSIZE(message), NULL);
				MessageBox(NULL, message, TEXT("Error"), MB_ICONERROR);
			}
		}
		break;
	case DLL_THREAD_ATTACH:
	case DLL_THREAD_DETACH:
	case DLL_PROCESS_DETACH:
		CoUninitialize();
		break;
	}
	return TRUE;
}