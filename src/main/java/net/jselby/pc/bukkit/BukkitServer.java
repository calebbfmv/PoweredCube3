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

import com.avaje.ebean.config.ServerConfig;
import net.jselby.pc.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.SimpleServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class BukkitServer implements Server {
    SimpleServicesManager mgr = new SimpleServicesManager();
    BukkitHelpMap helpmap = new BukkitHelpMap();
    BukkitMessenger messenger = new BukkitMessenger();
    BukkitCommandMap commands /**cough* worldedit *cough requires commandMap */ = new BukkitCommandMap();
    net.jselby.pc.bukkit.BukkitScheduler scheduler = new net.jselby.pc.bukkit.BukkitScheduler();

    @Override
    public String getName() {
        return PoweredCube.getInstance().getServerName();
    }

    @Override
    public String getVersion() {
        return PoweredCube.MC_VERSION;
    }

    @Override
    public String getBukkitVersion() {
        return PoweredCube.BUKKIT_VERSION;
    }

    @Override
    public Player[] getOnlinePlayers() {
        return PoweredCube.getInstance().players.toArray(new Player[]{});
    }

    @Override
    public int getMaxPlayers() {
        return PoweredCube.getInstance().getMaxPlayers();
    }

    @Override
    public int getPort() {
        return PoweredCube.getInstance().getNetworkServer().port; // TODO: Add configuration option for this
    }

    @Override
    public int getViewDistance() {
        return net.jselby.pc.world.World.INITIAL_SIZE;
    }

    @Override
    public String getIp() {
        return "0.0.0.0"; // TODO: Add configuration option for this
    }

    @Override
    public String getServerName() {
        return "A PoweredCube Server"; // TODO: Add configuration option for this
    }

    @Override
    public String getServerId() {
        return ""; // TODO: Add configuration option for this
    }

    @Override
    public String getWorldType() {
        return WorldType.FLAT.getName();  // TODO: Implement actual world generation
    }

    @Override
    public boolean getGenerateStructures() {
        return false; // TODO: Implement actual world generation
    }

    @Override
    public boolean getAllowEnd() {
        return false; // TODO: Implement actual world generation
    }

    @Override
    public boolean getAllowNether() {
        return false; // TODO: Implement actual world generation
    }

    @Override
    public boolean hasWhitelist() {
        return false; // TODO: Add configuration option for this
    }

    @Override
    public void setWhitelist(boolean b) {
        // TODO: Add configuration option for this
    }

    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return new HashSet<OfflinePlayer>();  // TODO: Add configuration option for this
    }

    @Override
    public void reloadWhitelist() {
        // TODO: Implement whitelist
    }

    @Override
    public int broadcastMessage(String s) {
        // TODO: Implement this
        return 0;
    }

    @Override
    public String getUpdateFolder() {
        return getUpdateFolderFile().getName();
    }

    @Override
    public File getUpdateFolderFile() {
        return new File("update");
    }

    @Override
    public long getConnectionThrottle() {
        return 0;  // TODO: Add configuration option for this
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        return 0;  // TODO: Add configuration option for this
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        return 0;  // TODO: Add configuration option for this
    }

    @Override
    public Player getPlayer(String s) {
        return PoweredCube.getInstance().getBukkitPlayer(s);
    }

    @Override
    public Player getPlayerExact(String s) {
        return PoweredCube.getInstance().getBukkitPlayer(s);
    }

    @Override
    public List<Player> matchPlayer(String s) {
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(getPlayer(s));
        return players;
    }

    @Override
    public PluginManager getPluginManager() {
        return PoweredCube.getInstance().getPluginManager();
    }

    @Override
    public BukkitScheduler getScheduler() {
        return scheduler;
    }

    @Override
    public ServicesManager getServicesManager() {
        return mgr;
    }

    @Override
    public List<World> getWorlds() {
        ArrayList<World> worlds = new ArrayList<World>();
        for (net.jselby.pc.world.World selectWorld : PoweredCube.getInstance().getWorlds().toArray(new net.jselby.pc.world.World[]{})) {
            worlds.add(selectWorld.getBukkitWorld());
        }
        return worlds;
    }

    @Override
    public World createWorld(WorldCreator worldCreator) {
        // TODO: Implement this
        return null;
    }

    @Override
    public boolean unloadWorld(String s, boolean b) {
        // TODO: Implement this
        return false;
    }

    @Override
    public boolean unloadWorld(World world, boolean b) {
        // TODO: Implement this
        return false;
    }

    @Override
    public World getWorld(String s) {
        PoweredCube.getInstance().getWorld(s);
        return null;
    }

    @Override
    public World getWorld(UUID uuid) {
        // TODO: Implement this
        return null;
    }

    @Override
    public MapView getMap(short i) {
        // TODO: Implement this
        return null;
    }

    @Override
    public MapView createMap(World world) {
        // TODO: Implement this
        return null;
    }

    @Override
    public void reload() {
        // TODO: Implement this
    }

    @Override
    public Logger getLogger() {
        return PoweredCube.getInstance().getLogger();
    }

    @Override
    public PluginCommand getPluginCommand(String s) {
        // TODO: Implement this
        return null;
    }

    @Override
    public void savePlayers() {
        // TODO: Implement this
    }

    @Override
    public boolean dispatchCommand(CommandSender sender, String s) throws CommandException {
        System.out.println("Dispatch command: " + s + ", " + sender.getClass().getName());
        if (sender instanceof Player) {
            PlayerCommandPreprocessEvent preprocessEvent = new PlayerCommandPreprocessEvent((Player)sender, s);
            getPluginManager().callEvent(preprocessEvent);
            if (preprocessEvent.isCancelled()) {
               System.out.println("Player chat event cancelled!");
               return false;
            }
        }
        String command = s.substring(1);
        String name = s.split(" ")[0];
        String[] args;
        if (command.contains(" ")) {
            args = command.substring(name.length()).split(" ");
        } else {
            args = new String[]{};
        }
        return PoweredCube.getInstance().getPluginManager().callCommand(sender, name, args);
    }

    @Override
    public void configureDbConfig(ServerConfig serverConfig) {
        // TODO: Implement this
    }

    @Override
    public boolean addRecipe(Recipe recipe) {
        // TODO: Implement this
        return false;
    }

    @Override
    public List<Recipe> getRecipesFor(ItemStack itemStack) {
        // TODO: Implement this
        return null;
    }

    @Override
    public Iterator<Recipe> recipeIterator() {
        // TODO: Implement this
        return null;
    }

    @Override
    public void clearRecipes() {
        // TODO: Implement this
    }

    @Override
    public void resetRecipes() {
        // TODO: Implement this
    }

    @Override
    public Map<String, String[]> getCommandAliases() {
        // TODO: Implement this
        return null;
    }

    @Override
    public int getSpawnRadius() {
        // TODO: Implement this
        return 0;
    }

    @Override
    public void setSpawnRadius(int i) {
        // TODO: Implement this
    }

    @Override
    public boolean getOnlineMode() {
        return false; // TODO: Implement this
    }

    @Override
    public boolean getAllowFlight() {
        return false; // TODO: Implement this
    }

    @Override
    public boolean isHardcore() {
        return false; // TODO: Implement this
    }

    @Override
    public boolean useExactLoginLocation() {
        return false; // TODO: Implement this
    }

    @Override
    public void shutdown() {
        // TODO: Implement this
    }

    @Override
    public int broadcast(String s, String s2) {
        return 0;  // TODO: Implement this
    }

    @Override
    public OfflinePlayer getOfflinePlayer(String s) {
        return null; // TODO: Implement this
    }

    @Override
    public Set<String> getIPBans() {
        return null; // TODO: Add configuration option for this
    }

    @Override
    public void banIP(String s) {
        // TODO: Add configuration option for this
    }

    @Override
    public void unbanIP(String s) {
        // TODO: Add configuration option for this
    }

    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        return null; // TODO: Add configuration option for this
    }

    @Override
    public Set<OfflinePlayer> getOperators() {
        return null; // TODO: Add configuration option for this
    }

    @Override
    public GameMode getDefaultGameMode() {
        return GameMode.SURVIVAL; // TODO: Implement gamemode switching
    }

    @Override
    public void setDefaultGameMode(GameMode gameMode) {
        // TODO: Implement gamemode switching
    }

    @Override
    public ConsoleCommandSender getConsoleSender() {
        return null;
    }

    @Override
    public File getWorldContainer() {
        return null;
    }

    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        return new OfflinePlayer[0];
    }

    @Override
    public Messenger getMessenger() {
        return messenger;
    }

    @Override
    public HelpMap getHelpMap() {
        return helpmap;
    }

    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, InventoryType inventoryType) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, int i) {
        return null;
    }

    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, int i, String s) {
        return null;
    }

    @Override
    public int getMonsterSpawnLimit() {
        return 0;
    }

    @Override
    public int getAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public int getAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public boolean isPrimaryThread() {
        return Thread.currentThread() == PoweredCube.getInstance().getMainThread();
    }

    @Override
    public String getMotd() {
        return null;
    }

    @Override
    public String getShutdownMessage() {
        return null;
    }

    @Override
    public Warning.WarningState getWarningState() {
        return null;
    }

    @Override
    public ItemFactory getItemFactory() {
        return null;
    }

    @Override
    public ScoreboardManager getScoreboardManager() {
        return null;
    }

    @Override
    public CachedServerIcon getServerIcon() {
        return null;
    }

    @Override
    public CachedServerIcon loadServerIcon(File file) throws Exception {
        return null;
    }

    @Override
    public CachedServerIcon loadServerIcon(BufferedImage bufferedImage) throws Exception {
        return null;
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes) {

    }

    @Override
    public Set<String> getListeningPluginChannels() {
        return null;
    }
}
