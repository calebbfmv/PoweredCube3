/*
 * PoweredCube3
 * Copyright (C) 2014 James
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.jselby.pc.network;

public class MetadataObject {
	private byte type = 0;
	private Object value;
	
	public MetadataObject(byte type, Object value) {
		this.type = type;
		this.value = value;
	}

	public byte getType() {
		return type;
	}
	
	public Object getValue() {
		return value;
	}

	public static class Type {
		 public static final byte BYTE = 0;
         public static final byte SHORT = 1;
         public static final byte INT = 2;
         public static final byte FLOAT = 3;
         public static final byte STRING = 4;
	}
}
