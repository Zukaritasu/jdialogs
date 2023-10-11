package com.zukadev.examples;
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

import com.zukadev.dialogs.FolderBrowserDialog;

/**
 * @author Zukaritasu
 *
 */
public class ExampleFolderBrowserDialog {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FolderBrowserDialog dialog = new FolderBrowserDialog("C:\\Users\\Zukaritasu\\Desktop");
		dialog.setDescription("Select a folder");
		if (dialog.show()) {
			System.out.println("The folder you selected was: " + dialog.getAbsolutePath());
		}
	}
}
