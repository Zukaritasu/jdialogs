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

import sun.awt.windows.WComponentPeer;
import sun.awt.windows.WWindowPeer;

/**
 * Esta clase cuanta con los m�todos b�sicos para crear un cuadro de
 * dialogo nativo de Windows. La clase cuanta con el m�todo
 * {@link #show(Window)} para mostrar el cuadro de dialogo y el m�todo
 * {@link #flag(int, int, boolean)} para modificar un campo de bits
 * <p>
 * Java es un lenguaje multiplataforma por lo cual para obtener el
 * identificador de una ventana se utiliza la clase {@link WComponentPeer}
 * que solo est� disponible para Windows.
 * 
 * @author Zukaritasu
 *
 */
abstract class CommonDialog {

	// Se carga la libreria
	static {
		NativeLibrary.loadLibrary();
	}

	/**
	 * Muestra el cuadro de dialogo.
	 * <P>
	 * Opcionalmente el metodo requiere de una ventana que este en el
	 * nivel superior, esa ventana que se pase por par�metro ser� la
	 * ventana padre del cuadro de dialogo la cual recibir� los mensajes o
	 * notificaciones del cuadro de dialogo
	 * 
	 * @param  parent la ventana padre
	 * @return {@code true} si la operaci�n tuvo �xito; {@code false}
	 *         si el usuario cancelo la operaci�n o ocurri� un error
	 */
	public abstract boolean show(Window parent);
	
	/**
	 * Muestra el cuadro de dialogo sin requerer de la ventana padre
	 * como en el metodo {@link #show(Window)} pero en este caso se
	 * omite lo que se explica en la documentacion de ese metodo
	 * 
	 * @return {@code true} si la operaci�n tuvo �xito; {@code false}
	 *         si el usuario cancelo la operaci�n o ocurri� un error
	 */
	public boolean show() {
		return show(null);
	}

	/**
	 * Retorna <code>true</code> si el identificador de la ventana es
	 * v�lido, de lo contrario retorna <code>false</code>
	 * 
	 * @param hwnd identificador de la ventana
	 * @return resultado
	 * @deprecated Ya no es necesario verificar si el identificar es
	 * valido, solo se usa sin importar su estado. Este metodo ya no
	 * cuenta con un vinculo nativo !! No llamar este metodo !!
	 */
	@Deprecated
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
	 * como ventana padre que recibir� los mensajes o notificaciones
	 * del cuadro de dialogo. Si el par�metro es <code>null</code>
	 * el m�todo retornara 0
	 * 
	 * @param window la ventana de donde se obtendr� el identificador 
	 * @return el identificador de la ventana
	 */
	static final long getHandleWindow(Window window) {
		if (window != null && window.isDisplayable()) {
			Object windowPeer = WWindowPeer.getPeerForTarget(window);
			if (windowPeer != null) {
				return ((WComponentPeer)windowPeer).getHWnd();
			}
		}
		return 0;
	}
}
