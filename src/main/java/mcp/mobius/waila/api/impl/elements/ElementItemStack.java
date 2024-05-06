package mcp.mobius.waila.api.impl.elements;


import mcp.mobius.waila.api.elements.IElement;
import mcp.mobius.waila.api.elements.IItemStyle;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class ElementItemStack implements IElement {

    private final ItemStack itemStack;
    private final IItemStyle style;

    public ElementItemStack(ItemStack itemStack, IItemStyle style) {
        this.itemStack = itemStack;
        this.style = style;
    }

    @Override
    public void render(int x, int y) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        DisplayUtil.elementRenderStack(x, y, itemStack, style);
    }

    @Override
    public int getWidth() {
        return style.getWidth();
    }

    @Override
    public int getHeight() {
        return style.getHeight();
    }
}
