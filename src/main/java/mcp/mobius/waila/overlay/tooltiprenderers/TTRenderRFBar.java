package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;
import java.text.NumberFormat;
import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;

public class TTRenderRFBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    private final Function<Integer, String> formatNumber;
    private static final NumberFormat energyFormat = NumberFormat.getInstance();
    private static final int height = 12;
    private static final int width = 2;

    static {
        energyFormat.setGroupingUsed(true);
    }

    public TTRenderRFBar() {
        formatNumber = energyFormat::format;
    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        int commaCount = (int) (Math.floor((params[0].length() - 1) / 3D) + Math.floor((params[1].length() - 1) / 3D));
        StringBuilder sb = new StringBuilder(commaCount);
        for (int i = 0; i < commaCount; i++) {
            sb.append(",");
        }
        return new Dimension(
                DisplayUtil.getDisplayWidth(String.format("%s / %s RF%s", params[0], params[1], sb)) + 4,
                height);
    }

    public static final ResourceLocation barTexture = new ResourceLocation("waila", "textures/rf_energy_bar.png");
    public static final ResourceLocation gradientTexture = new ResourceLocation("waila", "textures/gradient.png");

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        int amount = Integer.parseInt(params[0]);
        int capacity = Integer.parseInt(params[1]);
        Tessellator tessellator = Tessellator.instance;

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(barTexture);

        GL11.glColor4f(1F, 1F, 1F, 1F);
        tessellator.startDrawingQuads();

        // Draw dark (uncharged) background for whole bar first
        for (int i = 0; i < (maxStringW - 2); i += width) {
            drawRect(tessellator, 1 + i, 0, 0, width, height, 0.0, 0.0, 0.5, 1.0);
        }

        double i = (double) (maxStringW - 2) * amount / capacity;
        int drawnRects = 0;
        for (; i > width; i -= width) {
            drawRect(tessellator, 1 + (drawnRects * width), 0, 0, width, height, 0.5, 0.0, 1.0, 1.0);
            drawnRects++;
        }
        // Do less than full increments just as much as they take up on the scaled texture
        drawRectD(tessellator, 1 + (drawnRects * width), 0, 0, i, height, 0.5, 0.0, 0.5 + 0.25 * i, 1.0);
        tessellator.draw();

        // Border
        drawThickBeveledBox(0, 0, maxStringW, height, 1, 0xFF505050, 0xFF505050, -1);

        // Gradient
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 0.5F);
        mc.getTextureManager().bindTexture(gradientTexture);
        tessellator.startDrawingQuads();
        drawRect(tessellator, 1, 1, 0, maxStringW - 2, height - 2, 0, 0, 1, 1);
        tessellator.draw();

        DisplayUtil.drawString(
                String.format("%s / %s RF", formatNumber.apply(amount), formatNumber.apply(capacity)),
                2,
                2,
                0xFFFFFFFF,
                true);

    }

    public static void drawRect(Tessellator tessellator, int x, int y, double z, int width, int height, double minU,
            double minV, double maxU, double maxV) {
        tessellator.addVertexWithUV(x, y + height, z, minU, maxV);
        tessellator.addVertexWithUV(x + width, y + height, z, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y, z, minU, minV);
    }

    public static void drawRectD(Tessellator tessellator, double x, double y, double z, double width, double height,
            double minU, double minV, double maxU, double maxV) {
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
