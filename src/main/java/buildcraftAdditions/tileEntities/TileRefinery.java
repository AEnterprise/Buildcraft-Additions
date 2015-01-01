package buildcraftAdditions.tileEntities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.network.NetworkRegistry;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import cofh.api.energy.IEnergyHandler;

import buildcraft.api.recipes.IFlexibleCrafter;

import buildcraftAdditions.BuildcraftAdditions;
import buildcraftAdditions.api.RecepieMananger;
import buildcraftAdditions.api.RefineryRecepie;
import buildcraftAdditions.multiBlocks.IMultiBlockTile;
import buildcraftAdditions.networking.MessageByteBuff;
import buildcraftAdditions.networking.PacketHandeler;
import buildcraftAdditions.reference.Variables;
import buildcraftAdditions.tileEntities.Bases.TileBase;
import buildcraftAdditions.utils.ITankHolder;
import buildcraftAdditions.utils.Location;
import buildcraftAdditions.utils.MultiBlockData;
import buildcraftAdditions.utils.RotationUtils;
import buildcraftAdditions.utils.Tank;

import io.netty.buffer.ByteBuf;
/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class TileRefinery extends TileBase implements IMultiBlockTile, IFluidHandler, IFlexibleCrafter, IEnergyHandler, ITankHolder {
	public int timer, energy, maxEnergy, currentHeat, requiredHeat, energyCost, heatTimer, lastRequiredHeat;
	public boolean init, valve, isCooling, moved;
	public TileRefinery master;
	private Tank input = new Tank(3000, this, "Input");
	private Tank output = new Tank(3000, this, "Output");
	private Fluid outputFluid;
	private int inputAmount, outputAmount;
	private MultiBlockData data = new MultiBlockData().setPatern(Variables.Paterns.REFINERY);

	public TileRefinery() {
		maxEnergy = 50000;
		timer = 0;
		init = false;
	}

	public void updateEntity() {
		if (worldObj.isRemote)
			return;
		if (data.moved) {
			data.afterMoveCheck(worldObj);
			data.moved = false;
		}
		if (input.getFluid() != null && input.getFluid().amount == 0)
			input.setFluid(null);
		if (input.getFluid() == null)
			updateRecepie();
		if (timer == 0) {
			sync();
			timer = 40;
		}
		timer--;
		updateHeat();
		if (!data.isMaster)
			return;
		energyCost = (isCooling || energy < (int) (50 + (50 * ((double) currentHeat / 100)))) ? 0 : (int) (50 + (50 * ((double) currentHeat / 100)));
		energy -= energyCost;
		if (currentHeat < requiredHeat) {
			return;
		}

		if (energyCost == 0 || output.isFull())
			return;
		input.drain(inputAmount, true);
		output.fill(new FluidStack(outputFluid, outputAmount), true);
	}

	private void updateHeat() {
		if (worldObj.isRemote)
			return;
		if (heatTimer == 0) {
			if ((currentHeat > requiredHeat || (energy < energyCost || energyCost == 0)) && currentHeat > 0) {
				currentHeat--;
				isCooling = true;
			}
			if (currentHeat < requiredHeat && (energy >= energyCost && energyCost != 0)) {
				currentHeat++;
				isCooling = false;
			}
			heatTimer = 10;
		}
		if (currentHeat == 0)
			isCooling = false;
		heatTimer -= 1;
	}

	private void updateRecepie() {
		if (!input.isEmpty()) {
			RefineryRecepie recepie = RecepieMananger.getRefineryRecepie(input.getFluid().getFluid());
			requiredHeat = recepie.getRequiredHeat();
			lastRequiredHeat = requiredHeat;
			outputFluid = recepie.getOutput();
			outputAmount = recepie.getOutputAmount();
			inputAmount = recepie.getInputAmount();
		} else
		requiredHeat = 0;
	}

	@Override
	public void makeMaster(int rotationIndex) {
		data.isMaster = true;
		data.partOfMultiBlock = true;
		data.rotationIndex = rotationIndex;
	}

	public void findMaster() {
		TileEntity entity = worldObj.getTileEntity(data.masterX, data.masterY, data.masterZ);
		if (entity instanceof TileRefinery) {
			master = (TileRefinery) entity;
		}
		if (master == null || !master.data.isMaster) {
			master = null;
			invalidateMultiblock();
		}
	}

	@Override
	public void sync() {
		if (!worldObj.isRemote) {
			NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 20);
			PacketHandeler.instance.sendToAllAround(new MessageByteBuff(this), point);
		}
	}

	@Override
	public void invalidateMultiblock() {
		if (isMaster())
			data.patern.destroyMultiblock(worldObj, xCoord, yCoord, zCoord, data.rotationIndex);
		else
			data.patern.destroyMultiblock(worldObj, data.masterX, data.masterY, data.masterZ, data.rotationIndex);
	}

	private void emptyTanks() {
		if (input.getFluid() == null || input.getFluid().amount < 1000 || input.getFluidType() == null)
			return;
		ForgeDirection[] directions = RotationUtils.rotateDirections(new ForgeDirection[]{ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.UP}, data.rotationIndex);
		Location location = new Location(this);
		location.move(directions);
		while (input.getFluid().amount > 1000) {
			if (input.getFluidType().getBlock() != null)
				location.setBlock(input.getFluidType().getBlock());
			input.drain(1000, true);
			location.move(RotationUtils.rotatateDirection(ForgeDirection.NORTH, data.rotationIndex));
		}
		location.move(RotationUtils.rotatateDirection(ForgeDirection.NORTH, data.rotationIndex));
		if (output.getFluid() == null || output.getFluid().amount < 1000 || output.getFluidType() == null)
			return;
		while (output.getFluid().amount > 1000) {
			location.setBlock(output.getFluidType().getBlock());
			output.drain(1000, true);
			location.move(RotationUtils.rotatateDirection(ForgeDirection.NORTH, data.rotationIndex));
		}
	}

	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if (data.isMaster) {
			if (!worldObj.isRemote) {
				player.openGui(BuildcraftAdditions.instance, Variables.Gui.REFINERY, worldObj, xCoord, yCoord, zCoord);
			}
			return true;
		}
		if (data.partOfMultiBlock) {
			if (master == null)
				findMaster();
			if (master != null)
				return master.onBlockActivated(player);
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		valve = tag.getBoolean("valve");
		energy = tag.getInteger("energy");
		currentHeat = tag.getInteger("currentHeat");
		requiredHeat = tag.getInteger("requiredHeat");
		data.readFromNBT(tag);
		input.readFromNBT(tag);
		output.readFromNBT(tag);
		updateRecepie();
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		data.writeToNBT(tag);
		tag.setBoolean("valve", valve);
		tag.setInteger("energy", energy);
		tag.setInteger("currentHeat", currentHeat);
		tag.setInteger("requiredHeat", requiredHeat);
		input.saveToNBT(tag);
		output.saveToNBT(tag);
	}

	@Override
	public void formMultiblock(int masterX, int masterY, int masterZ, int rotationIndex) {
		data.formMultiBlock(masterX, masterY, masterZ, rotationIndex);
		sync();
	}

	@Override
	public void invalidateBlock() {
		if (data.isMaster)
			emptyTanks();
		worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 2);
		worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, worldObj.getBlock(xCoord, yCoord, zCoord), 80);
		input.setFluid(null);
		output.setFluid(null);
		requiredHeat = 0;
		data.invalidate();
		sync();
	}

	@Override
	public void moved(ForgeDirection direction) {
		data.onMove(direction);
		master = null;
	}

	@Override
	public int getMasterX() {
		return data.masterX;
	}

	@Override
	public int getMasterY() {
		return data.masterY;
	}

	@Override
	public int getMasterZ() {
		return data.masterZ;
	}

	@Override
	public int getRotationIndex() {
		return data.rotationIndex;
	}

	@Override
	public boolean isMaster() {
		return data.isMaster;
	}

	@Override
	public boolean isPartOfMultiblock() {
		return data.partOfMultiBlock;
	}

	@Override
	public void setMasterX(int masterX) {
		data.masterX = masterX;
	}

	@Override
	public void setMasterY(int masterY) {
		data.masterY = masterY;
	}

	@Override
	public void setMasterZ(int masterZ) {
		data.masterZ = masterZ;
	}

	@Override
	public void setIsMaster(boolean isMaster) {
		data.isMaster = isMaster;
	}

	@Override
	public void setPartOfMultiBlock(boolean partOfMultiBlock) {
		data.partOfMultiBlock = partOfMultiBlock;
	}

	@Override
	public void setRotationIndex(int rotationIndex) {
		data.rotationIndex = rotationIndex;
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if (!valve)
			return 0;
		if (master == null)
			findMaster();
		if (master != null)
			return master.realFill(resource, doFill);
		return 0;
	}

	public int realFill(FluidStack resource, boolean doFill) {
		int result = input.fill(resource, doFill);
		updateRecepie();
		return result;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}

	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		if (!valve)
			return null;
		if (master == null)
			findMaster();
		if (master != null)
			return master.realDrain(maxDrain, doDrain);
		return null;
	}

	public FluidStack realDrain(int maxDrain, boolean doDrain) {
		FluidStack result = output.drain(maxDrain, doDrain);
		updateRecepie();
		return result;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return data.partOfMultiBlock && valve;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return data.partOfMultiBlock && valve;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		if (!valve)
			return null;
		if (master == null)
			findMaster();
		if (master != null)
			return master.realGetTankInfo();
		return new FluidTankInfo[]{new FluidTankInfo(input), new FluidTankInfo(output)};
	}

	public FluidTankInfo[] realGetTankInfo() {
		return new FluidTankInfo[]{new FluidTankInfo(input), new FluidTankInfo(output)};
	}

	@Override
	public int getCraftingItemStackSize() {
		return 0;
	}

	@Override
	public ItemStack getCraftingItemStack(int slotid) {
		return null;
	}

	@Override
	public ItemStack decrCraftingItemStack(int slotid, int val) {
		return null;
	}

	@Override
	public FluidStack getCraftingFluidStack(int tankid) {
		return input.getFluid();
	}

	@Override
	public FluidStack decrCraftingFluidStack(int tankid, int amount) {
		FluidStack fluid = null;
		if (amount > input.getFluidAmount()) {
			fluid = input.getFluid();
			input.setFluid(null);
		} else {
			fluid = input.getFluid().copy();
			fluid.amount = amount;
			input.getFluid().amount -= amount;
		}
		return fluid;
	}

	@Override
	public int getCraftingFluidStackSize() {
		return 1;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (data.isMaster) {
			int recieved = maxReceive;
			if (recieved > maxEnergy - energy)
				recieved = maxEnergy - energy;
			if (!simulate)
				energy += recieved;
			return recieved;
		} else {
			if (master == null)
				findMaster();
			if (master != null)
				return master.receiveEnergy(from, maxReceive, simulate);
		}
		return 0;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
		return 0;
	}

	@Override
	public int getEnergyStored(ForgeDirection from) {
		if (data.isMaster)
			return energy;
		else {
			if (master == null)
				findMaster();
			if (master != null)
				return master.getEnergyStored(from);
		}
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection from) {
		if (data.isMaster)
			return maxEnergy;
		else {
			if (master == null)
				findMaster();
			if (master != null)
				return master.getMaxEnergyStored(from);
		}
		return 0;
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public Tank[] getTanks() {
		return new Tank[]{input, output};
	}

	@Override
	public ByteBuf writeToByteBuff(ByteBuf buf) {
		buf.writeBoolean(valve);
		buf.writeInt(currentHeat);
		buf.writeInt(lastRequiredHeat);
		buf.writeInt(energyCost);
		buf.writeInt(requiredHeat);
		input.writeToByteBuff(buf);
		output.writeToByteBuff(buf);
		data.writeToByteBuff(buf);
		return buf;
	}

	@Override
	public ByteBuf readFromByteBuff(ByteBuf buf) {
		valve = buf.readBoolean();
		currentHeat = buf.readInt();
		lastRequiredHeat = buf.readInt();
		energyCost = buf.readInt();
		requiredHeat = buf.readInt();
		buf = input.readFromByteBuff(buf);
		buf = output.readFromByteBuff(buf);
		data.readFromByteBuff(buf);
		return buf;
	}
}
