package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.elements.IItemStyle;

public class ItemStyle implements IItemStyle {
    private int width = 20;
    private int height = 20;

    public ItemStyle(int w, int h) {
        width = w;
        height = h;
    }

    public ItemStyle() {
    }

    public IItemStyle width(int w) {
        this.width = w;
        return this;
    }

    public IItemStyle height(int h) {
        this.height = h;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
}
