//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo CommonDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;

import sun.awt.windows.WComponentPeer;

/**
 * 
 * @author Zukaritasu
 *
 */
abstract class CommonDialog {
	
	// Se carga la libreria correspondiente a la arquitectura del
	// sistema
	static {
		String filename = "libwin32-dialogs";
		if (System.getenv("PROCESSOR_ARCHITECTURE").contains("64"))
			filename += "-x64";
		System.loadLibrary(filename);
	}
	
	/**
	 * Titulo del cuadro de dialogo
	 * 
	 * @see #setTitle(String)
	 * @see #getTitle()
	 */
	String title;
	
	/**
	 * Identificador del cuadro de dialogo. La variable se declara de
	 * tipo <code>long</code> para que la librería tenga soporte para
	 * sistemas de 64 bits
	 * 
	 * @see #validateHandle(long)
	 */
	long handle;
	
	/**
	 * Retorna el titulo asociado al cuadro de dialogo. Si no se ha
	 * hecho un llamado previo al método {@link #setTitle(String)} para
	 * asociar un titulo el método retornara <code>null</code>
	 * 
	 * @see #setTitle(String)
	 * @return
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Asocia un titulo al cuadro de dialogo. Por defecto Windows le 
	 * asocia un titulo cuando se crea el cuadro de dialogo
	 * 
	 * @see   #getTitle()
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Verifica si la ventana se encuentra activa para usarla como
	 * ventana padre del cuadro de dialogo. Si el identificador de la
	 * ventana es valido el metodo retornara el identificador de la
	 * ventana, de lo contrario retornara un 0 equivalente a un
	 * <code>NULL</code>
	 * 
	 * @param  window ventana padre
	 * @return HWND
	 */
	@SuppressWarnings("deprecation")
	static final long getHandleWindow(Window window) {
		if (window != null && 
				window.getPeer() instanceof WComponentPeer) {
			long hwnd = ((WComponentPeer) window.getPeer()).getHWnd();
			if (validateHandle(hwnd)) {
				return hwnd;
			}
		}
		return 0;
	}
	
	/**
	 * Muestra el cuadro de dialogo. Para generar el efecto modal se
	 * requiere una ventana que se encuentre activa en la aplicacion,
	 * de lo contrario se mostrara el cuadro de dialogo sin el efecto
	 * modal
	 * 
	 * @param  owner Ventana padre que se usara para el efecto modal
	 * @return Si la operación tuvo éxito el método retornara 
	 *         {@code true}; {@code false} en caso contrario
	 */
	public abstract boolean show(Window owner);
	
	/**
	 * Inicializa las propiedades del cuadro de dialogo cuando llega
	 * el mensaje <code>WM_INITDIALOG</code> o cuando llega el mensaje
	 * <code>BFFM_INITIALIZED</code> (solo para {@link DirectoryDialog})
	 * al procedimiento de la ventana. Una de esas propiedades puede
	 * ser asignarle el título al cuadro de dialogo
	 * 
	 * @param hwnd identificador del cuadro de dialogo
	 */
	protected abstract void initializeDialog(long hwnd);
	
	/**
	 * Procesa los mensajes del cuadro de dialogo. El primer mensaje 
	 * que llega al procedimiento es {@code WM_INITDIALOG} Sin embargo
	 * no sucede lo mismo con el cuadro de dialogo
	 * {@link DirectoryDialog}
	 * 
	 */
	protected abstract long hookProc(long hwnd, int msg, long wparam, 
			long lparam);
	
	/**
	 * Retorna <code>true</code> si el identificador de una ventana
	 * es válido; <code>false</code> si no es el caso
	 * 
	 * @param  handle identificador de la ventana
	 * @return validateHandle
	 */
	static final native boolean validateHandle(long handle);
	
	/**
	 * Le asocia un titulo al cuadro de dialogo
	 * 
	 * @param title  nuevo titulo
	 * @param handle identificador del cuadro de dialogo
	 * @return       {@code true} si la funcion tuvo exito, de lo
	 *               contrario {@code false} si ocurrio un error
	 */
	static final native boolean setDialogTitle(String title, 
			long handle);
	
	/**
	 * Edita una bandera sumándole o removiendo una bandera con el
	 * uso del parámetro {@param on}
	 * 
	 * @param flag     bandera que sera editada
	 * @param newFlag  bandera que se sumara o que se removera
	 * @param on       <code>true</code> para sumarle la bandera o
	 *                 <code>false</code> para removerla
	 * @return         bandera modificada
	 */
	protected static final int flag(int flag, int newFlag, boolean on) {
		return (on ? (flag |= newFlag) : (flag &= ~newFlag));
	}
}
