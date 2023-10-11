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
 * Types and group of buttons available in the {@link MessageBox}
 * <p>
 * The documentation for this class listed is based on Microsoft official website
 *
 * @author Zukaritasu
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox#parameters">Parameters</a>
 */
public enum MessageBoxButton {

	/** The message box contains one push button: OK. This is the default */
	OK               (0x00000000),
	/** The message box contains two push buttons: OK and Cancel */
	OKCANCEL         (0x00000001),
	/** The message box contains three push buttons: Abort, Retry, and Ignore */
	ABORTRETRYIGNORE (0x00000002),
	/** The message box contains three push buttons: Yes, No, and Cancel */
	YESNOCANCEL      (0x00000003),
	/** The message box contains two push buttons: Yes and No */
	YESNO            (0x00000004),
	/** The message box contains two push buttons: Retry and Cancel */
	RETRYCANCEL      (0x00000005),
	/** The message box contains three push buttons: Cancel, Try Again, Continue */
	CANCELTRYCONTINUE(0x00000006);
	
	private int button;
	
	MessageBoxButton(int button) {
		this.button = button;
	}

	/**
	 * Returns the ID of the button
	 *
	 * @return id
	 */
	public int getID() {
		return button;
	}
}
