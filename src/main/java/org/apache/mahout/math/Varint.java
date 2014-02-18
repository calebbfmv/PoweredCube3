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

package org.apache.mahout.math;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.google.common.base.Preconditions;
import net.jselby.pc.network.StandardInput;

/**
 * <p>Encodes signed and unsigned values using a common variable-length
 * scheme, found for example in
 * <a href="http://code.google.com/apis/protocolbuffers/docs/encoding.html">
 * Google's Protocol Buffers</a>. It uses fewer bytes to encode smaller values,
 * but will use slightly more bytes to encode large values.</p>
 *
 * <p>Signed values are further encoded using so-called zig-zag encoding
 * in order to make them "compatible" with variable-length encoding.</p>
 */
public final class Varint {

    private Varint() {
    }

    /**
     * Encodes a value using the variable-length encoding from
     * <a href="http://code.google.com/apis/protocolbuffers/docs/encoding.html">
     * Google Protocol Buffers</a>. It uses zig-zag encoding to efficiently
     * encode signed values. If values are known to be nonnegative,
     * {@link #writeUnsignedVarLong(long, DataOutput)} should be used.
     *
     * @param value value to encode
     * @param out to write bytes to
     * @throws IOException if {@link DataOutput} throws {@link IOException}
     */
    public static void writeSignedVarLong(long value, DataOutput out) throws IOException {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
        writeUnsignedVarLong((value << 1) ^ (value >> 63), out);
    }

    /**
     * Encodes a value using the variable-length encoding from
     * <a href="http://code.google.com/apis/protocolbuffers/docs/encoding.html">
     * Google Protocol Buffers</a>. Zig-zag is not used, so input must not be negative.
     * If values can be negative, use {@link #writeSignedVarLong(long, DataOutput)}
     * instead. This method treats negative input as like a large unsigned value.
     *
     * @param value value to encode
     * @param out to write bytes to
     * @throws IOException if {@link DataOutput} throws {@link IOException}
     */
    public static void writeUnsignedVarLong(long value, DataOutput out) throws IOException {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            out.writeByte(((int) value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte((int) value & 0x7F);
    }

    /**
     * @see #writeSignedVarLong(long, DataOutput)
     */
    public static void writeSignedVarInt(int value, DataOutput out) throws IOException {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
        writeUnsignedVarInt((value << 1) ^ (value >> 31), out);
    }

    /**
     * @see #writeUnsignedVarLong(long, DataOutput)
     */
    public static void writeUnsignedVarInt(int value, DataOutput out) throws IOException {
        while ((value & 0xFFFFFF80) != 0L) {
            out.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        out.writeByte(value & 0x7F);
    }

    /**
     * @param in to read bytes from
     * @return decode value
     * @throws IOException if {@link DataInput} throws {@link IOException}
     * @throws IllegalArgumentException if variable-length value does not terminate
     *  after 9 bytes have been read
     * @see #writeSignedVarLong(long, DataOutput)
     */
    public static long readSignedVarLong(DataInput in, StandardInput input) throws IOException {
        long raw = readUnsignedVarLong(in, input);
        // This undoes the trick in writeSignedVarLong()
        long temp = (((raw << 63) >> 63) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1L << 63));
    }

    /**
     * @param in to read bytes from
     * @param input
     * @return decode value
     * @throws IOException if {@link DataInput} throws {@link IOException}
     * @throws IllegalArgumentException if variable-length value does not terminate
     *  after 9 bytes have been read
     * @see #writeUnsignedVarLong(long, DataOutput)
     */
    public static long readUnsignedVarLong(DataInput in, StandardInput input) throws IOException {
        long value = 0L;
        int i = 0;
        long b;
        while (((b = readByte(in, input)) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            Preconditions.checkArgument(i <= 63, "Variable length quantity is too long");
        }
        return value | (b << i);
    }

    /**
     * @throws IllegalArgumentException if variable-length value does not terminate
     *  after 5 bytes have been read
     * @throws IOException if {@link DataInput} throws {@link IOException}
     */
    public static int readSignedVarInt(DataInput in, StandardInput input) throws IOException {
        int raw = readUnsignedVarInt(in, input);
        // This undoes the trick in writeSignedVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
    }

    /**
     * @throws IllegalArgumentException if variable-length value does not terminate
     *  after 5 bytes have been read
     * @throws IOException if {@link DataInput} throws {@link IOException}
     */
    public static int readUnsignedVarInt(DataInput in, StandardInput input) throws IOException {
        int value = 0;
        int i = 0;
        int b;
        while (((b = readByte(in, input)) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            Preconditions.checkArgument(i <= 35, "Variable length quantity is too long");
        }
        return value | (b << i);
    }

    private static int readByte(DataInput in, StandardInput input) throws IOException {
        return in.readByte();
    }

}