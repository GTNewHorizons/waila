package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.IWidget;

public class ButtonIntegerConfig extends ButtonInteger {

    private String category;
    private String configKey;
    private boolean instant;

    public ButtonIntegerConfig(final IWidget parent, final String category, final String configKey, final String... texts) {
        this(parent, category, configKey, true, 0, texts);
    }

    public ButtonIntegerConfig(final IWidget parent, final String category, final String configKey, final boolean instant, final int state_, final String... texts) {
        super(parent, texts);
        this.category = category;
        this.configKey = configKey;
        this.instant = instant;

        this.state = ConfigHandler.instance().getConfig(this.category, this.configKey, state_);

        for (int i = 0; i < this.nStates; i++)
            this.getWidget(String.format("Label_%d", i)).hide();

        this.getWidget(String.format("Label_%d", state)).show();
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        super.onMouseClick(event);

        if (this.instant)
            ConfigHandler.instance().setConfig(this.category, this.configKey, this.state);
    }

}
