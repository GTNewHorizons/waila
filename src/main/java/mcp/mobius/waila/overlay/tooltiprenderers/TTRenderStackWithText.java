package mcp.mobius.waila.overlay.tooltiprenderers;

import java.awt.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.common.registry.GameData;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.overlay.DisplayUtil;

public class TTRenderStackWithText implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        String text = params[2];
        int padding = Integer.parseInt(params[3]);
        return new Dimension(DisplayUtil.getDisplayWidth(text) + padding + 16, 8);
    }

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        String name = params[0];
        int meta = Integer.parseInt(params[1]);
        String text = params[2];
        int padding = Integer.parseInt(params[3]);
        float scale = 0.6f;

        ItemStack stack = new ItemStack((Item) Item.itemRegistry.getObject(name), meta);

        RenderHelper.enableGUIStandardItemLighting();

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScaled(scale, scale, 1.0f);

        DisplayUtil.renderStack(padding, 0, stack);

        GL11.glPopMatrix();

        if (text != null && !text.isEmpty()) {
            FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

            int textX = (int) (12 + (padding * scale));
            int textY = (12 - fontRenderer.FONT_HEIGHT) / 2;

            fontRenderer.drawString(text, textX, textY, 0xFFFFFF);
        }

        RenderHelper.disableStandardItemLighting();
    }

    public static String create(ItemStack itemStack, String text, int padding) {
        String name = GameData.getItemRegistry().getNameForObject(itemStack.getItem());
        int meta = itemStack.getItemDamage();

        return SpecialChars
                .getRenderString("waila.stack.text", name, String.valueOf(meta), text, String.valueOf(padding));
    }

    public static String create(ItemStack itemStack, String text) {
        return create(itemStack, text, 0);
    }
}
