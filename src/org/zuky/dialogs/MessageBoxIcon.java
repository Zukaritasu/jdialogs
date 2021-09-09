//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MessageBoxIcon.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

/**
 * @author Zukaritasu
 *
 */
public enum MessageBoxIcon {

	WARNING      (0x00000030),
	ERROR        (0x00000010),
	QUESTION     (0x00000020),
	INFORMATION  (0x00000040);
	
	private int icon;
	
	MessageBoxIcon(int icon) {
		this.icon = icon;
	}
	
	public int getIcon() {
		return icon;
	}
}
