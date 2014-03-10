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

package net.jselby.pc.player;

import net.jselby.pc.blocks.ItemStack;
import net.jselby.pc.network.Client;
import net.jselby.pc.network.Slot;
import net.jselby.pc.network.packets.mcplay.PacketOutSetSlot;
import org.bukkit.inventory.Inventory;

/**
 * Represents a player's inventory. Contains Slots.
 *
 * @author j_selby
 */
public class PlayerInventory {
    public static final int MAX_STACK_SIZE = 100;

    private Slot[] slots = new Slot[45];
    private Client c;

    public PlayerInventory(Client client) {
        this.c = client;
    }

    public Client getClient() {
        return c;
    }

    public Slot addItem(ItemStack stack) {
        // Find item by type, and increment
        boolean alreadyUpdated = false;
        Slot s = findByType(stack, false);

        if (s == null) {
            // Find a empty slot
            for (int i = slots.length - 9; i < slots.length; i++) {
                if (slots[i] == null) {
                    alreadyUpdated = true;
                    s = slots[i] = new Slot(i, stack.id, stack.count, stack.data);
                    break;
                }
            }
            if (s == null) {
                for (int i = 9; i < slots.length - 9; i++) {
                    if (slots[i] == null) {
                        alreadyUpdated = true;
                        s = slots[i] = new Slot(i, stack.id, stack.count, stack.data);
                        break;
                    }
                }
            }
        }

        if (s == null) {
            return null;
        }

        if (alreadyUpdated) {
            sendUpdate(s);
            return s;
        }

        // Found a slot, do something with it
        if (stack.count + s.itemCount > MAX_STACK_SIZE) {
            ItemStack left = new ItemStack(stack.id, stack.data, stack.count + s.itemCount - MAX_STACK_SIZE);
            s.itemCount += stack.count - left.count;
            sendUpdate(s);
            return addItem(left);
        } else {
            s.itemCount = s.itemCount + stack.count;
            sendUpdate(s);
            return s;
        }
    }

    public void sendUpdate(Slot s) {
        // Updates inventory on client
        PacketOutSetSlot slot = new PacketOutSetSlot();
        slot.slotData = s;
        slot.slot = (short) s.position;
        c.writePacket(slot);
    }

    public void updateAll() {
        for (Slot s : slots) {
            if (s == null) {
                continue;
            }
            PacketOutSetSlot slot = new PacketOutSetSlot();
            slot.slotData = s;
            slot.slot = (short) s.position;
            c.writePacket(slot);
        }
    }

    public Slot findByType(ItemStack stack, boolean allowFull) {
        // under 9 represents armor etc.
        for (int i = 9; i < slots.length; i++) {
            if (slots[i] == null) {
                continue;
            }
            if (slots[i].itemId == stack.id && slots[i].itemDamage == stack.data) {
                if (slots[i].itemCount >= MAX_STACK_SIZE && !allowFull) {
                    continue;
                }
                return slots[i];
            }
        }

        return null;
    }

    public boolean removeItem(ItemStack stack) {
        Slot s = findByType(stack, true);
        if (s == null) {
            return false;
        }

        if (s.itemCount - stack.count < 0) {
            // Under 0!
            int left = stack.count - s.itemCount;
            ItemStack takeLeft = new ItemStack(s.itemId, s.itemDamage, left);
            s.itemCount = 0;
            s.itemId = 0;
            s.itemDamage = 0;
            slots[s.position] = null;
            sendUpdate(s);
            return removeItem(takeLeft);
        } else if (s.itemCount - stack.count < 1) {
            // No more left
            s.itemCount = 0;
            s.itemId = 0;
            s.itemDamage = 0;
            slots[s.position] = null;
            // Assume client worked it out
            return true;
        } else {
            // Plenty left
            s.itemCount -= stack.count;
            sendUpdate(s);
            return true;
        }
    }

    public Slot getSlot(int slot) {
        return slots[slot];
    }

    public void setInventory(Slot[] inventory) {
        slots = inventory;
    }

    public Slot[] getArray() {
        return slots;
    }

    public int getSize() {
        return slots.length;
    }
}
