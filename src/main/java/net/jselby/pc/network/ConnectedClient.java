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
import net.jselby.pc.PlayerInventory;
import net.jselby.pc.blocks.ItemStack;
import net.jselby.pc.blocks.Material;
import net.jselby.pc.entities.FloatingItem;
import net.jselby.pc.world.Chunk;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.world.World;
import net.jselby.pc.network.packets.mcplay.*;
import org.bukkit.block.Block;

import java.io.IOException;

/**
 * A class representing a connected client. Most networking action will go through here.
 *
 * @author j_selby
 */
public class ConnectedClient extends Client {
    public static final int LOADED_CHUNKS = 12;
    public static final int LOADED_BLOCKS = LOADED_CHUNKS * 16;

    public int tick = 0;
    public World world;
    public int gamemode = 0;

    public int selectedSlot = 0; // begins at inv.slots.length - 9, or 36

    public PlayerInventory inv = new PlayerInventory(this);

    /**
     * Creates a ConnectedClient from a ALREADY existing connection.
     * @param ctx The context of the connection
     */
    public ConnectedClient(ChannelHandlerContext ctx) {
        super(ctx);
        world = PoweredCube.getInstance().getWorlds().get(0); // Default world
    }

    @Override
    public void onPacketReceive(Packet packet) {
        //if (packet.getId() != 3 && packet.getId() != 0 && packet.getId() != 4 && packet.getId() != 5 && packet.getId() != 6)
        //    System.out.println("Got packet: " + packet.getId());
        // Check for the player "cache"
        if (cache == null) {
            cache = world.getPlayerCache(name);
            if (cache.inventory != null) {
                inv.setInventory(cache.inventory);
                inv.updateAll();
            } else {
                // Give starting goods!
                inv.addItem(new ItemStack(Material.DIAMOND_PICKAXE.getId(), (byte)0, 1));
                inv.addItem(new ItemStack(Material.DIAMOND_SPADE.getId(), (byte)0, 1));
                inv.addItem(new ItemStack(Material.DIAMOND_AXE.getId(), (byte)0, 1));
            }
        }

        if (packet instanceof PacketInPlayer) {
            // Check if player is onGround
            PacketInPlayer instance = (PacketInPlayer) packet;
            this.onGround = instance.onGround;

        } else if (packet instanceof PacketInPlayerPosition) {
            // Update the player position
            PacketInPlayerPosition instance = (PacketInPlayerPosition) packet;
            this.x = instance.x;
            this.y = instance.headY;
            this.z = instance.z;
            this.onGround = instance.onGround;

        } else if (packet instanceof PacketInPlayerLook) {
            // Update the player looking location
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
                PacketOutChatMessage msg = new PacketOutChatMessage();
                msg.message = ChatMessage.convertToJson("<" + name + "> " + instance.message);
                PoweredCube.getInstance().distributePacket(
                       msg);
            }

        } else if (packet instanceof PacketInClientSettings) {
            PacketInClientSettings instance = (PacketInClientSettings) packet;

        } else if (packet instanceof PacketInPlayerBlockPlacement) {
            PacketInPlayerBlockPlacement instance = (PacketInPlayerBlockPlacement) packet;
            if (instance.direction > -1) {
                PacketInPlayerBlockPlacement.Directions dir = PacketInPlayerBlockPlacement.Directions.values()[(int)instance.direction];
                Block block = world.getBlockAt(instance.x + dir.getXAdd(), instance.y+ dir.getYAdd(),
                    instance.z+ dir.getZAdd()).getBukkitBlock();

                // Get what player is holding
                Slot slot = inv.getSlot(36 + selectedSlot);
                if (slot != null && slot.itemId != 0) {
                    block.setTypeIdAndData(slot.itemId, slot.itemDamage, true);
                    inv.removeItem(new ItemStack(slot.itemId, slot.itemDamage, 1));
                }
            } else {
                // Right click AIR
            }

        } else if (packet instanceof PacketInPlayerDigging) {
            PacketInPlayerDigging instance = (PacketInPlayerDigging) packet;
            if (instance.status == 2) {
                net.jselby.pc.world.Block block = world.getBlockAt(instance.x, instance.y, instance.z);
                // Finished digging
                if (block.getTypeId() != 0) {
                    if (gamemode != 1) {
                        // Player isn't in creative mode - give them the block!
                        Material mat = Material.getMaterial(block.getTypeId());
                        // Make sure its breakable
                        mat = mat.getBreakBlock();
                        if (mat != null) {
                            world.spawnFloatingItem(instance.x + 0.5, instance.y + 0.5,
                                    instance.z + 0.5, mat.getId(), block.getData());
                        }
                    }
                    block.breakNaturally();
                }
            } else if (instance.status == 0) {
                // Instant breakable blocks (long grass only one?)
                net.jselby.pc.world.Block block = world.getBlockAt(instance.x, instance.y, instance.z);
                if (block.getTypeId() == Material.LONG_GRASS.getId()) {
                    block.breakNaturally();
                }
            }

        } else if (packet instanceof PacketInHeldItemChange) {
            PacketInHeldItemChange instance = (PacketInHeldItemChange) packet;
            selectedSlot = instance.slot;
        }

        // Update the cache
        cache.x = x;
        cache.y = y;
        cache.z = z;
        cache.yaw = yaw;
        cache.pitch = pitch;
        cache.inventory = inv.getArray();
    }

    public long timeOfDay = 6000;
    @Override
    public void tick() {
        tick++;
        // Check that client is loaded area-wise
        for (double tempX = x - (LOADED_BLOCKS / 2); tempX < x + (LOADED_BLOCKS / 2); tempX += 16) {
            for (double tempZ = z - (LOADED_BLOCKS / 2); tempZ < z + (LOADED_BLOCKS / 2); tempZ += 16) {
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
            // Keep the client sync'd up with the world time
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
    public void sendMessage(String s) {
        ChatMessage msg = ChatMessage.convertToJson(s);
        PacketOutChatMessage resp = new PacketOutChatMessage();
        resp.message = msg;
        writePacket(resp);
    }

    @Override
    public void onDisconnect() {
        PoweredCube.getInstance().clients.remove(this);
        PoweredCube.getInstance().players.remove(PoweredCube.getInstance().getBukkitPlayer(name));
    }

    @Override
    public boolean pickUpItem(FloatingItem item) {
        // Send inventory change to player
        ItemStack newItem = new ItemStack(item.getId(), item.getData(), item.count);

        return inv.addItem(newItem) != null;
    }
}
