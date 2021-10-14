//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MessageBoxIcon.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

/**
 * Los iconos que puede mostrar {@link MessageBox}
 * 
 * @author Zukaritasu
 *
 */
public enum MessageBoxIcon {

	/** Un mensaje de advertencia */
	WARNING      (0x00000030),
	/** Un mensaje de error */
	ERROR        (0x00000010),
	/** Un mensaje para cuestionar una operación */
	QUESTION     (0x00000020),
	/** Un mensaje informativo */
	INFORMATION  (0x00000040);
	
	private int icon;
	
	MessageBoxIcon(int icon) {
		this.icon = icon;
	}
	
	/**
	 * Retorna un ID que identifica el icono de {@link MessageBox}
	 * 
	 * @return ID del icono
	 */
	public int getIDIcon() {
		return icon;
	}
}
