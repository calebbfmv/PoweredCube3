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

import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Defines what Packets exist for the protocol we are talking about.
 */
public class PacketDefinitions {
    private ConcurrentHashMap<State, ArrayList<PacketWrapper>> packets = new ConcurrentHashMap<State, ArrayList<PacketWrapper>>();

    public PacketDefinitions(String packageToSearch) {
        search(packageToSearch);
    }

    private void search(String packageToSearch) {
        //System.out.println("Searching for packets in package \"" + packageToSearch + "\"...");

        // Search using Reflections & Javassist
        Reflections reflections = new Reflections(packageToSearch);
        Set<Class<? extends Packet>> classes = reflections.getSubTypesOf(Packet.class);
        for (Class<? extends Packet> foundClass : classes) {
            // Create a instance of the packet, to find out what it is

            try {
                PacketWrapper wrapper = new PacketWrapper(foundClass);
                //System.out.println("Found packet: " + foundClass.getName() + " (ID: " + convertToHexString(wrapper.getId()) + ", State: " + wrapper.getState() + ")");

                // Debug print some information about the packet

                if (!wrapper.getRegisterForIncoming()) {
                    continue;
                }

                // Add the packet to the tempPackets list
                if (!packets.containsKey(wrapper.getState())) {
                    // Create a ArrayList for this State
                    ArrayList<PacketWrapper> stateList = new ArrayList<PacketWrapper>();

                    // Add the packet to the list
                    stateList.add(wrapper);

                    // And add the stateList to the tempPackets list
                    packets.put(wrapper.getState(), stateList);
                } else {
                    // Obtain the old array, and add this packet
                    packets.get(wrapper.getState()).add(wrapper);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Class<? extends Packet> searchForPacket(State state, int id) {
        if (state == null) {
            return null;
        }
        if (!packets.containsKey(state)) {
            return null;
        }
        for (PacketWrapper wrapper : packets.get(state)) {
            if (state != wrapper.getState()) {
                System.err.println("Something horrible has happened: A packet got lost!");
                packets.remove(state);
                continue;
            }
            if (id == wrapper.getId()) {
                return wrapper.getPacket();
            }
        }
        return null;
    }

    public static String convertToHexString(int id) {
        return "0x" + String.format("%02X", id & 0xFFFFF);
    }

    public static enum State {
        DEFAULT, PLAY, LOGIN, PING
    }
}
