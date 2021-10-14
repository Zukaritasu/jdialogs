//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo ColorDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Color;
import java.awt.Window;

/**
 * Esta clase representa el cuadro de dialogo selector de
 * colores de Windows en el cual se puede seleccionar un color de
 * los 48 colores predeterminados o elegir un color personalizado,
 * para ello puede llamar el método {@link #setFullOpen(boolean)}.
 * <p>
 * El cuadro de dialogo cuenta con 16 casillas en donde se podrán
 * guardar los colores personalizados por el usuario, para obtener
 * esos colores puede llamar el método {@link #getColors()} y para
 * definir previamente los colores personalizados 
 * {@link #setColors(int[])} o desde el constructor
 * {@link #ColorDialog(int[])}
 * 
 * @author Zukaritasu
 *
 */
public class ColorDialog extends CommonDialog {

	/**
	 * Indica la cantidad máxima de casillas de colores
	 * personalizados que tiene el cuadro de dialogo
	 */
	public static final int MAX_CUSTOM_COLORS = 16;
	
	/**
	 * Los colores personalizados
	 * 
	 * @see #setColors(int[])
	 * @see #getColors()
	 */
	private final int[] custom = new int[MAX_CUSTOM_COLORS];
	
	/**
	 * El color seleccionado
	 * 
	 * @see #getColor()
	 */
	private int color;
	
	/**
	 * Las banderas del cuadro de dialogo
	 */
	private int flags;

	/**
	 * Crea un nuevo cuadro de dialogo selector de colores
	 * {@link ColorDialog}
	 * 
	 * @see #ColorDialog(int[])
	 */
	public ColorDialog() {
	}
	
	/**
	 * Crea un nuevo cuadro de dialogo selector de colores
	 * {@link ColorDialog}. El constructor recibe como parámetro
	 * un array de enteros con los colores RGB que se mostraran
	 * en las casillas de colores personalizados
	 * 
	 * @param colors el arreglo de colores
	 * @exception IllegalArgumentException el array tiene una
	 *            longitud que supera {@link #MAX_CUSTOM_COLORS}
	 * @exception NullPointerException el parametro es {@code null}
	 */
	public ColorDialog(int[] colors) {
		setColors(colors);
	}

	@Override
	public synchronized boolean show(Window parent) {
		return showDialog(flags, getHandleWindow(parent));
	}
	
	/**
	 * Habilita el cuadro de dialogo para que se muestre con
	 * funciones extendidas donde se podrá seleccionar un color
	 * personalizado. {@code true} para habilitar las funciones
	 * extendidas y {@code false} para los contrario.
	 * <P>
	 * Por defecto no se muestran las funciones extendidas del
	 * cuadro de dialogo 
	 * 
	 * @see #isFullOpen()
	 * @param fullOpen usar funciones extendidas
	 */
	public void setFullOpen(boolean fullOpen) {
		flags = flag(flags, 0x00000002, fullOpen);
	}
	
	/**
	 * Retorna {@code true} si el cuadro de dialogo se muestra con
	 * funciones extendidas para definir un color personalizado,
	 * retorna {@code false} en caso contrario 
	 * 
	 * @see #setFullOpen(boolean)
	 * @return se muestra con funciones extendidas 
	 */
	public boolean isFullOpen() {
		return (flags & 0x00000002) != 0;
	}
	
	/**
	 * Retorna el ultimo color que fue seleccionado en el cuadro
	 * de dialogo 
	 * 
	 * @return el color
	 */
	public Color getColor() {
		return new Color(color);
	}
	
	/**
	 * Retorna un array de enteros con los colores <b>RGB</b> que
	 * fueron seleccionados en el cuadro de dialogo como parte de
	 * los colores personalizados 
	 * 
	 * @see #setColors(int[])
	 * @return el array con los colores <b>RGB</b>
	 */
	public int[] getColors() {
		int[] colors = new int[MAX_CUSTOM_COLORS];
		System.arraycopy(custom, 0, colors, 0, MAX_CUSTOM_COLORS);
		return colors;
	}
	
	private native boolean showDialog(int flags, long hwndParent);
	
	/**
	 * El método recibe como parámetro un array que contiene
	 * los colores <b>RGB</b> que se mostraran en las casillas de
	 * colores personalizados. Recuerda que el array no puede
	 * tener una longitud superior de la señalada por la constante
	 * {@link #MAX_CUSTOM_COLORS}
	 * <P>
	 * El cuadro de dialogo no soporta el canal alpha por ende los
	 * colores que contienen transparencia son ignorados y solo
	 * se muestra el color <b>RGB</b> sin transparencia
	 * 
	 * @see #getColors()
	 * @param colors el arreglo de colores <b>RGB</b>
	 * @exception IllegalArgumentException el array tiene una
	 *            longitud que supera {@link #MAX_CUSTOM_COLORS}
	 * @exception NullPointerException el parametro es {@code null}
	 */
	public void setColors(int[] colors) {
		if (colors.length > MAX_CUSTOM_COLORS)
			throw new IllegalArgumentException("length > 16");
		System.arraycopy(colors, 0, custom, 0, colors.length);
	}
}
