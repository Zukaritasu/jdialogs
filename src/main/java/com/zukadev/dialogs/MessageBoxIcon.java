/**
 * Copyright (C) 2021-2023 Zukaritasu
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

package com.zukadev.dialogs;

/**
 * The icons available in the {@link MessageBox}
 * <p>
 * The documentation for this class listed is based on Microsoft official website
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox#remarks">
 * 	MessageBox - Remarks
 * </a>
 * @author Zukaritasu
 *
 */
public enum MessageBoxIcon {

	/** Displays an error message */
	ERROR        (0x00000010),
	/** Displays a message generating a question */
	QUESTION     (0x00000020),
	/** Displays a warning message to the user */
	WARNING      (0x00000030),
	/** Displays an information message */
	INFORMATION  (0x00000040);
	
	private int icon;
	
	MessageBoxIcon(int icon) {
		this.icon = icon;
	}

	/**
	 * Returns the icon ID
	 *
	 * @return id
	 */
	public int getID() {
		return icon;
	}
}
