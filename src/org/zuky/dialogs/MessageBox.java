//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MessageBox.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;


/**
 * La clase cuanta con una sobrecarga de método para mostrar un mensaje
 * al usuario. Dependiendo de lo que se requiera se puede usar uno de los
 * métodos declarados en esta clase
 * 
 * @see MessageBoxButton
 * @see MessageBoxIcon
 * 
 * @author Zukaritasu
 *
 */
public final class MessageBox {
	
	// No usar el constructor
	private MessageBox() {}

	/**
	 * Muestra un mensaje en pantalla. Este cuadro de dialogo no tiene
	 * título y solo se usa el botón <code>OK</code>. El tipo de cuadro
	 * de dialogo usado por defecto es {@link MessageBoxIcon#INFORMATION}
	 * 
	 * @param parent  ventana padre
	 * @param message mensaje que se mostrara
	 * @return resultado
	 */
	public static DialogResult show(Window parent, String message) {
		return show(parent, message, "");
	}
	
	/**
	 * Muestra un mensaje en pantalla. Este cuadro de dialogo solo muestra
	 * el botón <code>OK</code>. El tipo de cuadro de  dialogo usado por
	 * defecto es {@link MessageBoxIcon#INFORMATION}
	 * 
	 * @param parent  ventana padre
	 * @param message mensaje que se mostrara
	 * @param caption titulo del cuadro de dialogo
	 * @return resultado
	 */
	public static DialogResult show(Window parent, String message,
			String caption) {
		return show(parent, message, caption, MessageBoxButton.OK);
	}
	
	/**
	 * Muestra un mensaje en pantalla. Este cuadro de dialogo muestra el
	 * boton especificado por el cuarto parametro utilizando un enumerado
	 * declarado en <code>MessageBoxButton</code>. El tipo de cuadro de
	 * dialogo usado por defecto es {@link MessageBoxIcon#INFORMATION}
	 * 
	 * @param parent  ventana padre
	 * @param message mensaje que se mostrara
	 * @param caption titulo del cuadro de dialogo
	 * @param button  botones de opcion
	 * @return resultado
	 */
	public static DialogResult show(Window parent, String message, 
			String caption, MessageBoxButton button) {
		return show(parent, message, caption, button, 
			MessageBoxIcon.INFORMATION);
	}
	
	/**
	 * Muestra un mensaje en pantalla. Este cuadro de dialogo muestra el
	 * boton especificado por el cuarto parametro utilizando un enumerado
	 * declarado en <code>MessageBoxButton</code> y el tipo de cuadro de
	 * dialogo señalado por uno de los enumerados declarados en
	 * <code>MessageBoxIcon</code>
	 * 
	 * @param parent  ventana padre
	 * @param message mensaje que se mostrara
	 * @param caption titulo del cuadro de dialogo
	 * @param button  botones de opcion
	 * @param icon    tipo de cuadro de dialogo
	 * @return resultado
	 */
	public static DialogResult show(Window parent, String message, 
			String caption, MessageBoxButton button, MessageBoxIcon icon) {
		return DialogResult.getDialogResult(
				showMessage(CommonDialog.getHandleWindow(parent), message, 
				caption, button.getIDButton() | icon.getIDIcon()));
	}
	
	
	
	
	private static native int showMessage(long hwndParent, String message, 
			String caption, int type);
}
