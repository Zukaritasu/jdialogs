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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;


/**
 * @author Zukaritasu
 *
 */
final class NativeLibrary {
	
	/**
	 * El nombre de la libreria.
	 * @see NativeLibrary#loadLibrary()
	 */
	static final String libraryName = "jdialogs-win32-v20220318.dll";
	
	/**
	 * La ruta de la libreria. Dependiendo del sistema oporativo y la
	 * arquitectura, el nombre de la libreria y la ruta puede cambiar
	 * {@link #libraryName}
	 * 
	 * @see NativeLibrary#loadLibrary()
	 */
	static final String libraryPath = "/org/zuky/dialogs/win32/jdialogs" 
									+ (isArch64() ? "64" : "") + ".dll";

	/**
	 * Retorna {@code true} si el sistema operativo es Windows, de lo
	 * contrario retorna {@code false}
	 * 
	 * @return es Windows
	 */
	static boolean isWindows() {
		return System.getProperty("os.name").toLowerCase()
				.startsWith("win");
	}
	
	/**
	 * Retorna {@code true} si el sistema operativo es de 64 bits, de
	 * lo contrario retorna {@code false} si es de 32 bits.
	 * 
	 * @return es arquitectura de 64 bits
	 */
	static boolean isArch64() {
		return System.getProperty("os.arch").contains("64");
	}
	
	/**
	 * Carga la libreria {@link #libraryName} pero antes que nada se
	 * verifica que el sistema operativo sea Windows de lo contrario
	 * el metodo lanzara una excepcion de tipo {@link RuntimeException}.
	 * <p>
	 * Si la carpeta <code>.jdialogs</code> no existe el la ruta
	 * <code>user.home</code> entonces se crea la carpeta y se copia
	 * la DLL correspondiente en la carpeta
	 * 
	 * @throws IOException error en el proceso de copiar la DLL en
	 *         la carpeta
	 */
	static void loadLibrary() throws IOException {
		if (!isWindows())
			throw new RuntimeException("this system is not supported");
		File lib = new File(System.getProperty("user.home") + "\\.jdialogs");
		lib.mkdirs();
		lib = new File(lib, libraryName);
		if (!lib.exists())
			Files.copy(NativeLibrary.class
					.getResourceAsStream(libraryPath), lib.toPath());
		System.load(lib.getAbsolutePath());
	}
}
