package mcp.mobius.waila.api.impl.elements;

import net.minecraft.client.Minecraft;

import mcp.mobius.waila.api.elements.IElement;
import mcp.mobius.waila.api.elements.ITextStyle;
import mcp.mobius.waila.overlay.DisplayUtil;

public class ElementText implements IElement {

    private final String text;
    private final ITextStyle style;

    private static final int DEFAULT_HEIGHT = 10;

    public ElementText(String text) {
        this(text, new TextStyle());
    }

    public ElementText(String text, ITextStyle style) {
        this.text = text;
        this.style = style;
    }

    public void render(int x, int y) {
        int width = getTextWidth();
        switch (style.getAlignment()) {
            case ALIGN_BOTTOMRIGHT -> DisplayUtil.drawString(
                    text,
                    (x + getInternalWidth() - width) + style.getLeftPadding(),
                    y + style.getTopPadding(),
                    style.getColor(),
                    style.getShadow());
            case ALIGN_CENTER -> DisplayUtil.drawString(
                    text,
                    ((x + (getInternalWidth() / 2)) - (width / 2)) + style.getLeftPadding(),
                    y + style.getTopPadding(),
                    style.getColor(),
                    style.getShadow());
            case ALIGN_TOPLEFT -> DisplayUtil.drawString(
                    text,
                    x + style.getLeftPadding(),
                    y + style.getTopPadding(),
                    style.getColor(),
                    style.getShadow());
        }
    }

    public int getTextWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    protected int getInternalWidth() {
        return (style.getWidth() != null ? style.getWidth() : getTextWidth());
    }

    @Override
    public int getWidth() {
        return style.getLeftPadding() + (style.getWidth() != null ? style.getWidth() : getTextWidth())
                + style.getRightPadding();
    }

    @Override
    public int getHeight() {
        return style.getTopPadding() + (style.getHeight() != null ? style.getHeight() : DEFAULT_HEIGHT)
                + style.getBottomPadding();
    }
}
