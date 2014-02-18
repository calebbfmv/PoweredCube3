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
import java.util.Locale;
import java.util.StringTokenizer;

/**
 * Created by James on 2/2/14.
 */
public class PacketInClientSettings extends Packet {
    public String locale;
    public byte viewDistance;
    public byte chatFlags;
    public boolean chatColours;
    public byte difficulty;
    public boolean showCapes;

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {}

    @Override
    public void read(Client cl, StandardInput in) throws IOException {
        locale = in.readString();
        viewDistance = in.readByte();
        chatFlags = in.readByte();
        chatColours = in.readBoolean();
        difficulty = in.readByte();
        showCapes = in.readBoolean();
    }

    @Override
    public int getId() {
        return 0x15;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }

    public Locale stringToLocale(String s) {
        StringTokenizer tempStringTokenizer = new StringTokenizer(s,"_");
        String l = null;
        String c = null;
        if(tempStringTokenizer.hasMoreTokens()) {
            l = (String) tempStringTokenizer.nextElement();
        }
        if(tempStringTokenizer.hasMoreTokens()) {
            c = (String) tempStringTokenizer.nextElement();
        }
        return new Locale(l.toLowerCase(),c.toLowerCase());
    }
}
