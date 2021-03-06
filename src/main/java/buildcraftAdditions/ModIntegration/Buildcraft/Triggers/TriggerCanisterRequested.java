package buildcraftAdditions.ModIntegration.Buildcraft.Triggers;

import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.common.util.ForgeDirection;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;

import buildcraftAdditions.tileEntities.TileFluidicCompressor;

/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class TriggerCanisterRequested extends BasicTrigger {

	public TriggerCanisterRequested() {
		super("canisterRequested", "TriggerCanisterRequested");
	}

	@Override
	public boolean isTriggerActive(TileEntity target, ForgeDirection side, IStatementContainer source, IStatementParameter[] parameters) {
		if ((target instanceof TileFluidicCompressor))
			return ((TileFluidicCompressor) target).getStackInSlot(0) == null;
		return false;
	}
}
