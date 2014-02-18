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

package net.jselby.pc.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.util.List;

/**
 * Created by James on 2/1/14.
 */
public class PacketDecoder extends ReplayingDecoder<PacketDecoder.State> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> objects) throws Exception {
        try {
            byteBuf.markReaderIndex();

            StandardInput in = new StandardInput(new DataInputStream(new ByteBufInputStream(byteBuf)));
            int length = in.readVarInt();

            if (byteBuf.readableBytes() < length) {
                // Not enough data yet! Wait for a while
                byteBuf.resetReaderIndex();
                return;
            }

            byte[] packet = in.readBytes(length);

            // Create a "unread" packet, for the connection handler to process
            StandardInput packetIn = new StandardInput(new DataInputStream(new ByteArrayInputStream(packet)));

            UnreadPacket packetContainer = new UnreadPacket();
            packetContainer.in = packetIn;
            packetContainer.length = length;

            objects.add(packetContainer);
        } catch (EOFException e) {
            System.out.println("Client " + ctx.channel().remoteAddress() + " disconnected: End of stream");
            UnreadPacket packetContainer = new UnreadPacket();
            packetContainer.in = null;
            packetContainer.length = -1;
            objects.add(packetContainer);
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause.getMessage().equalsIgnoreCase("java.io.EOFException")) { ctx.close(); return; }
        cause.printStackTrace();
        ctx.close();
    }

    public enum State {
        LENGTH, PAYLOAD
    }
}
