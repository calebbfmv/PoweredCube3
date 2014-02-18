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
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.jselby.pc.PlayerCache;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.World;
import net.jselby.pc.network.packets.mcdefault.PacketInHandshake;
import net.jselby.pc.network.packets.mclogin.PacketInLoginStart;
import net.jselby.pc.network.packets.mclogin.PacketOutLoginSuccess;
import net.jselby.pc.network.packets.mcping.PacketInPing;
import net.jselby.pc.network.packets.mcping.PacketInRequest;
import net.jselby.pc.network.packets.mcping.PacketOutResponse;
import net.jselby.pc.network.packets.mcplay.*;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by James on 1/31/14.
 */
public class PendingClient extends Client {
    public PendingClient(ChannelHandlerContext ctx) {
        super(ctx);
    }

    @Override
    public void onPacketReceive(Packet packet) throws IOException {
        if (packet instanceof PacketInHandshake) {
            PacketInHandshake handshake = (PacketInHandshake) packet;
            state = handshake.nextState == 1 ? PacketDefinitions.State.PING : PacketDefinitions.State.LOGIN;
        } else if (packet instanceof PacketInRequest) {
            PacketOutResponse response = new PacketOutResponse();
            response.json = "{\n" +
                    "\t\"version\": {\n" +
                    "\t\t\"name\": \"1.7.2\",\n" +
                    "\t\t\"protocol\": 4\n" +
                    "\t},\n" +
                    "\t\"players\": {\n" +
                    "\t\t\"max\": 9001,\n" +
                    "\t\t\"online\": 2831,\n" +
                    "\t\t\"sample\":[\n" +
                    "\t\t\t{\"name\":\"Thinkofdeath\", \"id\":\"\"}\n" +
                    "\t\t]\n" +
                    "\t},\t\n" +
                    "\t\"description\": \"Â§cÂ§k|||||||| Â§6BoxHead Networks Â§cÂ§k||||||||                             Â§rÂ§f -Think Outside The Box-\",\n" +
                    "\t\"favicon\": \"data:image/png;base64,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\"\n" +
                    "}";
            writePacket(response);
        } else if (packet instanceof PacketInPing) {
            // Return the ping, we don't need to process it
            writePacket(packet);
            ctx.close();
        } else if (packet instanceof PacketInLoginStart) {
            PacketInLoginStart login = (PacketInLoginStart) packet;

            this.name = login.name;
            this.displayName = this.name;
            this.uuid = UUID.randomUUID();
            this.id = PoweredCube.getInstance().getNextEntityID();

            // We don't support encryption (yet), just accept the client.
            PacketOutLoginSuccess response = new PacketOutLoginSuccess();
            response.username = this.name;
            response.uuid = this.uuid;
            writePacket(response);

            // Finally, "beam" the player into the game
            state = PacketDefinitions.State.PLAY;
            PacketOutJoinGame join = new PacketOutJoinGame();
            join.difficulty = 0; // TODO: Get difficulty from main class
            join.dimension = 0; // TODO: Get dimension from main class
            join.entityId = id;
            join.gamemode = 0; // TODO: Get gamemode from main class
            join.levelType = "DEFAULT"; // TODO: Get level type from main class
            join.maxPlayers = 50; // TODO: Get max players from main class
            writePacket(join);

            // World Data
            PlayerCache c = PoweredCube.getInstance().getWorlds().get(0).getPlayerCache(name);

            x = c.x;
            z = c.z;
            y = c.y + 1;

            int absChunkX = (int) Math.floor(x / 16);
            int absChunkZ = (int) Math.floor(z / 16);

            for (int chunkX = (absChunkX-(3)); chunkX <= absChunkX+(3); chunkX++) {
                for (int chunkZ = (absChunkZ-(3)); chunkZ <= absChunkZ+(3); chunkZ++) {
                    loadedChunks.add(PoweredCube.getInstance().getWorlds().get(0)
                            .getChunkAt(chunkX, chunkZ));
                    PacketOutChunkData world = new PacketOutChunkData(chunkX, chunkZ);
                    if (world.data != null && world.data.length != 0) {
                        writePacket(world);
                    }
                }
            }

            // Spawn position
            PacketOutSpawnPosition pos = new PacketOutSpawnPosition();
            pos.x = (int) x;
            pos.y = (int) y;
            pos.z = (int) z;

            writePacket(pos);

            PacketOutPlayerPositionAndLook posAndLook = new PacketOutPlayerPositionAndLook();
            posAndLook.onGround = false;
            posAndLook.x = x;
            posAndLook.y = y;
            posAndLook.z = z;
            posAndLook.yaw = c.yaw;
            posAndLook.pitch = c.pitch;

            writePacket(posAndLook);

            PacketOutTimeUpdate time = new PacketOutTimeUpdate();
            time.timeOfDay = 6000;
            time.ageOfWorld = 0;

            writePacket(time);
        }
    }

    @Override
    public void tick() throws IOException {
        // No tick needed here
    }

    @Override
    public void sendMessage(String s) {}

    @Override
    public void onDisconnect() throws IOException {

    }
}
