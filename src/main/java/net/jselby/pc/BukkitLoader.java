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

package net.jselby.pc;

import net.jselby.pc.bukkit.BukkitPluginManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
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
    public static final String PREFIX = "BC";

    public BukkitLoader() {
        // Verify we are compatable with this version
        boolean isSilenced = false;
        try {
            JSONObject obj = JsonParser
                    .readJsonFromUrl("http://dl.bukkit.org/api/1.0/downloads/projects/"
                            + "craftbukkit/view/latest/?_accept=application%2Fjson");
            String ver = (String) obj.get("version");
            if (!PoweredCube.BUKKIT_VERSION.split("-")[0].equalsIgnoreCase(ver
                    .split("-")[0])) {
                System.out.println("[" + PREFIX
                        + "]: [WARNING] Bukkit version \"" + ver
                        + "\" is not compatable with");
                System.out.println("[" + PREFIX
                        + "]: [WARNING] this version"
                        + " of PoweredCube (we are built for \""
                        + PoweredCube.BUKKIT_VERSION + "\")!");
                System.out.println("[" + PREFIX
                        + "]: [WARNING] Be warned: errors may occur!");
                isSilenced = true;
            }
        } catch (Exception e1) {
            System.err.println("[" + PREFIX
                    + "]: Error while checking Bukkit version:");
            e1.printStackTrace();
        }
        File jar = new File("bukkit.jar");
        if (!jar.exists()) {
            System.out.println("[" + PREFIX + "]: "
                    + "Downloading Bukkit version "
                    + PoweredCube.BUKKIT_DL_VERSION + "...");

            try {
                URL bukkit = new URL(
                        "http://dl.bukkit.org/downloads/bukkit/get/"
                                + PoweredCube.BUKKIT_DL_VERSION
                                + "/bukkit-dev.jar");
                ReadableByteChannel rbc = Channels.newChannel(bukkit
                        .openStream());
                FileOutputStream fos = new FileOutputStream("bukkit.jar");
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

        }

        try {
            addToClasspath(jar);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Bukkit.setServer((Server) PoweredCube.getInstance().getBukkitServer());
        // Lets see if we can access a Bukkit method
        if (Bukkit.getBukkitVersion() != PoweredCube.BUKKIT_VERSION
                && !isSilenced) {
            throw new IllegalStateException(
                    "Downloaded Bukkit version isn't the "
                            + "version PoweredCube requested!");
        }
    }

    public static void addToClasspath(File file) throws Exception {
        Method method = URLClassLoader.class.getDeclaredMethod("addURL",
                new Class[] { URL.class });
        method.setAccessible(true);
        method.invoke(ClassLoader.getSystemClassLoader(), new Object[] { file
                .toURI().toURL() });
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
                addToClasspath(file);
                Class<JavaPlugin> pluginClass = (Class<JavaPlugin>) Class
                        .forName(bukkitClass);
                JavaPlugin plugin = pluginClass.newInstance();
                pluginClass = (Class<JavaPlugin>) plugin.getClass()
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
