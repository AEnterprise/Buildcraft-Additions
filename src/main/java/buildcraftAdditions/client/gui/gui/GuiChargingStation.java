package buildcraftAdditions.client.gui.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import buildcraftAdditions.client.gui.containers.ContainerChargingStation;
import buildcraftAdditions.tileEntities.TileChargingStation;

/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
@SideOnly(Side.CLIENT)
public class GuiChargingStation extends GuiBase {

	private static final ResourceLocation texture = new ResourceLocation("bcadditions", "textures/gui/guiChargingStation.png");
	private final TileChargingStation chargingStation;

	public GuiChargingStation(InventoryPlayer inventoryPlayer, TileChargingStation tile) {
		super(new ContainerChargingStation(inventoryPlayer, tile));
		setDrawPlayerInv(true);
		chargingStation = tile;
	}

	@Override
	public ResourceLocation texture() {
		return texture;
	}

	@Override
	public int getXSize() {
		return 176;
	}

	@Override
	public int getYSize() {
		return 53;
	}

	@Override
	public String getInventoryName() {
		return "chargingStation";
	}

	@Override
	public void initialize() {

	}
}
