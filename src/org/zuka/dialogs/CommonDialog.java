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

import java.awt.Window;

/**
 * It is the base class of the dialog windows that contains the
 * methods and functions necessary for its use.
 * 
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/uxguide/win-common-dlg">
 * 	Common Dialogs - Win32
 * </a>
 * @author Zukaritasu
 *
 */
abstract class CommonDialog {

	static {
		NativeLibrary.loadLibrary();
	}

	/**
	 * Displays the dialog box specifying the parent window for
	 * the modal state
	 * 
	 * @param  parent - the parent window
	 * @return <code>true</code> if the dialog box was closed
	 * 		   correctly or <code>false</code> if it was
	 * 		   canceled or an error occurred.
	 */
	public abstract boolean show(Window parent);

	/**
	 * Display the dialog box without defining a parent window
	 * 
	 * @return <code>true</code> if the dialog box was closed
	 * 		   correctly or <code>false</code> if it was
	 * 		   canceled or an error occurred.
	 * @see #show(Window)
	 */
	public boolean show() {
		// show without defining a parent window
		return show(null);
	}

	static final int maskBit(int bits, int flag, boolean on) {
		return (on ? (bits | flag) : (bits & ~flag));
	}

	private static native long getHWnd0(Window window);
	
	static final long getHWnd(Window window) {
		// If the window is not being displayed, 0 is returned.
		if (window != null && window.isDisplayable())
			return getHWnd0(window);
		return 0;
	}
}
