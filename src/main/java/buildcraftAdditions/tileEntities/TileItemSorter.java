package buildcraftAdditions.tileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;

import buildcraftAdditions.inventories.CustomInventory;

/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class TileItemSorter extends TileEntity implements ISidedInventory, IPipeConnection {

	protected ForgeDirection rotation = ForgeDirection.UP;
	protected CustomInventory inventory = new CustomInventory("ItemSorter", 49, 64, this);

	public int[] colors = new int[] {0, 0, 0, 0, 0, 0, 0, 0};

	public void setRotation(ForgeDirection dir) {
		rotation = dir;
		if (worldObj != null) {
			updateBlock();
			notifyNeighborBlockUpdate();
			worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, dir.ordinal(), 3);
		}
	}

	public ForgeDirection getRotation() {
		return rotation;
	}

	public ForgeDirection getEnterSide() {
		return rotation.getOpposite();
	}

	public ForgeDirection getExitSide() {
		return rotation;
	}

	protected void updateBlock() {
		if (!worldObj.isRemote)
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}
	protected void notifyNeighborBlockUpdate() {
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
	}

	protected void updateRender() {
		if (worldObj.isRemote)
			worldObj.markBlockRangeForRenderUpdate(xCoord, yCoord, zCoord, xCoord , yCoord, zCoord);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		rotation = ForgeDirection.getOrientation(tag.getInteger("Rotation"));
		colors = tag.getIntArray("Colors");
		inventory.readNBT(tag);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("Rotation", rotation.ordinal());
		tag.setIntArray("Colors", colors);
		inventory.writeNBT(tag);
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] {};
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return ForgeDirection.getOrientation(side).equals(getEnterSide()) && slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return inventory.getSizeInventory();
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return inventory.decrStackSize(slot, amount);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return inventory.getStackInSlotOnClosing(slot);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}

	@Override
	public String getInventoryName() {
		return inventory.getInventoryName();
	}

	@Override
	public boolean hasCustomInventoryName() {
		return inventory.hasCustomInventoryName();
	}

	@Override
	public int getInventoryStackLimit() {
		return inventory.getInventoryStackLimit();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}

	@Override
	public void openInventory() {

	}

	@Override
	public void closeInventory() {

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public ConnectOverride overridePipeConnection(IPipeTile.PipeType type, ForgeDirection side) {
		if ((side == getExitSide() || side == getEnterSide()) && type.equals(IPipeTile.PipeType.ITEM))
			return ConnectOverride.CONNECT;
		return ConnectOverride.DISCONNECT;
	}
}
