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

/**
 * Created by James on 2/2/14.
 */
public class PacketOutBlockChange extends Packet {
    public int x;
    public int y; /*unsigned byte*/
    public int z;
    public int blockId; /*varint*/
    public int data; /*unsigned byte*/

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {
        out.writeInt(x);
        out.writeUnsignedByte(y);
        out.writeInt(z);
        out.writeVarInt(blockId);
        out.writeUnsignedByte(data);
    }

    @Override
    public void read(Client cl, StandardInput in) throws IOException {

    }

    @Override
    public int getId() {
        return 0x23;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }
}
