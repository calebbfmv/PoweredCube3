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
import net.jselby.pc.entities.FloatingItem;
import net.jselby.pc.world.PlayerCache;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.network.packets.mcdefault.PacketInHandshake;
import net.jselby.pc.network.packets.mclogin.PacketInLoginStart;
import net.jselby.pc.network.packets.mclogin.PacketOutLoginSuccess;
import net.jselby.pc.network.packets.mcping.PacketInPing;
import net.jselby.pc.network.packets.mcping.PacketInRequest;
import net.jselby.pc.network.packets.mcping.PacketOutResponse;
import net.jselby.pc.network.packets.mcplay.*;

import java.io.IOException;
import java.util.UUID;

/**
 * Represents a client that just joined the server. This handles both pings and login.
 *
 * @author j_selby
 */
public class PendingClient extends Client {
    public PendingClient(ChannelHandlerContext ctx) {
        super(ctx);
    }

    @Override
    public void onPacketReceive(Packet packet) {
        if (packet instanceof PacketInHandshake) {
            PacketInHandshake handshake = (PacketInHandshake) packet;
            state = handshake.nextState == 1 ? PacketDefinitions.State.PING : PacketDefinitions.State.LOGIN;

        } else if (packet instanceof PacketInRequest) {
            PacketOutResponse response = new PacketOutResponse();
            response.json = "{\n" +
                    "\t\"version\": {\n" +
                    "\t\t\"name\": \"" + PoweredCube.MC_VERSION + "\",\n" +
                    "\t\t\"protocol\": 4\n" +
                    "\t},\n" +
                    "\t\"players\": {\n" +
                    "\t\t\"max\": " + PoweredCube.getInstance().getMaxPlayers() + ",\n" +
                    "\t\t\"online\": " + PoweredCube.getInstance().clients.size() + ",\n" +
                    "\t\t\"sample\":[\n" +
                    "\t\t\t{\"name\":\"j_selby\", \"id\":\"\"}\n" +
                    "\t\t]\n" +
                    "\t},\t\n" +
                    "\t\"description\": \"A PoweredCube Server\",\n" +
                    "\t\"favicon\": \"\"\n" +
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
    public void tick() {}

    @Override
    public void sendMessage(String s) {}

    @Override
    public void onDisconnect() {}

    @Override
    public boolean pickUpItem(FloatingItem item) {
        return false;
    }
}
