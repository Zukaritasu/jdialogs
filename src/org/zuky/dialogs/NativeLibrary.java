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

package org.zuky.dialogs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * @author Zukaritasu
 */
final class NativeLibrary {
	
	private static void unpackLibrary(String lib, String libName) {
		if (!Files.exists(Paths.get(libName, new String[] {}))) {
			try {
				Files.copy(Objects.requireNonNull(NativeLibrary.class.getClassLoader()
						.getResourceAsStream(lib)), Paths.get(libName, new String[] {}));
			} catch (IOException e) {
				System.err.println(e);
			}
		}
	}

	static void loadLibrary() {
		if (!System.getProperty("os.name").toLowerCase().startsWith("win"))
			throw new RuntimeException("This system is not supported");
		String libName = String.format("jdialogs-x%d.dll", System.getProperty("os.arch")
				.contains("64") ? 64 : 86);
		unpackLibrary("lib/" + libName, libName);
		System.load(new File(libName).getAbsolutePath());
	}
}
