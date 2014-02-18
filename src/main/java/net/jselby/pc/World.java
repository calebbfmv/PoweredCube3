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

package net.jselby.pc;

import net.jselby.pc.blocks.Material;
import net.jselby.pc.bukkit.BukkitWorld;
import net.jselby.pc.network.Slot;
import net.jselby.pc.network.packets.mcplay.PacketOutChunkData;
import net.jselby.pc.network.packets.mcplay.PacketOutSpawnObject;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by James on 2/1/14.
 */
public class World implements Serializable {
    public static final int INITIAL_SIZE = 1;

    private String name;
    public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
    public HashMap<String, PlayerCache> caches = new HashMap<String, PlayerCache>();
    private BukkitWorld thisAsBukkit;
    private Seed seed;

    public World(String name) {
        seed = new Seed(new Random().nextLong());
        this.name = name;
        System.out.println("Generating world \"" + name + "\" with seed " + seed + "...");
        for (int x = -INITIAL_SIZE; x <= INITIAL_SIZE; x++) {
            for (int y = -INITIAL_SIZE; y <= INITIAL_SIZE; y++) {
                chunks.add(new Chunk(this, x, y));
            }
        }
        thisAsBukkit = new BukkitWorld(this);
    }

    public World(String name, ArrayList<Chunk> chunks) {
        this.name = name;
        this.chunks = chunks;
        thisAsBukkit = new BukkitWorld(this);
    }

    public Chunk getChunkAt(int x, int z) {
        return getChunkAt(x, z, true);
    }

    public Chunk getChunkAt(int x, int z, boolean generateIfNotFound) {
        for (Chunk chunk : chunks.toArray(new Chunk[chunks.size()])) {
            if (chunk.getX() == x && chunk.getZ() == z) {
                return chunk;
            }
        }
        if (generateIfNotFound) {
            generateChunk(x, z);
            return getChunkAt(x, z, false);
        } else {
            return null;
        }
    }

    public void generateChunk(int x, int z) {
        if (getChunkAt(x, z, false) == null) {
            Chunk c = new Chunk(this, x, z);
            chunks.add(c);
        }
    }

    public org.bukkit.World getBukkitWorld() {
        return thisAsBukkit;
    }

    public Block getBlockAt(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int absChunkX = x - (chunkX * 16);
        int absChunkZ = z - (chunkZ * 16);
        //System.out.println(chunkX + ":" + chunkZ);

        Chunk c = getChunkAt(chunkX, chunkZ);
        if (c == null) {
            return null;
        }
        Block b = c.blocks[absChunkX][y][absChunkZ];
        if (b == null) {
            // int id, World world, Chunk chunk, byte data, int x, int y, int z
            // Empty block - fill it out
            //System.out.println("Null block, filling it in: " + absChunkX + ":" + y + ":" + absChunkZ);
            // int id, World world, Chunk chunk, byte data, int x, int y, int z

            c.blocks[absChunkX][y][absChunkZ] = new Block(Material.AIR, this, c, (byte) 0, x, y, z);
            b = c.blocks[absChunkX][y][absChunkZ];
        }
       // System.out.println(b.x + "?" + x + ":" + b.y + "?" + y + ":" + b.z + "?" + z);
        return b;
    }

    public String getName() {
        return name;
    }

    public int getSeaLevel() {
        return 70;
    }

    public Seed getSeed() {
        return seed;
    }

    public PlayerCache getPlayerCache(String name) {
        PlayerCache c = caches.get(name);
        if (c == null) {
            c = new PlayerCache();
            c.x = 7;
            c.z = 7;
            c.y = getBukkitWorld().getHighestBlockYAt((int)c.x, (int)c.z);
            caches.put(name, c);
        }
        return c;
    }

    public void spawnFloatingItem(int x, int y, int z, int typeId, byte data) {
        PacketOutSpawnObject packet = new PacketOutSpawnObject();
        packet.x = x;
        packet.y = y;
        packet.z = z;
        packet.entityId = PoweredCube.getInstance().getNextEntityID();
        packet.slot = new Slot((short)typeId, (byte)1, (short)data);
        packet.pitch = 0;
        packet.yaw = 0;
        packet.type = PacketOutSpawnObject.ObjectType.ITEM_STACK;
        packet.speedX = 0;
        packet.speedY = 0;
        packet.speedZ = 0;
        PoweredCube.getInstance().distributePacket(packet);
    }
}