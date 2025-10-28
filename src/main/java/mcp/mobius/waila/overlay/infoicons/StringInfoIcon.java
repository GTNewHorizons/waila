package mcp.mobius.waila.overlay.infoicons;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaInfoIcon;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;

public class StringInfoIcon implements IWailaInfoIcon {

    private String text;

    public StringInfoIcon(String s) {
        text = s;
    }

    @Override
    public int getWidth(IWailaCommonAccessor accessor) {
        return DisplayUtil.getDisplayWidth(text);
    }

    @Override
    public void draw(IWailaCommonAccessor accessor) {
        DisplayUtil.drawString(text, 0, ConfigHandler.infoIconHeight - 8, OverlayConfig.fontcolor, true);
    }
}
