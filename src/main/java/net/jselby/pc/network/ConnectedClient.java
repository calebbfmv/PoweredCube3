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
import net.jselby.pc.player.ChatMessage;
import net.jselby.pc.player.PlayerInventory;
import net.jselby.pc.blocks.ItemStack;
import net.jselby.pc.blocks.Material;
import net.jselby.pc.entities.FloatingItem;
import net.jselby.pc.world.Chunk;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.world.World;
import net.jselby.pc.network.packets.mcplay.*;
import org.bukkit.block.Block;

import java.util.ArrayList;

/**
 * A class representing a connected client. Most networking action will go through here.
 *
 * @author j_selby
 */
public class ConnectedClient extends Client {

    public int maxLoadedChunks = 4;
    public int maxLoadedBlocks = maxLoadedChunks * 16;

    public int tick = 0;
    public int gamemode = 0;
    public long loggedIn = 0;

    public int selectedSlot = 0; // begins at inv.slots.length - 9, or 36

    public PlayerInventory inv = new PlayerInventory(this);
    public float health = 20;

    /**
     * Creates a ConnectedClient from a ALREADY existing connection.
     * @param ctx The context of the connection
     */
    public ConnectedClient(ChannelHandlerContext ctx, String name) {
        super(ctx);
        world = PoweredCube.getInstance().getWorlds().get(0); // Default world
        loadCache(name);
    }

    @Override
    public void onPacketReceive(Packet packet) {
        double lastX = x;
        double lastY = y;
        double lastZ = z;
        float lastYaw = yaw;
        float lastPitch = pitch;
        int lastSelectedSlot = selectedSlot;
        //if (packet.getId() != 3 && packet.getId() != 0 && packet.getId() != 4 && packet.getId() != 5 && packet.getId() != 6)
        //    System.out.println("Got packet: " + packet.getId());

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
            System.out.println("View distance: " + instance.viewDistance);
            maxLoadedChunks = ((int)instance.viewDistance);
            if (maxLoadedChunks > PoweredCube.getInstance().getMaxViewDistance()) {
                maxLoadedChunks = PoweredCube.getInstance().getMaxViewDistance();
            }
            System.out.println("Client view distance " + instance.viewDistance + ", set to " + maxLoadedChunks);
            maxLoadedBlocks = maxLoadedChunks * 16;


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

        } else if (packet instanceof PacketInAnimation) {
            PacketInAnimation instance = (PacketInAnimation) packet;
            if (instance.animation == 1) {
                PacketOutAnimation out = new PacketOutAnimation();
                out.entityId = id;
                out.animationId = 0;
                ArrayList<Client> excludes = new ArrayList<Client>();
                excludes.add(this);
                PoweredCube.getInstance().distributePacket(out, excludes);
            }
        }

        // Update the cache
        cache.x = x;
        cache.y = y;
        cache.z = z;
        cache.yaw = yaw;
        cache.pitch = pitch;
        cache.inventory = inv.getArray();

        // Update position on other clients
        if (lastX != x || lastY != y || lastZ != z || lastYaw != yaw || lastPitch != pitch) {
            double differenceX = x - lastX;
            double differenceY = y - lastY;
            double differenceZ = z - lastZ;

            if (differenceX > 4 || differenceX < -4 ||
                    differenceY > 4 || differenceY < -4 ||
                    differenceZ > 4 || differenceZ < -4) {
                System.out.println("Teleport!");
            } else {
                PacketOutEntityLookRelativeMove movement = new PacketOutEntityLookRelativeMove();


                movement.pitch = (byte) (Math.toRadians(pitch) * 10 * 4);
                movement.yaw = (byte) (Math.toRadians(yaw) * 10 * 4);

                movement.entityId = id;
                movement.x = (byte) (differenceX * 36);
                movement.y = (byte) (differenceY * 36);
                movement.z = (byte) (differenceZ * 36);
                ArrayList<Client> excludes = new ArrayList<Client>();
                excludes.add(this);

                PacketOutEntityHeadLook look = new PacketOutEntityHeadLook();
                look.entityId = id;
                look.pitch = pitch;
                look.yaw = yaw;

                PoweredCube.getInstance().distributePacket(movement, excludes);
                PoweredCube.getInstance().distributePacket(look, excludes);
            }
        }

        // Update equipment on other clients
        if (!(lastSelectedSlot == selectedSlot)) {
            PacketOutEntityEquipment equipment = new PacketOutEntityEquipment();
            Slot s = inv.getSlot(inv.getSize() - 9 + selectedSlot);
            if (s == null) {
                equipment.item = new Slot(-1, -1, (byte)0);
            } else {
                equipment.item = s;
            }
            equipment.entityId = id;
            equipment.position = 0;
            ArrayList<Client> excludes = new ArrayList<Client>();
            excludes.add(this);
            PoweredCube.getInstance().distributePacket(equipment, excludes);
        }
    }

    public long timeOfDay = 6000;
    @Override
    public void tick() {
        tick++;
        loggedIn++;
        // Check that client is loaded area-wise
        if (tick % 20 == 0) {
            // Load chunks that are needed
            ArrayList<Chunk> loadingChunks = (ArrayList<Chunk>) loadedChunks.clone();

            for (int tempX = (int) (Math.floor(x / 16) - (maxLoadedChunks)); tempX < Math.floor(x / 16) + (maxLoadedChunks); tempX++) {
                for (int tempZ = (int) (Math.floor(z / 16) - (maxLoadedChunks)); tempZ < Math.floor(z / 16) + (maxLoadedChunks); tempZ++) {
                    if (!world.isChunkLoaded((int)tempX, (int)tempZ)) {
                        // Don't force the chunk load, let it happen by the ChunkLoadThread
                        world.requestChunkLoad((int)tempX, (int)tempZ);
                        continue;
                    }

                    Chunk c = world.getChunkAt((int)tempX, (int)tempZ);
                    loadingChunks.remove(c);

                    if (!loadedChunks.contains(c)) {
                        loadedChunks.add(c);
                        // Send data to client
                        PacketOutChunkData world = new PacketOutChunkData(c.getX(), c.getZ(), false);
                        if (world.data != null && world.data.length != 0) {
                            writePacket(world);
                        }
                    }
                }
            }

            // Find chunks to unload
            for (Chunk c : loadingChunks.toArray(new Chunk[loadingChunks.size()])) {
                // Give the client a few ticks to load first
                if (c == null) {
                    continue;
                }
                if (loggedIn > 200) {
                    //System.out.println("Unloading chunk: " + c);
                    /*loadedChunks.remove(c);
                    PacketOutChunkData world = new PacketOutChunkData(c.getX(), c.getZ(), true);
                    if (world.data != null) {
                        writePacket(world);
                    }*/
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

    private void loadCache(String name) {
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
}
