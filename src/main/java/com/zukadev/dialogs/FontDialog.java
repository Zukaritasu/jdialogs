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

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;

/**
 * Allows you to select a font from the system and give it size
 * and style. The dialog box also has a sample text to see the applied styles.
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/dlgbox/font-dialog-box">
 * 	Font Dialog Box - Win32
 * </a>
 * @author Zukaritasu
 *
 */
public class FontDialog extends CommonDialog {

	@NativeModified
	private Font font;

	private int flags = 0x00000001 | 0x00000040 | 0x00010000;
	
	@NativeModified
	private int color;

	/**
	 * Create a new instance of this class
	 */
	public FontDialog() {
	}

	/**
	 * Receives a font to be displayed as default font in the dialog box
	 *
	 * @param font model font
	 */
	public FontDialog(Font font) {
		this.font = font;
	}

	@Override
	public synchronized boolean show(Window window) {
		long parent = getHWnd(window);
		if (font == null)
			return showDialog(null, 0, 0, flags, color, parent);
		return showDialog(font.getName(),font.getStyle(), font.getSize(), flags, color, parent);
	}
	
	private native boolean showDialog(String name, int style,
			int size, int flags, int color, long hWndParent);

	/**
	 * Returns the font that was selected but if in such a case no font
     * was selected then the method returns null or the model font.
     *
	 * @return font
     * @see #setFont(Font)
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Sets the model font to be displayed in the dialog box.
	 * @param font model font
     * @see #getFont()
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Enables the effects and styles to be applied to the sample text
     * in the dialog box
     *
	 * @param effects enable or disable
	 */
	public void setEnabledEffects(boolean effects) {
		flags = maskBit(flags, 0x00000100, effects);
	}

	/**
	 * Returns {@code true} if visual effects and styles are enabled.
     *
	 * @return enable effects
	 */
	public boolean isEnabledEffects() {
		return (flags & 0x00000100) != 0;
	}

	/**
	 * Returns the color that was used in the sample text.
     * <p>
     * Default color is black
     *
	 * @return color
     * @see #setColor(Color)
	 */
	public Color getColor() {
		return new Color(color);
	}

	/**
	 * Sets the color of the sample text in the dialog box. If the color has
     * some level of transparency then it is ignored.
     *
	 * @param color text color
     * @see #getColor()    
	 */
	public void setColor(Color color) {
		this.color = color == null ? 0 : color.getRGB();
	}
}
