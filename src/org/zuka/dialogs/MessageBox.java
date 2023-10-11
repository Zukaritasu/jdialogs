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
 * Displays a message to the user
 * 
 * @see DialogResult
 * @see MessageBoxButton
 * @see MessageBoxIcon
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-messagebox">
 * 	MessageBox - Win32
 * 	</a>
 * @author Zukaritasu
 *
 */
public final class MessageBox {
	
	static {
		NativeLibrary.loadLibrary();
	}
	
	private MessageBox() {}

	/**
     * Displays a message to the user and returns a DialogResult. By default
     * the dialog box is an informational dialog {@link MessageBoxIcon#INFORMATION}
	 *
	 * @param parent  Parent window owner of the dialog box
	 * @param message The message that will be displayed in the dialog box
	 * @return        result
	 */
	public static DialogResult show(Window parent, String message) {
		return show(parent, message, "");
	}

    /**
     * Displays a message to the user and returns a DialogResult. By default
     * the dialog box is an informational dialog {@link MessageBoxIcon#INFORMATION}
     *
     * @param parent  Parent window owner of the dialog box
     * @param message The message that will be displayed in the dialog box
     * @param caption Dialog box title
     * @return        result
     */
	public static DialogResult show(Window parent, String message,
			String caption) {
		return show(parent, message, caption, MessageBoxButton.OK);
	}

    /**
     * Displays a message to the user and returns a DialogResult. By default
     * the dialog box is an informational dialog {@link MessageBoxIcon#INFORMATION}
     *
     * @param parent  Parent window owner of the dialog box
     * @param message The message that will be displayed in the dialog box
     * @param caption Dialog box title
     * @param button  Indicates which button group should be displayed in
     *                the dialog box. Use {@link MessageBoxButton}
     * @return        result
     */
	public static DialogResult show(Window parent, String message, 
			String caption, MessageBoxButton button) {
		return show(parent, message, caption, button, 
			MessageBoxIcon.INFORMATION);
	}

    /**
     * Displays a message to the user and returns a DialogResult. By default
     * the dialog box is an informational dialog {@link MessageBoxIcon#INFORMATION}
     *
     * @param parent  Parent window owner of the dialog box
     * @param message The message that will be displayed in the dialog box
     * @param caption Dialog box title
     * @param button  Indicates which button group should be displayed in
     *                the dialog box. Use {@link MessageBoxButton}
     * @param icon    Specifies which icon the dialog box should display.
     *                Depending on the icon the window will produce a sound when displayed.
     * @return        result
     */
	public static DialogResult show(Window parent, String message, 
			String caption, MessageBoxButton button, MessageBoxIcon icon) {
		return DialogResult.getDialogResult(
				showMessage(CommonDialog.getHWnd(parent), message, 
				caption, button.getID() | icon.getID()));
	}
	
	private static native int showMessage(long hWndParent, String message,
			String caption, int type);
}
