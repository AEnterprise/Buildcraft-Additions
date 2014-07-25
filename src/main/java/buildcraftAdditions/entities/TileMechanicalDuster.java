package buildcraftAdditions.entities;

import buildcraft.api.mj.MjBattery;
import buildcraftAdditions.entities.Bases.TileBaseDuster;
import buildcraftAdditions.inventories.CustomInventory;
import buildcraftAdditions.networking.MessageMechanicDuster;
import buildcraftAdditions.networking.PacketHandeler;
import buildcraftAdditions.utils.Utils;
import buildcraftAdditions.variables.Variables;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class TileMechanicalDuster extends TileBaseDuster {
    private CustomInventory inventory = new CustomInventory("mechanicalDuster", 1, 1, this);
    @MjBattery
    double energy;
    public int progress;

    public TileMechanicalDuster() {
        super(Variables.DustT2Key2);
    }


    @Override
    public void updateEntity() {
        if (energy >= 4 && getStackInSlot(0) != null && getDust(getStackInSlot(0)) != null){
            progress++;
            energy -= 4;
            if (progress >= 100) {
                dust();
                progress = 0;
            }
        }


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
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D,
                zCoord + 0.5D) <= 64.0D;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return false;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        inventory.writeNBT(tag);
        tag.setInteger("progress", progress);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.readNBT(tag);
        progress = tag.getInteger("progress");
    }

    @Override
    public void dust() {
        Utils.dropItemstack(worldObj, xCoord, yCoord, zCoord, getDust(getStackInSlot(0)));
        setInventorySlotContents(0, null);
        if (!worldObj.isRemote)
            PacketHandeler.instance.sendToAll(new MessageMechanicDuster(xCoord, yCoord, zCoord));
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
}
