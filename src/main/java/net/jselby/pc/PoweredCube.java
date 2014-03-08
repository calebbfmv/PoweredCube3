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

import net.jselby.pc.bukkit.BukkitLoader;
import net.jselby.pc.bukkit.BukkitPlayer;
import net.jselby.pc.bukkit.BukkitPluginManager;
import net.jselby.pc.bukkit.BukkitServer;
import net.jselby.pc.network.Client;
import net.jselby.pc.network.NetworkServer;
import net.jselby.pc.network.Packet;
import net.jselby.pc.network.PacketDefinitions;
import net.jselby.pc.world.World;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by James on 1/31/14.
 */
public class PoweredCube {
    public final static String BUKKIT_VERSION = "1.7.2-R0.2";
    public final static String MC_VERSION = "1.7.4";

    private static PoweredCube instance;
    private Server bukkitServer;
    private BukkitLoader bukkit;
    private Logger logger;
    private BukkitPluginManager pl;
    private Thread mainThread;

    // Vanilla variables
    private PacketDefinitions definitions;
    private NetworkServer net;
    private ArrayList<World> worlds;
    public ArrayList<Client> clients = new ArrayList<Client>();
    public ArrayList<BukkitPlayer> players = new ArrayList<BukkitPlayer>();

    private int maxPlayers = 50;
    private int nextEntityID = 0;

    // End vanilla variables

    public PoweredCube(int port) throws Exception {
        mainThread = Thread.currentThread();

        logger = new Logger(System.out);
        System.setOut(new PrintWrapper(logger));
        System.setErr(new ErrorWrapper(new Logger(System.err)));

        PoweredCube.instance = this;

        System.out.println("Starting PoweredCube...");

        // Create Bukkit server
        bukkitServer = new BukkitServer();
        pl = new BukkitPluginManager();

        // Load Minecraft world (if it exists)
        /*File worldIndex = new File("world" + File.separator + "level.dat");
        if (worldIndex.exists()) {
            System.out.println("Loading NBT world...");
            VanillaWorldLoader.loadWorld(worldIndex);
        }*/

        // Create/load world
        worlds = new ArrayList<World>();

        if (PoweredCubeWorldLoader.worldExists("world")) {
            World w = PoweredCubeWorldLoader.loadWorld("world");
            if (w == null) {
                System.err.println("World could not be loaded - check file!");
                System.exit(0);
            }
            worlds.add(w);
        } else {
            World w = new World("world");
            worlds.add(w);
            //PoweredCubeWorldLoader.saveWorld(w);
        }

        // Load Bukkit plugins
        bukkit = new BukkitLoader();
        bukkit.loadPlugins();

        // Create PacketDefinitions
        definitions = new PacketDefinitions("net.jselby.pc.network.packets");

        // Create network handler
        net = new NetworkServer(port);
        net.run();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("Saving world...");
                for (World world : worlds) {
                    //PoweredCubeWorldLoader.saveWorld(world);
                }
            }
        });

        System.out.println("Finished loading.");

        // Tick thread
        long maxWorkingTimePerFrame = 1000 / 20;
        long lastStartTime = System.currentTimeMillis();
        while(true) {
            long elapsedTime = System.currentTimeMillis() - lastStartTime;
            lastStartTime = System.currentTimeMillis();

            tick();

            long processingTimeForCurrentFrame = System.currentTimeMillis() - lastStartTime;
            if(processingTimeForCurrentFrame  < maxWorkingTimePerFrame) {
                try {
                    Thread.sleep(maxWorkingTimePerFrame - processingTimeForCurrentFrame);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void tick() {
        for (Client c : clients) {
            c.tick();
        }

        for (World w : worlds) {
            w.tick();
        }
    }

    public static void main(String[] args) throws Exception {
        PoweredCube server = new PoweredCube(25565);
        System.exit(0);
    }

    public static PoweredCube getInstance() {
        return instance;
    }

    public PacketDefinitions getPacketDefinitions() {
        return definitions;
    }

    public ArrayList<World> getWorlds() {
        return worlds;
    }

    public Server getBukkitServer() {
        return bukkitServer;
    }

    public NetworkServer getNetworkServer() {
        return net;
    }

    public Logger getLogger() {
        return logger;
    }

    public BukkitPluginManager getPluginManager() {
        return pl;
    }

    public String getServerName() {
        return "PoweredCube³";
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public Player getBukkitPlayer(String s) {
        for (BukkitPlayer p : players.toArray(new BukkitPlayer[players.size()])) {
            if (p.getName().equalsIgnoreCase(s)) {
                return p;
            }
        }
        return null;
    }

    public void distributePacket(Packet packet) {
        for (Client p : clients.toArray(new Client[clients.size()])) {
            p.writePacket(packet);
        }
    }

    public Thread getMainThread() {
        return mainThread;
    }

    public World getWorld(String s) {
        for (World w : worlds.toArray(new World[worlds.size()])) {
            if (w.getName().equalsIgnoreCase(s)) {
                return w;
            }
        }
        return null;
    }

    public int getNextEntityID() {
        return nextEntityID++;
    }
}
