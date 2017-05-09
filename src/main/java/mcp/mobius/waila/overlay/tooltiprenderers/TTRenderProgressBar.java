package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class TTRenderProgressBar implements IWailaTooltipRenderer {

    Minecraft mc = Minecraft.getMinecraft();
    ResourceLocation texture = new ResourceLocation("waila", "textures/sprites.png");

    @Override
    public Dimension getSize(final String[] params, final IWailaCommonAccessor accessor) {
        return new Dimension(32, 16);
    }

    @Override
    public void draw(final String[] params, final IWailaCommonAccessor accessor) {
        final int currentValue = Integer.valueOf(params[0]);
        final int maxValue = Integer.valueOf(params[1]);

        final int progress = (currentValue * 28) / maxValue;

        this.mc.getTextureManager().bindTexture(texture);

        DisplayUtil.drawTexturedModalRect(4, 0, 4, 16, 28, 16, 28, 16);
        DisplayUtil.drawTexturedModalRect(4, 0, 4, 0, progress + 1, 16, progress + 1, 16);

    }

}
