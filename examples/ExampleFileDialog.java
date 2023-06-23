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

import org.zuka.dialogs.FileDialog;
import org.zuka.dialogs.WindowsException;


/**
 * @author Zukaritasu
 *
 */
public class ExampleFileDialog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileDialog dialog = new FileDialog("C:\\Users\\Zukaritasu\\Pictures");
		dialog.setMultiSelected(true);
		dialog.setFilter(new String[] {
				"Type picture (*png *jpg *bmp)", "*png;*jpg;*bmp",
				"All files", "*.*"
		});
		
		try {
			if (dialog.show()) {
				for (String file : dialog.getSelectedFiles()) {
					System.out.println(file);
				}
			}
		} catch (WindowsException e) {
			e.printStackTrace();
		}
	}
}
