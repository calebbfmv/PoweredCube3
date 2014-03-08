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

/**
 * Created by James on 2/18/14.
 */
public class Slot extends WritableObject {
    public int position;
    public int itemId;
    public int itemCount = 1;
    public byte itemDamage;
    public byte[] nbt = new byte[0];

    public Slot(int position, int itemId, int itemCount, byte itemDamage) {
        super();
        this.position = position;
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.itemDamage = itemDamage;
    }

    public Slot(int itemId, int itemCount, byte itemDamage) {
        super();
        this.itemId = itemId;
        this.itemCount = itemCount;
        this.itemDamage = itemDamage;
    }

    @Override
    public void write(StandardOutput out) throws IOException {
        out.writeShort(itemId);
        if (itemId == -1) {
            return;
        }
        out.writeByte(itemCount);
        out.writeShort(itemDamage);
        if (nbt.length < 1) {
            out.writeShort(-1);
            return;
        }
        out.writeShort(nbt.length);
        out.writeBytes(nbt);
    }
}
