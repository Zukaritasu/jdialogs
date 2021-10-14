//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo DialogResult.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-

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
	 * El m�todo recibe como par�metro el resultado que retorna el
	 * m�todo nativo MessageBox#showMessage(long, String, String, int)
	 * y dependiendo del resultado el m�todo retorna un enumerado y
	 * si el resultado no es reconocido el m�todo retorna
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
