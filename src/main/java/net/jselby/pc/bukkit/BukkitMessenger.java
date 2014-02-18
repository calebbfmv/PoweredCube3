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

package net.jselby.pc.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.plugin.messaging.PluginMessageListenerRegistration;

import java.util.Set;

/**
 * Created by James on 2/1/14.
 */
public class BukkitMessenger implements Messenger {
    @Override
    public boolean isReservedChannel(String s) {
        return false;
    }

    @Override
    public void registerOutgoingPluginChannel(Plugin plugin, String s) {

    }

    @Override
    public void unregisterOutgoingPluginChannel(Plugin plugin, String s) {

    }

    @Override
    public void unregisterOutgoingPluginChannel(Plugin plugin) {

    }

    @Override
    public PluginMessageListenerRegistration registerIncomingPluginChannel(Plugin plugin, String s, PluginMessageListener pluginMessageListener) {
        return null;
    }

    @Override
    public void unregisterIncomingPluginChannel(Plugin plugin, String s, PluginMessageListener pluginMessageListener) {

    }

    @Override
    public void unregisterIncomingPluginChannel(Plugin plugin, String s) {

    }

    @Override
    public void unregisterIncomingPluginChannel(Plugin plugin) {

    }

    @Override
    public Set<String> getOutgoingChannels() {
        return null;
    }

    @Override
    public Set<String> getOutgoingChannels(Plugin plugin) {
        return null;
    }

    @Override
    public Set<String> getIncomingChannels() {
        return null;
    }

    @Override
    public Set<String> getIncomingChannels(Plugin plugin) {
        return null;
    }

    @Override
    public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin) {
        return null;
    }

    @Override
    public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(String s) {
        return null;
    }

    @Override
    public Set<PluginMessageListenerRegistration> getIncomingChannelRegistrations(Plugin plugin, String s) {
        return null;
    }

    @Override
    public boolean isRegistrationValid(PluginMessageListenerRegistration pluginMessageListenerRegistration) {
        return false;
    }

    @Override
    public boolean isIncomingChannelRegistered(Plugin plugin, String s) {
        return false;
    }

    @Override
    public boolean isOutgoingChannelRegistered(Plugin plugin, String s) {
        return false;
    }

    @Override
    public void dispatchIncomingMessage(Player player, String s, byte[] bytes) {

    }
}
