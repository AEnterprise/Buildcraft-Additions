package buildcraftAdditions.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import buildcraftAdditions.BuildcraftAdditions;
import buildcraftAdditions.reference.Variables;
import buildcraftAdditions.tileEntities.Bases.TileBase;
import buildcraftAdditions.utils.IConfigurableOutput;
/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class ItemMachineConfigurator extends ItemBase {

	public ItemMachineConfigurator() {
		super("MachineConfigurator");
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta, float hitX, float hitY, float hitZ) {
		if (world.getTileEntity(x, y, z) instanceof TileBase)
			((TileBase) world.getTileEntity(x, y, z)).sync();

		if (world.getTileEntity(x, y, z) instanceof IConfigurableOutput) {
			player.openGui(BuildcraftAdditions.instance, Variables.Gui.MACHINE_CONFIGURATOR, world, x, y, z);
			return true;
		}
		return false;
	}
}
