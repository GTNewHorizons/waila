package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ButtonLabel extends ButtonBase {

    public ButtonLabel(final IWidget parent, final String text) {
        super(parent);

        this.addWidget("Label", new LabelFixedFont(this, text));
        this.getWidget("Label").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        super.onMouseClick(event);
    }
}
