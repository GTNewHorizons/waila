package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
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

public class TTRenderFluidBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    private final Consumer<String> bindColor;
    private final Function<Integer, String> formatNumber;
    final int height = 12;

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
                        params[2] + " /   " + params[3] + ConfigHandler.instance().fluidUnit + params[1] + sb) + 4,
                height);
    }

    public static ResourceLocation gradient = new ResourceLocation("waila", "textures/gradient.png");

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        String fluidName = params[0];
        String localizedName = params[1];
        double amount = Double.parseDouble(params[2]);
        double capacity = Double.parseDouble(params[3]);
        Tessellator tessellator = Tessellator.instance;

        IIcon icon = FluidRegistry.getFluid(fluidName).getIcon();

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);

        bindColor.accept(fluidName);

        tessellator.startDrawingQuads();
        // Intentionally draw 2 pixels taller than needed than cover with the border to make the texture more visible
        int i = (int) ((double) (maxStringW - 2) * amount / capacity);
        int j = 0;
        for (; i > height; i = i - height) {
            drawRectFromIcon(tessellator, 1 + (j * height), 0, 0, icon, height, height);
            j++;
        }
        if (i > 0) drawRect(
                tessellator,
                1 + (j * height),
                0,
                0,
                i,
                height,
                icon.getMinU(),
                icon.getMinV(),
                icon.getMinU() + ((icon.getMaxU() - icon.getMinU()) * ((double) i / height)),
                icon.getMaxV());
        tessellator.draw();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 0.70F);
        mc.getTextureManager().bindTexture(gradient);
        tessellator.startDrawingQuads();
        drawRect(tessellator, 1, 0, 0, maxStringW - 2, height - 1, 0, 0, 1, 1);
        tessellator.draw();

        drawThickBeveledBox(0, 0, maxStringW, height, 1, 0xFF505050, 0xFF505050, -1);

        DisplayUtil.drawString(
                formatNumber.apply((int) amount) + " / "
                        + formatNumber.apply((int) capacity)
                        + " "
                        + ConfigHandler.instance().fluidUnit
                        + " "
                        + localizedName,
                2,
                2,
                0xFFFFFFFF,
                true);

    }

    public static void drawRectFromIcon(Tessellator tessellator, int x, int y, double z, IIcon icon, int width,
            int height) {
        drawRect(tessellator, x, y, z, width, height, icon.getMinU(), icon.getMinV(), icon.getMaxU(), icon.getMaxV());
    }

    public static void drawRect(Tessellator tessellator, int x, int y, double z, int width, int height, double minU,
            double minV, double maxU, double maxV) {
        tessellator.addVertexWithUV(x, y + height, z, minU, maxV);
        tessellator.addVertexWithUV(x + width, y + height, z, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y, z, minU, minV);
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
