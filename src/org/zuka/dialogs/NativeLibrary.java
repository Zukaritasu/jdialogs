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
 *
 */

package org.zuka.dialogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * @author Zukaritasu
 */
final class NativeLibrary {
	
	private static boolean isLoading;

	private static final String RESOURCE_DLL_NAME = "jdialogs-x64.dll";

	static void loadLibrary() {
		if (!isLoading) {
			if (!System.getProperty("os.name").toLowerCase().startsWith("win"))
				throw new RuntimeException("This system is not supported");
			System.load(exportLibrary());
			isLoading = true;
		}
	}

	private static String exportLibrary() {
		File dir = new File(System.getProperty("user.home") + "/.jdialogs");
		if (!dir.exists()) {
			if (!dir.mkdir()) {
				throw new RuntimeException("Create directory error" + dir);
			}
		}

		Path dllLibrary = new File(dir, RESOURCE_DLL_NAME).toPath();
		try {
			Files.copy(NativeLibrary.class.getResourceAsStream("/lib/" + RESOURCE_DLL_NAME), dllLibrary,
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dllLibrary.toString();
	}
}
