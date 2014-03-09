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
 * Created by James on 3/9/14.
 */
public class PacketOutSpawnPlayer extends Packet {
    public int entityId; // Var-int
    public String UUID;
    public String name;
    public double x; // Fixed point, done in write() method
    public double y; // Fixed point, done in write() method
    public double z; // Fixed point, done in write() method
    public byte yaw;
    public byte pitch;
    public short currentItem;
    public EntityMetadata metadata = new EntityMetadata();

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {
        out.writeVarInt(entityId);
        out.writeString(UUID);
        out.writeString(name);
        out.writeInt((int) (x * 32));
        out.writeInt((int) (y * 32));
        out.writeInt((int) (z * 32));
        out.writeByte(yaw);
        out.writeByte(pitch);
        out.writeShort(currentItem);
        metadata.write(out);
    }

    @Override
    public void read(Client cl, StandardInput in) throws IOException {}

    @Override
    public int getId() {
        return 0x0C;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }
}
