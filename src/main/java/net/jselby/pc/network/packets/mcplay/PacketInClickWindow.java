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
 * Created by James on 3/10/14.
 */
public class PacketInClickWindow extends Packet {
    public byte windowId;
    public short slot;
    public byte button;
    public short actionNumber;
    public byte mode;
    public InventoryAction action;

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {}

    @Override
    public void read(Client cl, StandardInput in) throws IOException {
        windowId = in.readByte();
        slot = in.readShort();
        button = in.readByte();
        actionNumber = in.readShort();
        mode = in.readByte();

        if (mode == 0) {
            if (button == 0) {
                action = InventoryAction.LEFT_CLICK;
            } else if (button == 1) {
                action = InventoryAction.RIGHT_CLICK;
            }
        } else if (mode == 1) {
            if (button == 0) {
                action = InventoryAction.SHIFT_LEFT_CLICK;
            } else if (button == 1) {
                action = InventoryAction.SHIFT_RIGHT_CLICK;
            }
        } else if (mode == 3) {
            if (button == 2) {
                action = InventoryAction.MIDDLE_CLICK;
            }
        } else if (mode == 6) {
            if (button == 0) {
                action = InventoryAction.DOUBLE_CLICK;
            }
        }

        if (action == null) {
            action = InventoryAction.OTHER;
        }
    }

    @Override
    public int getId() {
        return 0x0E;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }

    public enum InventoryAction {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_LEFT_CLICK, SHIFT_RIGHT_CLICK, DOUBLE_CLICK, MIDDLE_CLICK, OTHER
    }
}
