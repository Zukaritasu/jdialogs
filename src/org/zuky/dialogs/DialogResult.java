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

package org.zuky.dialogs;

/**
 * Resultados de {@link MessageBox}
 * 
 * @author Zukaritasu
 *
 */
public enum DialogResult {

	NONE (0),
	OK (1),
	CANCEL (2),
	ABORT (3),
	RETRY (4),
	IGNORE (5),
	YES (6),
	NO (7),
	TRY (10),
	CONTINUE (11);
	
	private final int result;
	
	DialogResult(int result) {
		this.result = result;
	}
	
	/**
	 * Retorna el ID del resultado
	 * 
	 * @return ID del resultado
	 */
	public int getIDResult() {
		return result;
	}
	
	/**
	 * El método recibe como parámetro el resultado que retorna el
	 * método nativo MessageBox#showMessage(long, String, String, int)
	 * y dependiendo del resultado el método retorna un enumerado y
	 * si el resultado no es reconocido el método retorna
	 * {@link DialogResult#NONE}
	 * 
	 * @param result el resultado
	 * @return el resultado definido por un enumerado
	 */
	static DialogResult getDialogResult(int result) {
		for (DialogResult dialogResult : values()) {
			if (dialogResult.result == result) {
				return dialogResult;
			}
		}
		return NONE;
	}
}
