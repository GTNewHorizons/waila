package mcp.mobius.waila.overlay;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;

/**
 * Draws a small turning 3D model of an entity as the tooltip icon. Based on {@code GuiInventory.func_147046_a} and the
 * entity icon of WDMla (https://github.com/GTNewHorizons/WDMla).
 */
public final class EntityIconRenderer {

    public static final String CFG_SHOW = "general.showentityicon";

    /** Degrees per second */
    private static final float TURN_SPEED = 45f;

    private static final Set<Class<?>> failedClasses = new HashSet<>();

    private EntityIconRenderer() {}

    public static boolean isEnabled() {
        return ConfigHandler.instance().getConfig(CFG_SHOW, true);
    }

    /**
     * Draws the entity in the square with the top left corner (x, y) and the side length size.
     */
    public static void render(int x, int y, int size, Entity entity) {
        if (entity == null || failedClasses.contains(entity.getClass())) return;

        final Minecraft mc = Minecraft.getMinecraft();
        // The body of most mobs is longer than the bounding box is wide, and the model turns
        float extent = Math.max(Math.max(entity.height, entity.width * 1.5f), 0.1f);
        float scale = size / extent;
        float angle = (Minecraft.getSystemTime() % 360000L) / 1000f * TURN_SPEED;

        float yaw = entity.rotationYaw;
        float prevYaw = entity.prevRotationYaw;
        float pitch = entity.rotationPitch;
        float prevPitch = entity.prevRotationPitch;
        float bodyYaw = 0, prevBodyYaw = 0, headYaw = 0, prevHeadYaw = 0;
        EntityLivingBase living = entity instanceof EntityLivingBase l ? l : null;
        if (living != null) {
            bodyYaw = living.renderYawOffset;
            prevBodyYaw = living.prevRenderYawOffset;
            headYaw = living.rotationYawHead;
            prevHeadYaw = living.prevRotationYawHead;
        }
        float playerViewY = RenderManager.instance.playerViewY;
        boolean hideGui = mc.gameSettings.hideGUI;

        // Entity renderers change blend, depth, alpha and lighting state (so save all of it)
        int matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            // Models can be larger than their bounding box, do not draw over the text
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            float factor = res.getScaleFactor() * OverlayConfig.scale;
            int margin = 3;
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                    (int) ((x - margin) * factor),
                    (int) (mc.displayHeight - (y + size + margin) * factor),
                    (int) ((size + 2 * margin) * factor),
                    (int) ((size + 2 * margin) * factor));

            GL11.glEnable(GL11.GL_COLOR_MATERIAL);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glColor4f(1f, 1f, 1f, 1f);

            GL11.glTranslatef(x + size / 2f, y + size / 2f + entity.height * scale / 2f, 100f);
            GL11.glScalef(-scale, scale, scale);
            GL11.glRotatef(180f, 0f, 0f, 1f);
            GL11.glRotatef(-15f, 1f, 0f, 0f);
            GL11.glRotatef(135f, 0f, 1f, 0f);
            RenderHelper.enableStandardItemLighting();
            GL11.glRotatef(-135f + angle, 0f, 1f, 0f);
            GL11.glTranslatef(0f, entity.yOffset, 0f);

            entity.rotationYaw = entity.prevRotationYaw = 0f;
            entity.rotationPitch = entity.prevRotationPitch = 0f;
            if (living != null) {
                living.renderYawOffset = living.prevRenderYawOffset = 0f;
                living.rotationYawHead = living.prevRotationYawHead = 0f;
            }
            RenderManager.instance.playerViewY = 180f;
            // Hide name tags
            mc.gameSettings.hideGUI = true;

            RenderManager.instance.renderEntityWithPosYaw(entity, 0d, 0d, 0d, 0f, 1f);
        } catch (Throwable e) {
            failedClasses.add(entity.getClass());
            WailaExceptionHandler.handleErr(e, "EntityIconRenderer | " + entity.getClass().getName(), null);
        } finally {
            mc.gameSettings.hideGUI = hideGui;
            RenderManager.instance.playerViewY = playerViewY;
            entity.rotationYaw = yaw;
            entity.prevRotationYaw = prevYaw;
            entity.rotationPitch = pitch;
            entity.prevRotationPitch = prevPitch;
            if (living != null) {
                living.renderYawOffset = bodyYaw;
                living.prevRenderYawOffset = prevBodyYaw;
                living.rotationYawHead = headYaw;
                living.prevRotationYawHead = prevHeadYaw;
            }

            // Also restores lighting, the active texture unit and the lightmap enable
            GL11.glPopAttrib();
            // A renderer that failed can leave a pushed matrix behind, but should never pop the callers
            while (GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH) > matrixDepth) GL11.glPopMatrix();
        }
    }
}
