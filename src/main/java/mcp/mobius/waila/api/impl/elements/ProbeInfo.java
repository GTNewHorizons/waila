package mcp.mobius.waila.api.impl.elements;

import java.util.List;

import mcp.mobius.waila.api.elements.ElementAlignment;
import mcp.mobius.waila.api.elements.IElement;

public class ProbeInfo extends ElementVertical {

    public List<IElement> getElements() {
        return this.children;
    }

    public ProbeInfo() {
        super(null, 2, ElementAlignment.ALIGN_TOPLEFT);
    }

    public void removeElement(IElement element) {
        this.getElements().remove(element);
    }
}
