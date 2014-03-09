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

import io.netty.channel.ChannelHandlerContext;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.bukkit.BukkitPlayer;
import net.jselby.pc.entities.PlayerDisplayer;
import net.jselby.pc.network.packets.mcplay.PacketOutChatMessage;
import net.jselby.pc.network.packets.mcplay.PacketOutSpawnPlayer;
import net.jselby.pc.player.ChatMessage;
import org.json.simple.JSONObject;

/**
 * Contains static method called when a player joins
 */
public class JoinHandler {
    public static void join(ChannelHandlerContext ctx, Client cl) {
        ConnectedClient client = (ConnectedClient) cl;
        System.out.println(cl.name + "[" + ctx.channel().remoteAddress() + "] logged in with entity id " +
                cl.id + " at ([world] " + cl.x
                + ", " + cl.y + ", " + cl.z + ")");

        // Welcome messages
        PacketOutChatMessage joinMsg = new PacketOutChatMessage();

        joinMsg.message = ChatMessage.convertToJson("Welcome to the server!");
        joinMsg.message.json.put("color", "gold");
        cl.writePacket(joinMsg);

        joinMsg.message = ChatMessage.convertToJson("This server is running PoweredCube - http://pc.jselby.net");
        joinMsg.message.json.put("color", "gold");
        JSONObject clickEvent = new JSONObject();
        clickEvent.put("action", "open_url");
        clickEvent.put("value", "http://pc.jselby.net");
        joinMsg.message.json.put("clickEvent", clickEvent);
        cl.writePacket(joinMsg);

        joinMsg.message = ChatMessage.convertToJson("Any number of bugs can occur. You have been warned!");
        joinMsg.message.json.put("color", "red");
        cl.writePacket(joinMsg);

        // Spawn entity
        for (Client c : PoweredCube.getInstance().clients.toArray(
                new Client[PoweredCube.getInstance().clients.size()])) {
            PlayerDisplayer.showPlayer(client, c);
        }

        BukkitPlayer p = new BukkitPlayer(cl);

        PoweredCube.getInstance().players.add(p);
        PoweredCube.getInstance().clients.add(cl);

        joinMsg.message = ChatMessage.convertToJson(cl.displayName + " joined the server.");
        PoweredCube.getInstance().distributePacket(joinMsg);

    }
}
