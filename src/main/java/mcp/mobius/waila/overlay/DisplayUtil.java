package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import static mcp.mobius.waila.api.SpecialChars.patternIcon;
import static mcp.mobius.waila.api.SpecialChars.patternMinecraft;
import static mcp.mobius.waila.api.SpecialChars.patternRender;
import static mcp.mobius.waila.api.SpecialChars.patternWaila;

public class DisplayUtil {
    private static FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
    protected static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

    public static int getDisplayWidth(final String s) {
        if (s == null || s.equals(""))
            return 0;

        int width = 0;

        final Matcher renderMatcher = patternRender.matcher(s);
        while (renderMatcher.find()) {
            final IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(renderMatcher.group("name"));
            if (renderer != null)
                width += renderer.getSize(renderMatcher.group("args").split(","), DataAccessorCommon.instance).width;
        }

        final Matcher iconMatcher = patternIcon.matcher(s);
        while (iconMatcher.find())
            width += 8;

        width += fontRendererObj.getStringWidth(stripSymbols(s));
        return width;
    }

    public static Dimension displaySize() {
        final Minecraft mc = Minecraft.getMinecraft();
        final ScaledResolution res = new ScaledResolution(mc);
        return new Dimension(res.getScaledWidth(), res.getScaledHeight());
    }

    public static String stripSymbols(final String s) {
        String result = patternRender.matcher(s).replaceAll("");
        result = patternMinecraft.matcher(result).replaceAll("");
        result = patternWaila.matcher(result).replaceAll("");
        return result;
    }

    public static String stripWailaSymbols(final String s) {
        String result = patternRender.matcher(s).replaceAll("");
        result = patternWaila.matcher(result).replaceAll("");
        return result;
    }

    public static void renderStack(final int x, final int y, final ItemStack stack) {
        enable3DRender();
        try {
            renderItem.renderItemAndEffectIntoGUI(stack, x, y);
            renderItem.renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, null);
        } catch (final Exception e) {
            final String stackStr = stack != null ? stack.toString() : "NullStack";
            WailaExceptionHandler.handleErr(e, "renderStack | " + stackStr, null);
        }
        enable2DRender();
    }

    public static void enable3DRender() {
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public static void enable2DRender() {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    public static void drawGradientRect(final int left, final int top, final int right, final int bottom, final int startColor, final int endColor) {
        final float zLevel = 0.0F;

        final float f = (float) (startColor >> 24 & 255) / 255.0F;
        final float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        final float f3 = (float) (startColor & 255) / 255.0F;
        final float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        final float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        final float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        final float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();
        t.begin(7, DefaultVertexFormats.POSITION_COLOR);
        t.pos((double) (left + right), (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        t.pos((double) left, (double) top, (double) zLevel).color(f1, f2, f3, f).endVertex();
        t.pos((double) left, (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).endVertex();
        t.pos((double) (left + right), (double) (top + bottom), (double) zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawTexturedModalRect(final int x, final int y, final int textureX, final int textureY, final int width, final int height, final int tw, final int th) {
        final float f = 0.00390625F;
        final float f1 = 0.00390625F;
        final float zLevel = 0.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();
        t.begin(7, DefaultVertexFormats.POSITION_TEX);
        t.pos((double) (x + 0), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + th) * f1)).endVertex();
        t.pos((double) (x + width), (double) (y + height), (double) zLevel).tex((double) ((float) (textureX + tw) * f), (double) ((float) (textureY + th) * f1)).endVertex();
        t.pos((double) (x + width), (double) (y + 0), (double) zLevel).tex((double) ((float) (textureX + tw) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
        t.pos((double) (x + 0), (double) (y + 0), (double) zLevel).tex((double) ((float) (textureX + 0) * f), (double) ((float) (textureY + 0) * f1)).endVertex();
        tessellator.draw();
    }

    public static void drawString(final String text, final int x, final int y, final int colour, final boolean shadow) {
        if (shadow)
            fontRendererObj.drawStringWithShadow(text, x, y, colour);
        else
            fontRendererObj.drawString(text, x, y, colour);
    }

    public static List<String> itemDisplayNameMultiline(final ItemStack itemstack) {
        List<String> namelist = null;
        try {
            namelist = itemstack.getTooltip(Minecraft.getMinecraft().player, Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        } catch (final Throwable ignored) {
        }

        if (namelist == null)
            namelist = new ArrayList<String>();

        if (namelist.size() == 0)
            namelist.add("Unnamed");

        if (namelist.get(0) == null || namelist.get(0).equals(""))
            namelist.set(0, "Unnamed");

        namelist.set(0, itemstack.getRarity().rarityColor.toString() + namelist.get(0));
        for (int i = 1; i < namelist.size(); i++)
            namelist.set(i, "\u00a77" + namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(final ItemStack itemstack) {
        final List<String> list = itemDisplayNameMultiline(itemstack);
        return list.get(0);
    }

    public static void renderIcon(final int x, final int y, final int sx, final int sy, final IconUI icon) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);

        if (icon == null)
            return;

        if (icon.bu != -1)
            DisplayUtil.drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }
}