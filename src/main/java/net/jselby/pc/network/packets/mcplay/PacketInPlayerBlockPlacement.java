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
 * Created by James on 2/16/14.
 */
public class PacketInPlayerBlockPlacement extends Packet {
    public int x;
    public int y; /*unsigned byte*/
    public int z;
    public byte direction;


    @Override
    public void write(Client cl, StandardOutput out) throws IOException {}

    @Override
    public void read(Client cl, StandardInput in) throws IOException {
        x = in.readInt();
        y = in.readUnsignedByte();
        z = in.readInt();
        direction = in.readByte();
        // TODO: Implement this
    }

    @Override
    public int getId() {
        return 0x08;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }

    /**
     * 0 = DOWN
     * 1 = UP
     * 2 = NORTH
     * 3 = SOUTH
     * 4 = WEST
     * 5 = EAST
     */
    public enum Directions {
        DOWN, UP, NORTH, SOUTH, WEST, EAST;

        public int getXAdd() {
            switch (this) {
                case DOWN:
                    return 0;
                case UP:
                    return 0;
                case NORTH:
                    return 0;
                case SOUTH:
                    return 0;
                case WEST:
                    return -1;
                case EAST:
                    return 1;
            }
            return 0;
        }

        public int getYAdd() {
            switch (this) {
                case DOWN:
                    return -1;
                case UP:
                    return 1;
                case NORTH:
                    return 0;
                case SOUTH:
                    return 0;
                case WEST:
                    return 0;
                case EAST:
                    return 0;
            }
            return 0;
        }

        public int getZAdd() {
            switch (this) {
                case DOWN:
                    return 0;
                case UP:
                    return 0;
                case NORTH:
                    return -1;
                case SOUTH:
                    return 1;
                case WEST:
                    return 0;
                case EAST:
                    return 0;
            }
            return 0;
        }
    }
}
