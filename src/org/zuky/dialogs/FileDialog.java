//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo FileDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * @author Zukaritasu
 *
 */
public class FileDialog extends CommonDialog {

	/**
	 * Nombre del archivo
	 * 
	 * @see #getFileName()
	 * @see #setFileName(String)
	 */
	private String filename; // fileTitle
	
	/**
	 * Ruta completa del archivo
	 * 
	 * @see #getFile()
	 * @see #getFiles()
	 */
	private String file;
	
	/**
	 * Directorio de busqueda
	 * 
	 * @see #setRoot(String)
	 * @see #getRoot()
	 */
	private String root;
	
	/**
	 * Filtro de busqueda
	 * 
	 * @see #setFilter(String)
	 * @see #getFilter()
	 */
	private String filter;
	
	/**
	 * Filtro de busqueda predeterminado
	 * 
	 * @see #setDefaultExtension(String)
	 * @see #getDefaultExtension()
	 */
	private String defaultExt;
	
	/**
	 * Indice del filtro
	 * 
	 * @see #setFilterIndex(int)
	 * @see #getFilterIndex()
	 */
	private int filterIndex;
	
	// usado para la concurrencia
	private final Object object = new Object();
	
	/**
	 * Banderas del cuadro de dialogo.
	 * Las banderas se encuentran en la cabecera <commdlg.h>
	 * <p>
	 * Banderas usadas para inicializar esta variable:
	 * <pre>
	 * OFN_EXPLORER
	 * OFN_ENABLESIZING
	 * </pre>
	 */
	protected int flags = 0x00080000 | 0x00800000;

	/**
	 * Crea una nueva instancia de esta clase
	 * 
	 */
	public FileDialog() {
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro la ruta de un directorio donde inicia la
	 * búsqueda. Si la ruta no existe, el cuadro de dialogo usara un
	 * directorio predeterminado
	 * 
	 * @param root directorio de busqueda
	 */
	public FileDialog(String root) {
		setRoot(root);
	}
	
	/**
	 * Retorna el nombre del archivo seleccionado. Si está habilitada
	 * la multiseleccion y hay más de 1 archivo seleccionado este
	 * método retornara una cadena vacia
	 * 
	 * @return fileName
	 */
	public String getFileName() {
		return filename;
	}
	
	/**
	 * Asigna un nombre de archivo que se mostrara en la caja de texto.
	 * Este nombre de archivo se usara para crear un nuevo archivo,
	 * esto solo se usa cuando se va a guardar un archivo
	 * 
	 * @see   #getFileName()
	 * @param filename
	 * @exception IllegalArgumentException el nombre del archivo tiene
	 *            una longitud mayor de 260 caracteres no permitido
	 *            por Windows
	 */
	public void setFileName(String filename) {
		if (filename != null && filename.length() > 260)
			throw new IllegalArgumentException("length > 260");
		this.filename = filename;
	}

	/**
	 * @deprecated Este método se encuentra obsoleto. Debe usar
	 * los métodos {@link #save(Window)} y {@link #open(Window)}
	 */
	@Deprecated
	@Override
	public final boolean show(Window owner) {
		return false;
	}
	
	/**
	 * Retorna un objeto de {@link File} con la ruta completa hacia el
	 * archivo seleccionado. Si la mutiseleccion está habilitada este
	 * método retornara <code>null</code>, en ese caso debe usar el
	 * método {@link #getFiles()}
	 * 
	 * @return file
	 */
	public File getFile() {
		if ((flags & 0x00000200) == 0 && file != null && 
				!file.isEmpty())
			return new File(file);
		return null;
	}
	
	/**
	 * Retorna los archivos seleccionados en el cuadro de dialogo. Si
	 * la multiseleccion esta deshabilitado es método retornara 
	 * <code>null</code>, en este caso debe usar el método
	 * {@link #getFile()}
	 * 
	 * @return files
	 */
	public File[] getFiles() {
		if ((flags & 0x00000200) != 0 && file != null && 
				!file.isEmpty()) {
			if (file.indexOf("\0") == -1) {
				return new File[] { new File(file) };
			} else {
				return getFileOfArray();
			}
		}
		return null;
	}
	
	/**
	 * Retorna un array de {@link File} con las rutas completa de los
	 * archivos. La variable {@link #file} contiene la ruta de los
	 * archivos seleccionados y el formato de la cadena de texto está
	 * limitado por el token ‘\0’ (solo en el caso que se haya
	 * seleccionado más de un archivo).
	 * <p>
	 * En el caso que se haya seleccionado más de un archivo el primer
	 * elemento limitado por el token ‘\0’ es la ruta de la carpeta y
	 * los siguientes elementos son los nombres de los archivos
	 * 
	 * @see    #getFiles()
	 * @return files
	 */
	private File[] getFileOfArray() {
		List<File> files = new ArrayList<>();
		StringTokenizer toks = new StringTokenizer(file, "\0");
		toks.hasMoreElements();
		String directory = toks.nextToken();
		while (toks.hasMoreElements())
			files.add(new File(directory + File.separator
					+ toks.nextToken()));
		return files.toArray(new File[] {});
	}
	
	/**
	 * Muestra un cuadro de dialogo para guardar un archivo. No es
	 * recomendable llamar este método nuevamente cuando ya existe
	 * un cuadro de dialogo visible de esta misma instancia de la
	 * clase
	 * 
	 * @param  owner ventana padre
	 * @return {@code true} si la operación tuvo éxito; 
	 *         {@code false} en caso contrario 
	 * @see    #open(Window)
	 */
	public boolean save(Window owner) {
		synchronized (object) {
			return showFileDialog(owner, false);
		}
	}
	
	/**
	 * Muestra un cuadro de dialogo para abrir uno o mas archivos.
	 * No es recomendable llamar este método nuevamente cuando ya
	 * existe un cuadro de dialogo visible de esta misma instancia
	 * de la clase
	 * 
	 * @param  owner ventana padre
	 * @return {@code true} si la operación tuvo éxito;
	 *         {@code false} en caso contrario 
	 * @see    #save(Window)
	 */
	public boolean open(Window owner) {
		synchronized (object) {
			return showFileDialog(owner, true);
		}
	}
	
	private boolean showFileDialog(Window owner, boolean mode) {
		return showFileDialog(this, mode, filter, null, 0, 
				filterIndex, filename, root,
				title, flags, 0, 0, defaultExt, 0, 
				Short.MAX_VALUE, getHandleWindow(owner));
	}

	/**
	 * @see #hookProc(long, int, long, long)
	 */
	@Override
	protected void initializeDialog(long hwnd) {
	}

	/**
	 * 
	 */
	@Override
	protected long hookProc(long hwnd, int msg, long wparam, 
			long lparam) {
		return 0L;
	}
	
	/**
	 * Asigna una ruta del directorio por donde iniciara la busqueda
	 * de archivos
	 * 
	 * @see   #getRoot()
	 * @param root
	 * @exception IllegalArgumentException la ruta del directorio
	 *            tiene una longitud mayor de 260 caracteres
	 */
	public void setRoot(String root) {
		if (root != null && root.length() > 260)
			throw new IllegalArgumentException("length > 260");
		this.root = root;
	}
	
	/**
	 * Retorna la ruta del directorio de navegación. Si no se ha
	 * hecho un llamado previo al metodo {@link #setRoot(String)}
	 * el metodo retornara <code>null</code>
	 * 
	 * @see    #setRoot(String)
	 * @return root
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Retorna el filtro que está usando el cuadro de dialogo
	 * para ayudar a la búsqueda de archivos
	 * 
	 * @see    #setFilter(String)
	 * @return filter
	 */
	public String getFilter() {
		return filter;
	}
	
	/**
	 * Asocia un filtro al cuadro de dialogo para ayudar a la
	 * búsqueda de archivos usando uno o más extensión de archivo
	 * <p>
	 * Un ejemplo de como debe estar estructurado un filtro de
	 * archivo:
	 * <pre>
	 * "Archivo de tipo (*.png)\0*.png\0\0"
	 * "Archivo de tipo (*.png *.jpg *.bmp *.ico)\0*.png;*.jpg;*.bmp;*.ico\0\0"
	 * "Todos los archivos (*.*)\0*.*\0Archivo de tipo (*.png)\0*.png\0\0"
	 * </pre>
	 * Para tener mas de una extension de archivo se debe usar el
	 * punto y coma ademas no debe existir ningun espacion entre
	 * cada extension de archivo, solo puede existir espacios en
	 * la descripcion de la extension
	 * <p>
	 * La cadena siempre debe estar finalizada obligatoriamente
	 * con 2 caracteres nulos '\0' como se muestra en el ejemplo,
	 * si no es así el programa puede dejar de funcionar
	 * 
	 * @see   #getFilter()
	 * @param filter
	 * @exception IllegalArgumentException la cadena de texto no
	 *            contiene los dos caracteres nulos '\0' al final
	 *            de la cadena
	 */
	public void setFilter(String filter) {
		if (filter != null && !filter.endsWith("\0\0"))
			throw new IllegalArgumentException("\\0\\0");
		this.filter = filter;
	}
	
	/**
	 * Habilita la multiseleccion de archivos, {@code true} para
	 * habilitarla o {@code false} para deshabilitarla. Por defecto
	 * la multiseleccion de archivos se encuentra deshabilitada
	 * 
	 * @see   #isMultiSelected()
	 * @param multiSelected
	 */
	public void setMultiSelected(boolean multiSelected) {
		flags = flag(flags, 0x00000200, multiSelected);
	}
	
	/**
	 * Retorna {@code true} si la multiseleccion está habilitada;
	 * {@code false} en caso contrario.
	 * 
	 * @see    #setMultiSelected(boolean)
	 * @return isMultiSelected
	 */
	public boolean isMultiSelected() {
		return (flags & 0x00000200) != 0;
	}
	
	/**
	 * Retorna el índice del filtro que se está aplicando en la
	 * búsqueda de archivos en el cuadro de dialogo.
	 * <p>
	 * Tenga en cuenta que la selección del filtro por gestión del
	 * usuario no está controlada por la clase, esto quiere decir
	 * que el método retornara el índice que se especifico antes
	 * de mostrar el cuadro de dialogo
	 * 
	 * @see    #setFilterIndex(int)
	 * @return filterIndex
	 */
	public int getFilterIndex() {
		return filterIndex;
	}
	
	/**
	 * Específica cual filtro debe aplicarse para la búsqueda de
	 * archivos. Antes que nada debe haberse asociado un filtro
	 * al cuadro de dialogo con un llamado al método
	 * {@link #setFilter(String)}
	 *
	 * @see   #getFilterIndex()
	 * @param filterIndex indice del filtro
	 */
	public void setFilterIndex(int filterIndex) {
		this.filterIndex = filterIndex;
	}

	/**
	 * Retorna la extension predeterminada. 
	 * 
	 * @see    #setDefaultExtension(String)
	 * @return defaultExt
	 */
	public String getDefaultExtension() {
		return defaultExt;
	}

	/**
	 * Asigna la extension predeterminada. El cuadro de dialogo
	 * usa esta extension cuando no existe un filtro presente.
	 * La cadena puede tener cualquier longitud pero solo se usan
	 * los tres primertos caracteres, ademas la cadena no puede
	 * contener puntos
	 * 
	 * @see   #setFilter(String)
	 * @see   #getDefaultExtension()
	 * @param defaultExt
	 * @exception IllegalArgumentException la cadena de texto
	 *            contiene un punto '.'
	 */
	public void setDefaultExtension(String defaultExt) {
		if (defaultExt != null && defaultExt.indexOf('.') > -1)
			throw new IllegalArgumentException("'.'");
		this.defaultExt = defaultExt;
	}
	
	/**
	 * Habilita la apariencia básica del explorador de Windows
	 * cuando se requiere guardar un archivo o abrir uno o mas
	 * archivos
	 * 
	 * @see   #isEnableBasicExplorer()
	 * @param basicExplorer
	 */
	public void enableBasicExplorer(boolean basicExplorer) {
		flags = flag(flags, 0x00000020, basicExplorer);
	}
	
	/**
	 * Retorna {@code true} si la apariencia básica del
	 * explorador de Windows está habilitada; {@code false} en
	 * caso contrario
	 * 
	 * @see #enableBasicExplorer(boolean)
	 * @return isEnableBasicExplorer
	 */
	public boolean isEnableBasicExplorer() {
		return (flags & 0x00000020) != 0;
	}
	
	/**
	 * Habilita el cuadro de dialogo para que pueda cambiar de
	 * tamaño cuando se use el mouse.
	 * <p>
	 * Esta funcion no tendra efecto si el cuadro de dialogo no
	 * tiene la apariencia basica del explorador de Windows 
	 * habilitada {@link #enableBasicExplorer(boolean)}
	 * 
	 * @see   #isResizabled()
	 * @param resizable
	 */
	public void setResizabled(boolean resizable) {
		flags = flag(flags, 0x00800000, resizable);
	}
	
	/**
	 * Retorna {@code true} si el cuadro de dialogo esta
	 * habilitado para cambiar de tamaño cuando se use el mouse;
	 * {@code false} en caso contrario
	 * 
	 * @see #setResizabled(boolean)
	 * @return isResizabled
	 */
	public boolean isResizabled() {
		return (flags & 0x00800000) != 0;
	}
	
	/**
	 * 
	 * @see #open(Window)
	 * @see #save(Window)
	 * @see #showFileDialog(Window, boolean)
	 */
	private static native boolean showFileDialog(FileDialog dialog,
			boolean mode, String filter, String customFilter,
			int maxCusFilter, int filterIndex, String fileTitle,
			String initialDir, String title, int flags, 
			int fileOffSet, int fileExtension, String defExtension,
			int flagsEx, int maxFile, long hwndParent);
}
