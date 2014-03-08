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
 * Created by James on 3/8/14.
 */
public class PacketInHeldItemChange extends Packet {
    public short slot;

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {}

    @Override
    public void read(Client cl, StandardInput in) throws IOException {
        slot = in.readShort();
    }

    @Override
    public int getId() {
        return 0x09;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }
}
