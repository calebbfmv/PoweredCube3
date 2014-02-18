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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;

import io.netty.buffer.ByteBuf;
import org.apache.mahout.math.Varint;

import javax.xml.crypto.Data;

public class StandardInput {

	private DataInputStream in;

    public StandardInput(DataInputStream in) {
        this.in = in;
    }

    public StandardInput() {
    }

	public boolean readBoolean() throws IOException {
		return this.readByte() == 1;
	}

	public byte readByte() throws IOException {
		return (byte) this.readUnsignedByte();
	}

	public int readUnsignedByte() throws IOException {
		int b = this.in.read();
		if (b < 0) {
			throw new EOFException();
		}

		return b;
	}

	public short readShort() throws IOException {
		return (short) this.readUnsignedShort();
	}

	public int readUnsignedShort() throws IOException {
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		return (ch1 << 8) + (ch2 << 0);
	}

	public char readChar() throws IOException {
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		return (char) ((ch1 << 8) + (ch2 << 0));
	}

	public int readInt() throws IOException {
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		int ch3 = this.readUnsignedByte();
		int ch4 = this.readUnsignedByte();
		return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
	}

	public long readLong() throws IOException {
		byte read[] = this.readBytes(8);
		return ((long) read[0] << 56) + ((long) (read[1] & 255) << 48)
				+ ((long) (read[2] & 255) << 40)
				+ ((long) (read[3] & 255) << 32)
				+ ((long) (read[4] & 255) << 24) + ((read[5] & 255) << 16)
				+ ((read[6] & 255) << 8) + ((read[7] & 255) << 0);
	}

	public float readFloat() throws IOException {
		return Float.intBitsToFloat(this.readInt());
	}

	public double readDouble() throws IOException {
		return Double.longBitsToDouble(this.readLong());
	}

    public byte[] readBytes(int length) throws IOException {
        byte b[] = new byte[length];
        if (length < 0) {
            throw new IndexOutOfBoundsException();
        }

        int n = 0;
        while (n < length) {
            int count = this.in.read(b, n, length - n);
            if (count < 0) {
                throw new EOFException();
            }

            n += count;
        }

        return b;
    }

    public String readString() throws IOException {
        int len = this.readVarInt();

        byte[] characters = new byte[len];
        for (int i = 0; i < len; i++) {
            characters[i] = this.readByte();
        }

        return new String(characters);
    }
	
	public int readVarInt() throws IOException {
		return Varint.readUnsignedVarInt(in, this);
	}

	public int available() throws IOException {
		return this.in.available();
	}

    public void setIn(DataInputStream in) {
        this.in = in;
    }
}