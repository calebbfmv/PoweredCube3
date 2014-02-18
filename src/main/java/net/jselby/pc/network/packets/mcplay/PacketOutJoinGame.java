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
 * Created by James on 2/1/14.
 */
public class PacketOutJoinGame extends Packet {
    public int entityId;
    public int gamemode; // unsigned byte
    public byte dimension;
    public int difficulty; // unsigned byte
    public int maxPlayers; // unsigned byte
    public String levelType;

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {
        out.writeInt(entityId);
        out.writeUnsignedByte(gamemode);
        out.writeByte(dimension);
        out.writeUnsignedByte(difficulty);
        out.writeUnsignedByte(maxPlayers);
        out.writeString(levelType);
    }

    @Override
    public void read(Client cl, StandardInput in) throws IOException {}

    @Override
    public int getId() {
        return 0x01;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }
}
