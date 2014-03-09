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

/**
 * Watches a World's chunkLoadQueue, and executes it slowly
 */
public class ChunkLoadThread extends Thread {
    public World w;

    public ChunkLoadThread(World w) {
        this.w = w;
    }

    @Override
    public void run() {
        System.out.println("Load thread: starting for world \"" + w.getName() + "\".");
        while(true) {
            try {
                while(!w.chunkLoadQueue.isEmpty()) {
                    String req = w.chunkLoadQueue.poll();
                    if (req == null) {
                        break;
                    }
                    //System.out.println("Load thread: Got request: " + req);
                    int x = Integer.parseInt(req.split(":")[0]);
                    int z = Integer.parseInt(req.split(":")[1]);
                    w.generateChunk(x, z);
                }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
