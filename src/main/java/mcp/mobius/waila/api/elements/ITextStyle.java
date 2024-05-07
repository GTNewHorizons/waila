package mcp.mobius.waila.api.elements;

import mcp.mobius.waila.api.impl.elements.TextStyle;

public interface ITextStyle {

    /// Allows copying the state for easier template creation
    ITextStyle copy();

    default ITextStyle padding(int padding) {
        return topPadding(padding).bottomPadding(padding).leftPadding(padding).rightPadding(padding);
    }

    default ITextStyle vPadding(int padding) {
        return topPadding(padding).bottomPadding(padding);
    }

    default ITextStyle hPadding(int padding) {
        return leftPadding(padding).rightPadding(padding);
    }

    ITextStyle topPadding(int padding);

    ITextStyle bottomPadding(int padding);

    ITextStyle leftPadding(int padding);

    ITextStyle rightPadding(int padding);

    default ITextStyle bounds(Integer width, Integer height) {
        return width(width).height(height);
    }

    ITextStyle width(Integer width);

    ITextStyle height(Integer height);

    ITextStyle alignment(ElementAlignment align);

    TextStyle color(int color);

    TextStyle shadow(boolean shadow);

    int getLeftPadding();

    int getRightPadding();

    int getTopPadding();

    int getBottomPadding();

    Integer getWidth();

    Integer getHeight();

    ElementAlignment getAlignment();

    int getColor();

    boolean getShadow();
}
