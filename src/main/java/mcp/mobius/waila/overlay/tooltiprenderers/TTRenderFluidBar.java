package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Loader;
import gregtech.api.util.GTUtil;
import gregtech.api.util.GTUtility;
import gregtech.common.fluid.GTFluid;
import gtPlusPlus.api.objects.minecraft.FluidGT6;
import gtPlusPlus.core.util.minecraft.FluidUtils;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderFluidBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    private final Consumer<String> bindColor;
    private final Function<Integer, String> formatNumber;

    public TTRenderFluidBar() {
        if (Loader.isModLoaded("gregtech")) {
            bindColor = (fluidName) -> {
                FluidStack aCheck = FluidUtils.getWildcardFluidStack(fluidName, 1000);
                if (aCheck != null && (aCheck.getFluid() instanceof FluidGT6 || aCheck.getFluid() instanceof GTFluid)) {
                    short[] RGBa = GTUtil.getRGBaArray(aCheck.getFluid().getColor());
                    GL11.glColor4f(RGBa[0] / 255F, RGBa[1] / 255F, RGBa[2] / 255F, 1F);
                } else {
                    GL11.glColor4f(1F, 1F, 1F, 1F);
                }
            };

            formatNumber = GTUtility::formatNumbers;
        } else {
            bindColor = (fluidName) -> GL11.glColor4f(1F, 1F, 1F, 1F);
            formatNumber = (number) -> String.format("%,d", number);
        }
    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        int commaCount = (int) (Math.floor((params[2].length() - 1) / 3D) + Math.floor((params[3].length() - 1) / 3D));
        // ",".repeat(commaCount) doesn't exist in java 8 so do this instead.
        StringBuilder sb = new StringBuilder(commaCount);
        for (int i = 0; i < commaCount; i++) {
            sb.append(",");
        }
        return new Dimension(
                DisplayUtil.getDisplayWidth(
                        params[2] + " /   "
                                + params[3]
                                + ConfigHandler.instance().fluidUnit
                                + params[1].replace('\u0080', ',')
                                + sb)
                        + 4,
                12);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        String fluidName = params[0];
        String localizedName = params[1].replace('\u0080', ',');
        double amount = Double.parseDouble(params[2]);
        double capacity = Double.parseDouble(params[3]);

        drawThickBeveledBox(0, 0, maxStringW, 12, 1, 0xFF505050, 0xFF505050, -1);

        IIcon icon = FluidRegistry.getFluid(fluidName).getIcon();

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        bindColor.accept(fluidName);

        int i = (int) ((double) (maxStringW - 2) * amount / capacity);
        int j = 0;
        for (; i > 10; i = i - 10) {
            drawTexturedModelRectFromIcon(1 + (j * 10), 1, icon, 10, 10, 100);
            j++;
        }
        if (i > 0) drawTexturedModelRectFromIcon(
                1 + (j * 10),
                1,
                icon,
                i,
                10,
                100,
                icon.getMinU() + ((icon.getMaxU() - icon.getMinU()) * ((double) i / 10D)),
                icon.getMaxV());

        DisplayUtil.drawString(
                formatNumber.apply((int) amount) + " / "
                        + formatNumber.apply((int) capacity)
                        + " "
                        + ConfigHandler.instance().fluidUnit
                        + " "
                        + localizedName,
                2,
                2,
                OverlayConfig.fontcolor,
                true);

    }

    public static void drawTexturedModelRectFromIcon(int x, int y, IIcon icon, int width, int height, double z) {
        drawTexturedModelRectFromIcon(x, y, icon, width, height, z, icon.getMaxU(), icon.getMaxV());
    }

    public static void drawTexturedModelRectFromIcon(int x, int y, IIcon icon, int width, int height, double z,
            double maxU, double maxV) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + height, z, icon.getMinU(), maxV);
        tessellator.addVertexWithUV(x + width, y + height, z, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, z, maxU, icon.getMinV());
        tessellator.addVertexWithUV(x, y, z, icon.getMinU(), icon.getMinV());
        tessellator.draw();
    }

    public static void drawThickBeveledBox(int x1, int y1, int x2, int y2, int thickness, int topleftcolor,
            int botrightcolor, int fillcolor) {
        if (fillcolor != -1) {
            Gui.drawRect(x1 + 1, y1 + 1, x2 - 1, y2 - 1, fillcolor);
        }
        Gui.drawRect(x1, y1, x2 - 1, y1 + thickness, topleftcolor);
        Gui.drawRect(x1, y1, x1 + thickness, y2 - 1, topleftcolor);
        Gui.drawRect(x2 - thickness, y1, x2, y2 - 1, botrightcolor);
        Gui.drawRect(x1, y2 - thickness, x2, y2, botrightcolor);
    }

    @Override
    public void setMaxLineWidth(int width) {
        maxStringW = width + 2;
    }

    @Override
    public int getMaxLineWidth() {
        return maxStringW;
    }
}
