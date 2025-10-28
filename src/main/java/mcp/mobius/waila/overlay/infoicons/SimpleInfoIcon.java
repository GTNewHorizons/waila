package mcp.mobius.waila.overlay.infoicons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaInfoIcon;
import mcp.mobius.waila.api.impl.ConfigHandler;

public class SimpleInfoIcon implements IWailaInfoIcon {

    private ResourceLocation texture;

    public SimpleInfoIcon(ResourceLocation t) {
        texture = t;
    }

    @Override
    public int getWidth(IWailaCommonAccessor accessor) {
        return ConfigHandler.infoIconHeight;
    }

    @Override
    public void draw(IWailaCommonAccessor accessor) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.renderEngine.bindTexture(texture);
        GL11.glColor4f(1f, 1f, 1f, 1f);

        Tessellator tessellator = Tessellator.instance;

        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(0, ConfigHandler.infoIconHeight, 0, 0.0D, 1.0D);
        tessellator.addVertexWithUV(ConfigHandler.infoIconHeight, ConfigHandler.infoIconHeight, 0, 1.0D, 1.0D);
        tessellator.addVertexWithUV(ConfigHandler.infoIconHeight, 0, 0, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0, 0, 0, 0.0D, 0.0D);
        tessellator.draw();
    }
}
