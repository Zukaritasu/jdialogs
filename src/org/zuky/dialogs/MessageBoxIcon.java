/**
 * Copyright (C) 2021 Zukaritasu
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
 *
 */

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
	/** Un mensaje para preguntar */
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
