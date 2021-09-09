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
 * 
 * @author Zukaritasu
 *
 */
public class FontDialog extends CommonDialog {
	
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
	// TIPOS DE FUENTES
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		
	public static final int FONTTYPE_SIMULATED   = 0x8000;
	public static final int FONTTYPE_PRINTER     = 0x4000;
	public static final int FONTTYPE_SCREEN      = 0x2000;
	public static final int FONTTYPE_BOLD        = 0x0100;
	public static final int FONTTYPE_ITALIC      = 0x0200;
	public static final int FONTTYPE_REGULAR     = 0x0400;
		
	//-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

	/**
	 * Fuente seleccionada
	 */
	private Font font;
	
	/**
	 * Banderas del cuadro de dialogo.
	 * Las banderas se encuentran en la cabecera <commdlg.h>
	 * <p>
	 * Banderas usadas para inicializar esta variable:
	 * <pre>
	 * CF_SCREENFONTS
	 * CF_INITTOLOGFONTSTRUCT
	 * CF_FORCEFONTEXIST
	 * </pre>
	 */
	protected int flags = 0x00000001 | 0x00000040 | 0x00010000;
	
	/**
	 * Color del texto
	 */
	private int color;
	
	/**
	 * Tipo de fuente
	 * 
	 * @see #getFontType()
	 */
	private int fontType = FONTTYPE_REGULAR;
	
	/**
	 * Crea una nueva instancia de esta clase
	 * 
	 * @see #FontDialog(Font)
	 */
	public FontDialog() {
	}
	
	/**
	 * Crea una nueva instancia de esta clase. El constructor recibe
	 * como parámetro una fuente que se cargara en el cuadro de
	 * dialogo cuando se llame el método {@link #show(Window)}. 
	 * 
	 * @param font
	 */
	public FontDialog(Font font) {
		this.font = font;
	}

	
	@Override
	public synchronized boolean show(Window owner)  {
		long parent = getHandleWindow(owner);
		if (font == null) {
			return showFontDialog(this, null, 0, 0, flags, color, 
					parent);
		} else {
			return showFontDialog(this, font.getName(),
					font.getStyle(), font.getSize(), flags, color,
					parent);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>
	 * Este método no tiene ninguna función si no se le suma la
	 * bandera <code>CF_ENABLEHOOK</code> a la variable {@link #flags}
	 * 
	 */
	@Override
	protected void initializeDialog(long hwnd) {
		handle = hwnd;
		if (title != null) {
			setDialogTitle(title, handle);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Este método no tiene ninguna función si no se le suma la
	 * bandera <code>CF_ENABLEHOOK</code> a la variable {@link #flags}
	 * 
	 */
	@Override
	protected long hookProc(long hwnd, int msg, long wparam, 
			long lparam) {
		if (msg == 0x0110) // WM_INITDIALOG
			initializeDialog(hwnd);
		return 0L;
	}
	
	/**
	 * Retorna el tipo de fuente. Los tipos de fuentes están declaradas
	 * en esta clase con el prefijo {@code FONTTYPE_}
	 * <p>
	 * Por defecto el tipo de fuente es {@link #FONTTYPE_REGULAR}
	 * 
	 * @return fontType
	 */
	public int getFontType() {
		return fontType;
	}
	
	private static native boolean showFontDialog(FontDialog dialog, 
			String name, int style, int size, int flags, int color,
			long hwndParent);

	/**
	 * Retorna la fuente que fue seleccionada desde el cuadro de
	 * dialogo. Si la operación de selección de la fuente fue
	 * cancelada, el método retornara la fuente que se asocio con
	 * el llamado al método {@link #setFont(Font)} o desde el
	 * constructor {@link #FontDialog(Font)}
	 * 
	 * @return font
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * Asocia una fuente al cuadro de dialogo. Esta fuente será
	 * cargada en el cuadro de dialogo cuando se llame el método 
	 * {@link #show(Window)}. 
	 * 
	 * @param font la fuente
	 */
	public void setFont(Font font) {
		this.font = font;
	}
	
	/**
	 * Habilita los efectos del texto de muestra en el cuadro de
	 * dialogo. Estos efectos sirven para darle color al texto de
	 * muestra y un subrayado. Si los efecto están habilitados se
	 * puede llamar el método {@link #setColor(Color)} para asignar
	 * un color personalizado
	 * 
	 * @see   #isEnabledEffects()
	 * @param effects habilitar el efecto del texto
	 */
	public void enabledEffects(boolean effects) {
		flags = flag(flags, 0x00000100, effects);
	}
	
	/**
	 * Retorna {@code true} si los efectos del texto en el cuadro
	 * de dialogo están habilitado, en caso contrario {@code false}
	 * 
	 * @see    #enabledEffects(boolean)
	 * @return isEnabledEffects
	 */
	public boolean isEnabledEffects() {
		return (flags & 0x00000100) != 0;
	}

	/**
	 * Retorna el color RGB que fue utilizado para darle color al
	 * texto de muestra. Si los efectos están deshabilitados en
	 * método retornara el color que se asocio al control por un
	 * llamado previo al método {@link #setColor(Color)}
	 * 
	 * @see    #setColor(Color)
	 * @return color
	 */
	public Color getColor() {
		return new Color(color);
	}

	/**
	 * Asocia un color al cuadro de dialogo para que coloree el texto
	 * de muestra, no obstante los efectos deben estar habilitados
	 * para que el color surta efecto 
	 * {@link #enabledEffects(boolean)}
	 * <p>
	 * Si el parámetro es <code>null</code> el color del texto será
	 * negro aunque por defecto el color es negro
	 * 
	 * @see   #getColor()
	 * @param color el color del texto
	 */
	public void setColor(Color color) {
		if (color == null) {
			this.color = 0;
		} else {
			this.color = color.getRGB();
		}
	}
}
