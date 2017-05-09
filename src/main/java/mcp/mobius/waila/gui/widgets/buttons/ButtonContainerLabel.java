package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.LayoutBase;
import mcp.mobius.waila.gui.widgets.WidgetBase;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;
import org.lwjgl.util.Point;

//TODO : THIS CLASS IS WIP AND CAN'T WORK RIGHT NOW

public class ButtonContainerLabel extends WidgetBase {

    private int nButtons = 0;
    private int columns;
    private int buttonSize;
    private double spacing;

    public ButtonContainerLabel(final IWidget parent, final int columns, final int buttonSize, final double spacing) {
        super(parent);
        this.columns = columns;
        this.spacing = spacing;
        this.buttonSize = buttonSize;
    }

    public void addButton(final ButtonBase button, final String label) {
        final String buttonName = String.format("Button_%d", nButtons);
        final String layoutName = String.format("Layout_%d", nButtons);
        final String layoutLabelName = String.format("LayoutLabel_%d", nButtons);
        final String labelName = String.format("Label_%d", nButtons);

        this.addWidget(layoutName, new LayoutBase(this));
        this.addWidget(layoutLabelName, new LayoutBase(this));

        final int column = (this.nButtons % this.columns) * 2;
        final int row = this.nButtons / this.columns;
        final double sizeColumn = 100.0 / (this.columns * 2);

        this.getWidget(layoutName).setGeometry(new WidgetGeometry(sizeColumn * (column + 1), spacing * row, sizeColumn, spacing, CType.REL_X, CType.REL_X, WAlign.LEFT, WAlign.TOP));
        this.getWidget(layoutName).addWidget(buttonName, button);
        this.getWidget(layoutName).getWidget(buttonName).setGeometry(new WidgetGeometry(50.0, 50.0, buttonSize, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));

        this.getWidget(layoutLabelName).setGeometry(new WidgetGeometry(sizeColumn * column, spacing * row, sizeColumn, spacing, CType.REL_X, CType.REL_X, WAlign.LEFT, WAlign.TOP));
        this.getWidget(layoutLabelName).addWidget(labelName, new LabelFixedFont(this, label));
        this.getWidget(layoutLabelName).getWidget(labelName).setGeometry(new WidgetGeometry(50.0, 50.0, buttonSize, 20.0, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));

        this.nButtons += 1;
    }

    @Override
    public void draw(final Point pos) {
    }
}
