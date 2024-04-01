package mcp.mobius.waila.overlay;

import static mcp.mobius.waila.api.SpecialChars.patternIcon;
import static mcp.mobius.waila.api.SpecialChars.patternMinecraft;
import static mcp.mobius.waila.api.SpecialChars.patternRender;
import static mcp.mobius.waila.api.SpecialChars.patternWaila;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class DisplayUtil {

    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    private static TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    private static RenderItem renderItem = new RenderItem();

    public static int getDisplayWidth(String s) {
        if (s == null || s.isEmpty()) return 0;

        int width = 0;

        Matcher renderMatcher = patternRender.matcher(s);
        while (renderMatcher.find()) {
            IWailaTooltipRenderer renderer = ModuleRegistrar.instance().getTooltipRenderer(renderMatcher.group("name"));
            if (renderer != null)
                width += renderer.getSize(renderMatcher.group("args").split(","), DataAccessorCommon.instance).width;
        }

        Matcher iconMatcher = patternIcon.matcher(s);
        while (iconMatcher.find()) width += 8;

        width += fontRenderer.getStringWidth(stripSymbols(s));
        return width;
    }

    public static Dimension displaySize() {
        Minecraft mc = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        return new Dimension(res.getScaledWidth(), res.getScaledHeight());
    }

    public static String stripSymbols(String s) {
        String result = patternRender.matcher(s).replaceAll("");
        result = patternMinecraft.matcher(result).replaceAll("");
        result = patternWaila.matcher(result).replaceAll("");
        return result;
    }

    public static String stripWailaSymbols(String s) {
        String result = patternRender.matcher(s).replaceAll("");
        result = patternWaila.matcher(result).replaceAll("");
        return result;
    }

    public static void renderStack(int x, int y, ItemStack stack) {
        enable3DRender();
        try {
            renderItem.renderItemAndEffectIntoGUI(fontRenderer, textureManager, stack, x, y);
            renderItem.renderItemOverlayIntoGUI(fontRenderer, textureManager, stack, x, y);
        } catch (Exception e) {
            String stackStr = stack != null ? stack.toString() : "NullStack";
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

    public static void drawGradientRect(int x, int y, int w, int h, int grad1, int grad2) {
        float zLevel = 0.0f;

        float f = (float) (grad1 >> 24 & 255) / 255.0F;
        float f1 = (float) (grad1 >> 16 & 255) / 255.0F;
        float f2 = (float) (grad1 >> 8 & 255) / 255.0F;
        float f3 = (float) (grad1 & 255) / 255.0F;
        float f4 = (float) (grad2 >> 24 & 255) / 255.0F;
        float f5 = (float) (grad2 >> 16 & 255) / 255.0F;
        float f6 = (float) (grad2 >> 8 & 255) / 255.0F;
        float f7 = (float) (grad2 & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(f1, f2, f3, f);
        tessellator.addVertex(x + w, y, zLevel);
        tessellator.addVertex(x, y, zLevel);
        tessellator.setColorRGBA_F(f5, f6, f7, f4);
        tessellator.addVertex(x, y + h, zLevel);
        tessellator.addVertex(x + w, y + h, zLevel);
        tessellator.draw();
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }

    public static void drawTexturedModalRect(int x, int y, int u, int v, int w, int h, int tw, int th) {
        float f = 0.00390625F;
        float f1 = 0.00390625F;
        float zLevel = 0.0F;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorOpaque_F(1, 1, 1);
        tessellator.addVertexWithUV(x, y + h, zLevel, (u) * f, (v + th) * f1);
        tessellator.addVertexWithUV(x + w, y + h, zLevel, (u + tw) * f, (v + th) * f1);
        tessellator.addVertexWithUV(x + w, y, zLevel, (u + tw) * f, (v) * f1);
        tessellator.addVertexWithUV(x, y, zLevel, (u) * f, (v) * f1);
        tessellator.draw();
    }

    public static void drawString(String text, int x, int y, int colour, boolean shadow) {
        if (shadow) fontRenderer.drawStringWithShadow(text, x, y, colour);
        else fontRenderer.drawString(text, x, y, colour);
    }

    public static List<String> itemDisplayNameMultiline(ItemStack itemstack) {
        List<String> namelist = null;
        try {
            namelist = itemstack.getTooltip(
                    Minecraft.getMinecraft().thePlayer,
                    Minecraft.getMinecraft().gameSettings.advancedItemTooltips);
        } catch (Throwable ignored) {}

        if (namelist == null) namelist = new ArrayList<>();

        if (namelist.isEmpty()) namelist.add("Unnamed");

        if (namelist.get(0) == null || namelist.get(0).isEmpty()) namelist.set(0, "Unnamed");

        namelist.set(0, itemstack.getRarity().rarityColor.toString() + namelist.get(0));
        for (int i = 1; i < namelist.size(); i++) namelist.set(i, "\u00a77" + namelist.get(i));

        return namelist;
    }

    public static String itemDisplayNameShort(ItemStack itemstack) {
        List<String> list = itemDisplayNameMultiline(itemstack);
        return list.get(0);
    }

    public static void renderIcon(int x, int y, int sx, int sy, IconUI icon) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);

        if (icon == null) return;

        if (icon.bu != -1) DisplayUtil.drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
        DisplayUtil.drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }

}
