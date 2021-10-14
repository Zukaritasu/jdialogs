//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo MainTest.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package app;

import org.zuky.dialogs.FileDialog;

/**
 * @author Zukaritasu
 *
 */
public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FileDialog dialog = new FileDialog();
		dialog.setSelectedFolders(true);
		dialog.open(null);
	}

}
