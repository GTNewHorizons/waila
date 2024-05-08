package mcp.mobius.waila.overlay;

import java.awt.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.elements.ProbeInfo;
import mcp.mobius.waila.utils.Constants;

public class TOPOverlayRenderer {

    protected static boolean hasBlending;
    protected static boolean hasLight;
    protected static boolean hasDepthTest;
    protected static boolean hasLight0;
    protected static boolean hasLight1;
    protected static boolean hasRescaleNormal;
    protected static boolean hasColorMaterial;
    protected static int boundTexIndex;

    static int w, h, x, y, ty;
    private static float progressAlpha = 0;
    private static long lastMilliSecond;
    private static float savedProgress = 0;

    private static final int MARGIN = 5;
    private static final int DEFAULT_COLOR = 0xFFA0A0A0;
    private static final int FAILURE_COLOR = 0xFFAA0000;

    public static void renderOverlay() {
        Minecraft mc = Minecraft.getMinecraft();
        if (!(mc.currentScreen == null && mc.theWorld != null
                && Minecraft.isGuiEnabled()
                && !mc.gameSettings.keyBindPlayerList.getIsKeyPressed()
                && ConfigHandler.instance().showTooltip()
                && RayTracing.instance().getTarget() != null))
            return;

        computePositionAndSize(WailaTickHandler.instance().probe);
        renderElements(WailaTickHandler.instance().probe);
    }

    public static void renderElements(ProbeInfo probeInfo) {
        GL11.glPushMatrix();
        saveGLState();

        GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawTooltipBox(x, y, w, h, OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);
        drawProgressBar();

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        probeInfo.render(x + MARGIN, y + MARGIN);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        loadGLState();
        GL11.glPopMatrix();
    }

    private static void drawProgressBar() {
        float elapsedSecond = (float) (System.currentTimeMillis() - lastMilliSecond) / 1000;
        float damage = Minecraft.getMinecraft().playerController.curBlockDamageMP;
        if (damage == 0 && progressAlpha <= 0) {
            lastMilliSecond = System.currentTimeMillis();
            return;
        }

        int drawX = x + 1;
        int drawY = y; // TODO: configurable top / bottom drawing

        if (damage > 0) {
            progressAlpha = Math.min(damage, 0.6F);
            progressAlpha += 0.4F * damage;
            savedProgress = damage;
        } else {
            progressAlpha -= elapsedSecond;
        }

        int color = applyAlpha(DEFAULT_COLOR, progressAlpha); // TODO: change color with harvestability
        int width = (int) ((w - 1) * savedProgress);
        DisplayUtil.drawGradientRect(drawX, drawY, width - 1, 2, color, color);
        lastMilliSecond = System.currentTimeMillis();
    }

    private static int applyAlpha(int color, float alpha) {
        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int appliedAlpha = (int) (alpha * 255); // アルファ値を0〜255の範囲に変換
        return (appliedAlpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static void saveGLState() {
        hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
        hasLight = GL11.glGetBoolean(GL11.GL_LIGHTING);
        hasDepthTest = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
        boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }

    public static void loadGLState() {
        if (hasBlending) GL11.glEnable(GL11.GL_BLEND);
        else GL11.glDisable(GL11.GL_BLEND);
        if (hasLight1) GL11.glEnable(GL11.GL_LIGHT1);
        else GL11.glDisable(GL11.GL_LIGHT1);
        if (hasDepthTest) GL11.glEnable(GL11.GL_DEPTH_TEST);
        else GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
        GL11.glPopAttrib();
    }

    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2) {
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

    private static void computePositionAndSize(ProbeInfo probeInfo) {
        Point pos = new Point(
                ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSX, 0),
                ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_POSY, 0));

        w = probeInfo.getWidth() + MARGIN * 2;
        h = probeInfo.getHeight() + MARGIN * 2;

        Dimension size = DisplayUtil.displaySize();
        x = ((int) (size.width / OverlayConfig.scale) - w - 1) * pos.x / 10000;
        y = ((int) (size.height / OverlayConfig.scale) - h - 1) * pos.y / 10000;

        ty = (h - probeInfo.getHeight()) / 2 + 1;
    }

}
