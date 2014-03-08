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

package net.jselby.pc.blocks;

import net.jselby.pc.world.Block;
import net.jselby.pc.world.Chunk;

import java.util.Random;

/**
 * Created by James on 2/16/14.
 */
public class Tree {
    public static void generate(Chunk chunk, int x, int y, int z, long seed) {
        // Trunk
        Random r = new Random(seed);
        int trunkAdd = r.nextInt(3);
        for (int buildY = y; buildY < y + 5 + trunkAdd; buildY++) {
            chunk.blocks[x][buildY][z] =
                    new Block(Material.LOG, chunk.getWorld(),
                            chunk, (byte)2, x, buildY, z);
        }

        for (int buildX = x - 2; buildX <= x + 2; buildX++) {
            if (buildX < 0) {
                buildX = 0;
            }
            if (buildX > 15) {
                buildX = 15;
            }
            for (int buildY = y + 5; buildY <= y + 3 + 3 + trunkAdd; buildY++) {
                if (buildY < 0) {
                    buildY = 0;
                }
                if (buildY > 254) {
                    buildY = 254;
                }
                for (int buildZ = z - 2; buildZ <= z + 2; buildZ++) {
                    if (buildZ < 0) {
                        buildZ = 0;
                    }
                    if (buildZ > 15) {
                        buildZ = 15;
                    }
                    if (!(buildX == x - 2 || buildX == x + 2 || buildZ == z - 2 || buildZ == z + 2)) {
                        if (chunk.blocks[buildX][buildY][buildZ] == null ||
                                chunk.blocks[buildX][buildY][buildZ].getTypeId() == 0) {
                            chunk.blocks[buildX][buildY][buildZ] =
                                new Block(Material.LEAVES, chunk.getWorld(),
                                        chunk, buildX, buildY, buildZ);
                        }
                    }
                    if (buildZ == 15) {
                        break;
                    }
                }
                if (buildY == 254) {
                    break;
                }
            }
            if (buildX == 15) {
                break;
            }
        }
    }
}
