package mcp.mobius.waila.api.impl.elements;

import java.util.Iterator;

import mcp.mobius.waila.api.elements.ElementAlignment;
import mcp.mobius.waila.api.elements.IElement;

public class ElementVertical extends AbstractElementPanel {

    public ElementVertical(Integer borderColor, int spacing, ElementAlignment alignment) {
        super(borderColor, spacing, alignment);
    }

    public void render(int x, int y) {
        super.render(x, y);
        if (this.borderColor != null) {
            x += 3;
            y += 3;
        }

        int totWidth = this.getWidth();

        IElement element;
        for (Iterator var4 = this.children.iterator(); var4.hasNext(); y += element.getHeight() + this.spacing) {
            element = (IElement) var4.next();
            int w = element.getWidth();
            int cx = x;
            switch (this.alignment) {
                case ALIGN_TOPLEFT:
                default:
                    break;
                case ALIGN_CENTER:
                    cx = x + (totWidth - w) / 2;
                    break;
                case ALIGN_BOTTOMRIGHT:
                    cx = x + totWidth - w;
            }

            element.render(cx, y);
        }

    }

    private int getBorderSpacing() {
        return this.borderColor == null ? 0 : 6;
    }

    public int getHeight() {
        int h = 0;

        IElement element;
        for (Iterator var2 = this.children.iterator(); var2.hasNext(); h += element.getHeight()) {
            element = (IElement) var2.next();
        }

        return h + this.spacing * (this.children.size() - 1) + this.getBorderSpacing();
    }

    public int getWidth() {
        int w = 0;
        Iterator var2 = this.children.iterator();

        while (var2.hasNext()) {
            IElement element = (IElement) var2.next();
            int ww = element.getWidth();
            if (ww > w) {
                w = ww;
            }
        }

        return w + this.getBorderSpacing();
    }
}
