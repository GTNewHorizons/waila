package mcp.mobius.waila.gui.testing;

import net.minecraft.client.gui.GuiScreen;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.screens.ScreenBase;
import mcp.mobius.waila.gui.widgets.ViewTable;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ScreenTest extends ScreenBase {

    public ScreenTest(GuiScreen parent) {
        super(parent);

        this.getRoot().addWidget("Test", new ViewTable(null)).setGeometry(
                new WidgetGeometry(50.0, 50.0, 90.0, 90.0, CType.RELXY, CType.RELXY, WAlign.CENTER, WAlign.CENTER));
        ((ViewTable) this.getRoot().getWidget("Test")).setColumnsTitle("Column1", "Column2", "Column3")
                .setColumnsWidth(30.0, 30.0, 30.0).setColumnsAlign(WAlign.CENTER, WAlign.CENTER, WAlign.CENTER)
                .addRow("test1", "test2", "test3").addRow("test1", "test2", "test3").addRow("test1", "test2", "test3")
                .addRow("test1", "test2", "test3").addRow("test1", "test2", "test3").addRow("test1", "test2", "test3")
                .addRow("test1", "test2", "test3").addRow("test1", "test2", "test3").addRow("test1", "test2", "test3")
                .addRow("test1", "test2", "test3");
    }
}
