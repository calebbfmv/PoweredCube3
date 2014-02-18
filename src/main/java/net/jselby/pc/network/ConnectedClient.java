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
import net.jselby.pc.ChatMessage;
import net.jselby.pc.Chunk;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.World;
import net.jselby.pc.bukkit.BukkitChunk;
import net.jselby.pc.network.packets.mcplay.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.io.IOException;

/**
 * Created by James on 2/1/14.
 */
public class ConnectedClient extends Client {
    public int tick = 0;
    public World world;
    public int gamemode = 0;

    public ConnectedClient(ChannelHandlerContext ctx) {
        super(ctx);
        world = PoweredCube.getInstance().getWorlds().get(0); // Default world
    }

    @Override
    public void onPacketReceive(Packet packet) throws IOException {
        // Checking for stuff
        // Check for cache
        if (cache == null) {
            cache = world.getPlayerCache(name);
        }

        if (packet instanceof PacketInPlayer) {
            PacketInPlayer instance = (PacketInPlayer) packet;
            this.onGround = instance.onGround;
        } else if (packet instanceof PacketInPlayerPosition) {
            PacketInPlayerPosition instance = (PacketInPlayerPosition) packet;
            this.x = instance.x;
            this.y = instance.headY;
            this.z = instance.z;
            this.onGround = instance.onGround;
        } else if (packet instanceof PacketInPlayerLook) {
            PacketInPlayerLook instance = (PacketInPlayerLook) packet;
            this.yaw = instance.yaw;
            this.pitch = instance.pitch;
            this.onGround = instance.onGround;
        } else if (packet instanceof PacketInPlayerPositionAndLook) {
            PacketInPlayerPositionAndLook instance = (PacketInPlayerPositionAndLook) packet;
            this.x = instance.x;
            this.y = instance.headY;
            this.z = instance.z;
            this.yaw = instance.yaw;
            this.pitch = instance.pitch;
            this.onGround = instance.onGround;
        } else if (packet instanceof PacketInChatMessage) {
            PacketInChatMessage instance = (PacketInChatMessage) packet;
            if (instance.message.startsWith("/")) {
                System.out.println(name);
                boolean res = PoweredCube.getInstance().getBukkitServer().dispatchCommand(PoweredCube.getInstance().getBukkitPlayer(name),
                        instance.message);
                if (!res) {
                    sendMessage("Unknown command. Type \"help\" for help.");
                }
            } else {
                sendMessage("<" + name + "> " + instance.message);
            }
        } else if (packet instanceof PacketInClientSettings) {
            PacketInClientSettings instance = (PacketInClientSettings) packet;
            //System.out.println(name + " speaks " + instance.stringToLocale(instance.locale).getDisplayLanguage() + "!");
        } else if (packet instanceof PacketInPlayerBlockPlacement) {
            PacketInPlayerBlockPlacement instance = (PacketInPlayerBlockPlacement) packet;
            if (instance.direction > -1) {
                PacketInPlayerBlockPlacement.Directions dir = PacketInPlayerBlockPlacement.Directions.values()[(int)instance.direction];
                Block block = world.getBlockAt(instance.x + dir.getXAdd(), instance.y+ dir.getYAdd(),
                    instance.z+ dir.getZAdd()).getBukkitBlock();
                block.setTypeId(1);
            } else {
                System.out.println("Right click AIR!");
            }
        } else if (packet instanceof PacketInPlayerDigging) {
            PacketInPlayerDigging instance = (PacketInPlayerDigging) packet;
            if (instance.status == 2) {
                net.jselby.pc.Block block = world.getBlockAt(instance.x, instance.y, instance.z);
                // Finished digging
                if (block.getTypeId() != 0) {
                    if (gamemode != 1) {
                        // Player isn't in creative mode - give them the block!
                        world.spawnFloatingItem(instance.x, instance.y, instance.z, block.getTypeId(), block.getData());
                    }
                    System.out.println(instance.x + ":" + instance.y + ":" + instance.z);
                    block.setTypeId(0);//double x, double y, double z, int id, byte data
                }
            }
        }

        cache.x = x;
        cache.y = y;
        cache.z = z;
        cache.yaw = yaw;
        cache.pitch = pitch;
    }

    public long timeOfDay = 6000;
    @Override
    public void tick() throws IOException {
        tick++;
        // Check that client is loaded areawise
        for (double tempX = x - 100; tempX < x + 100; tempX += 16) {
            for (double tempZ = z - 100; tempZ < z + 100; tempZ += 16) {
                Chunk c = world.getBlockAt((int)tempX, 1, (int)tempZ).chunk;
                if (!loadedChunks.contains(c)) {
                    loadedChunks.add(c);
                    PacketOutChunkData world = new PacketOutChunkData(c.getX(), c.getZ());
                    if (world.data != null && world.data.length != 0) {
                        writePacket(world);
                    }
                }
            }
        }

        if (tick % 20 == 0) {
            //try {
            // PoweredCube.getInstance().getWorlds().get(0).getBlockAt((int)x, (int)y - 2, (int)z)
            //          .setTypeId(id1);
            // } catch (Exception e) {}
            PacketOutTimeUpdate time = new PacketOutTimeUpdate();
            time.timeOfDay = timeOfDay+=20;
            time.ageOfWorld = 0;

            writePacket(time);
        }
        if (tick % 20 == 0) {
            writePacket(new PacketKeepAlive());
            tick = 0;
        }
    }

    @Override
    public void sendMessage(String s) throws IOException {
        ChatMessage msg = ChatMessage.convertToJson(s);
        PacketOutChatMessage resp = new PacketOutChatMessage();
        resp.message = msg;
        writePacket(resp);
    }

    @Override
    public void onDisconnect() throws IOException {
        PoweredCube.getInstance().clients.remove(this);
        PoweredCube.getInstance().players.remove(PoweredCube.getInstance().getBukkitPlayer(name));
    }
}
