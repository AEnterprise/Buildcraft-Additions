package buildcraftAdditions.items;

import buildcraftAdditions.BuildcraftAdditions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of the Minecraft Mod Public
 * License 1.0, or MMPL. Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class ItemDust extends Item {
    public IIcon icon;
    public String metal;

    public ItemDust(String metal){
        this.setCreativeTab(BuildcraftAdditions.bcadditions);
        this.setUnlocalizedName("dust" + metal);
        this.metal = metal;
    }

    @Override
    public int getColorFromItemStack(ItemStack stack, int meta) {
        if (this.metal == "Gold")
            return Integer.parseInt("F8DF17", 16);
        if (this.metal == "Copper")
            return  Integer.parseInt("BF5E1F", 16);
        if (this.metal == "Nickel")
            return Integer.parseInt("BAB0A4", 16);
        if (this.metal == "Silver")
            return Integer.parseInt("B3B3B3", 16);
        if (this.metal == "Tin")
            return Integer.parseInt("F2F2F2", 16);
        if (this.metal == "Platinum")
            return Integer.parseInt("ABCDEF", 16);
        if (this.metal == "Lead")
            return Integer.parseInt("808096", 16);
        return Integer.parseInt("D2CEC9", 16);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        icon = par1IconRegister.registerIcon("bcadditions:dust");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        return icon;
    }

}
