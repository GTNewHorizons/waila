package mcp.mobius.waila.gui.widgets.buttons;

import org.lwjgl.util.Point;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

public class ButtonBooleanConfig extends ButtonBoolean {

    protected String category;
    protected String configKey;
    protected boolean instant;

    public ButtonBooleanConfig(IWidget parent, String category, String configKey, String textFalse, String textTrue) {
        this(parent, category, configKey, true, true, textFalse, textTrue);
    }

    public ButtonBooleanConfig(IWidget parent, String category, String configKey, boolean instant, boolean state_,
            String textFalse, String textTrue) {
        super(parent, textFalse, textTrue);
        this.category = category;
        this.configKey = configKey;
        this.instant = instant;

        this.state = ConfigHandler.instance().getConfig(this.category, this.configKey, state_);

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
        if (!ConfigHandler.instance().forcedConfigs.containsKey(this.configKey)) super.onMouseClick(event);

        if (this.instant) ConfigHandler.instance().setConfig(this.category, this.configKey, this.state);
    }

    @Override
    public void draw(Point pos) {
        if (!ConfigHandler.instance().forcedConfigs.containsKey(this.configKey)) super.draw(pos);
        else {
            this.saveGLState();
            this.drawVanillaButton(-1);
            this.loadGLState();
        }
    }

}
