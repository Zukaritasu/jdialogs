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

import java.awt.Window;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

/**
 * Allows single and multiple selection of files and folders in the
 * dialog box. It also allows a more precise search by specifying
 * a search filter by calling the {@link #setFilter(String[])} method.
 *
 * @see <a href="https://learn.microsoft.com/en-us/windows/win32/dlgbox/open-and-save-as-dialog-boxes">
 * 	Open and Save As dialog boxes - Win32
 * </a>
 * @author Zukaritasu
 *
 */
public class FileDialog extends CommonDialog {

	private String root;

	private String fileName;

	@NativeModified
	private String absolutePath;

	@NativeModified
	private String[] selectedFiles;

	private String[] filter;

	private String title;

	private String defaultExt;

	private String fileNameLabel;

	private String okButtonLabel;

	private int options;

	@NativeModified
	private int filterIndex;

	private final Object treeLock = new Object();

	private static final boolean isSupportWinVersion;
	
	static {
		// The setSelectedFolders option is only supported since Windows version 6.
		String[] version = System.getProperty("os.version").split(".");
		isSupportWinVersion = Integer.parseInt(version[0]) > 6;
	}

	/**
	 * Create a new instance of this class
	 */
	public FileDialog() {
	}

	/**
	 * Receives as argument the path where the dialog box should start.
	 * If the path does not exist the system will choose the default path.
	 *
	 * @param root Root folder
	 * @exception NullPointerException the argument is null
	 * @see #FileDialog(File) 
	 */
	public FileDialog(String root) {
		setRoot(root);
	}

	/**
	 * Receives as argument the path where the dialog box should start.
	 * If the path does not exist the system will choose the default path.
	 *
	 * @param root Root folder
	 * @exception NullPointerException the argument is null
	 * @see #FileDialog(String)
	 */
	public FileDialog(File root) {
		setRoot(Objects.requireNonNull(root, "root is null").getAbsolutePath());
	}

	/**
	 * {@inheritDoc}
	 * @deprecated This method is in disuse because it does not
	 * comply with the dynamics of the class, in this case you should use
	 * {@link #open(Window)} or {@link #save(Window)}.
	 */
	@Deprecated
	@Override
	public final boolean show(Window parent) {
		return false;
	}

	/**
	 * Displays the dialog box in the mode of selecting one or more items
	 *
	 * @param parent The parent window
	 * @return If the operation was successful the method returns {@code true}
	 */
	public boolean open(Window parent) {
		synchronized (treeLock) {
			return showDialog(parent, true, options);	
		}
	}

	/**
	 * Displays the dialog box in the mode of save
	 *
	 * @param parent The parent window
	 * @return If the operation was successful the method returns {@code true}
	 */
	public boolean save(Window parent) {
		synchronized (treeLock) {
			return showDialog(parent, false, options & ~0x20);
		}
	}
	
	private boolean showDialog(Window parent, boolean mode, int options) {
		return showDialog(mode, fileNameLabel, okButtonLabel, root,
				fileName, title, defaultExt, options, filter,
				filterIndex, getHWnd(parent));
	}

	/**
	 * Returns the root folder or parent folder where the search was started
	 *
	 * @return root Root folder
	 * @see #setRoot(String)
	 */
	public String getRoot() {
		return root;
	}

	/**
	 * Sets the folder where the dialog box should start the search.
	 *
	 * @param root Root folder
	 * @exception NullPointerException the argument is null
	 * @see #getRoot()
	 */
	public void setRoot(String root) {
		this.root = Objects.requireNonNull(root, "root is null");
	}

	/**
	 * Returns the name of the file that was defined to save a file with that name
	 *
	 * @return file name
	 * @see #setFileName(String) 
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Define or set the name of the file to be saved, by defining
	 * the default name and extension the dialog box creates the absolute
	 * path with its default name and extension.
	 *
	 * @param fileName file name
	 * @see #getFileName()    
	 * @see #setDefaultExtension(String)
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Returns the path to the selected file or folder. If nothing has
	 * been selected yet the method returns null. If you set a multiple
	 * selection you must call {@link #getSelectedFiles()}
	 *
	 * @return absolute path
	 * @see #setMultiSelected(boolean)
	 * @see #getSelectedFiles()
	 */
	public String getAbsolutePath() {
		return absolutePath;
	}

	/**
	 * Returns an array with the selected files. First of all you must
	 * activate the multiple selection by calling {@link #setMultiSelected(boolean)}.
	 * <p>
	 * If no files or folders were selected, the method returns {@code null}.
	 *
	 * @return selected files
	 * @see #getAbsolutePath() 
	 * @see #setMultiSelected(boolean)
	 */
	public String[] getSelectedFiles() {
		return selectedFiles;
	}

	/**
	 * Sets the title of the dialog box. By default, the system sets a
	 * default title depending on the case
	 *
	 * @param title window title
	 * @see #getTitle()
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Returns the window title
	 *
	 * @return title
	 * @see #setTitle(String)
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Returns the default file extension
	 *
	 * @return default extension
	 * @see #setDefaultExtension(String) 
	 */
	public String getDefaultExtension() {
		return defaultExt;
	}

	/**
	 * Sets a default extension for the file name in case the user does
	 * not set an extension for the file name.
	 *
	 * @param defaultExt default extension
	 * @see #getDefaultExtension()
	 */
	public void setDefaultExtension(String defaultExt) {
		this.defaultExt = defaultExt;
	}
	
	private native boolean showDialog(boolean mode, String fileNameLabel, 
			String okButtonLabel, String root, String fileName,
			String title, String defaultExt, int options, String[] filter,
			int filterIndex, long hWndParent);

	/**
	 * Returns an array representing the file filter. If no filter is
	 * specified the method returns {@code null}.
	 *
	 * @return filter
	 * @see #setFilter(String[]) 
	 */
	public String[] getFilter() {
		return filter != null ? Arrays.copyOf(filter, filter.length) : null;
	}

	/**
	 * <pre>
	 * 	setFilter(new String[] {
	 * 	      "Archivo de texto (*.txt)", "*.txt",
	 * 	      "Archivo de imagen (*.jpg *.png)", "*.jpg;*.png",
	 * 	      "Todo los archivos", "*.*"
	 * 	});
	 * </pre>
	 */


	/**
	 * Allows you to add a file filter in the dialog box.
	 * <p>
	 * Every file matching the extension you specified in the filter will
	 * be visible in the dialog box.
	 * <p>
	 * The array must be formed first with a description of the extension
	 * and then of the extension, which means that the first element of the
	 * array is the description and the second element is the extension and
	 * so on with the third element being the description and the fourth
	 * element the extension. This means that the length of the array must
	 * be divisible by 2, if this is not the case the method will throw an
	 * exception of type {@link IllegalArgumentException}, also no element
	 * must be null.
	 *
	 * <pre>
	 * 	setFilter(new String[] {
	 * 	    "Text file (*.txt)", "*.txt",
	 * 	    "Image file (*.jpg *.png)", "*.jpg;*.png",
	 * 	    "All Files", "*.*"
	 * 	});
	 * </pre>
	 * @param filter file filter
	 * @exception IllegalArgumentException The array is not correct
	 * @exception NullPointerException An array element is {@code null}
	 * @see #getFilter()
	 */
	public void setFilter(String[] filter) {
		filterIndex = 0;
		if (filter == null)
			this.filter = null;
		else if ((filter.length % 2) != 0)
			throw new IllegalArgumentException();
		else if (Arrays.stream(filter).noneMatch(element -> element == null))
			throw new NullPointerException("no element of the array can be null");
		else
			this.filter = Arrays.copyOf(filter, filter.length);
	}

	/**
	 * Returns the index of the priority filter.
	 *
	 * @return filter index
	 * @see #setFilterIndex(int) 
	 */
	public int getFilterIndex() {
		return filterIndex;
	}

	/**
	 * Tells the dialog box to prioritize a specific filter using the index.
	 * <p>
	 * The index cannot be a negative number or greater than the index
	 * of the array entered from the {@link #setFilter(String[])} method,
	 * if that is the case an {@link IllegalArgumentException} is thrown.
	 *
	 * @param filterIndex filter index
	 */
	public void setFilterIndex(int filterIndex) {
		if (filterIndex < 0 || (filter != null && filterIndex > (filter.length / 2)))
			throw new IllegalArgumentException();
		this.filterIndex = filterIndex;
	}

	/**
	 * Enables the multiple selection option
	 * <p>
	 * If this option is enabled, you must call method {@link #getSelectedFiles()}
	 * after the dialog box has ended.
	 *
	 * @param multi multi file selection
	 * @see #isMultiSelected()
	 */
	public void setMultiSelected(boolean multi) {
		options = maskBit(options, 0x200, multi);
	}

	/**
	 * Returns a boolean value indicating whether multiple selection is
	 * enabled in the dialog box.
	 *
	 * @return is multi selected
	 * @see #setMultiSelected(boolean) 
	 */
	public boolean isMultiSelected() {
		return (options & 0x200) != 0;
	}

	/**
	 * Allows you to select folders in the system.
	 * <p>
	 * This option is only available from version 6 of Windows, if you
	 * call this function in a lower version the method will
	 * throw an {@link UnsupportedOperationException}.If this option is
	 * not supported you must use the {@link FolderBrowserDialog} class.
	 *
	 * @param selectedFolders Selected folders
	 * @exception UnsupportedOperationException system version not supported
	 */
	public void setSelectedFolders(boolean selectedFolders) {
		if (!isSupportWinVersion)
			throw new UnsupportedOperationException("Not supported OS version: " +
					System.getProperty("os.version"));
		options = maskBit(options, 0x20, selectedFolders);
	}

	/**
	 * returns {@code true} if the option to select folders is enabled.
	 *
	 * @return is enabled option
	 * @see #setSelectedFolders(boolean) 
	 */
	public boolean isSelectedFolders() {
		return (options & 0x20) != 0;
	}

	/**
	 * Returns the label text that is located next to the text box
	 * where the file name is displayed.
	 *
	 * @return text
	 * @see #setFileNameLabel(String)
	 */
	public String getFileNameLabel() {
		return fileNameLabel;
	}

	/**
	 * Sets the label text that is located next to the text box where
	 * the file name is displayed.
	 *
	 * @param fileNameLabel Text label
	 * @see #getFileNameLabel()
	 */
	public void setFileNameLabel(String fileNameLabel) {
		this.fileNameLabel = fileNameLabel;
	}

	/**
	 * Returns the text of the "Ok" button. If the button text was not
	 * specified with a call to the setOkButtonLabel method,
	 * the method returns {@code null}.
	 *
	 * @return text
	 * @see #setOkButtonLabel
	 */
	public String getOkButtonLabel() {
		return okButtonLabel;
	}

	/**
	 * Set the text of the "Ok" button.
	 *
	 * @param okButtonLabel Button text
	 * @see #getOkButtonLabel()
	 */
	public void setOkButtonLabel(String okButtonLabel) {
		this.okButtonLabel = okButtonLabel;
	}

	/**
	 * By enabling this option the dialog box will now be able to
	 * show the files and folders that are hidden in the system.
	 *
	 * @param forceShowHidden force viewing of hidden files
	 * @see #isForceShowHidden()
	 */
	public void setForceShowHidden(boolean forceShowHidden) {
		options = maskBit(options, 0x10000000, forceShowHidden);
	}

	/**
	 * Returns {@code true} if this option is enabled.
	 *
	 * @return is this option enabled?
	 * @see #setForceShowHidden(boolean)
	 */
	public boolean isForceShowHidden() {
		return (options & 0x10000000) != 0;
	}
}
