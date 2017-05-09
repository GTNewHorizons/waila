package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import org.lwjgl.util.Point;

public class LabelFixedFont extends WidgetBase {

    protected String text = "";
    protected int color;

    public LabelFixedFont(final IWidget parent, final String text) {
        super(parent);
        this.setText(text);
        this.color = 0xFFFFFF;
    }

    public LabelFixedFont(final IWidget parent, final String text, final int color) {
        super(parent);
        this.setText(text);
        this.color = color;
    }

    @Override
    public IWidget setGeometry(final WidgetGeometry geom) {
        this.geom = geom;
        this.updateGeometry();
        return this;
    }

    public void setText(final String text) {
        this.text = LangUtil.translateG(text);
        this.updateGeometry();
    }

    public void setColor(final int color) {
        this.color = color;
    }

    private void updateGeometry() {
        if (this.geom == null)
            this.geom = new WidgetGeometry(0, 0, 50, 50, CType.ABSXY, CType.ABSXY);

        this.geom = new WidgetGeometry(this.geom.x, this.geom.y, this.mc.fontRendererObj.getStringWidth(this.text), 8, this.geom.posType, CType.ABSXY, this.geom.alignX, this.geom.alignY);
    }

    @Override
    public void draw(final Point pos) {
        this.saveGLState();
        this.mc.fontRendererObj.drawString(this.text, pos.getX(), pos.getY(), this.color);
        this.loadGLState();
    }
}
