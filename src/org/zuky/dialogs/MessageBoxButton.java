//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MessageBoxButton.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

/**
 * @author Zukaritasu
 *
 */
public enum MessageBoxButton {

	OK               (0x00000000),
	OKCANCEL         (0x00000001),
	ABORTRETRYIGNORE (0x00000002),
	YESNOCANCEL      (0x00000003),
	YESNO            (0x00000004),
	RETRYCANCEL      (0x00000005),
	CANCELTRYCONTINUE(0x00000006);
	
	private int button;
	
	MessageBoxButton(int button) {
		this.button = button;
	}
	
	public int getButton() {
		return button;
	}
}
