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

import net.jselby.pc.*;
import net.jselby.pc.blocks.Material;
import net.jselby.pc.bukkit.BukkitWorld;
import net.jselby.pc.entities.Entity;
import net.jselby.pc.entities.EntityManager;
import net.jselby.pc.entities.FloatingItem;
import net.jselby.pc.network.Client;
import net.jselby.pc.network.packets.mcplay.PacketOutDestroyEntities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by James on 2/1/14.
 */
public class World implements Serializable {
    public static final int INITIAL_SIZE = 1;
    public static final int MAX_HEIGHT = 255;

    private String name;
    public ArrayList<Chunk> chunks = new ArrayList<Chunk>();
    public HashMap<String, PlayerCache> caches = new HashMap<String, PlayerCache>();
    private BukkitWorld thisAsBukkit;
    private Seed seed;
    public EntityManager m = new EntityManager();
    public Random random;

    public int tick;

    public double spawnX;
    public double spawnY;
    public double spawnZ;
    public WorldLoader loader;

    public World(String name, WorldLoader loader) {
        loader.worldExists("");
        this.loader = loader;
        seed = new Seed(new Random().nextLong());
        this.random = new Random(seed.seed);

        this.name = name;
        System.out.println("Generating world \"" + name + "\" with seed " + seed.seed + "...");

        thisAsBukkit = new BukkitWorld(this);

        // Find the spawn point
        int searchX = 0;
        int searchZ = 0;
        while(true) {
            Block b = getHighestBlockAt(searchX, searchZ);

            if (b != null && b.getTypeId() != Material.WATER.getId()) {
                // Safe place
                // Clear out chunk cache, we will do it again ourselves
                System.out.println("Resetting cache...");
                chunks.clear();
                spawnX = ((double)searchX) + 0.5;
                spawnY = b.y;
                spawnZ = ((double)searchZ) + 0.5;

                /*for (int x = -INITIAL_SIZE + searchX; x <= INITIAL_SIZE + searchX; x++) {
                    for (int y = -INITIAL_SIZE + searchZ; y <= INITIAL_SIZE + searchZ; y++) {
                        //getChunkAt((x / 16), (y / 16));
                    }
                }*/
                break;
            }

            searchX += 12;
        }


    }

    public World(String name, ArrayList<Chunk> chunks, WorldLoader loader) {
        this.name = name;
        this.chunks = chunks;
        this.loader = loader;
        thisAsBukkit = new BukkitWorld(this);
    }

    private Block getHighestBlockAt(int x, int z) {
        for (int y = MAX_HEIGHT; y > 0; y--) {
            if (getBlockAt(x, y - 1, z).getTypeId() != 0) {
                return getBlockAt(x, y - 1, z);
            }
        }
        return null;
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
            // See if we have already got a chunk saved
            if (loader.chunkExists(this, x, z)) {
                //System.out.println("Loading already generated chunk: " + x + ":" + z);
                try {
                    chunks.add(loader.loadChunk(this, x, z));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
            //System.out.println("Generating chunk: " + x + ":" + z);
            Chunk c = new Chunk(this, x, z);
            chunks.add(c);
            c.generate();
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

        Chunk c = getChunkAt(chunkX, chunkZ);
        if (c == null) {
            return null;
        }
        Block b = c.blocks[absChunkX][y][absChunkZ];
        if (b == null) {
            // int id, World world, Chunk chunk, byte data, int x, int y, int z
            // Empty block - fill it out

            c.blocks[absChunkX][y][absChunkZ] = new Block(Material.AIR, c, (byte) 0, x, y, z);
            b = c.blocks[absChunkX][y][absChunkZ];
        }

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
            c.x = spawnX;
            c.z = spawnZ;
            c.y = getBukkitWorld().getHighestBlockYAt((int)c.x, (int)c.z) + 1;
            caches.put(name, c);
        }
        return c;
    }

    public void spawnFloatingItem(double x, double y, double z, int typeId, byte data) {
        Random r = new Random();

        // Create entity
        FloatingItem i = new FloatingItem(PoweredCube.getInstance().getNextEntityID(), this, x, y, z, typeId, data);
        i.xSpeed = (short) (r.nextInt(2000) - 1000);
        i.ySpeed = (short) r.nextInt(1000);
        i.zSpeed = (short) (r.nextInt(2000) - 1000);
        m.addEntity(i);

        // Show entity
        for (Client c : PoweredCube.getInstance().clients) {
            i.showToClient(c);
        }
    }

    public void removeEntity(Entity ent) {

        if (m.containsEntity(ent)) {
            m.removeEntity(ent);

            PacketOutDestroyEntities destroyEntities = new PacketOutDestroyEntities();
            destroyEntities.id = (ent.id);

            PoweredCube.getInstance().distributePacket(destroyEntities);
        }

    }

    public void tick() {
        // Tick the entities
        m.tick();

        // Unload ALL the chunks (or just the ones without players nearby)
        if (tick == 20) {
            ArrayList<Chunk> keepChunks = new ArrayList<Chunk>();

            for (Client c : PoweredCube.getInstance().clients.toArray(new Client[0])) {
                if (c.world != this) {
                    continue;
                }
                for (Chunk chunk : c.loadedChunks) {
                    keepChunks.add(chunk);
                }
            }

            for (Chunk chunk : chunks.toArray(new Chunk[chunks.size()])) {
                if (!keepChunks.contains(chunk)) {
                    //System.out.println("Unloading chunk " + chunk.getX() + ":" + chunk.getZ());
                    loader.saveChunk(chunk);
                    chunks.remove(chunk);
                }
            }

            System.gc();

            System.out.println("Chunks loaded: " + chunks.size());
        }

        tick++;
        if (tick > 20) {
            tick = 0;
        }
    }
}