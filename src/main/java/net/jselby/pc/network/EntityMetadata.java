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

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by James on 3/8/14.
 */
public class EntityMetadata extends WritableObject {
    public HashMap<EntityPosition, WritableObject> entries = new HashMap<EntityPosition, WritableObject>();

    @Override
    public void write(StandardOutput out) throws IOException {
        for (Map.Entry<EntityPosition, WritableObject> ent : entries.entrySet()) {
            BitSet bits = new BitSet();

            bits.set(8, true);

            boolean[] index = toBits(ent.getKey().index);

            for (int i = 0; i < 5; i++) {
                bits.set(i, index[i]);
            }

            boolean[] type = toBits((byte) ent.getKey().type.toByte());

            for (int i = 0; i < 3; i++) {
                bits.set(i + 5, type[i]);
            }

            out.writeByte(bits.toByteArray()[0]);
            ent.getValue().write(out);
        }

        out.writeByte(0x7F); // Stop reading
    }

    public static boolean[] toBits(final byte b) {
        return new boolean[] {
                (b &    1) != 0,
                (b &    2) != 0,
                (b &    4) != 0,
                (b &    8) != 0,
                (b & 0x10) != 0,
                (b & 0x20) != 0,
                (b & 0x40) != 0,
                (b & 0x80) != 0
        };
    }
    public enum Types {
        BYTE, SHORT, INT, FLOAT, STRING, SLOT;

        public byte toByte() {
            switch(this) {
                case BYTE:
                    return 0;
                case SHORT:
                    return 1;
                case INT:
                    return 2;
                case FLOAT:
                    return 3;
                case STRING:
                    return 4;
                case SLOT:
                    return 5;
            }
            return 0x00;
        }
    }
}
