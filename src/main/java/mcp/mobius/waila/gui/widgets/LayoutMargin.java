package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import org.lwjgl.util.Point;

public class LayoutMargin extends LayoutBase {

    int left = 0;
    int right = 0;
    int top = 0;
    int bottom = 0;

    public LayoutMargin(final IWidget parent) {
        super(parent);
        this.setGeometry(new WidgetGeometry(0, 0, 0, 0, CType.ABSXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
    }

    public void setMargins(final int left, final int right, final int top, final int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public Point getSize() {
        final Point parentSize = this.parent.getSize();
        return new Point(parentSize.getX() - left - right, parentSize.getY() - top - bottom);
    }

    @Override
    public Point getPos() {
        final Point parentPos = this.parent.getPos();
        return new Point(parentPos.getX() + left, parentPos.getY() + top);
    }

    @Override
    public void draw(final Point pos) {
        super.draw(pos);
    }

}
