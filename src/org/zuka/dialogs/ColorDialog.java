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

import java.awt.Color;
import java.awt.Window;
import java.util.Objects;

/**
 * This class Represents the WinAPI custom color selection dialog box.
 * <p>
 * The dialog box does not support the alpha channel and in that
 * case the alpha channel or transparency is ignored.
 * 
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/dlgbox/color-dialog-box">
 * 	Color Dialog Box - Win32
 * </a>
 * @author Zukaritasu
 *
 */
public class ColorDialog extends CommonDialog {

	/**
	 * Maximum number of colors you can customize
	 * 
	 * @see #setColors(int[])
	 * @see #getColors()
	 */
	public static final int MAX_CUSTOM_COLORS = 16;

	@NativeModified
	private final int[] custom = new int[MAX_CUSTOM_COLORS];

	@NativeModified
	private int color;

	private int flags;

	/**
	 * Create an instance of this class
	 * 
	 * @see #ColorDialog(int[])
	 */
	public ColorDialog() {
	}

	/**
	 * Creates an instance of this class where it receives an array of 
	 * integers that defines the color of each ColorDialog box.
	 * 
	 * @param colors Array of integers defining the color of each cell
	 * @exception NullPointerException The array is <code>null</code>
	 * @exception IllegalArgumentException The array size exceeds the
	 * 		maximum amount defined by {@link #MAX_CUSTOM_COLORS}
	 * @see #setColors(int[])
	 */
	public ColorDialog(int[] colors) {
		setColors(colors);
	}

	@Override
	public synchronized boolean show(Window parent) {
		return showDialog(flags, getHWnd(parent));
	}

	/**
	 * When opening the dialog box you can indicate whether it should
	 * be displayed with the extension to edit the RGB colors.
	 * 
	 * @param fullOpen Show edit extension
	 */
	public void setFullOpen(boolean fullOpen) {
		flags = maskBit(flags, 0x00000002, fullOpen);
	}

	/**
	 * Returns true if {@link ColorDialog} is displayed with extension
	 * 
	 * @return Full open
	 */
	public boolean isFullOpen() {
		return (flags & 0x00000002) != 0;
	}

	/**
	 * Returns the last color that was selected. If you have not
	 * selected a color the result will be a black color.
	 * 
	 * @return Color
	 */
	public Color getColor() {
		return new Color(color);
	}

	/**
	 * Returns an array with the colors of the boxes including
	 * custom and selected colors.
	 * 
	 * @return Array of colors
	 * @see #setColors(int[])
	 */
	public int[] getColors() {
		int[] colors = new int[MAX_CUSTOM_COLORS];
		System.arraycopy(custom, 0, colors, 0, MAX_CUSTOM_COLORS);
		return colors;
	}
	
	private native boolean showDialog(int flags, long hWndParent);

	/**
	 * Define the colors to be displayed in each box of the dialog box
	 * <p>
	 * The alpha channel or transparency is ignored because Windows
	 * {@link ColorDialog} is not supported.
	 * 
	 * @param colors Array of colors
	 * @exception NullPointerException The array is <code>null</code>
	 * @exception IllegalArgumentException The array size exceeds the
	 * 		maximum amount defined by {@link #MAX_CUSTOM_COLORS}
	 * @see #MAX_CUSTOM_COLORS
	 * @see #getColors()
	 */
	public void setColors(int[] colors) {
		if (Objects.requireNonNull(colors, "colors is null").length > MAX_CUSTOM_COLORS)
			throw new IllegalArgumentException("colors.length > 16");
		System.arraycopy(colors, 0, custom, 0, colors.length);
	}
}
