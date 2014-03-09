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

package net.jselby.pc.network.packets.mcplay;

import net.jselby.pc.world.Block;
import net.jselby.pc.world.Chunk;
import net.jselby.pc.PoweredCube;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

/**
 * Created by James on 2/1/14.
 */
public class PacketOutMapChunkBulk {

    public static byte[] deflate(byte[] input) throws IOException {
        Deflater df = new Deflater();
        df.setInput(input);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        df.finish();
        byte[] buff = new byte[1024];
        while (!df.finished()) {
            int count = df.deflate(buff);
            baos.write(buff, 0, count);
        }
        baos.close();
        return baos.toByteArray();
    }

    public static byte[] generateChunk(int chunkX, int chunkY) {
        int chunkSize = 256 * 16 * 16;

        Chunk chunk = ((Chunk) (PoweredCube.getInstance().getWorlds()
                .get(0).getChunkAt(chunkX, chunkY)));
        if (chunk == null) {
            return null;
        }

        byte[] blockTypeArray = new byte[chunkSize];
        byte[] blockMetadataArray = new byte[chunkSize / 2];
        byte[] blockLightArray = new byte[chunkSize / 2];
        byte[] blockSkylightArray = new byte[chunkSize / 2];
        byte[] addArray = new byte[chunkSize / 2];
        byte[] biomeArray = new byte[256];

        int x = 1; // Changes the fastest
        int z = 1; // Second fastest
        int y = 1; // Third fastest
        int lastData = 0x00;
        for (int i = 0; i < chunkSize; i++) {
            int block = chunk.blocks[x - 1][y - 1][z - 1];
            blockTypeArray[i] = (byte) block;
            if (!(i % 2 == 0)) {
                // 2nd data
                int data = chunk.data[x - 1][y - 1][z - 1];
                int data2 = lastData;

                blockMetadataArray[i / 2] = (byte) ((data << 4) + data2);
                        //(byte)(blockMetadataArray[i / 2] + data);
            }
            lastData = chunk.data[x - 1][y - 1][z - 1];
            x++;
            if (x > 16) {
                z++;
                x = 1;
            }
            if (z > 16) {
                y++;
                z = 1;
            }
        }

        for (int i = 0; i < chunkSize / 2; i++) {
            //blockMetadataArray[i] = 0x00;
        }

        for (int i = 0; i < chunkSize / 2; i++) {
            blockLightArray[i] = (byte) 0;
        }

        for (int i = 0; i < chunkSize / 2; i++) {
            blockSkylightArray[i] = (byte) 255;
        }

        for (int i = 0; i < chunkSize / 2; i++) {
            //addArray[i] = 0x01;
        }

        for (int i = 0; i < 256; i++) {
            biomeArray[i] = 1;
        }

        byte[] byteArray = new byte[blockTypeArray.length
                + blockMetadataArray.length + blockLightArray.length
                + blockSkylightArray.length/* + addArray.length*/
                + biomeArray.length];

        int position = 0;

        for (int i = position + 0; i < position + blockTypeArray.length; i++) {
            byteArray[i] = blockTypeArray[i - position];
        }

        position += blockTypeArray.length;

        for (int i = position + 0; i < position + blockMetadataArray.length; i++) {
            byteArray[i] = blockMetadataArray[i - position];
        }

        position += blockMetadataArray.length;

        for (int i = position + 0; i < position + blockLightArray.length; i++) {
            byteArray[i] = blockLightArray[i - position];
        }

        position += blockLightArray.length;

        for (int i = position + 0; i < position + blockSkylightArray.length; i++) {
            byteArray[i] = blockSkylightArray[i - position];
        }

        position += blockSkylightArray.length;

        /*for (int i = position + 0; i < position + addArray.length; i++) {
            byteArray[i] = addArray[i - position];
        }

        position += addArray.length;*/

        for (int i = position + 0; i < position + biomeArray.length; i++) {
            byteArray[i] = biomeArray[i - position];
        }

        position += biomeArray.length;

        return byteArray;
    }
}
