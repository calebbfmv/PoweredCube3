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

import net.jselby.pc.blocks.Material;
import net.jselby.pc.blocks.Tree;
import net.jselby.pc.bukkit.BukkitChunk;
import org.bukkit.util.noise.PerlinNoiseGenerator;
import org.bukkit.util.noise.SimplexNoiseGenerator;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by James on 2/1/14.
 */
public class Chunk implements Serializable {
    public int[][][] blocks = new int[16][256][16];
    public byte[][][] data = new byte[16][256][16];

    //public Block[][][] blocks = null;
    public World world;
    private int x;
    private int z;
    private BukkitChunk c;

    public Chunk(World world, int chunkX, int chunkZ) {

        this.world = world;
        this.x = chunkX;
        this.z = chunkZ;

        c = new BukkitChunk(this);
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public World getWorld() {
        return world;
    }

    public int getSeaLevel() {
        return getWorld().getSeaLevel();
    }

    public org.bukkit.Chunk getBukkitChunk() {
        return c;
    }

    public void generate() {
        int absChunkX = x * 16;
        int absChunkZ = z * 16;

        // Generate perlin noise
        Random r = world.random;
        Seed noiseSeed = world.getSeed();

        // Bukkit PerlinNoiseGenerator
        PerlinNoiseGenerator generator = new PerlinNoiseGenerator(noiseSeed.seed);

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int actualY = (int)Math.floor(70 + generator.noise((absChunkX + x) / 512f,
                        (absChunkZ + z) / 512f, 7, (double) 1.5, 1.5));

                //FastNoise.noise(((float) x + (chunkX * 16)) / 512f,
                //((float) z + (chunkZ * 16)) / 512f, 7);
                if (actualY < 70) {
                    // Water
                    for (double y = 70; y >= actualY; y--) {
                        blocks[x][(int)y][z] = Material.WATER.getId();
                    }
                } else {
                    if (r.nextInt(500) == 1) {
                        // Generate Tree here
                        Tree.generate(this, absChunkX + x, actualY, absChunkZ + z, r.nextLong());
                    } else if (r.nextInt(2) == 1) {
                        // Generate grass here
                        blocks[x][actualY + 1][z] = Material.LONG_GRASS.getId();
                        data[x][actualY + 1][z] = 0x01;
                    }
                    blocks[x][actualY][z] = Material.GRASS.getId();
                }

                if (actualY < 70) {
                    // Raise underwater height
                    actualY ++;
                }
                for (double y = actualY - 1; y > actualY - 5; y--) {
                    blocks[x][(int)y][z] = Material.DIRT.getId();
                }

                for (double y = actualY - 5; y > 1; y--) {
                    blocks[x][(int)y][z] = Material.STONE.getId();
                }
                blocks[x][1][z] = Material.BEDROCK.getId();
            }
        }
    }
}
