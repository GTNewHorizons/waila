package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class TTRenderString implements IWailaTooltipRenderer {

    final String data;
    final Dimension size;

    public TTRenderString(String data) {
        this.data = data;
        this.size = new Dimension(DisplayUtil.getDisplayWidth(data), data.isEmpty() ? 0 : 8);
    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        return size;
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        DisplayUtil.drawString(data, 0, 0, OverlayConfig.fontcolor, true);
    }

}
