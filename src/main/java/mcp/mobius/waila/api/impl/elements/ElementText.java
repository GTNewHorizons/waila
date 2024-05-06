package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.elements.IElement;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.OverlayConfig;
import net.minecraft.client.Minecraft;

public class ElementText implements IElement {
    private final String text;

    public ElementText(String text) {
        this.text = text;
    }

    public void render(int x, int y) {
        DisplayUtil.drawString(this.text, x, y, OverlayConfig.fontcolor, true);
    }

    public int getWidth() {
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public int getHeight() {
        return 10;
    }
}
