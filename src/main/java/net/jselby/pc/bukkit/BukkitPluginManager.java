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

import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by James on 2/1/14.
 */
public class BukkitPluginManager implements PluginManager {
    private ArrayList<Permission> permissions = new ArrayList<Permission>();
    private ArrayList<JavaPlugin> plugins = new ArrayList<JavaPlugin>();
    private ArrayList<PluginCommand> commands = new ArrayList<PluginCommand>();

    @Override
    public void addPermission(Permission arg0) {
        if (!permissions.contains(arg0)) {
            permissions.add(arg0);
        }
    }

    @Override
    public void callEvent(Event arg0) throws IllegalStateException {
        System.out.println("Event thrown - " + arg0.getClass().getName());
    }

    @Override
    public void clearPlugins() {
        plugins.clear();
    }

    @Override
    public void disablePlugin(Plugin arg0) {
        arg0.onDisable();
        plugins.remove(arg0);
    }

    @Override
    public void disablePlugins() {
        for (Plugin plugin : plugins.toArray(new Plugin[plugins.size()])) {
            plugin.onDisable();
        }
        clearPlugins();
    }

    @Override
    public void enablePlugin(Plugin arg0) {
        arg0.onEnable();
    }

    @Override
    public Set<Permissible> getDefaultPermSubscriptions(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Permission> getDefaultPermissions(boolean arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Permission getPermission(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Permissible> getPermissionSubscriptions(String arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Permission> getPermissions() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Plugin getPlugin(String arg0) {
        for (Plugin plugin : getPlugins()) {
            if (plugin.getName().equalsIgnoreCase(arg0)) {
                return plugin;
            }
        }
        return null;
    }

    @Override
    public Plugin[] getPlugins() {
        return plugins.toArray(new Plugin[plugins.size()]);
    }

    @Override
    public boolean isPluginEnabled(String arg0) {
        return true;
    }

    @Override
    public boolean isPluginEnabled(Plugin arg0) {
        return arg0.isEnabled();
    }

    @Override
    public Plugin loadPlugin(File arg0) throws InvalidPluginException,
            InvalidDescriptionException, UnknownDependencyException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Plugin[] loadPlugins(File arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void recalculatePermissionDefaults(Permission arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEvent(Class<? extends Event> arg0, Listener arg1,
                              EventPriority arg2, EventExecutor arg3, Plugin arg4) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEvent(Class<? extends Event> arg0, Listener arg1,
                              EventPriority arg2, EventExecutor arg3, Plugin arg4, boolean arg5) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerEvents(Listener arg0, Plugin arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerInterface(Class<? extends PluginLoader> arg0)
            throws IllegalArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePermission(Permission arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removePermission(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToDefaultPerms(boolean arg0, Permissible arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribeToPermission(String arg0, Permissible arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unsubscribeFromDefaultPerms(boolean arg0, Permissible arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unsubscribeFromPermission(String arg0, Permissible arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean useTimings() {
        // TODO Auto-generated method stub
        return false;
    }

    public void registerPlugin(JavaPlugin plugin) {
        plugins.add(plugin);
    }

    public PluginCommand addCommand(PluginCommand command) {
        if (!commands.contains(command)) {
            commands.add(command);
        }
        System.out.println("Created command: " + command.getName());
        return command;
    }

    public PluginCommand getCommand(String name) {
        System.out.println("Got command - " + name);
        for (PluginCommand command : commands
                .toArray(new PluginCommand[commands.size()])) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
        }
        // Find plugin
        Class<PluginCommand> pluginClass = PluginCommand.class;
        Constructor<?> init = pluginClass.getDeclaredConstructors()[0];
        init.setAccessible(true);

        try {
            return addCommand((PluginCommand) init.newInstance(name, null));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public boolean callCommand(CommandSender sender, String name, String[] args) {
        try {
            for (Plugin p : getPlugins()) {
                for (String s : args) {
                    System.out.println("Arg: " + s);
                }
                return p.onCommand(sender, new BukkitCommand(name, (String)p.getConfig().get("commands." + name + ".description", ""),
                    (String)p.getConfig().get("commands." + name + ".usage", ""), new ArrayList<String>()), name, args);
            //if (p.getConfig().isSet("commands." + name)) {
            //    System.out.println("Found plugin to handle command!");
            //    return p.onCommand(sender, new BukkitCommand(name, (String)p.getConfig().get("commands." + name + ".description", ""),
            //            (String)p.getConfig().get("commands." + name + ".usage", ""), null), name, args);
            //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
