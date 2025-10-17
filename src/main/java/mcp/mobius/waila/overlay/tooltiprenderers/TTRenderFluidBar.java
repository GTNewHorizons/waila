package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderFluidBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(DisplayUtil.getDisplayWidth(params[1] + 2), 12);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        drawThickBeveledBox(0, 0, maxStringW, 12, 1, 0xFF505050, 0xFF505050, -1);

        IIcon icon = FluidRegistry.getFluid(params[0]).getIcon();

        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(net.minecraft.client.renderer.texture.TextureMap.locationBlocksTexture);
        GL11.glColor4f(1F, 1F, 1F, 1F);

        int i = (int) ((maxStringW - 2) * Double.parseDouble(params[2]));
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

        DisplayUtil.drawString(params[1], 2, 2, OverlayConfig.fontcolor, true);

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
    public void setMaxStringW(int width) {
        maxStringW = width + 2;
    }

    @Override
    public int getMaxStringW() {
        return maxStringW;
    }
}
