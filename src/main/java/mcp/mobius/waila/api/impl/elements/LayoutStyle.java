package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.elements.ElementAlignment;
import mcp.mobius.waila.api.elements.ILayoutStyle;

public class LayoutStyle implements ILayoutStyle {
    private Integer borderColor = null;
    private ElementAlignment alignment;
    private int spacing;

    public LayoutStyle() {
        this.alignment = ElementAlignment.ALIGN_TOPLEFT;
        this.spacing = -1;
    }

    public ILayoutStyle alignment(ElementAlignment alignment) {
        this.alignment = alignment;
        return this;
    }

    public ElementAlignment getAlignment() {
        return this.alignment;
    }

    public LayoutStyle borderColor(Integer c) {
        this.borderColor = c;
        return this;
    }

    public LayoutStyle spacing(int f) {
        this.spacing = f;
        return this;
    }

    public Integer getBorderColor() {
        return this.borderColor;
    }

    public int getSpacing() {
        return this.spacing;
    }
}