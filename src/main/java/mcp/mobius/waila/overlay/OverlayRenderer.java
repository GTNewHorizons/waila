package mcp.mobius.waila.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MovingObjectPosition;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.LoadedMods;

public class OverlayRenderer {

    private static boolean hasBlending;
    private static boolean hasDepthTest;
    private static int boundTexIndex;

    public static boolean isOverlayVisible() {
        final Minecraft mc = Minecraft.getMinecraft();
        return (mc.currentScreen == null && mc.theWorld != null
                && Minecraft.isGuiEnabled()
                && !mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
                && ConfigHandler.instance().showTooltip());
    }

    public static void renderOverlay(Tooltip tooltip) {
        if (!isOverlayVisible() || RayTracing.instance().getTarget() == null) return;

        if (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK
                && RayTracing.instance().getTargetStack() != null) {
            doRenderOverlay(tooltip);
        }

        if (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY
                && ConfigHandler.instance().getConfig("general.showents")) {
            doRenderOverlay(tooltip);
        }
    }

    /** Draws a sample box, for previews like a HUD editor's. */
    static void drawPreview(Tooltip tooltip) {
        doRenderOverlay(tooltip);
    }

    /**
     * Call right before drawing the info box, with its place and size before scaling, also when another mod draws the
     * box. Lets a HUD editor move, resize or hide it. False means the box is hidden, so skip drawing it and do not call
     * {@link #endBox()}.
     */
    public static boolean beginBox(int x, int y, int width, int height) {
        return !LoadedMods.DASHBOARD || DashboardHud.beginBox(x, y, width, height);
    }

    /** Call right after drawing the info box, only if {@link #beginBox} returned true. */
    public static void endBox() {
        if (LoadedMods.DASHBOARD) DashboardHud.endBox();
    }

    private static void doRenderOverlay(Tooltip tooltip) {
        if (!beginBox(tooltip.x, tooltip.y, tooltip.w + 1, tooltip.h + 1)) return;

        GL11.glPushMatrix();
        saveGLState();

        GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawTooltipBox(
                tooltip.x,
                tooltip.y,
                tooltip.w,
                tooltip.h,
                OverlayConfig.bgcolor,
                OverlayConfig.gradient1,
                OverlayConfig.gradient2);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        tooltip.draw();
        GL11.glDisable(GL11.GL_BLEND);

        tooltip.draw2nd();

        if (tooltip.hasIcon) RenderHelper.enableGUIStandardItemLighting();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (tooltip.hasIcon && tooltip.stack != null && tooltip.stack.getItem() != null)
            DisplayUtil.renderStack(tooltip.x + 5, tooltip.y + tooltip.h / 2 - 8, tooltip.stack);

        loadGLState();
        GL11.glPopMatrix();
        endBox();
    }

    private static void saveGLState() {
        hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }

    private static void loadGLState() {
        if (hasBlending) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
        GL11.glPopAttrib();
    }

    private static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
        DisplayUtil.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);// center
        DisplayUtil.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        DisplayUtil.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
        DisplayUtil.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
        DisplayUtil.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }
}
