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

import org.zuka.dialogs.ColorDialog;

/**
 * Small example on the use of the {@link ColorDialog} class.
 * For more information read the documentation
 * 
 * @author Zukaritasu
 *
 */
public class ExampleColorDialog {

	/**
	 * @param args program arguments
	 */
	public static void main(String[] args) {
		ColorDialog dialog = new ColorDialog(/* auxiliar new int[] { colors RGB } */);
		dialog.setFullOpen(true); /* open custom colors */
		if (dialog.show()) {
			System.out.println("Selected color: " + dialog.getColor());
			for (int color : dialog.getColors()) {
				System.out.println("Color RGB: " + color);
			}
		}
	}
}
