package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import org.lwjgl.util.Point;

public class ButtonBooleanConfigRemote extends ButtonBooleanConfig {

    public ButtonBooleanConfigRemote(final IWidget parent, final String category, final String configKey, final String textFalse, final String textTrue) {
        this(parent, category, configKey, true, true, textFalse, textTrue);
    }

    public ButtonBooleanConfigRemote(final IWidget parent, final String category, final String configKey, final boolean instant, final boolean state_, final String textFalse, final String textTrue) {
        super(parent, category, configKey, instant, state_, textFalse, textTrue);
        if (!Waila.instance.serverPresent)
            this.state = false;

        if (this.state) {
            this.getWidget("LabelTrue").show();
            this.getWidget("LabelFalse").hide();
        } else {
            this.getWidget("LabelTrue").hide();
            this.getWidget("LabelFalse").show();
        }
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        if ((Waila.instance.serverPresent) && !ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))
            super.onMouseClick(event);
    }

    @Override
    public void draw(final Point pos) {
        if ((Waila.instance.serverPresent) && !ConfigHandler.instance().forcedConfigs.containsKey(this.configKey))
            super.draw(pos);
        else {
            this.saveGLState();
            final int texOffset = -1;
            this.mc.getTextureManager().bindTexture(widgetsTexture);
            UIHelper.drawTexture(this.getPos().getX(), this.getPos().getY(), this.getSize().getX(), this.getSize().getY(), 0, 66 + texOffset * 20, 200, 20);
            this.loadGLState();
        }
    }
}
