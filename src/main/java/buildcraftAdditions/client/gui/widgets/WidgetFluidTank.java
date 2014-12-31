package buildcraftAdditions.client.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fluids.FluidStack;

import buildcraftAdditions.client.gui.gui.GuiBase;
import buildcraftAdditions.utils.Tank;
import buildcraftAdditions.utils.Utils;
/**
 * Copyright (c) 2014, AEnterprise
 * http://buildcraftadditions.wordpress.com/
 * Buildcraft Additions is distributed under the terms of GNU GPL v3.0
 * Please check the contents of the license located in
 * http://buildcraftadditions.wordpress.com/wiki/licensing-stuff/
 */
public class WidgetFluidTank extends WidgetBase {
	public ResourceLocation BLOCK_TEXTURE = TextureMap.locationBlocksTexture;
	private Tank tank;

	public WidgetFluidTank(int id, int x, int y, int width, int height, GuiBase gui, Tank tank) {
		super(id, x, y, 0, 0, width, height, gui);
		this.tank = tank;
	}

	@Override
	public void render(int mouseX, int mouseY) {
		super.render(mouseX, mouseY);
		if (tank.getFluid() != null)
			drawFluid(tank.getFluid(), ((float) (tank.getFluid().amount * height) / (float) (tank.getMaxCapacity())), x, y, width, height);
	}

	@Override
	public List<String> getToolTip() {
		List<String> list = new ArrayList<String>(3);
		String fluid = Utils.localize("gui.empty");
		if (tank.getFluid() != null && tank.getFluidAmount() > 0)
			fluid = tank.getFluid().getLocalizedName();
		list.add(fluid);
		if (!fluid.equals(Utils.localize("gui.empty")))
			list.add(tank.getFluidAmount() + "mB");
		return list;
	}

	private void drawFluid(FluidStack fluid, float level, int x, int y, int width, int height) {
		if (fluid == null || fluid.getFluid() == null) {
			return;
		}
		IIcon icon = fluid.getFluid().getIcon(fluid);
		gui.mc.renderEngine.bindTexture(BLOCK_TEXTURE);
		Utils.setGLColorFromInt(fluid.getFluid().getColor(fluid));
		int fullX = width / 16;
		int fullY = height / 16;
		int lastX = width - fullX * 16;
		int lastY = height - fullY * 16;
		float fullLvl = (height - level) / 16;
		float lastLvl = (height - level) - fullLvl * 16;
		for (int i = 0; i < fullX; i++) {
			for (int j = 0; j < fullY; j++) {
				if (j >= fullLvl) {
					drawCutIcon(icon, x + i * 16, y + j * 16, 16, 16, j == fullLvl ? lastLvl : 0);
				}
			}
		}
		for (int i = 0; i < fullX; i++) {
			drawCutIcon(icon, x + i * 16, y + fullY * 16, 16, lastY, fullLvl == fullY ? lastLvl : 0);
		}
		for (int i = 0; i < fullY; i++) {
			if (i >= fullLvl) {
				drawCutIcon(icon, x + fullX * 16, y + i * 16, lastX, 16, i == fullLvl ? lastLvl : 0);
			}
		}
		drawCutIcon(icon, x + fullX * 16, y + fullY * 16, lastX, lastY, fullLvl == fullY ? lastLvl : 0);
	}

	private void drawCutIcon(IIcon icon, int x, int y, int width, int height, float cut) {
		Tessellator tess = Tessellator.instance;
		tess.startDrawingQuads();
		tess.addVertexWithUV(x, y + height, 0, icon.getMinU(), icon.getInterpolatedV(height));
		tess.addVertexWithUV(x + width, y + height, 0, icon.getInterpolatedU(width), icon.getInterpolatedV(height));
		tess.addVertexWithUV(x + width, y + cut, 0, icon.getInterpolatedU(width), icon.getInterpolatedV(cut));
		tess.addVertexWithUV(x, y + cut, 0, icon.getMinU(), icon.getInterpolatedV(cut));
		tess.draw();
	}
}