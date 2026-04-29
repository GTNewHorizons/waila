package mcp.mobius.waila.gui.widgets.buttons;

import org.lwjgl.util.Point;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

public class ButtonBooleanConfigRemote extends ButtonBooleanConfig {

    public ButtonBooleanConfigRemote(IWidget parent, String category, String configKey, String textFalse,
            String textTrue) {
        this(parent, category, configKey, true, true, textFalse, textTrue);
    }

    public ButtonBooleanConfigRemote(IWidget parent, String category, String configKey, boolean instant, boolean state_,
            String textFalse, String textTrue) {
        super(parent, category, configKey, instant, state_, textFalse, textTrue);
        if (!Waila.instance.serverPresent) this.state = false;

        if (this.state) {
            this.getWidget("LabelTrue").show();
            this.getWidget("LabelFalse").hide();
        } else {
            this.getWidget("LabelTrue").hide();
            this.getWidget("LabelFalse").show();
        }
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        if ((Waila.instance.serverPresent) && !ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))
            super.onMouseClick(event);
    }

    @Override
    public void draw(Point pos) {
        if ((Waila.instance.serverPresent) && !ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))
            super.draw(pos);
        else {
            this.saveGLState();
            this.drawVanillaButton(-1);
            this.loadGLState();
        }
    }
}
