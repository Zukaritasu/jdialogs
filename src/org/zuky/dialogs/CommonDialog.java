//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo CommonDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

package org.zuky.dialogs;

import java.awt.Window;
import java.awt.peer.ComponentPeer;

import sun.awt.windows.WComponentPeer;

/**
 * Esta clase cuanta con los métodos básicos para crear un cuadro de
 * dialogo nativo de Windows. La clase cuanta con el método
 * {@link #show(Window)} para mostrar el cuadro de dialogo y el método
 * {@link #flag(int, int, boolean)} para modificar un campo de bits
 * <p>
 * Java es un lenguaje multiplataforma por lo cual para obtener el
 * identificador de una ventana se utiliza {@link WComponentPeer} que
 * solo está disponible para Windows. Todo lo implementado en esta
 * clase y las que hereden de esta clase solo tiene soporte para
 * Windows pero en futuras versiones se podrá tener soporte para otros
 * sistemas operativos como <code>Linux</code> y <code>iOS</code>
 * 
 * @author Zukaritasu
 *
 */
abstract class CommonDialog {

	// Se carga la libreria correspondiente a la arquitectura del
	// sistema
	static {
		String filename = "jdialogs";
		if (System.getenv("PROCESSOR_ARCHITECTURE").contains("64"))
			filename += "64";
		System.loadLibrary(filename);
	}

	/**
	 * Muestra el cuadro de dialogo.
	 * <P>
	 * Para tener el efecto modal se requiere de una ventana que este
	 * en el nivel superior, esa ventana que se pase por parámetro
	 * será la ventana padre del cuadro de dialogo la cual recibirá
	 * los mensajes o notificaciones del cuadro de dialogo
	 * 
	 * @param  parent la ventana padre
	 * @return {@code true} si la operación tuvo éxito; {@code false}
	 *         si el usuario cancelo la operación o ocurrió un error
	 */
	public abstract boolean show(Window parent);

	/**
	 * Retorna <code>true</code> si el identificador de la ventana es
	 * válido, de lo contrario retorna <code>false</code>
	 * 
	 * @param hwnd identificador de la ventana
	 * @return resultado
	 */
	private static native boolean validateHandle(long hwnd);

	/**
	 * Modifica un campo de bits sumándole o removiéndole una bandera
	 * indicado por el tercer parámetro <code>on</code>. El método
	 * retorna el resultado de la operación con el campo de
	 * <code>bits</code> modificado
	 * 
	 * @param bits campo de bits
	 * @param flag bandera que se sumara o que se removera
	 * @param on   {@code true} para sumarle la bandera o
	 *             {@code false} para removerla
	 * @return     el campo de bits modificado
	 */
	protected static final int flag(int bits, int flag, boolean on) {
		return (on ? (bits | flag) : (bits & ~flag));
	}

	/**
	 * Obtiene el identificador de la ventana la cual será usada
	 * como ventana padre que recibirá los mensajes o nitificaciones
	 * del cuadro de dialogo. Si el parámetro es <code>null</code> o
	 * el identificador es inválido el método retornara 0
	 * <p>
	 * En una futura versión este método puede ser modificador o
	 * eliminado pero por los momentos no será declarado como en
	 * desuso {@link Deprecated}
	 * 
	 * @param window la ventana de donde se obtendrá el identificador 
	 * @return el identificador de la ventana
	 */
	@SuppressWarnings("deprecation")
	static final long getHandleWindow(Window window) {
		if (window != null && window.isDisplayable()) {
			ComponentPeer peer = window.getPeer();
			if (peer instanceof WComponentPeer) {
				long hwnd = ((WComponentPeer)peer).getHWnd();
				if (validateHandle(hwnd)) {
					return hwnd;
				}
			}
		}
		return 0;
	}
}
