package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Color;
import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderAspectString implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return new Dimension(DisplayUtil.getDisplayWidth(params[0]) + 10, params[0].isEmpty() ? 0 : 8);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        try {
            if (params[0].equals("???")) {
                drawTag(0, 0, ThaumcraftModule.unknownAspectTexture, ThaumcraftModule.unknownAspectColor, 200, 771, 1F);
                DisplayUtil.drawString(params[0], 10, 0, OverlayConfig.fontcolor, true);
                return;
            }
            Object aspect = ThaumcraftModule.Aspect_getAspect.invoke(null, params[0]);
            if (aspect == null) return;
            drawTag(
                    0,
                    0,
                    (ResourceLocation) ThaumcraftModule.Aspect_getImage.invoke(aspect),
                    new Color((int) ThaumcraftModule.Aspect_getColor.invoke(aspect)),
                    200,
                    771,
                    1F);
            DisplayUtil.drawString(
                    (String) ThaumcraftModule.Aspect_getName.invoke(aspect),
                    10,
                    0,
                    OverlayConfig.fontcolor,
                    true);
        } catch (ReflectiveOperationException e) {
            DisplayUtil.drawString(params[0], 10, 0, OverlayConfig.fontcolor, true);
            throw new RuntimeException(e);
        }
    }

    public static void drawTag(double x, double y, ResourceLocation image, Color color, double z, int blend,
            float alpha) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glDisable(2896);
        GL11.glAlphaFunc(516, 0.003921569F);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, blend);
        mc.renderEngine.bindTexture(image);
        GL11.glColor4f(
                (float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F,
                (float) color.getBlue() / 255.0F,
                alpha);

        Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.setColorRGBA_F(
                (float) color.getRed() / 255.0F,
                (float) color.getGreen() / 255.0F,
                (float) color.getBlue() / 255.0F,
                alpha);

        var9.addVertexWithUV(x + 0.0D, y + 8.0D, z, 0.0D, 1.0D);
        var9.addVertexWithUV(x + 8.0D, y + 8.0D, z, 1.0D, 1.0D);
        var9.addVertexWithUV(x + 8.0D, y + 0.0D, z, 1.0D, 0.0D);
        var9.addVertexWithUV(x + 0.0D, y + 0.0D, z, 0.0D, 0.0D);
        var9.draw();
        GL11.glPopMatrix();
    }

}
