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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by James on 2/18/14.
 */
public class PacketOutSpawnObject extends Packet {
    public int entityId; /*varInt*/
    public ObjectType type;
    public double x;
    public double y;
    public double z;
    public byte pitch;
    public byte yaw;

    // Object Data:
    // All Objects:
    public short speedX;
    public short speedY;
    public short speedZ;
    // Floating Items:
    public Slot slot;

    @Override
    public void write(Client cl, StandardOutput out) throws IOException {
        out.writeVarInt(entityId);
        System.out.println(entityId);
        out.writeByte((byte)type.getId());
        System.out.println((byte)type.getId());
        out.writeInt((int) (x * 32));
        System.out.println((int) (x * 32));
        out.writeInt((int) (y * 32));
        System.out.println((int) (y * 32));
        out.writeInt((int) (z * 32));
        System.out.println((int) (z * 32));
        out.writeByte(pitch);
        System.out.println(pitch);
        out.writeByte(yaw);
        System.out.println(yaw);
        byte[] data = new byte[]{};

        // Data:
        if (type == ObjectType.ITEM_STACK) {
            // Get byte[] from slot
            ByteArrayOutputStream in = new ByteArrayOutputStream();
            DataOutputStream outWrapper = new DataOutputStream(in);
            StandardOutput outArray = new StandardOutput(outWrapper);
            slot.write(outArray);

            // write speed
            outArray.writeShort(speedX);
            outArray.writeShort(speedY);
            outArray.writeShort(speedZ);

            outWrapper.flush();
            in.flush();
            data = in.toByteArray();
        }

        out.writeInt(data.length);
        System.out.println(data.length);
        if (data.length > 0) {
            out.writeBytes(data);
            System.out.println(new String(data));
        }
    }

    @Override
    public void read(Client cl, StandardInput in) throws IOException {}

    @Override
    public int getId() {
        return 0x0E;
    }

    @Override
    public PacketDefinitions.State getState() {
        return PacketDefinitions.State.PLAY;
    }

    public enum ObjectType {
        BOAT,
        ITEM_STACK,
        MINECART,
        @Deprecated
        MINECART_STORAGE,
        @Deprecated
        MINECART_POWERED,
        ACTIVATED_TNT,
        ENDER_CRYSTAL,
        ARROW,
        SNOWBALL,
        EGG,
        FIREBALL,
        FIRECHARGE,
        THROWN_ENDERPEARL,
        WITHER_SKULL,
        FALLING_OBJECTS,
        ITEM_FRAME,
        EYE_OF_ENDER,
        THROWN_POTION,
        FALLING_DRAGON_EGG,
        THROWN_EXP_BOTTLE,
        FISHING_FLOAT;

        public int getId() {
            switch(this) {
                case BOAT:
                    return 1;
                case ITEM_STACK:
                    return 2;
                case MINECART:
                    return 10;
                /**
                 * NOTE: Do not use in 1.7 and up -
                 * Minecarts now take data argument
                 */
                case MINECART_STORAGE:
                    return 11;
                /**
                 * NOTE: Do not use in 1.7 and up -
                 * Minecarts now take data argument
                 */
                case MINECART_POWERED:
                    return 12;
                case ACTIVATED_TNT:
                    return 50;
                case ENDER_CRYSTAL:
                    return 51;
                case ARROW:
                    return 60;
                case SNOWBALL:
                    return 61;
                case EGG:
                    return 62;
                case FIREBALL:
                    return 63;
                case FIRECHARGE:
                    return 64;
                case THROWN_ENDERPEARL:
                    return 65;
                case WITHER_SKULL:
                    return 66;
                case FALLING_OBJECTS:
                    return 70;
                case ITEM_FRAME:
                    return 71;
                case EYE_OF_ENDER:
                    return 72;
                case THROWN_POTION:
                    return 73;
                case FALLING_DRAGON_EGG:
                    return 74;
                case THROWN_EXP_BOTTLE:
                    return 75;
                case FISHING_FLOAT:
                    return 90;
            }
            return -1;
        }
    }
}
