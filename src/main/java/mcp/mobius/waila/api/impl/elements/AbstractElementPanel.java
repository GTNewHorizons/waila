package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.elements.*;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractElementPanel implements IElement, IProbeInfo {
    protected List<IElement> children = new ArrayList();
    protected Integer borderColor;
    protected int spacing;
    protected ElementAlignment alignment;

    public void render(int x, int y) {
        if (this.borderColor != null) {
            int w = this.getWidth();
            int h = this.getHeight();
            drawHorizontalLine(x, y, x + w - 1, this.borderColor);
            drawHorizontalLine(x, y + h - 1, x + w - 1, this.borderColor);
            drawVerticalLine(x, y, y + h - 1, this.borderColor);
            drawVerticalLine(x + w - 1, y, y + h, this.borderColor);
        }

    }

    public AbstractElementPanel(Integer borderColor, int spacing, ElementAlignment alignment) {
        this.borderColor = borderColor;
        this.spacing = spacing;
        this.alignment = alignment;
    }

    public IProbeInfo text(String text) {
        this.children.add(new ElementText(text));
        return this;
    }

    public IProbeInfo vertical(ILayoutStyle style) {
        ElementVertical e = new ElementVertical(style.getBorderColor(), style.getSpacing(), style.getAlignment());
        this.children.add(e);
        return e;
    }

    public IProbeInfo vertical() {
        ElementVertical e = new ElementVertical(null, 2, ElementAlignment.ALIGN_TOPLEFT);
        this.children.add(e);
        return e;
    }

    public IProbeInfo horizontal(ILayoutStyle style) {
        ElementHorizontal e = new ElementHorizontal(style.getBorderColor(), style.getSpacing(), style.getAlignment());
        this.children.add(e);
        return e;
    }

    public IProbeInfo horizontal() {
        ElementHorizontal e = new ElementHorizontal((Integer)null, this.spacing, ElementAlignment.ALIGN_TOPLEFT);
        this.children.add(e);
        return e;
    }

    public IProbeInfo item(ItemStack stack, IItemStyle style) {
        this.children.add(new ElementItemStack(stack, style));
        return this;
    }

    public IProbeInfo item(ItemStack stack) {
        return this.item(stack, new ItemStyle());
    }

    public IProbeInfo progress(int current, int max) {
        return this.progress(current, max, new ProgressStyle());
    }

    public IProbeInfo progress(int current, int max, IProgressStyle style) {
        this.children.add(new ElementProgress((long)current, (long)max, style));
        return this;
    }

    public IProbeInfo progress(long current, long max) {
        return this.progress(current, max, new ProgressStyle());
    }

    public IProbeInfo progress(long current, long max, IProgressStyle style) {
        this.children.add(new ElementProgress(current, max, style));
        return this;
    }

    //tmp
    public static void drawHorizontalLine(int x1, int y1, int x2, int color) {
        Gui.drawRect(x1, y1, x2, y1 + 1, color);
    }

    //tmp
    public static void drawVerticalLine(int x1, int y1, int y2, int color) {
        Gui.drawRect(x1, y1, x1 + 1, y2, color);
    }
}
