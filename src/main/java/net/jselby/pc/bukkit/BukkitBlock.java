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

import net.jselby.pc.world.Block;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Created by James on 2/2/14.
 */
public class BukkitBlock implements org.bukkit.block.Block, Serializable {
    private Block block;

    public BukkitBlock(Block block) {
        this.block = block;
    }

    @Override
    public byte getData() {
        return block.getData();
    }

    @Override
    public org.bukkit.block.Block getRelative(int x, int y, int z) {
        return block.chunk.world.getBlockAt(getX() + x, getY() + y, getZ() + z).getBukkitBlock();
    }

    @Override
    public org.bukkit.block.Block getRelative(BlockFace bf) {
        return getRelative(bf.getModX(), bf.getModY(), bf.getModZ());
    }

    @Override
    public org.bukkit.block.Block getRelative(BlockFace bf, int i) {
        return getRelative(bf.getModX() * i, bf.getModY() * i, bf.getModZ() * i);
    }

    @Override
    public Material getType() {
        return Material.getMaterial(block.getTypeId());
    }

    @Override
    public int getTypeId() {
        return block.getTypeId();
    }

    @Override
    public byte getLightLevel() {
        return 0;
    }

    @Override
    public byte getLightFromSky() {
        return 0;
    }

    @Override
    public byte getLightFromBlocks() {
        return 0;
    }

    @Override
    public World getWorld() {
        return block.chunk.world.getBukkitWorld();
    }

    @Override
    public int getX() {
        return block.x;
    }

    @Override
    public int getY() {
        return block.y;
    }

    @Override
    public int getZ() {
        return block.z;
    }

    @Override
    public Location getLocation() {
        return new Location(getWorld(), getX(), getY(), getZ());
    }

    @Override
    public Location getLocation(Location location) {
        return getLocation().add(location);
    }

    @Override
    public Chunk getChunk() {
        return block.chunk.getBukkitChunk();
    }

    @Override
    public void setData(byte b) {
        setData(b, true);
    }

    @Override
    public void setData(byte b, boolean update) {
        block.setData(b, update);
    }

    @Override
    public void setType(Material material) {
        setTypeId(material.getId());
    }

    @Override
    public boolean setTypeId(int i) {
        return setTypeId(i, true);
    }

    @Override
    public boolean setTypeId(int i, boolean b) {
        block.setTypeId(i, b);
        return true;
    }

    @Override
    public boolean setTypeIdAndData(int i, byte b, boolean update) {
        setTypeId(i, false);
        setData(b, update);
        return true;
    }

    @Override
    public BlockFace getFace(org.bukkit.block.Block block) {
        return BlockFace.NORTH; // TODO: Fix face
    }

    @Override
    public BlockState getState() {
        return null;
    }

    @Override
    public Biome getBiome() {
        return null;
    }

    @Override
    public void setBiome(Biome biome) {

    }

    @Override
    public boolean isBlockPowered() {
        return false;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return false;
    }

    @Override
    public boolean isBlockFacePowered(BlockFace blockFace) {
        return false;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(BlockFace blockFace) {
        return false;
    }

    @Override
    public int getBlockPower(BlockFace blockFace) {
        return 0;
    }

    @Override
    public int getBlockPower() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return getTypeId() == 0;
    }

    @Override
    public boolean isLiquid() {
        throw new NotImplementedException("No liquid checking");
    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    public double getHumidity() {
        return 0;
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction() {
        return null;
    }

    @Override
    public boolean breakNaturally() {
        return breakNaturally(null);
    }

    @Override
    public boolean breakNaturally(ItemStack itemStack) {
        return setTypeId(0);
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public Collection<ItemStack> getDrops(ItemStack itemStack) {
        return null;
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue) {

    }

    @Override
    public List<MetadataValue> getMetadata(String s) {
        return null;
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;
    }

    @Override
    public void removeMetadata(String s, Plugin plugin) {

    }

    public Block getPCBlock() {
        return block;
    }
}
