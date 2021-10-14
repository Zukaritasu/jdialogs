//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MessageBoxButton.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

/**
 * Los botones que puede mostrar {@link MessageBox}
 * 
 * @author Zukaritasu
 *
 */
public enum MessageBoxButton {

	/** {@link MessageBox} muestra el botón {@link MessageBoxButton#OK} */
	OK               (0x00000000),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#OKCANCEL} */
	OKCANCEL         (0x00000001),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#ABORTRETRYIGNORE} */
	ABORTRETRYIGNORE (0x00000002),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#YESNOCANCEL} */
	YESNOCANCEL      (0x00000003),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#YESNO} */
	YESNO            (0x00000004),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#RETRYCANCEL} */
	RETRYCANCEL      (0x00000005),
	/** {@link MessageBox} muestra los botones {@link MessageBoxButton#CANCELTRYCONTINUE} */
	CANCELTRYCONTINUE(0x00000006);
	
	private int button;
	
	MessageBoxButton(int button) {
		this.button = button;
	}
	
	/**
	 * Retorna un ID que identifica el botón o los botones que
	 * se mostraran en {@link MessageBox}
	 * 
	 * @return ID del botón
	 */
	public int getIDButton() {
		return button;
	}
}
