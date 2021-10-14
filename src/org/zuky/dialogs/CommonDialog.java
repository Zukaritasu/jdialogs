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
 * Esta clase cuanta con los m�todos b�sicos para crear un cuadro de
 * dialogo nativo de Windows. La clase cuanta con el m�todo
 * {@link #show(Window)} para mostrar el cuadro de dialogo y el m�todo
 * {@link #flag(int, int, boolean)} para modificar un campo de bits
 * <p>
 * Java es un lenguaje multiplataforma por lo cual para obtener el
 * identificador de una ventana se utiliza {@link WComponentPeer} que
 * solo est� disponible para Windows. Todo lo implementado en esta
 * clase y las que hereden de esta clase solo tiene soporte para
 * Windows pero en futuras versiones se podr� tener soporte para otros
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
	 * en el nivel superior, esa ventana que se pase por par�metro
	 * ser� la ventana padre del cuadro de dialogo la cual recibir�
	 * los mensajes o notificaciones del cuadro de dialogo
	 * 
	 * @param  parent la ventana padre
	 * @return {@code true} si la operaci�n tuvo �xito; {@code false}
	 *         si el usuario cancelo la operaci�n o ocurri� un error
	 */
	public abstract boolean show(Window parent);

	/**
	 * Retorna <code>true</code> si el identificador de la ventana es
	 * v�lido, de lo contrario retorna <code>false</code>
	 * 
	 * @param hwnd identificador de la ventana
	 * @return resultado
	 */
	private static native boolean validateHandle(long hwnd);

	/**
	 * Modifica un campo de bits sum�ndole o removi�ndole una bandera
	 * indicado por el tercer par�metro <code>on</code>. El m�todo
	 * retorna el resultado de la operaci�n con el campo de
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
	 * Obtiene el identificador de la ventana la cual ser� usada
	 * como ventana padre que recibir� los mensajes o nitificaciones
	 * del cuadro de dialogo. Si el par�metro es <code>null</code> o
	 * el identificador es inv�lido el m�todo retornara 0
	 * <p>
	 * En una futura versi�n este m�todo puede ser modificador o
	 * eliminado pero por los momentos no ser� declarado como en
	 * desuso {@link Deprecated}
	 * 
	 * @param window la ventana de donde se obtendr� el identificador 
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
