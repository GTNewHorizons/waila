package mcp.mobius.waila.api.impl.elements;

import io.netty.buffer.ByteBuf;
import mcp.mobius.waila.api.elements.ElementAlignment;
import mcp.mobius.waila.api.elements.IElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
