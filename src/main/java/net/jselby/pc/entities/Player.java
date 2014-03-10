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

import net.jselby.pc.network.ConnectedClient;
import net.jselby.pc.network.packets.mcplay.PacketKeepAlive;
import net.jselby.pc.network.packets.mcplay.PacketOutChunkData;
import net.jselby.pc.network.packets.mcplay.PacketOutTimeUpdate;
import net.jselby.pc.world.Chunk;

import java.util.ArrayList;

/**
 * Represents a player as a entity. Does most of the actual action.
 */
public class Player {
    private ConnectedClient c;

    public Player(ConnectedClient c) {
        this.c = c;
    }

    public long timeOfDay = 6000;
    public void tick() {
        c.tick++;
        c.loggedIn++;
        // Check that client is loaded area-wise
        if (c.tick % 20 == 0) {
            // Load chunks that are needed
            ArrayList<Chunk> loadingChunks = (ArrayList<Chunk>) c.loadedChunks.clone();

            for (int tempX = (int) (Math.floor(c.x / 16) - (c.maxLoadedChunks));
                 tempX < Math.floor(c.x / 16) + (c.maxLoadedChunks); tempX++) {
                for (int tempZ = (int) (Math.floor(c.z / 16) - (c.maxLoadedChunks));
                     tempZ < Math.floor(c.z / 16) + (c.maxLoadedChunks); tempZ++) {
                     if (!c.world.isChunkLoaded((int)tempX, (int)tempZ)) {
                         // Don't force the chunk load, let it happen by the ChunkLoadThread
                         c.world.requestChunkLoad((int)tempX, (int)tempZ);
                         continue;
                     }

                     Chunk chunk = c.world.getChunkAt((int)tempX, (int)tempZ);
                     loadingChunks.remove(chunk);

                     if (!c.loadedChunks.contains(chunk)) {
                         c.loadedChunks.add(chunk);
                         // Send data to client
                         PacketOutChunkData world = new PacketOutChunkData(chunk.getX(), chunk.getZ(), false);
                         if (world.data != null && world.data.length != 0) {
                             c.writePacket(world);
                         }
                     }
                }
            }

                // Find chunks to unload
                for (Chunk chunk : loadingChunks.toArray(new Chunk[loadingChunks.size()])) {
                    // Give the client a few ticks to load first
                    if (chunk == null) {
                        continue;
                    }
                    if (c.loggedIn > 200) {
                        //System.out.println("Unloading chunk: " + chunk);
                        /*loadedChunks.remove(chunk);
                        PacketOutChunkData world = new PacketOutChunkData(chunk.getX(), chunk.getZ(), true);
                        if (world.data != null) {
                            writePacket(world);
                        }*/
                    }
                }
        }

        if (c.tick % 20 == 0) {
            // Keep the client sync'd up with the world time
            PacketOutTimeUpdate time = new PacketOutTimeUpdate();
            time.timeOfDay = timeOfDay+=20;
            time.ageOfWorld = 0;

            c.writePacket(time);
        }
        if (c.tick % 20 == 0) {
            c.writePacket(new PacketKeepAlive());
            c.tick = 0;
        }
    }
}
