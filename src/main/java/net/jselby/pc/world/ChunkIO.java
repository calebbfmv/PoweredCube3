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

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Simple class to read and write chunks to the filesystem/network/wherever.
 */
public class ChunkIO {

    public ChunkIO() {}

    public void write(OutputStream out, Chunk c) throws IOException {
        if (c == null) {
            throw new IllegalStateException("No chunk set!");
        }
        GZIPOutputStream gzipOut = new GZIPOutputStream(out);

        DataOutputStream dataOut = new DataOutputStream(gzipOut);

        dataOut.writeInt(c.getX());
        dataOut.writeInt(c.getZ());

        int blockCount = 0;
        for (int x = 0; x < c.blocks.length; x++) {
            for (int y = 0; y < c.blocks[x].length; y++) {
                for (int z = 0; z < c.blocks[x][y].length; z++) {
                    Block b = c.blocks[x][y][z];
                    if (b != null) {
                        blockCount++;
                    }
                }
            }
        }

        dataOut.writeInt(blockCount);

        for (int x = 0; x < c.blocks.length; x++) {
            for (int y = 0; y < c.blocks[x].length; y++) {
                for (int z = 0; z < c.blocks[x][y].length; z++) {
                    Block b = c.blocks[x][y][z];
                    if (b != null) {
                        dataOut.writeInt(b.x);
                        dataOut.writeInt(b.y);
                        dataOut.writeInt(b.z);
                        dataOut.writeInt(b.getTypeId());
                        dataOut.writeByte(b.getData());
                    }
                }
            }
        }

        dataOut.flush();
        gzipOut.flush();

        gzipOut.finish();
        gzipOut.flush();
    }

    public Chunk read(InputStream in, World world) throws IOException {
        DataInputStream dataIn = new DataInputStream(new GZIPInputStream(in));

        int chunkX = dataIn.readInt();
        int chunkZ = dataIn.readInt();

        int absChunkX = chunkX * 16;
        int absChunkZ = chunkZ * 16;

        Chunk c = new Chunk(world, chunkX, chunkZ);

        // Rest of file is chunks
        int blockCount = dataIn.readInt();

        for (int i = 0; i < blockCount; i++) {
            int blockX = dataIn.readInt();
            int blockY = dataIn.readInt();
            int blockZ = dataIn.readInt();
            int blockId = dataIn.readInt();
            byte data = dataIn.readByte();
            Block b = new Block(blockId, c, data, blockX, blockY, blockZ);
            int relBlockX = blockX - absChunkX;
            int relBlockY = blockY - 1;
            int relBlockZ = blockZ - absChunkZ;
            if (relBlockX < 0 || relBlockY < 0 || relBlockZ < 0 || relBlockX > 15 || relBlockY > 254 || relBlockZ > 15) {
                System.out.println("Fatal chunk: " + relBlockX + ":" + relBlockY + ":" + relBlockZ);
            }
            c.blocks[relBlockX][relBlockY][relBlockZ] = b;
        }

        return c;
    }
}
