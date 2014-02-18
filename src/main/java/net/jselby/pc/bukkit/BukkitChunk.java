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

package net.jselby.pc.bukkit;

import net.jselby.pc.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;

import java.io.Serializable;

/**
 * Created by James on 2/2/14.
 */
public class BukkitChunk implements org.bukkit.Chunk, Serializable {
    private Chunk c;

    public BukkitChunk(Chunk c) {
        this.c = c;
    }

    @Override
    public int getX() {
        return c.getX();
    }

    @Override
    public int getZ() {
        return c.getZ();
    }

    @Override
    public World getWorld() {
        return c.getWorld().getBukkitWorld();
    }

    @Override
    public Block getBlock(int i, int i2, int i3) {
        return c.getWorld().getBukkitWorld().getBlockAt(i + (c.getX() * 16), i2, i3 + (c.getZ() * 16));
    }

    @Override
    public ChunkSnapshot getChunkSnapshot() {
        return null;
    }

    @Override
    public ChunkSnapshot getChunkSnapshot(boolean b, boolean b2, boolean b3) {
        return null;
    }

    @Override
    public Entity[] getEntities() {
        return new Entity[0];
    }

    @Override
    public BlockState[] getTileEntities() {
        return new BlockState[0];
    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public boolean load(boolean b) {
        return false;
    }

    @Override
    public boolean load() {
        return false;
    }

    @Override
    public boolean unload(boolean b, boolean b2) {
        return false;
    }

    @Override
    public boolean unload(boolean b) {
        return false;
    }

    @Override
    public boolean unload() {
        return false;
    }
}
