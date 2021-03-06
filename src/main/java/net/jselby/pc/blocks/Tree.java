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
        // Bug with world generator ?

        y += 1;
        // Trunk
        Random r = new Random(seed);
        int trunkAdd = r.nextInt(3);
        for (int buildY = y; buildY < y + 3 + trunkAdd; buildY++) {
            chunk.getWorld().getBlockAt(x, buildY, z).setTypeId(Material.LOG.getId(), false);
            chunk.getWorld().getBlockAt(x, buildY, z).setData((byte)0, false);
        }

        for (int buildX = x - 2; buildX <= x + 2; buildX++) {
            for (int buildY = y + 3; buildY <= y + 3 + 2 + trunkAdd; buildY++) {
                if (buildY < 0) {
                    buildY = 0;
                }
                if (buildY > 254) {
                    buildY = 254;
                }
                for (int buildZ = z - 2; buildZ <= z + 2; buildZ++) {
                    Block b = chunk.getWorld().getBlockAt(buildX, buildY, buildZ);
                    if (b.getTypeId() == 0) {
                        b.setTypeId(Material.LEAVES.getId(), false);
                    }
                }
                if (buildY == 254) {
                    break;
                }
            }
        }
    }
}
