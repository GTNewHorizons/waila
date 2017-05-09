package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

public abstract class ButtonBase extends WidgetBase {

    protected boolean mouseOver = false;
    protected static ResourceLocation widgetsTexture = new ResourceLocation("textures/gui/widgets.png");

    public ButtonBase(final IWidget parent) {
        super(parent);
    }

    @Override
    public void draw() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        for (final IWidget widget : this.widgets.values())
            if (widget instanceof LabelFixedFont)
                if (this.mouseOver)
                    ((LabelFixedFont) widget).setColor(0xffffa0);
                else
                    ((LabelFixedFont) widget).setColor(0xffffff);

        super.draw();
    }

    @Override
    public void draw(final Point pos) {
        this.saveGLState();
        int texOffset = 0;

        if (this.mouseOver)
            texOffset = 1;

        this.mc.getTextureManager().bindTexture(widgetsTexture);
        UIHelper.drawTexture(this.getPos().getX(), this.getPos().getY(), this.getSize().getX(), this.getSize().getY(), 0, 66 + texOffset * 20, 200, 20);

        this.loadGLState();
    }

    @Override
    public void onMouseEnter(final MouseEvent event) {
        mouseOver = true;
    }

    @Override
    public void onMouseLeave(final MouseEvent event) {
        mouseOver = false;
    }

    @Override
    public IWidget getWidgetAtCoordinates(final double posX, final double posY) {
        return this;
    }

    @Override
    public void onMouseClick(final MouseEvent event) {
        if (event.button == 0)
//			this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));

            this.emit(Signal.CLICKED, event.button);
    }
}
