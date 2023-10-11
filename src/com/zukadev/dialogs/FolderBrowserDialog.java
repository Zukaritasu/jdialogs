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

package com.zukadev.dialogs;

import java.awt.Window;
import java.io.File;
import java.util.Objects;

/**
 * Allow you to navigate between system directories. By invoking the constructor
 * {@link #FolderBrowserDialog(String)} "" or the method {@link #setRoot(String)}
 * you can tell the dialog box in which directory to start browsing.
 * <p>
 * If you notice that it performs a function similar to {@link FileDialog}
 * although note that in the dialog box it allows you to view files if required
 * but it is more advisable to use {@link FileDialog}.
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/msi/browse-dialog">
 * 	Browse Dialog - Win32
 * </a>
 * @author Zukaritasu
 *
 */
public class FolderBrowserDialog extends CommonDialog {

	@NativeModified
	private String displayName;

	@NativeModified
	private String absolutePath;

	private String root;

	private String description;
	
	// 0x00000040 - BIF_NEWDIALOGSTYLE
	// 0x00000004 - BIF_STATUSTEXT
	// 0x00000010 - BIF_EDITBOX
	// 0x00000001 - BIF_RETURNONLYFSDIRS

	private int flags = 0x00000040 | 0x00000004 | 0x00000010 | 0x00000001;

	/**
	 * Creates a new instance of this class
	 * 
	 * @see #FolderBrowserDialog(File) 
	 * @see #FolderBrowserDialog(String) 
	 */
	public FolderBrowserDialog() {
	}

	/**
	 * Creates a new instance of this class receiving the path of the
	 * directory where the navigation will start.
	 *
	 * @param root directory root
	 */
	public FolderBrowserDialog(String root) {
		setRoot(root);
	}

	/**
	 * Creates a new instance of this class receiving the path of the
	 * directory where the navigation will start.
	 *
	 * @exception NullPointerException param is {@code null}
	 * @param root directory root
	 */
	public FolderBrowserDialog(File root) {
		setRoot(Objects.requireNonNull(root, "root is null").getAbsolutePath());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @param parent - the parent window
	 * @return is success
	 */
	@Override
	public synchronized boolean show(Window parent) {
		return showDialog(root, description, flags, getHWnd(parent));
	}
	
	private native boolean showDialog(String root, String title,
			int flags, long hwndParent);

	/**
	 * Returns the name of the selected directory displayed in the text box.
	 *
	 * @return directory name
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Returns the absolute path of the selected element.
	 * If the dialog box was canceled the method returns {@code null}.
	 *
	 * @return absolute path
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * Returns the path from where you started the search in the dialog box.
	 * <p>
	 * note that if I select a directory, this method does not return the
	 * parent directory of the selected directory.
	 *
	 * @return root directory
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Specifies in which directory the navigation should start.
	 * If the path entered is invalid or does not exist, in that case
	 * the dialog box will make the decision where to start.
	 *
	 * @param root directory path
	 */
	public void setRoot(String root) {
		this.root = root;
	}

	/**
	 * Returns the description of the dialog box. If no description has
	 * been set then it returns {@code null}.
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * The dialog box allows you to display a description at the top of
	 * the tree folders.
	 * <p>
	 * If no description is specified, no message will be displayed.
	 *
	 * @param description description of the dialog box
	 * @see #getDescription()
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * If you need to have the option to create a directory by pressing
	 * a button you can enable this option.
	 *
	 * @param newFolder create folder
	 */
	public void setButtonNewFolder(boolean newFolder) {
		/* BIF_NONEWFOLDERBUTTON */
		flags = maskBit(flags, 0x00000200, !newFolder);
	}

	/**
	 * Returns {@code true} if the dialog box displays the button to create
	 * a new directory.
	 *
	 * @return button new folder
	 * @see #setButtonNewFolder(boolean) 
	 */
	public boolean isButtonNewFolder() {
		/* BIF_NONEWFOLDERBUTTON */
		return (flags & 0x00000200) == 0;
	}

	/**
	 * When the dialog box is displayed it will include in its search the
	 * files found in the directory.
	 *
	 * @param includeFiles include files
	 * @see #isIncludeFiles()    
	 */
	public void setIncludeFiles(boolean includeFiles) {
		/* BIF_BROWSEINCLUDEFILES */
		flags = maskBit(flags, 0x00004000, includeFiles);
	}

	/**
	 * Returns true if the dialog box includes files in navigation.
	 *
	 * @return include files
	 * @see #setIncludeFiles(boolean)
	 */
	public boolean isIncludeFiles() {
		/* BIF_BROWSEINCLUDEFILES */
		return (flags & 0x00004000) != 0;
	}
}
