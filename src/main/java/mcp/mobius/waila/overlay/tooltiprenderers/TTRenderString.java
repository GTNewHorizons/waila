package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

import java.awt.*;

public class TTRenderString implements IWailaTooltipRenderer {

    final String data;
    final Dimension size;

    public TTRenderString(final String data) {
        this.data = data;
        this.size = new Dimension(DisplayUtil.getDisplayWidth(data), data.equals("") ? 0 : 8);
    }

    @Override
    public Dimension getSize(final String[] params, final IWailaCommonAccessor accessor) {
        return size;
    }

    @Override
    public void draw(final String[] params, final IWailaCommonAccessor accessor) {
        DisplayUtil.drawString(data, 0, 0, OverlayConfig.fontcolor, true);
    }

}
