//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo DirectoryDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.Objects;

/**
 * 
 * @author Zukaritasu
 *
 */
public class DirectoryDialog extends CommonDialog {
	
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// LOS MENSAJES DEL CUADRO DE DIALOGO
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		
	static final int BFFM_INITIALIZED       = 1;
	static final int BFFM_SELCHANGED        = 2;
	static final int BFFM_VALIDATEFAILEDA   = 3;
	static final int BFFM_VALIDATEFAILEDW   = 4;
	static final int BFFM_IUNKNOWN          = 5;
	
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	
	/**
	 * Nombre de la carpeta
	 * 
	 * @see #getDisplayName()
	 */
	protected String displayName;
	
	/**
	 * Ruta de la carpeta
	 * 
	 * @see #getAbsolutePath()
	 */
	protected String absolutePath;
	
	/**
	 * Indice de la imagen
	 * 
	 * @see #getImageIndex()
	 */
	protected int imageIndex;
	
	/**
	 * @see #setRoot(String)
	 * @see #getRoot()
	 */
	protected String root;
	
	/**
	 * @see #setDescription(String)
	 * @see #getDescription()
	 */
	protected String description;
	
	/**
	 * Banderas del cuadro de dialogo.
	 * Las banderas se encuentran en la cabecera <shlobj.h>
	 * <p>
	 * Banderas usadas para inicializar esta variable:
	 * <pre>
	 * BIF_NEWDIALOGSTYLE
	 * BIF_STATUSTEXT
	 * BIF_EDITBOX
	 * </pre>
	 */
	protected int flags = 0x00000040 | 0x00000004 | 0x00000010;

	/**
	 * Crea una nueva instancia de esta clase
	 * 
	 * @see #DirectoryDialog(File)
	 * @see #DirectoryDialog(String)
	 */
	public DirectoryDialog() {
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro la ruta de la carpeta donde se iniciara la
	 * navegacion
	 * 
	 * @see   #DirectoryDialog()
	 * @see   #DirectoryDialog(File)
	 * @param root la ruta de navegacion
	 */
	public DirectoryDialog(String root) {
		setRoot(root);
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro la ruta de la carpeta donde se iniciara la
	 * navegacion
	 * 
	 * @see   #DirectoryDialog(String)
	 * @see   #DirectoryDialog()
	 * @param root la ruta de navegacion
	 * @exception NullPointerException el parametro es {@code null}
	 */
	public DirectoryDialog(File root) {
		setRoot(Objects.requireNonNull(root, "param is null")
				.getAbsolutePath());
	}
	
	/**
	 * Retorna el nombre completo de la carpeta 
	 * 
	 * @return displayName
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Retorna la ruta completa de la carpeta seleccionada
	 * 
	 * @return absolutePath
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	/**
	 * Retorna el indice de la imagen asociada a la carpeta
	 * 
	 * @return imageIndex
	 */
	public int getImageIndex() {
		return imageIndex;
	}
	
	/**
	 * Retorna la ruta de la carpeta por donde inicio el cuadro de
	 * dialogo
	 * 
	 * @see    #setRoot(String)
	 * @return root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Asocia la ruta de una carpeta que es la carpeta raíz por
	 * donde debe iniciar la navegacion el cuadro de dialogo. Si la
	 * ruta es invalida el cuadro de dialogo iniciara desde una ruta
	 * predeterminada
	 * 
	 * @see   #getRoot()
	 * @param root
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * Retorna la descripcion que se muestra en la parte superior del
	 * cuadro de dialogo. Si no se especifico una descripción con el
	 * llamado previo al método {@link #setDescription(String)} el
	 * método retorna <code>null</code>
	 * 
	 * @see    #setDescription(String)
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Muestra una descripcion de lo que debe hacer el usuario. La
	 * información se muestra en la parte superior del cuadro de
	 * dialogo. Si el parámetro es <code>null</code> no se mostrara
	 * ninguna información
	 * 
	 * @param description informacion que se mostrara
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public synchronized boolean show(Window owner) {
		return showDirectoryDialog(this, root, description, flags, 
				getHandleWindow(owner));
	}

	@Override
	protected void initializeDialog(long hwnd) {
		handle = hwnd;
		if (title != null) {
			setDialogTitle(title, handle);
		}
	}

	/**
	 * Procesa todos los mensajes del cuadro de dialogo. El primer
	 * mensaje que es procesado es {@link #BFFM_INITIALIZED}
	 * <p>
	 * Este metodo no procesa mensajes de procedimiento de ventana,
	 * solo mensajes de procedimiento de seleccion de carpetas. Los
	 * mensajes estan declarado en esta clase con el prefijo
	 * <code>BFFM_</code>
	 */
	@Override
	protected long hookProc(long hwnd, int msg, long wparam, 
			long lparam) {
		if (msg == BFFM_INITIALIZED)
			initializeDialog(hwnd);
		return 0L;
	}
	
	private static native boolean showDirectoryDialog(
			DirectoryDialog dialog, 
			String root, String title, int flags, long hwndParent);
	
	/**
	 * Habilita el cuadro de dialogo para que muestre un botón en la
	 * parte inferior para crear una nueva carpeta en el directorio
	 * seleccionado. {@code true} para habilitarlo; {@code false}
	 * para deshabilitarlo. Por defecto el botón se muestra
	 * 
	 * @param newFolder
	 */
	public void setButtonNewFolder(boolean newFolder) {
		flags = flag(flags, 0x00000200, !newFolder);
	}
	
	/**
	 * Retorna <code>true</code> si el cuadro de dialogo muestra el
	 * botón para crear una nueva carpeta; <code>false</code> en
	 * caso contrario
	 * 
	 * @return isButtonNewFolder
	 */
	public boolean isButtonNewFolder() {
		return (flags & 0x00000200) == 0;
	}
}
