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

import net.jselby.pc.JsonParser;
import net.jselby.pc.PoweredCube;
import net.jselby.pc.bukkit.BukkitPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;

/**
 * Created by James on 2/1/14.
 */
public class BukkitLoader {
    public BukkitLoader() {
        // We no longer need to download Bukkit
        Bukkit.setServer(PoweredCube.getInstance().getBukkitServer());
    }

    public static void addToClasspath(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[]{file
                .toURI().toURL()});
    }

    @SuppressWarnings("unchecked")
    public void loadPlugin(File file) throws ZipException, IOException,
            InvalidPluginException, InvalidDescriptionException {
        if (file.getName().endsWith(".jar")) {
            JarFile jarFile = new JarFile(file);

            URL[] urls = { new URL("jar:file:" + file + "!/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            ZipEntry bukkit = jarFile.getEntry("plugin.yml");
            String bukkitClass = "";
            PluginDescriptionFile reader = null;
            if (bukkit != null) {
                reader = new PluginDescriptionFile(new InputStreamReader(
                        jarFile.getInputStream(bukkit)));
                bukkitClass = reader.getMain();
                System.out
                        .println("Loading plugin " + reader.getName() + "...");
            }

            jarFile.close();

            try {
                //addToClasspath(file);
                JavaPluginLoader pluginLoader = new JavaPluginLoader(PoweredCube.getInstance().getBukkitServer());
                Plugin plugin = pluginLoader.loadPlugin(file);
                Class<JavaPlugin> pluginClass = (Class<JavaPlugin>) plugin.getClass()
                        .getSuperclass();
                for (Method method : pluginClass.getDeclaredMethods()) {
                    if (method.getName().equalsIgnoreCase("init")
                            || method.getName().equalsIgnoreCase("initialize")) {
                        method.setAccessible(true);
                        method.invoke(plugin, null, PoweredCube.getInstance().getBukkitServer(),
                                reader, new File("plugins" + File.separator
                                + reader.getName()), file, cl);
                    }
                }

                // Load the plugin, using its default methods
                BukkitPluginManager mgr = (BukkitPluginManager) Bukkit
                        .getPluginManager();
                mgr.registerPlugin((JavaPlugin) plugin);
                plugin.onLoad();
                plugin.onEnable();
            } catch (Exception e1) {
                e1.printStackTrace();
            } catch (Error e1) {
                e1.printStackTrace();
            }

            jarFile.close();
        }
    }

    public void loadPlugins() {
        File pluginsDir = new File("plugins");
        if (!pluginsDir.exists()) {
            pluginsDir.mkdirs();
        }
        if (!pluginsDir.isDirectory()) {
            throw new Error("Plugins \"folder\" is a file!");
        }
        for (File file : pluginsDir.listFiles()) {
            try {
                loadPlugin(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
