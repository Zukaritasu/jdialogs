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
 * 
 * 
 * @author Zukaritasu
 *
 */
public class WindowsException extends RuntimeException {
	
	private static final long serialVersionUID = 2644189949941497397L;
	private int code;
	private String message;
	
	public WindowsException(String message) {
		this.message = message;
	}
	
	public WindowsException(int code) {
		if (code == 0) 
			throw new IllegalArgumentException("this error code is invalid 0");
		this.code = code;
		this.message = getFormatMessage(code);
	}
	
	public int getCode() {
		return code;
	}
	
	@Override
	public String getMessage() {
        return message;
    }
	
	private static native String getFormatMessage(int code);
}