//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo ColorDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Window;




/**
 * 
 * @author Zukaritasu
 *
 */
public class ColorDialog extends CommonDialog {

	/**
	 * Cantidad máxima de casillas que contiene la caja de colores
	 * 
	 * @see #getColors()
	 * @see #setColors(int[])
	 */
	public static final int MAX_CUSTOM_COLORS = 16;
	
	/**
	 * Este arreglo representa las asillas de colores del cuadro de
	 * dialogo
	 * 
	 * @see #getColors()
	 * @see #setColors(int[])
	 */
	private final int[] custom = new int[MAX_CUSTOM_COLORS];
	
	/**
	 * Color seleccionado
	 * 
	 * @see #getColor()
	 */
	private int color;
	
	/**
	 * Banderas del cuadro de dialogo.
	 * Las banderas se encuentran en la cabecera <commdlg.h>
	 * <p>
	 * Banderas usadas para inicializar esta variable:
	 * <pre>
	 * CC_RGBINIT
	 * CC_FULLOPEN
	 * CC_ENABLEHOOK
	 * </pre>
	 */
	protected int flags = 0x00000001 | 0x00000002 | 0x00000010;
	
	/**
	 * Crea una nueva instancia de esta clase
	 * 
	 * @see #ColorDialog(String)
	 * @see #ColorDialog(String, int[])
	 */
	public ColorDialog() {
		
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro un titulo que se asociara al cuadro de dialogo
	 * 
	 * @see   #ColorDialog()
	 * @see   #ColorDialog(String, int[])
	 * @param title
	 */
	public ColorDialog(String title) {
		setTitle(title);
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro un titulo que se asociara al cuadro de dialogo
	 * y un array de enteros que contiene los colores rgb que se
	 * implementaran en cada casilla
	 * 
	 * @see   #ColorDialog()
	 * @see   #ColorDialog(String)
	 * @param title
	 * @param colors
	 */
	public ColorDialog(String title, int[] colors) {
		setColors(colors);
		setTitle(title);
	}
	
	/**
	 * El método recibe un array de enteros que contiene los colores
	 * rgb que se implementaran en las 16 casillas de colores
	 * personalizados del cuadro de dialogo. Recuerda que el array
	 * no puede tener una longitud superior de 16 señalada por la
	 * constante {@link #MAX_CUSTOM_COLORS}
	 * <p>
	 * El cuadro de dialogo no tiene soporte para el canal alpha,
	 * solo tiene soporte para colores {@code RGB} sin transparencia
	 * 
	 * @exception IllegalArgumentException la longitud del array es
	 *            superior a {@link #MAX_CUSTOM_COLORS}
	 * @param colors
	 * @exception NullPointerException el parametro es {@code null}
	 */
	public void setColors(int[] colors) {
		if (colors.length > MAX_CUSTOM_COLORS)
			throw new IllegalArgumentException("length > MAX_CUSTOM_COLORS");
		System.arraycopy(colors, 0, custom, 0, colors.length);
	}
	
	/**
	 * Retorna un array de enteror con las 16 casillas de los colores
	 * personalizados del cuadro de dialogo
	 * 
	 * @return colors
	 */
	public int[] getColors() {
		int[] colors = new int[MAX_CUSTOM_COLORS];
		System.arraycopy(custom, 0, colors, 0, MAX_CUSTOM_COLORS);
		return colors;
	}
	
	/**
	 * Retorna el ultimo color que fue seleccionado en el cuadro de
	 * dialogo
	 * 
	 * @see    #getColors()
	 * @return color
	 */
	public int getColor() {
		return color;
	}

	@Override
	protected long hookProc(long hwnd, int msg, long wparam, 
			long lparam) {
		if (msg == 0x0110) // WM_INITDIALOG
			initializeDialog(hwnd);
		return 0L;
	}

	@Override
	public synchronized boolean show(Window window) {
		return showColorDialog(this, flags, getHandleWindow(window));
	}
	
	@Override
	protected void initializeDialog(long hwnd) {
		handle = hwnd;
		if (title != null) {
			setDialogTitle(title, handle);
		}
	}
	
	/**
	 * Muestra el cuadro de dialogo Selector de Colores de Windows.
	 * 
	 * @param dialog     cuadro de dialogo
	 * @param flags      banderas
	 * @param hwndParent ventana padre
	 * @return           resultado
	 */
	private static native boolean showColorDialog(
			ColorDialog dialog, int flags, long hwndParent);
}

