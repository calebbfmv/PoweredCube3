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

/**
 * Created by James on 1/31/14.
 */
public class PacketWrapper {
    private Class<? extends Packet> packetClass;
    private int id;
    private PacketDefinitions.State state;
    private boolean registerForIncoming;

    public PacketWrapper(Class<? extends Packet> packetClass) throws IllegalAccessException, InstantiationException {
        this.packetClass = packetClass;
        Packet packet = packetClass.newInstance();
        this.id = packet.getId();
        this.state = packet.getState();
        this.registerForIncoming = packet.registerForIncoming();
    }

    public int getId() {
        return id;
    }

    public PacketDefinitions.State getState() {
        return state;
    }

    public Class<? extends Packet> getPacket() {
        return packetClass;
    }

    public boolean getRegisterForIncoming() {
        return registerForIncoming;
    }
}
