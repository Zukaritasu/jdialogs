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

package org.zuka.dialogs;

/**
 * All the possible results that {@link MessageBox} can return
 * depending on what is specified in the flags {@link MessageBoxButton}
 * 
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox#return-value">
 * 	MessageBox - Return value</a>
 * @author Zukaritasu
 *
 */
public enum DialogResult {

	/** Null result */
	NONE (0),
	/** The OK button was selected */
	OK (1),
	/** The Cancel button was selected */
	CANCEL (2),
	/** The Abort button was selected */
	ABORT (3),
	/** The Retry button was selected */
	RETRY (4),
	/** The Ignore button was selected */
	IGNORE (5),
	/** The Yes button was selected */
	YES (6),
	/** The No button was selected */
	NO (7),
	/** The Try Again button was selected */
	TRY (10),
	/** The Continue button was selected */
	CONTINUE (11);
	
	private final int result;
	
	DialogResult(int result) {
		this.result = result;
	}

	/**
	 * Return the id that is linked to the enumerated one.
	 * 
	 * @return id
	 */
	public int getID() {
		return result;
	}
	
	static DialogResult getDialogResult(int result) {
		for (DialogResult dialogResult : values()) {
			if (dialogResult.result == result) {
				return dialogResult;
			}
		}
		return NONE;
	}
}
