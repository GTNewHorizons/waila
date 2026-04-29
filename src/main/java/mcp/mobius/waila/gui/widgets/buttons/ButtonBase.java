package mcp.mobius.waila.gui.widgets.buttons;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetBase;

public abstract class ButtonBase extends WidgetBase {

    protected static final int BUTTON_TEX_U = 0;
    protected static final int BUTTON_TEX_V_BASE = 66;
    protected static final int BUTTON_TEX_V_STEP = 20;
    protected static final int BUTTON_TEX_W = 200;
    protected static final int BUTTON_TEX_H = 20;
    protected static final int BUTTON_BORDER = 2;
    protected static final int COLOR_TEXT = 0xffffff;
    protected static final int COLOR_TEXT_HOVER = 0xffffa0;

    protected boolean mouseOver = false;
    protected static final ResourceLocation widgetsTexture = new ResourceLocation("textures/gui/widgets.png");

    public ButtonBase(IWidget parent) {
        super(parent);
    }

    @Override
    public void draw() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        int textColor = this.mouseOver ? COLOR_TEXT_HOVER : COLOR_TEXT;

        for (IWidget widget : this.widgets.values()) {
            if (!(widget instanceof LabelFixedFont)) continue;
            LabelFixedFont label = (LabelFixedFont) widget;
            label.setShadow(true);
            label.setColor(textColor);
        }

        super.draw();
    }

    @Override
    public void draw(Point pos) {
        this.saveGLState();
        int texOffset = 0;

        if (this.mouseOver) texOffset = 1;

        this.drawVanillaButton(texOffset);

        this.loadGLState();
    }

    protected void drawVanillaButton(int texOffset) {
        int x = this.getPos().getX();
        int y = this.getPos().getY();
        int width = this.getSize().getX();
        int height = this.getSize().getY();
        int v = BUTTON_TEX_V_BASE + texOffset * BUTTON_TEX_V_STEP;

        this.mc.getTextureManager().bindTexture(widgetsTexture);
        if (width <= 0 || height <= 0) return;

        int borderX = Math.min(BUTTON_BORDER, width / 2);
        int borderY = Math.min(BUTTON_BORDER, height / 2);
        int midW = width - borderX * 2;
        int midH = height - borderY * 2;
        int srcMidW = BUTTON_TEX_W - BUTTON_BORDER * 2;
        int srcMidH = BUTTON_TEX_H - BUTTON_BORDER * 2;

        // Corners
        UIHelper.drawTexture(x, y, borderX, borderY, BUTTON_TEX_U, v, BUTTON_BORDER, BUTTON_BORDER);
        UIHelper.drawTexture(
                x + width - borderX,
                y,
                borderX,
                borderY,
                BUTTON_TEX_W - BUTTON_BORDER,
                v,
                BUTTON_BORDER,
                BUTTON_BORDER);
        UIHelper.drawTexture(
                x,
                y + height - borderY,
                borderX,
                borderY,
                BUTTON_TEX_U,
                v + BUTTON_TEX_H - BUTTON_BORDER,
                BUTTON_BORDER,
                BUTTON_BORDER);
        UIHelper.drawTexture(
                x + width - borderX,
                y + height - borderY,
                borderX,
                borderY,
                BUTTON_TEX_W - BUTTON_BORDER,
                v + BUTTON_TEX_H - BUTTON_BORDER,
                BUTTON_BORDER,
                BUTTON_BORDER);

        if (midW > 0) {
            // Top + bottom edges
            UIHelper.drawTexture(x + borderX, y, midW, borderY, BUTTON_BORDER, v, srcMidW, BUTTON_BORDER);
            UIHelper.drawTexture(
                    x + borderX,
                    y + height - borderY,
                    midW,
                    borderY,
                    BUTTON_BORDER,
                    v + BUTTON_TEX_H - BUTTON_BORDER,
                    srcMidW,
                    BUTTON_BORDER);
        }

        if (midH > 0) {
            // Left + right edges
            UIHelper.drawTexture(
                    x,
                    y + borderY,
                    borderX,
                    midH,
                    BUTTON_TEX_U,
                    v + BUTTON_BORDER,
                    BUTTON_BORDER,
                    srcMidH);
            UIHelper.drawTexture(
                    x + width - borderX,
                    y + borderY,
                    borderX,
                    midH,
                    BUTTON_TEX_W - BUTTON_BORDER,
                    v + BUTTON_BORDER,
                    BUTTON_BORDER,
                    srcMidH);
        }

        if (midW > 0 && midH > 0) {
            // Center
            UIHelper.drawTexture(
                    x + borderX,
                    y + borderY,
                    midW,
                    midH,
                    BUTTON_BORDER,
                    v + BUTTON_BORDER,
                    srcMidW,
                    srcMidH);
        }
    }

    @Override
    public void onMouseEnter(MouseEvent event) {
        mouseOver = true;
    }

    @Override
    public void onMouseLeave(MouseEvent event) {
        mouseOver = false;
    }

    @Override
    public IWidget getWidgetAtCoordinates(double posX, double posY) {
        return this;
    }

    @Override
    public void onMouseClick(MouseEvent event) {
        if (event.button == 0) this.mc.getSoundHandler()
                .playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));

        this.emit(Signal.CLICKED, event.button);
    }
}
