//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo FolderBrowserDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.Objects;

/**
 * Esta clase representa el cuadro de dialogo selector de carpetas
 * de Windows en donde se podrá seleccionar una carpeta del sistema.
 * <p>
 * En el cuadro de dialogo se puede asignar una descripción
 * {@link #setDescription(String)} que le indica al usuario la operación
 * que se está realizando o lo que conlleva seleccionar una carpeta
 * 
 * @author Zukaritasu
 *
 */
public class FolderBrowserDialog extends CommonDialog {

	/**
	 * El nombre de la carpeta
	 * 
	 * @see #getDisplayName()
	 */
	private String displayName;
	
	/**
	 * La ruta absoluta de la carpeta
	 * 
	 * @see #getAbsolutePath()
	 */
	private String absolutePath;
	
	/**
	 * La carpeta donde el cuadro de dialogo iniciara la navegación 
	 * 
	 * @see #setRoot(String)
	 * @see #getRoot()
	 */
	private String root;
	
	/**
	 * La descripción que se mostrara en el cuadro de dialogo  
	 */
	private String description;
	
	/**
	 * Banderas del cuadro de dialogo
	 */
	private int flags = 0x00000040 | 0x00000004 | 0x00000010 | 0x00000001;
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de carpetas
	 * 
	 * @see #FolderBrowserDialog(File)
	 * @see #FolderBrowserDialog(String)
	 */
	public FolderBrowserDialog() {
		
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de carpetas. El
	 * constructor recibe como parámetro la carpeta donde el cuadro
	 * de dialogo iniciara la navegación
	 * <p>
	 * Si la ruta no es válida el cuadro de dialogo iniciara desde
	 * una ruta predeterminada del sistema
	 * 
	 * @see #FolderBrowserDialog(File)
	 * @param root la ruta de la carpeta
	 */
	public FolderBrowserDialog(String root) {
		setRoot(root);
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de carpetas. El
	 * constructor recibe como parámetro la carpeta donde el cuadro
	 * de dialogo iniciara la navegación
	 * <p>
	 * Si la ruta no es válida el cuadro de dialogo iniciara desde
	 * una ruta predeterminada del sistema
	 * 
	 * @see #FolderBrowserDialog(String)
	 * @exception NullPointerException el parametro es {@code null}
	 * @param root la ruta de la carpeta
	 */
	public FolderBrowserDialog(File root) {
		setRoot(Objects.requireNonNull(root, "param is null")
				.getAbsolutePath());
	}

	@Override
	public synchronized boolean show(Window parent) {
		return showDialog(root, description, flags, 
				getHandleWindow(parent));
	}
	
	private native boolean showDialog(String root, String title,
			int flags, long hwndParent);
	
	/**
	 * Retorna el nombre de la carpeta que se muestra en la caja
	 * de texto del cuadro de dialogo
	 * 
	 * @return el nombre de la carpeta
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Retorna la ruta absoluta de la carpeta que fue seleccionada
	 * en el cuadro de dialogo. Para obtener el nombre de la
	 * carpeta {@link #getDisplayName()}
	 * 
	 * @return la ruta absoluta de la carpeta
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * Retorna la ruta de la carpeta por donde inicio la navegación
	 * el cuadro de dialogo. Si no se ha hecho un llamado previo al
	 * método {@link #setRoot(String)}, en ese caso el método
	 * retorna <code>null</code>
	 * 
	 * @see #setRoot(String)
	 * @return la carpeta donde inicio la navegación el cuadro de
	 *         dialogo
	 */
	public String getRoot() {
		return root;
	}
	
	/**
	 * Asocia la carpeta por donde iniciara la navegación el cuadro
	 * de dialogo. Si la ruta no es valida el cuadro de dialogo
	 * iniciara desde una ruta predeterminada del sistema
	 * 
	 * @see #getRoot()
	 * @param root la ruta de la carpeta
	 */
	public void setRoot(String root) {
		this.root = root;
	}
	
	/**
	 * Retorna la descripción que se muestra en la parte superior
	 * del cuadro de dialogo. Si no se ha hecho un llamado previo
	 * al método {@link #setDescription(String)}, en ese caso el
	 * método retorna <code>null</code> 
	 * 
	 * @see #setDescription(String)
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Asigna la descripción que se mostrara en la parte superior
	 * del cuadro de dialogo. Esta descripción le indica que le
	 * indica al usuario la operación que se está realizando o lo
	 * que conlleva seleccionar una carpeta
	 * 
	 * @param description la descripción
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Habilita el cuadro de dialogo para que muestre un botón en la
	 * parte inferior para crear una nueva carpeta en el directorio
	 * seleccionado. {@code true} para habilitarlo; {@code false}
	 * para deshabilitarlo. Por defecto el botón se muestra
	 * 
	 * @param newFolder mostrar el botón
	 */
	public void setButtonNewFolder(boolean newFolder) {
		flags = flag(flags, 0x00000200, !newFolder);
	}
	
	/**
	 * Retorna <code>true</code> si el cuadro de dialogo muestra el
	 * botón para crear una nueva carpeta; <code>false</code> en
	 * caso contrario
	 * 
	 * @return se muestra el botón
	 */
	public boolean isButtonNewFolder() {
		return (flags & 0x00000200) == 0;
	}
	
	/**
	 * Habilita el cuadro de dialogo para que además de incluir y
	 * seleccionar carpetas también se pueda incluir y seleccionar
	 * archivos pero es recomendable utilizar {@link FileDialog}
	 * porque contiene más funcionalidades como seleccionar múltiples
	 * archivos o asociar un filtro de busqueda por extensión
	 * 
	 * @see #isIncludeFiles()
	 * @param includeFiles incluir archivos
	 */
	public void setIncludeFiles(boolean includeFiles) {
		flags = flag(flags, 0x00004000, includeFiles);
	}
	
	/**
	 * Retorna <code>true</code> si el cuadro de dialogo incluye
	 * archivos de lo contrario retorna <code>false</code>
	 * 
	 * @see #setIncludeFiles(boolean)
	 * @return incluye archivos
	 */
	public boolean isIncludeFiles() {
		return (flags & 0x00004000) != 0;
	}
}
