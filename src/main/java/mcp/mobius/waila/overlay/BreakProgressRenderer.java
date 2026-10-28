package mcp.mobius.waila.overlay;

import java.lang.reflect.Field;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.ForgeHooks;

import cpw.mods.fml.relauncher.ReflectionHelper;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.ColorUtils;

/**
 * Draws the block break progress as a thin line on the edge of the tooltip box (Jade style). Based on the break
 * progress of WDMla (https://github.com/GTNewHorizons/WDMla).
 */
public final class BreakProgressRenderer {

    public static final String CFG_ENABLED = "general.breakprogress";
    public static final String CFG_TOP = "general.breakprogresstop";

    /** Time in ms that the line needs to fade out completely. */
    private static final long FADE_TIME = 250;
    private static final int THICKNESS = 2;

    private static float shownProgress;
    private static boolean canHarvest = true;
    private static long fadeStart = -1;

    private static int lastX, lastY, lastZ;
    private static Block lastBlock;

    private static Field curBlockDamageField;
    private static boolean fieldFailed;

    private BreakProgressRenderer() {}

    private static boolean isEnabled() {
        return ConfigHandler.instance().getConfig(CFG_ENABLED, true);
    }

    /**
     * Updates the progress state. Call this one time per frame before you draw.
     */
    public static void update() {
        final Minecraft mc = Minecraft.getMinecraft();
        if (!isEnabled() || mc.playerController == null || mc.theWorld == null) {
            reset();
            return;
        }

        float damage = MathHelper.clamp_float(getBlockDamage(mc.playerController), 0f, 1f);
        if (damage > 0) {
            shownProgress = damage;
            fadeStart = -1;
            MovingObjectPosition target = RayTracing.instance().getTarget();
            if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                lastX = target.blockX;
                lastY = target.blockY;
                lastZ = target.blockZ;
                lastBlock = mc.theWorld.getBlock(lastX, lastY, lastZ);
                int meta = mc.theWorld.getBlockMetadata(lastX, lastY, lastZ);
                canHarvest = ForgeHooks.canHarvestBlock(lastBlock, mc.thePlayer, meta);
            }
            return;
        }

        if (shownProgress > 0 && fadeStart < 0) {
            // Mining stopped. If the block is gone, show a full line before it fades out.
            if (lastBlock != null && mc.theWorld.getBlock(lastX, lastY, lastZ) != lastBlock) {
                shownProgress = 1f;
            }
            fadeStart = Minecraft.getSystemTime();
        }
    }

    private static float getBlockDamage(PlayerControllerMP controller) {
        if (fieldFailed) return 0f;
        try {
            if (curBlockDamageField == null) {
                curBlockDamageField = ReflectionHelper
                        .findField(PlayerControllerMP.class, "curBlockDamageMP", "field_78770_f");
            }
            return curBlockDamageField.getFloat(controller);
        } catch (Exception e) {
            fieldFailed = true;
            Waila.log.error("Cannot read the block damage, break progress is disabled", e);
            return 0f;
        }
    }

    /**
     * @return true while the line fades out after the player stopped mining or broke the block.
     */
    public static boolean isFading() {
        return fadeStart >= 0 && getAlpha() > 0;
    }

    private static void reset() {
        shownProgress = 0;
        fadeStart = -1;
        lastBlock = null;
    }

    private static float getAlpha() {
        // Same curve as WDMla and Jade: the line gets more opaque while the progress increases.
        float alpha = Math.min(Math.min(shownProgress, 0.6f) + 0.4f * shownProgress, 1f);
        if (fadeStart >= 0) {
            float faded = 1f - (float) (Minecraft.getSystemTime() - fadeStart) / FADE_TIME;
            alpha *= Math.max(faded, 0f);
        }
        return alpha;
    }

    /**
     * Draws the line on the tooltip box. The arguments are the box geometry from {@link Tooltip}.
     */
    public static void draw(int x, int y, int w, int h) {
        if (!isEnabled() || shownProgress <= 0) return;

        float alpha = getAlpha();
        if (alpha <= 0) {
            reset();
            return;
        }

        int width = (int) ((w - 1) * shownProgress);
        if (width <= 0) return;

        int base = canHarvest ? ColorUtils.breakProgress.getColor() : ColorUtils.breakProgressFailure.getColor();
        int color = ((int) (((base >>> 24) & 0xFF) * alpha) << 24) | (base & 0xFFFFFF);

        boolean top = ConfigHandler.instance().getConfig(CFG_TOP, false);
        int lineY = top ? y : y + h + 1 - THICKNESS;
        DisplayUtil.drawGradientRect(x + 1, lineY, width, THICKNESS, color, color);
    }
}
