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

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.bukkit.BukkitPlayer;
import net.jselby.pc.network.packets.mcplay.PacketOutChatMessage;
import net.jselby.pc.player.ChatMessage;
import org.json.simple.JSONObject;

import java.net.SocketAddress;

/**
 * Created by James on 1/31/14.
 */
public class ClientConnectionHandler extends ChannelHandlerAdapter {
    private Client cl = null;
    private StandardOutput out;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (cl == null) {
            // Call our init() method, if nothing has happened yet
            init(ctx.channel().remoteAddress(), ctx);
        }

        // Switch the client, if the state has changed
        if (cl.state == PacketDefinitions.State.PLAY && !(cl instanceof ConnectedClient)) {
            Client newClient = new ConnectedClient(ctx, cl.name);
            newClient.name = cl.name;
            newClient.uuid = cl.uuid;
            newClient.pitch = cl.pitch;
            newClient.yaw = cl.yaw;
            newClient.state = cl.state;
            newClient.x = cl.x;
            newClient.y = cl.y;
            newClient.z = cl.z;
            newClient.id = cl.id;
            newClient.displayName = cl.displayName;
            newClient.loadedChunks = cl.loadedChunks;
            cl = newClient;

            JoinHandler.join(ctx, cl);
        }

        UnreadPacket packetContainer = (UnreadPacket) msg;
        if (packetContainer.length == -1) {
            // Disconnect/Error, I guess
            cl.onDisconnect();
            return;
        }

        StandardInput packetIn = packetContainer.in;

        // Get the id
        int id = packetIn.readVarInt();

        // Find a packet to parse this
        Class<? extends Packet> packet = PoweredCube.getInstance().getPacketDefinitions().searchForPacket(cl.state, id);
        if (packet == null) {
            System.err.println("No packet to suit id " + PacketDefinitions.convertToHexString(id) + " in state " + cl.state + "!");
            return;
        }

        Packet ins = packet.newInstance();
        ins.read(cl, packetIn);

        // Pass on the new packet onto the client
        cl.onPacketReceive(ins);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void init(SocketAddress remoteAddress, ChannelHandlerContext ctx) throws Exception {
        PendingClient cl = new PendingClient(ctx);
        this.cl = cl;
        cl.state = PacketDefinitions.State.DEFAULT;
    }
}
