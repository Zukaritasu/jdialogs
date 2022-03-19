/**
 * Copyright (C) 2021-2022 Zukaritasu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.zuky.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import sun.awt.OSInfo;
import sun.awt.OSInfo.WindowsVersion;

/**
 * Esta clase representa el selector de archivos o carpetas de
 * <code>Windows</code>. La clase cuenta con dos métodos para mostrar
 * el cuadro de dialogo cumpliendo dos funciones diferentes,
 * {@link #save(Window)} para guardar un archivo especificado por el
 * usuario y {@link #open(Window)} para abrir un archivo o una carpeta.
 * En el caso que se desee seleccionar más de un archivo o carpeta puede
 * llamar el método {@link #setMultiSelected(boolean)}
 * <p>
 * El paquete {@link org.zuky.dialogs} cuenta con la clase
 * {@link FolderBrowserDialog} para seleccionar una sola carpeta pero
 * en esta clase se puede seleccionar más de una carpeta pero hay que
 * tener en cuenta la versión del sistema porque la selección de carpeta
 * no está soportada desde versiones anteriores a <code>Windows Vista</code>,
 * en ese aso es recomendable usar la clase {@link FolderBrowserDialog}
 * para seleccionar carpetas porque tiene mayor soporte para versiones
 * viejas de <code>Windows</code>
 * 
 * @author Zukaritasu
 *
 */
public class FileDialog extends CommonDialog {

	/**
	 * La carpeta de navegación
	 * 
	 * @see #setRoot(String)
	 * @see #getRoot()
	 */
	private String root;
	
	/**
	 * El nombre del archivo
	 * 
	 * @see #getFileName()
	 * @see #setFileName(String)
	 */
	private String fileName;
	
	/**
	 * @see #getAbsolutePath()
	 */
	@NativeValue
	private String absolutePath;
	
	/**
	 * @see #getSelectedFiles()
	 */
	@NativeValue
	private String[] selectedFiles;
	
	/**
	 * El filtro de extensiones de archivo
	 * 
	 * @see #setFilter(String[])
	 * @see #getFilter()
	 */
	private String[] filter;
	
	/**
	 * El titulo del cuadro de dialogo
	 * 
	 * @see #setTitle(String)
	 * @see #getTitle()
	 */
	private String title;
	
	/**
	 * La extensión de archivo por defecto
	 * 
	 * @see #getDefaultExtension()
	 * @see #setDefaultExtension(String)
	 */
	private String defaultExt;
	
	/**
	 * Texto de la etiqueta que acompaña la caja de texto
	 * 
	 * @see #setFileNameLabel(String)
	 * @see #getFileNameLabel()
	 */
	private String fileNameLabel;
	
	/**
	 * Texto del botón <code>OK</code>
	 * 
	 * @see #setOkButtonLabel(String)
	 * @see #getOkButtonLabel()
	 */
	private String okButtonLabel;
	
	/**
	 * Opciones de cuadro de dialogo
	 */
	private int options;
	
	/**
	 * Indice del filtro de extensiones de archivo
	 * 
	 * @see #setFilterIndex(int)
	 * @see #getFilterIndex()
	 */
	@NativeValue
	private int filterIndex;
	
	/**
	 * Para la concurrencia
	 */
	private final Object treeLock = new Object();
	
	/**
	 * Indica si la versión del OS puede soportar
	 * {@link #setSelectedFolders(boolean)} por parte de {@link FileDialog}
	 */
	private static final boolean isSupportWinVersion;
	
	static {
		WindowsVersion version = OSInfo.getWindowsVersion();
		isSupportWinVersion = version.getMajor() >= 6;
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de archivos
	 * o carpetas de Windows.
	 * 
	 */
	public FileDialog() {
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de archivos
	 * o carpetas de Windows. El constructor recibe como parámetro
	 * la ruta de la carpeta donde el cuadro de dialogo iniciara la
	 * navegación 
	 * 
	 * @param root la carpeta de navegación
	 */
	public FileDialog(String root) {
		setRoot(root);
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de archivos
	 * o carpetas de Windows. El constructor recibe como parámetro
	 * de tipo {@link File} que contiene la ruta de la carpeta donde
	 * el cuadro de dialogo iniciara la navegación 
	 * 
	 * @param root la carpeta de navegación
	 * @exception NullPointerException el parametro es {@code null}
	 */
	public FileDialog(File root) {
		setRoot(Objects.requireNonNull(root, "param is null")
				.getAbsolutePath());
	}

	/**
	 * @deprecated este método se encuentra en desuso porque no
	 * cumple con ninguna función en esta clase, a cambio debe usar
	 * los métodos {@link #open(Window)} y {@link #save(Window)}
	 */
	@Deprecated
	@Override
	public final boolean show(Window parent) {
		return false;
	}
	
	/**
	 * Muestra el cuadro de dialogo para que el usuario seleccione
	 * archivos o carpetas (en el caso que este habilitada la opción
	 * {@link #setSelectedFolders(boolean)}). Para establecer un
	 * filtro de extensiones de archivo puede llamar el método
	 * {@link #setFilter(String[])} pero esto no aplica si solo se
	 * va a seleccionar carpetas. Si se desea seleccionar más de un
	 * archivo o carpeta debe llamar el método
	 * {@link #setMultiSelected(boolean)}
	 * <p>
	 * Si el usuario realizo la operación de seleccionar un archivo
	 * o una carpeta sin cancelar el cuadro de dialogo y además de
	 * no haber ocurrido un error inesperado en el proceso el método
	 * retorna <code>true</code>, de lo contrario retorna
	 * <code>false</code>
	 * 
	 * @see #save(Window)
	 * @param parent ventana padre
	 * @return resultado
	 */
	public boolean open(Window parent) {
		synchronized (treeLock) {
			return showDialog(parent, true, options);	
		}
	}	
	
	/**
	 * Muestra el cuadro de dialogo para que el usuario seleccione
	 * o cree un archivo para que la aplicación guarde dicha
	 * información en el archivo.
	 * <p>
	 * Si el usuario realizo la operación de seleccionar o
	 * crear un archivo sin cancelar el cuadro de dialogo y además de
	 * no haber ocurrido un error inesperado en el proceso el método
	 * retorna <code>true</code>, de lo contrario retorna
	 * <code>false</code>
	 * 
	 * @see #open(Window)
	 * @param parent ventana padre
	 * @return resultado
	 */
	public boolean save(Window parent) {
		synchronized (treeLock) {
			return showDialog(parent, false, options & ~0x20);
		}
	}
	
	private boolean showDialog(Window parent, boolean mode, int options) {
		return showDialog(mode, fileNameLabel, okButtonLabel, root,
				fileName, title, defaultExt, options, filter,
				filterIndex, getHandleWindow(parent));
	}

	/**
	 * Retorna la ruta de la carpeta donde el cuadro de dialogo
	 * inicio la navegación que fue asociada mediante el método
	 * {@link #setRoot(String)} o desde uno de los constructores
	 * de la clase {@link #FileDialog(File)} o {@link #FileDialog(String)}
	 * 
	 * @see #setRoot(String)
	 * @return la carpeta de navegación
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Asocia la ruta de una carpeta la cual va a hacer usada por
	 * el cuadro de dialogo para iniciar la navegación o búsqueda
	 * de archivos o carpetas desde esa ruta.
	 * <p>
	 * Tenga en cuenta que si la ruta no es válida el cuadro
	 * dialogo iniciara desde una ruta predeterminada o desde la
	 * ruta que se uso anteriormente para la selección de archivos
	 * o carpetas
	 * 
	 * @see #getRoot()
	 * @param root la carpeta de navegación
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * Retorna el nombre de archivo que fue asociado desde un
	 * llamado previo al método {@link #setFileName(String)}. Si
	 * no se estableció un nombre de archivo el método retornara
	 * <code>null</code>
	 * 
	 * @see #setFileName(String)
	 * @return el nombre de archivo
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Establece un nombre de archivo predeterminado que será usado
	 * por el cuadro dialogo como nombre de archivo por defecto que
	 * se mostrara en la caja de texto cuando se vaya a guardar un
	 * archivo.
	 * <P>
	 * Es recomendable que el nombre de archivo no contenga una
	 * extensión de archivo si ya se declaro una extensión de
	 * archivo por defecto con un llamado previo al método
	 * {@link #setDefaultExtension(String)}
	 * <p>
	 * Si establece el nombre de archivo con <code>null</code> el
	 * cuadro de dialogo no mostrara un nombre de archivo por
	 * defecto
	 * 
	 * @see #getFileName()
	 * @param fileName el nombre de archivo
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * Retorna la ruta absoluta del archivo o carpeta que fue
	 * seleccionado en el cuadro de dialogo.
	 * <p>
	 * Si está habilitada la multi selección de archivos o carpeta
	 * este método retorna <code>null</code>, en ese caso se debe
	 * usar el método {@link #getSelectedFiles()}
	 * 
	 * @see #getSelectedFiles()
	 * @return la ruta absoluta
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}
	
	/**
	 * Retorna un array de {@link String} que contiene los archivos
	 * o carpetas que fueron seleccionadas en el cuadro de dialogo
	 * con un llamado al método {@link #open(Window)}. La multi
	 * selección debe estar habilitada {@link #setMultiSelected(boolean)}
	 * <p>
	 * Si la multi selección esta deshabilitada este método retorna
	 * <code>null</code>, para ello debe usar el método
	 * {@link #getAbsolutePath()} 
	 * 
	 * @return el array con las rutas de los archivos o carpetas
	 */
	public String[] getSelectedFiles() {
		return selectedFiles;
	}

	/**
	 * Establece un título que se mostrara en el cuadro de dialogo.
	 * Si el parámetro es <code>null</code> el cuadro de dialogo
	 * usara un titulo por defecto como <b>Guardar</b>, <b>Abrir</b>
	 * o <b>Seleccionar carpeta</b>
	 * 
	 * @param title el titulo del cuadro de dialogo
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Retorna el titulo del cuadro de dialogo. Si no se ha hecho
	 * un llamado previo al método {@link #setTitle(String)} para
	 * establecer un titulo el método retorna <code>null</code>
	 * 
	 * @see #setTitle(String)
	 * @return el titulo del cuadro de dialogo
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Retorna la extensión por defecto que es usada cuando el
	 * usuario no especifica una extensión de archivo  en la caja
	 * de texto del cuadro de dialogo.
	 * <p>
	 * Si aun no se ha hecho un llamado previo al método
	 * {@link #setDefaultExtension(String)} para establecer una
	 * extensión el método retorna <code>null</code>
	 * 
	 * @see #setDefaultExtension(String)
	 * @return la extensión por defecto 
	 */
	public String getDefaultExtension() {
		return defaultExt;
	}

	/**
	 * Establece una extensión de archivo por defecto. El cuadro de
	 * dialogo usa esta extensión de archivo para concatenarlo con
	 * el nombre de archivo cuando el usuario no especifica una
	 * extensión de archivo en la caja de texto
	 * 
	 * @see #getDefaultExtension()
	 * @param defaultExt La extensión por defecto 
	 */
	public void setDefaultExtension(String defaultExt) {
		this.defaultExt = defaultExt;
	}
	
	private native boolean showDialog(boolean mode, String fileNameLabel, 
			String okButtonLabel, String root, String fileName,
			String title, String defaultExt, int options, String[] filter,
			int filterIndex, long hwndParent);

	/**
	 * Retorna el filtro de extensiones de archivo que usa el cuadro
	 * de dialogo para la búsqueda de archivos. Si no ha establecido
	 * un filtro llamando el método {@link #setFilter(String[])}, en
	 * ese caso el método retorna <code>null</code>
	 * 
	 * @see #setFilter(String[])
	 * @return el filtro de extensiones de archivo
	 */
	public String[] getFilter() {
		if (filter != null)
			return Arrays.copyOf(filter, filter.length);
		return null;
	}

	/**
	 * Establece un filtro de extensiones de archivo la cual ayuda a
	 * la búsqueda de archivos mediante su extensión. Un filtro está
	 * constituido por su descripción y sus extensiones. Un ejemplo
	 * seria
	 * <pre>
	 * 	setFilter(new String[] {
	 * 	      "Archivo de texto (*.txt)", "*.txt",
	 * 	      "Archivo de imagen (*.jpg *.png)", "*.jpg;*.png",
	 * 	      "Todo los archivos", "*.*"
	 * 	});
	 * </pre>
	 * Como se puede observar en el ejemplo la descripción del filtro
	 * debe estar acompañado con sus extensiones y si se quiere
	 * declarar más de una extensión se debe usar el punto y coma ‘;’
	 * <p>
	 * Si se establece un filtro y anteriormente se avía asignado el
	 * índice del filtro {@link #setFilterIndex(int)}, en ese caso
	 * el índice cambia a cero 0
	 * 
	 * @see #getFilter()
	 * @see #setFilterIndex(int)
	 * @param filter el filtro de extensiones de archivo
	 * @exception IllegalArgumentException el filtro no es válido
	 *            porque se encuentra incompleto
	 * @exception NullPointerException un elemento del arreglo es
	 * 			  <code>null</code>
	 */
	public void setFilter(String[] filter) {
		filterIndex = 0;
		if (filter != null) {
			if ((filter.length % 2) != 0)
				throw new IllegalArgumentException();
			for (String string : filter)
				Objects.requireNonNull(string, "ningun elemento del array puede ser null");
			this.filter = Arrays.copyOf(filter, filter.length);
		} else {
			this.filter = null;
		}
	}

	/**
	 * Retorna el índice del filtro que se está aplicando en el
	 * cuadro de dialogo. Para establecer un índice debe usar el
	 * método {@link #setFilterIndex(int)} pero antes que nada debe
	 * establecer un filtro usando el método
	 * {@link #setFilter(String[])}
	 * <p>
	 * Cuando el usuario selecciona un filtro y luego el cuadro de
	 * dialogo es cerrado cuando el usuario le da aceptar, el índice
	 * del filtro establecido cambia al índice del filtro que
	 * selecciono el usuario pero solo cuando el usuario no cancela
	 * la operación 
	 * 
	 * @see #setFilterIndex(int)
	 * @return el indice del filtro
	 */
	public int getFilterIndex() {
		return filterIndex;
	}

	/**
	 * Establece el índice del filtro que se aplicara
	 * cuando el cuadro de dialogo se muestre, para ello se debió
	 * haber establecido un filtro de extensiones de archivos con
	 * una llamada el método {@link #setFilter(String[])}
	 * <p>
	 * El índice del filtro comienza desde 1 hasta la longitud del
	 * filtro dividido entre 2 <code>(length / 2)</code>. En todo
	 * caso si el índice es un numero negativo o supera la longitud
	 * del filtro divido entre dos el método lanza una excepción de
	 * tipo {@link IllegalArgumentException}
	 * 
	 * @see #getFilterIndex()
	 * @param filterIndex el indice del filtro
	 * @exception IllegalArgumentException el índice es inválido
	 */
	public void setFilterIndex(int filterIndex) {
		if (filterIndex < 0 || (filter != null && filterIndex > (filter.length / 2)))
			throw new IllegalArgumentException();
		this.filterIndex = filterIndex;
	}
	
	/**
	 * Permite que el usuario pueda seleccionar varios archivos o
	 * carpetas a la vez. Esto no se aplica si solo se requiere guardar
	 * un archivo. En el caso que se desee seleccionar una o más
	 * carpetas debe usar el método {@link #setSelectedFolders(boolean)}
	 * para habilitar esa opción
	 * 
	 * @see #isMultiSelected()
	 * @param multi seleccionar varios archivos o carpetas
	 */
	public void setMultiSelected(boolean multi) {
		options = flag(options, 0x200, multi);
	}
	
	/**
	 * Retorna <code>true</code> si el usuario puede seleccionar
	 * varios archivos o carpetas a la vez, de lo contrario el método
	 * retorna <code>false</code>
	 * 
	 * @see #setMultiSelected(boolean)
	 * @return seleccionar varios archivos o carpetas  a la vez
	 */
	public boolean isMultiSelected() {
		return (options & 0x200) != 0;
	}
	
	/**
	 * Permite que el usuario pueda seleccionar carpetas en vez de
	 * archivos y si desea seleccionar varias carpetas debe habilitar
	 * esta opción {@link #setMultiSelected(boolean)}
	 * <p>
	 * Tenga en cuenta que establecer un nombre de archivo
	 * {@link #setFileName(String)}, un filtro {@link #setFilter(String[])},
	 * un índice {@link #setFilterIndex(int)} o una extensión por
	 * defecto {@link #setDefaultExtension(String)} no aplica al
	 * cuadro de dialogo cuando solo se debe seleccionar carpetas.
	 * Si esta opción es habilitada en el cuadro de dialogo solo
	 * aparecerán carpetas y no archivos
	 * 
	 * @exception IllegalStateException La versión del sistema es
	 *            inferior a 6, en ese caso debe usar la clase
	 *            {@link FolderBrowserDialog}
	 * @see org.zuky.dialogs.FolderBrowserDialog
	 * @see #isSelectedFolders()
	 * @param selectedFolders seleccionar carpetas
	 */
	public void setSelectedFolders(boolean selectedFolders) {
		if (!isSupportWinVersion)
			throw new IllegalStateException(System.getProperty("os.version"));
		options = flag(options, 0x20, selectedFolders);
	}
	
	/**
	 * Retorna <code>true</code> si el usuario debe seleccionar
	 * carpetas y no archivos, de lo contrario el método retorna
	 * <code>false</code>
	 * 
	 * @see #setSelectedFolders(boolean)
	 * @return seleccionar carpetas
	 */
	public boolean isSelectedFolders() {
		return (options & 0x20) != 0;
	}

	/**
	 * Retorna el texto de la etiqueta que aparece al lado de la
	 * caja de texto del cuadro de dialogo. Si no se ha establecido
	 * el texto de la etiqueta con un llamado al método
	 * {@link #setFileNameLabel(String)}, en ese caso el método
	 * retorna <code>null</code>
	 * 
	 * @return el texto de la etiqueta
	 */
	public String getFileNameLabel() {
		return fileNameLabel;
	}

	/**
	 * Establece el texto de la etiqueta que aparece al lado de
	 * la caja de texto del cuadro de dialogo. Si el parámetro es
	 * <code>null</code> el cuadro de dialogo muestra el texto
	 * predeterminado
	 * 
	 * @see #getFileNameLabel()
	 * @param fileNameLabel el texto de la etiqueta
	 */
	public void setFileNameLabel(String fileNameLabel) {
		this.fileNameLabel = fileNameLabel;
	}

	/**
	 * Retorna el texto establecido en el botón <code>OK</code>
	 * del cuadro de dialogo. Si no se ha establecido el texto del
	 * botón con un llamado al método {@link #setOkButtonLabel(String)},
	 * en ese caso el método retorna <code>null</code>
	 * 
	 * @see #setOkButtonLabel(String)
	 * @return el texto establecido en el botón <code>OK</code>
	 */
	public String getOkButtonLabel() {
		return okButtonLabel;
	}

	/**
	 * Establece el texto del botón <code>OK</code>  del cuadro
	 * de dialogo. Si el parámetro es <code>null</code> el cuadro
	 * de dialogo muestra el texto predeterminado la cual es
	 * <code>Aceptar</code>, <code>Guardar</code> o
	 * <code>Seleccionar carpeta</code>
	 * 
	 * @see #getOkButtonLabel()
	 * @param okButtonLabel el texto del botón
	 */
	public void setOkButtonLabel(String okButtonLabel) {
		this.okButtonLabel = okButtonLabel;
	}
	
	/**
	 * Permite que el cuadro de dialogo muestre carpetas,
	 * archivos y elementos del sistema que se encuentran ocultos.
	 * <p>
	 * Si el usuario edito las opciones de carpetas del sistema
	 * para que archivos y carpetas que se encuentran ocultos
	 * sean visibles, entonces el método no cumple ninguna
	 * función en particular
	 * 
	 * @see #isForceShowHidden()
	 * @param forceShowHidden permitir elementos ocultos
	 */
	public void setForceShowHidden(boolean forceShowHidden) {
		options = flag(options, 0x10000000, forceShowHidden);
	}
	
	/**
	 * Retorna <code>true</code> si el cuadro de dialogo muestra
	 * carpetas y elementos del sistema que estan ocultos, en
	 * caso contrario retorna <code>false</code>
	 * 
	 * @see #setForceShowHidden(boolean)
	 * @return permitir elementos ocultos
	 */
	public boolean isForceShowHidden() {
		return (options & 0x10000000) != 0;
	}
}
