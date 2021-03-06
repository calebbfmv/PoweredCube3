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

package net.jselby.pc.world;

import net.jselby.pc.PoweredCube;
import net.jselby.pc.blocks.Material;
import net.jselby.pc.bukkit.BukkitBlock;
import net.jselby.pc.network.packets.mcplay.PacketOutBlockChange;
import net.jselby.pc.world.Chunk;
import net.jselby.pc.world.World;

import java.io.Serializable;

/**
 * Created by James on 2/1/14.
 */
public class Block implements Serializable {
    private int id;
    private byte data;
    public final int x;
    public final int y;
    public final int z;
    public final Chunk chunk;

    public Block(Material mat, Chunk chunk, int x, int y, int z) {
        this(mat.getId(), chunk, (byte) 0, x, y, z);
    }

    @Deprecated
    public Block(int id, Chunk chunk, int x, int y, int z) {
        this(id, chunk, (byte) 0, x, y, z);
    }

    @Deprecated
    public Block(int id, Chunk chunk, byte data, int x, int y,
                     int z) {
        this.id = id;
        this.data = data;
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk;
    }

    @SuppressWarnings("deprecation")
    public Block(Material id, Chunk chunk, byte data, int x,
                     int y, int z) {
        this(id.getId(), chunk, data, x, y, z);
    }

    public org.bukkit.block.Block getBukkitBlock() {
        return  new BukkitBlock(this);
    }

    public void setTypeId(int typeId, boolean update) {
        id = typeId;
        chunk.blocks[x - (chunk.getX() * 16)][y][z - (chunk.getZ() * 16)] = id;
        if (update) {
            update();
        }
    }

    public void setTypeId(int typeId) {
        setTypeId(typeId, true);
    }

    public void setData(byte data, boolean update) {
        this.data = data;
        chunk.data[x - (chunk.getX() * 16)][y][z - (chunk.getZ() * 16)] = this.data;
        if (update) {
            update();
        }
    }

    public void setData(byte data) {
        setData(data, true);
    }

    public void breakNaturally() {
        // Make sure there's nothing above us, that falls
        Block b = chunk.world.getBlockAt(x, y + 1, z);
        if (b != null && b.getTypeId() != 0 && b.hasGravity()) {
            b.breakNaturally();
        }
        setTypeId(0);
        setData((byte)0);
    }

    public boolean hasGravity() {
        Material mat = Material.getMaterial(id);
        if (mat == Material.LONG_GRASS) {
            return true;
        }
        return mat.hasGravity();
    }

    public void update() {
        PacketOutBlockChange blockChange = new PacketOutBlockChange();
        blockChange.x = x;
        blockChange.y = y;
        blockChange.z = z;
        blockChange.blockId = id;
        blockChange.data = (int)data;
        PoweredCube.getInstance().distributePacket(blockChange);
    }

    public int getTypeId() {
        return id;
    }

    public byte getData() {
        return data;
    }
}
