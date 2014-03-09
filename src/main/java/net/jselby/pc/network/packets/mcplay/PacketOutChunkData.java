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

package net.jselby.pc.network.packets.mcplay;

import net.jselby.pc.network.*;

import java.io.IOException;
import java.util.BitSet;

/**
 * Created by James on 2/1/14.
 */
public class PacketOutChunkData extends Packet {
    public int x;
    public int z;
    public byte[] data;
    public boolean unload = false;

    public PacketOutChunkData() {}

    public PacketOutChunkData(int x, int z, boolean unload) {
        this.x = x;
        this.z = z;
        if (unload) {
            this.unload = true;
            data = new byte[0];
        } else {
            try {
                byte[] toData = PacketOutMapChunkBulk.generateChunk(x, z);
                if (toData == null) {
                    byte[] data = new byte[0];
                    return;
                }
                data = PacketOutMapChunkBulk.deflate(toData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void write(Client con, StandardOutput output) throws IOException {
        int chunkX = x;
        int chunkZ = z;
        boolean groundUpContinuous = true;
        BitSet primaryBitmap = new BitSet(256 / 16);
        if (unload) {
        } else {
            primaryBitmap.set(0, 256 / 16, true); // We include all chunks
        }
        BitSet addBitmap = new BitSet(256 / 16);
        if (unload) {
        } else {
            addBitmap.set(0, 256 / 16, false); // We don't have anything above
        }
        int dataSize = data.length;

        output.writeInt(chunkX);
        output.writeInt(chunkZ);
        output.writeBoolean(groundUpContinuous);
        output.writeShort(convert(primaryBitmap));
        output.writeShort(convert(addBitmap));
        output.writeInt(dataSize);
        output.writeBytes(data);
    }

    @Override
    public void read(Client cl, StandardInput in) throws IOException {}

    public static short convert(BitSet bits) {
        short value = 0;
        for (int i = 0; i < bits.length(); ++i) {
            value += bits.get(i) ? (1L << i) : 0L;
        }
        return value;
    }

    @Override
    public int getId() {
        return 0x21;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }
}
