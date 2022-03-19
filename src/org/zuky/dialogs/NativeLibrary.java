/**
 * Copyright (C) 2021 Zukaritasu
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
 *
 */

package org.zuky.dialogs;

/**
 * @author Zukaritasu
 *
 */
final class NativeLibrary {

	static void loadLibrary() {
		if (!System.getProperty("os.name").toLowerCase().startsWith("win"))
			throw new RuntimeException("this system is not supported");
		
		String libraryName = "jdialogs";
		if (System.getProperty("os.arch").contains("64"))
			libraryName += 64;
		libraryName += ".dll";
		System.load(libraryName);
	}
}
