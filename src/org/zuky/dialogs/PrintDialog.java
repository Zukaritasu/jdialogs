//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2021-10-19
// - Nombre de archivo PrintDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;

/**
 * @author Zukaritasu
 *
 */
class PrintDialog extends CommonDialog {
	
	private int copies;
	
	private int flags;

	/**
	 * 
	 */
	public PrintDialog() {
		
	}
	
	public int getCopies() {
		return copies;
	}
	
	public void setCopies(int copies) {
		this.copies = copies;
	}

	@Override
	public boolean show(Window parent) {
		return showDialog();
	}
	
	public void enabledPageNums(boolean b) {
		
	}
	
	public boolean isEnabledPageNums() {
		return false;
	}
	
	private native boolean showDialog();
}
