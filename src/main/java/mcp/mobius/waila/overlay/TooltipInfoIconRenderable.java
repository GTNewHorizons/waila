package mcp.mobius.waila.overlay;

import java.awt.*;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaInfoIcon;
import mcp.mobius.waila.utils.WailaExceptionHandler;

// This is the icon equivalent to the private ToolTip.Renderable class.
// Holds the icon's position and renders it with translation.
class TooltipInfoIconRenderable {

    final IWailaInfoIcon icon;
    final Point pos;

    public TooltipInfoIconRenderable(IWailaInfoIcon icon, Point pos) {
        this.icon = icon;
        this.pos = pos;
    }

    public Point getPos() {
        return this.pos;
    }

    public int getWidth(IWailaCommonAccessor accessor) {
        int width = 0;
        try {
            width = this.icon.getWidth(accessor);
        } catch (Throwable e) {
            WailaExceptionHandler.handleErr(e, this.icon.getClass().getName() + ".getWidth()", null);
        }
        return width;
    }

    public void draw(IWailaCommonAccessor accessor, int x, int y) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x + this.pos.x, y + this.pos.y, 0);
        try {
            this.icon.draw(accessor);
        } catch (Throwable e) {
            WailaExceptionHandler.handleErr(e, this.icon.getClass().getName() + ".draw()", null);
        }
        GL11.glPopMatrix();
    }

    @Override
    public String toString() {
        return String.format("TooltipInfoIconRenderable@[%d,%d] | %s", pos.x, pos.y, icon);
    }
}
