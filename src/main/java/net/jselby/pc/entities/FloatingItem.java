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

import net.jselby.pc.Entity;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.network.Client;
import net.jselby.pc.network.Slot;
import net.jselby.pc.network.packets.mcplay.PacketOutCollectItem;
import net.jselby.pc.network.packets.mcplay.PacketOutSpawnObject;
import net.jselby.pc.world.Block;
import net.jselby.pc.world.World;

import java.io.IOException;
import java.io.Serializable;

/**
 * Represents a floating itemstack. Simply stores a block id, and a data argument, and has the ability to
 * show itself to a client.
 *
 * @author j_selby
 */
public class FloatingItem extends Entity {
    private int blockId;
    private byte data;
    public int count = 1;

    public FloatingItem(int entityId, World w, double x, double y, double z, int blockId, byte data) {
        this.id = entityId;
        this.world = w;
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockId = blockId;
        this.data = data;
    }

    public int getId() {
        return blockId;
    }

    public byte getData() {
        return data;
    }

    @Override
    public void onApproach(Client c) {
        // Client picks up item
        if (c.pickUpItem(this)) {
            // Make item fly towards player
            PacketOutCollectItem collect = new PacketOutCollectItem();
            collect.collectedEntityId = id;
            collect.collectorEntityId = c.id;
            PoweredCube.getInstance().distributePacket(collect);
            world.removeEntity(this);
        }
    }

    @Override
    public void showToClient(Client c) {
        PacketOutSpawnObject packet = new PacketOutSpawnObject();
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.entityId = id;
        packet.slot = new Slot(blockId, count, data);
        packet.pitch = 12;
        packet.yaw = 12;
        packet.type = PacketOutSpawnObject.ObjectType.ITEM_STACK;
        packet.speedX = this.xSpeed;
        packet.speedY = this.ySpeed;
        packet.speedZ = this.zSpeed;
        c.writePacket(packet);
    }

    @Override
    public void tick() {
        // Keep falling until on solid
        Block b = world.getBlockAt((int)Math.floor(x), (int)(y - 1), (int)Math.floor(z));
        if (b != null && b.getTypeId() != 0) {
            // Don't fall
        } else {
            y -= 0.5;
        }
    }
}
