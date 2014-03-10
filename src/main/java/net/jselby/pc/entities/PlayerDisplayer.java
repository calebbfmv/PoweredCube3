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

package net.jselby.pc.entities;

import net.jselby.pc.PoweredCube;
import net.jselby.pc.network.*;
import net.jselby.pc.network.packets.mcplay.PacketOutEntityHeadLook;
import net.jselby.pc.network.packets.mcplay.PacketOutSpawnPlayer;

/**
 * Created by James on 3/9/14.
 */
public class PlayerDisplayer {
    public static void showPlayer(ConnectedClient client, Client toWhom) {
        PacketOutSpawnPlayer spawnPlayer = new PacketOutSpawnPlayer();
        spawnPlayer.entityId = client.id;
        spawnPlayer.name = client.displayName;

        spawnPlayer.yaw = (byte) ((Math.toRadians(client.yaw) * 10 * 4));
        spawnPlayer.pitch = (byte) (client.pitch);
        Slot s = client.inv.getSlot(client.inv.getSize() - 9 + client.selectedSlot);
        if (s != null) {
            spawnPlayer.currentItem = (short) s.itemId;
            System.out.println("Item: " + spawnPlayer.currentItem);
        }
        spawnPlayer.x = client.x;
        spawnPlayer.y = (client.y - 1.5);
        spawnPlayer.z = client.z;
        spawnPlayer.UUID = client.uuid.toString();

        // Entity
        // Special combination byte
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.BYTE, (byte)0), new WritableByte((byte)0));
        // Air
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.SHORT, (byte)1), new WritableShort((short)20));

        // Living entity
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.FLOAT, (byte)6), new WritableFloat(client.health));
        // Potion effect
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.INT, (byte)7), new WritableInt(0));
        // Ambient potion effect
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.BYTE, (byte)8), new WritableByte((byte)0));
        // Arrows in entity
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.BYTE, (byte)9), new WritableByte((byte)0));
        // Display name
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.STRING, (byte)10), new WritableString(client.displayName));
        // Always display name tag
        spawnPlayer.metadata.entries.put(
                new EntityPosition(EntityMetadata.Types.BYTE, (byte)11), new WritableByte((byte)1));

        toWhom.writePacket(spawnPlayer);

        PacketOutEntityHeadLook look = new PacketOutEntityHeadLook();
        look.entityId = client.id;
        look.pitch = client.pitch;
        look.yaw = client.yaw;

        toWhom.writePacket(look);
    }
}
