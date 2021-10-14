//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-
// - Zukaritasu
// - Copyright (c) 2021
// - Fecha 2012-11-01
// - Nombre de archivo FontDialog.java
//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-


package org.zuky.dialogs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Window;

/**
 * Esta clase representa el selector de fuentes de Windows que
 * cuenta con una lista de fuentes y opciones para darle tamaño y
 * estilos a la fuente seleccionada, además cuenta con la opción para
 * darle efectos al texto {@link #setEnabledEffects(boolean)} de
 * muestra como asignarle un color {@link #setColor(Color)}, subrayado
 * y tachado del texto.
 * <p>
 * Las fuentes que aparecen en la lista son las fuentes que tiene el
 * sistema operativo instalado. Cuando se crea el cuadro de dialogo
 * se necesita un fuente para inicializar las cajas de texto con el
 * nombre de la fuente, los estilos y el tamaño de la fuente, para
 * ello se puede llamar el método {@link #setFont(Font)} para pasarle
 * la fuente de inicialización o desde el constructor
 * {@link #FontDialog(Font)}, en todo caso si no se especifico una
 * fuente el cuadro de dialogo mostrara las casillas vacías y solo
 * tocara seleccionar e ir dándole los estilos y el tamaño
 * 
 * @author Zukaritasu
 *
 */
public class FontDialog extends CommonDialog {

	/**
	 * La fuente que seleccionada en el cuadro de dialogo, ademas
	 * también representa la fuente que se usa para inicializar el
	 * cuadro de dialogo
	 */
	private Font font;
	
	/**
	 * Las banderas del cuadro de dialogo
	 */
	private int flags = 0x00000001 | 0x00000040 | 0x00010000;
	
	/**
	 * El color que se uso para colorear el texto de muestra
	 */
	private int color;
	
	/**
	 * Crea una nueva instancia de la clase {@link FontDialog}
	 * para el cuadro de dialogo selector de fuentes
	 * 
	 * @see #FontDialog(Font)
	 */
	public FontDialog() {
	}
	
	/**
	 * Crea una nueva instancia de la clase {@link FontDialog}
	 * para el cuadro de dialogo selector de fuentes. El constructor
	 * recibe como parámetro la fuente que se usara para inicializar
	 * el cuadro de dialogo
	 * 
	 * @see #setFont(Font)
	 * @param font la fuente de inicialización 
	 */
	public FontDialog(Font font) {
		this.font = font;
	}

	@Override
	public synchronized boolean show(Window window) {
		long parent = getHandleWindow(window);
		if (font == null) {
			return showDialog(null, 0, 0, flags, color, 
					parent);
		} else {
			return showDialog(font.getName(),
					font.getStyle(), font.getSize(), flags, color,
					parent);
		}
	}
	
	private native boolean showDialog(String name, int style,
			int size, int flags, int color, long hwndParent);

	/**
	 * Retorna la fuente que fue seleccionada en el cuadro de
	 * dialogo. El método retorna <code>null</code> si la operación
	 * fue cancelada o en el caso que no se haya hecho un llamado
	 * previo al método {@link #setFont(Font)} para asociar la fuente
	 * de inicialización
	 * 
	 * @see #setFont(Font)
	 * @return la fuente seleccionada
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Asocia una fuente con la que se inicializara el cuadro de
	 * dialogo. Si el parámetro es <code>null</code> el cuadro de
	 * dialogo no mostrara un fuente de inicio y en el caso que la
	 * fuente no exista de igual forma no la mostrara
	 * 
	 * @param font la fuente de inicialización
	 */
	public void setFont(Font font) {
		this.font = font;
	}

	/**
	 * Habilita los efectos en el texto de muestra. Si los efectos
	 * están habilitados se podrá asignar un color al texto con un
	 * llamado al método {@link #setColor(Color)} además se puede
	 * hacer subrayado y tachado del texto
	 * <p>
	 * Para obtener el color que se selecciono para darle color al
	 * texto de muestra debe llamar el método {@link #getColor()}
	 * 
	 * @see #isEnabledEffects()
	 * @param effects habilitar los efectos
	 */
	public void setEnabledEffects(boolean effects) {
		flags = flag(flags, 0x00000100, effects);
	}

	/**
	 * Retorna <code>true</code> si los efectos están habilitados 
	 * de lo contrario retorna <code>false</code>. Por defecto los
	 * efectos se encuentran deshabilitados
	 * 
	 * @return estan habilitados los efectos
	 */
	public boolean isEnabledEffects() {
		return (flags & 0x00000100) != 0;
	}
	
	/**
	 * Retorna el color del texto de muestra que forma parte de los
	 * efectos del texto. Si aun no se ha asignado un color mediante
	 * un llamado previo al método {@link #setColor(Color)} el
	 * método retornara un color negro
	 * 
	 * @see #setColor(Color)
	 * @return el color del texto
	 */
	public Color getColor() {
		return new Color(color);
	}
	
	/**
	 * Asocia un color que será el color del texto de muestra. Si
	 * el parámetro es <code>null</code> el color es negro. Tenga
	 * en cuenta que el color del texto predeterminado es negro
	 * <p>
	 * El cuadro de dialogo no soporta un color <b>RGB</b> con
	 * transparencia. En el caso que el color tenga transparencia
	 * el cuadro de dialogo le quita la transparencia para solo
	 * obtener un color solido <b>RGB</b>
	 * 
	 * @see #getColor()
	 * @param color el color del texto de muestra
	 */
	public void setColor(Color color) {
		if (color == null) {
			this.color = 0;
		} else {
			this.color = color.getRGB();
		}
	}
}
