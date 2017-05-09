package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.gui.truetyper.FontHelper;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.overlay.DisplayUtil;

import java.awt.*;

public class TTRenderTrueTyper implements IWailaTooltipRenderer {

    final String data;
    final Dimension size;

    public TTRenderTrueTyper(final String data) {
        this.data = data;
        this.size = new Dimension(DisplayUtil.getDisplayWidth(data), data.equals("") ? 0 : (int) ((TrueTypeFont) Waila.proxy.getFont()).getHeight() / 2);
    }

    @Override
    public Dimension getSize(final String[] params, final IWailaCommonAccessor accessor) {
        return size;
    }

    @Override
    public void draw(final String[] params, final IWailaCommonAccessor accessor) {
        FontHelper.drawString(data, 0f, 0f, (TrueTypeFont) Waila.proxy.getFont(), 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f);
    }

}
