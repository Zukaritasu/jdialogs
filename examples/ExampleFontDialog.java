/**
 * Copyright (C) 2021-2022 Zukaritasu
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

import java.awt.Font;

import org.zuky.dialogs.FontDialog;


/**
 * @author Zukaritasu
 *
 */
public class ExampleFontDialog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		FontDialog dialog = new FontDialog(new Font("Consolas", 0, 13));
		if (dialog.show()) {
			// The selected source is displayed 
			System.out.println(dialog.getFont());
		}
	}
}
