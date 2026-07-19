package mcp.mobius.waila.overlay.tooltiprenderers;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;

public class TTRenderRFBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    private static final int height = 12;
    private static final int width = 2;

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(
                DisplayUtil.getDisplayWidth(buildDisplayText(Integer.parseInt(params[0]), Integer.parseInt(params[1])))
                        + 4,
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
            DisplayUtil.drawRect(tessellator, 1 + i, 0, 0, width, height, 0.0, 0.0, 0.5, 1.0);
        }

        double i = (double) (maxStringW - 2) * ((double) amount / Math.max(capacity, amount));
        int drawnRects = 0;
        for (; i > width; i -= width) {
            DisplayUtil.drawRect(tessellator, 1 + (drawnRects * width), 0, 0, width, height, 0.5, 0.0, 1.0, 1.0);
            drawnRects++;
        }
        // Do less than full increments just as much as they take up on the scaled texture
        drawRectD(tessellator, 1 + (drawnRects * width), 0, 0, i, height, 0.5, 0.0, 0.5 + 0.25 * i, 1.0);
        tessellator.draw();

        // Border
        DisplayUtil.drawThickBeveledBox(0, 0, maxStringW, height, 1, 0xFF505050, 0xFF505050, -1);

        // Gradient
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glColor4f(1F, 1F, 1F, 0.5F);
        mc.getTextureManager().bindTexture(gradientTexture);
        tessellator.startDrawingQuads();
        DisplayUtil.drawRect(tessellator, 1, 1, 0, maxStringW - 2, height - 2, 0, 0, 1, 1);
        tessellator.draw();

        DisplayUtil.drawString(buildDisplayText(amount, capacity), 2, 2, 0xFFFFFFFF, true);
    }

    public String buildDisplayText(int amount, int capacity) {
        return String.format("%s / %s RF", formatNumber(amount), formatNumber(capacity));
    }

    public static void drawRectD(Tessellator tessellator, double x, double y, double z, double width, double height,
            double minU, double minV, double maxU, double maxV) {
        tessellator.addVertexWithUV(x, y + height, z, minU, maxV);
        tessellator.addVertexWithUV(x + width, y + height, z, maxU, maxV);
        tessellator.addVertexWithUV(x + width, y, z, maxU, minV);
        tessellator.addVertexWithUV(x, y, z, minU, minV);
    }

    @Override
    public void setMaxLineWidth(int width) {
        maxStringW = width;
    }

    @Override
    public int getMaxLineWidth() {
        return maxStringW;
    }
}
