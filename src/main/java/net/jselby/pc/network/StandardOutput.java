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

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.mahout.math.Varint;

/**
 * StandardOutput class, modified from mc-protocol-lib
 */
public class StandardOutput {
	private DataOutputStream out;

    public StandardOutput(DataOutputStream out) {
        this.out = out;
    }

    public void setByteBuf(DataOutputStream out) {
        this.out = out;
    }

	public void startPacket(Packet packet) throws IOException {
		// Write packet id
		writeVarInt(packet.getId());
	}

	public DataOutputStream getStream() {
		return out;
	}

	public void writeBoolean(boolean b) throws IOException {
		this.writeByte(b ? 1 : 0);
	}

	public void writeByte(int b) throws IOException {
		this.out.write(b);
	}

	public void writeShort(int s) throws IOException {
		this.writeByte((byte) ((s >>> 8) & 0xFF));
		this.writeByte((byte) ((s >>> 0) & 0xFF));
	}

	public void writeChar(int c) throws IOException {
		this.writeByte((byte) ((c >>> 8) & 0xFF));
		this.writeByte((byte) ((c >>> 0) & 0xFF));
	}

	public void writeInt(int i) throws IOException {
		this.writeByte((byte) ((i >>> 24) & 0xFF));
		this.writeByte((byte) ((i >>> 16) & 0xFF));
		this.writeByte((byte) ((i >>> 8) & 0xFF));
		this.writeByte((byte) ((i >>> 0) & 0xFF));
	}

	public void writeLong(long l) throws IOException {
		this.writeByte((byte) (l >>> 56));
		this.writeByte((byte) (l >>> 48));
		this.writeByte((byte) (l >>> 40));
		this.writeByte((byte) (l >>> 32));
		this.writeByte((byte) (l >>> 24));
		this.writeByte((byte) (l >>> 16));
		this.writeByte((byte) (l >>> 8));
		this.writeByte((byte) (l >>> 0));
	}

	public void writeFloat(float f) throws IOException {
		this.writeInt(Float.floatToIntBits(f));
	}

	public void writeDouble(double d) throws IOException {
		this.writeLong(Double.doubleToLongBits(d));
	}

	public void writeBytes(byte b[]) throws IOException {
		this.writeBytes(b, b.length);
	}

	public void writeBytes(byte b[], int length) throws IOException {
		this.out.write(b, 0, length);
	}

	public void writeString(String s) throws IOException {
		if (s == null) {
			throw new IllegalArgumentException("String cannot be null!");
		}

		int len = s.length();
		if (len >= 65536) {
			throw new IllegalArgumentException("String too long.");
		}

		this.writeVarInt(len);
		for (int i = 0; i < len; ++i) {
			this.writeByte(s.charAt(i));
		}
	}

	public void writeUnsignedByte(int b) throws IOException {
		this.out.writeByte(b);
	}

	public void writeVarInt(int number) throws IOException {
		Varint.writeUnsignedVarInt(number, (DataOutput) out);
	}

	public void writeMetadata(MetadataObject data[]) throws IOException {
		for (MetadataObject obj : data) {
			int header = (obj.getType() << 5 | obj.getType() & 0x1f) & 0xff;
			this.writeByte(header);
			switch (obj.getType()) {
			case MetadataObject.Type.BYTE:
				this.writeByte((Byte) obj.getValue());
				break;
			case MetadataObject.Type.SHORT:
				this.writeShort((Short) obj.getValue());
				break;
			case MetadataObject.Type.INT:
				this.writeInt((Integer) obj.getValue());
				break;
			case MetadataObject.Type.FLOAT:
				this.writeFloat((Float) obj.getValue());
				break;
			case MetadataObject.Type.STRING:
				this.writeString((String) obj.getValue());
				break;
			}
		}

		this.writeByte(127);
	}
}
