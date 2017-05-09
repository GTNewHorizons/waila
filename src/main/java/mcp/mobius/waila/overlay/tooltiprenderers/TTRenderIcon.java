package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;

import java.awt.*;

import static mcp.mobius.waila.api.SpecialChars.WailaIcon;
import static mcp.mobius.waila.api.SpecialChars.WailaStyle;

public class TTRenderIcon implements IWailaTooltipRenderer {

    final String type;
    final int IconSize = 8;

    public TTRenderIcon(final String type) {
        this.type = type;
    }

    @Override
    public Dimension getSize(final String[] params, final IWailaCommonAccessor accessor) {
        return new Dimension(IconSize, IconSize);
    }

    @Override
    public void draw(final String[] params, final IWailaCommonAccessor accessor) {
        DisplayUtil.renderIcon(0, 0, IconSize, IconSize, IconUI.bySymbol(WailaStyle + WailaIcon + type));
    }

}
