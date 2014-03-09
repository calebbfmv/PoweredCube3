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

import net.jselby.pc.world.Chunk;
import net.jselby.pc.world.ChunkIO;
import net.jselby.pc.world.World;
import net.jselby.pc.world.WorldLoader;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by James on 2/16/14.
 */
public class PoweredCubeWorldLoader extends WorldLoader {
    public ChunkIO chunkIO = new ChunkIO();

    public World loadWorld(String world) {
        try {
            System.out.println("Loading world \"" + world + "\" from file...");
            File file = new File(world + ".world");
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            World chunks = (World) in.readObject();
            in.close();

            chunks.loader = this;
            chunks.chunks = new ArrayList<Chunk>();

            // Chunks are loaded by the world itself
            return chunks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveWorld(World world) {
        try {
            File file = new File(world.getName() + ".world");
            if (file.exists()) {
                file.delete();
            }

            ArrayList<Chunk> chunks = world.chunks;
            WorldLoader loader = world.loader;

            world.chunks = null;
            world.loader = null;

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(world);
            out.close();

            world.chunks = chunks;
            world.loader = loader;

            // Make sure we have a "chunks" listing
            file = new File(world.getName() + "_chunks");
            if (!file.exists()) {
                file.mkdir();
            }
            ArrayList<String> alreadySaved = new ArrayList<String>();
            for (Chunk c : chunks) {
                if (alreadySaved.contains(c.getX() + ":" + c.getZ())) {
                    System.out.println("We have a duplicate chunk at " + c.getX() + ":" + c.getZ());
                    continue;
                }
                alreadySaved.add(c.getX() + ":" + c.getZ());
                saveChunk(c);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean worldExists(String world) {
        return new File(world + ".world").exists();
    }

    @Override
    public boolean chunkExists(World world, int x, int z) {
        File chunk = new File(new File(world.getName() + "_chunks"), x + "-" + z + ".chunk");
        return chunk.exists();
    }

    @Override
    public Chunk loadChunk(World world, int x, int z) {
        try {
            File chunk = new File(new File(world.getName() + "_chunks"), x + "-" + z + ".chunk");
            InputStream in = new FileInputStream(chunk);
            Chunk c = chunkIO.read(in, world);
            in.close();
            c.world = world;
            return c;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void saveChunk(Chunk c) {
        //System.out.println("Saving chunk: " + c.getX() + ":" + c.getZ());
        try {
            File chunkFile = new File(new File(c.world.getName() + "_chunks"), c.getX() + "-" + c.getZ() + ".chunk");
            OutputStream chunkOut = new FileOutputStream(chunkFile);
            chunkIO.write(chunkOut, c);
            chunkOut.flush();
            chunkOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
