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
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import net.jselby.pc.PlayerInventory;
import net.jselby.pc.entities.FloatingItem;
import net.jselby.pc.world.Chunk;
import net.jselby.pc.world.PlayerCache;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by James on 1/31/14.
 */
public abstract class Client {
    public PacketDefinitions.State state;
    public String name;
    public UUID uuid;
    public ChannelHandlerContext ctx;

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround = false;
    public int id;
    public String displayName;
    public PlayerCache cache;

    public ArrayList<Chunk> loadedChunks = new ArrayList<Chunk>();

    public Client(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public abstract void onPacketReceive(Packet packet);

    public abstract void tick();

    public ChannelFuture writePacket(Packet packet) {
        try {
            // Write the packet to a byte[] array
            ByteArrayOutputStream in = new ByteArrayOutputStream();
            StandardOutput out = new StandardOutput(new DataOutputStream(in));
            out.startPacket(packet);
            packet.write(this, out);

            byte[] packetContents = in.toByteArray();

            // Add the length header
            ByteArrayOutputStream in1 = new ByteArrayOutputStream();
            out = new StandardOutput(new DataOutputStream(in1));
            out.writeVarInt(packetContents.length);
            out.writeBytes(packetContents);
            packetContents = in1.toByteArray();

            // Convert it to a Netty stream
            ByteBuf buffer = ctx.alloc().buffer(packetContents.length);
            buffer.writeBytes(packetContents);

            return ctx.writeAndFlush(buffer);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public abstract void sendMessage(String s);

    public abstract void onDisconnect();

    public abstract boolean pickUpItem(FloatingItem item);
}
