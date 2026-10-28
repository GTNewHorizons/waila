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

    /** Alpha change per game tick */
    private static final float FADE_PER_TICK = 0.1f;
    /** Alpha while mining. The line is fully opaque only in the last tick before the block breaks. */
    private static final float MAX_ALPHA = 0.6f;

    private static float progress;
    private static float alpha;
    private static boolean canHarvest = true;
    private static long lastFrame;

    private static Field curBlockDamageField;
    private static boolean fieldFailed;

    private BreakProgressRenderer() {}

    private static boolean isEnabled() {
        return ConfigHandler.instance().getConfig(CFG_ENABLED, true);
    }

    /**
     * Updates the progress state. Call this one time per frame before you draw.
     */
    public static void update(float partialTicks) {
        final Minecraft mc = Minecraft.getMinecraft();
        long now = Minecraft.getSystemTime();
        float deltaTicks = (now - lastFrame) / 50f;
        lastFrame = now;

        if (!isEnabled() || mc.playerController == null || mc.theWorld == null || mc.thePlayer == null) {
            progress = alpha = 0;
            return;
        }

        float damage = getBlockDamage(mc.playerController);
        MovingObjectPosition target = RayTracing.instance().getTarget();
        if (damage <= 0 || target == null || target.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            // Not mining (anymore), fade out the last progress
            alpha = Math.max(alpha - deltaTicks * FADE_PER_TICK, 0f);
            return;
        }

        int x = target.blockX, y = target.blockY, z = target.blockZ;
        Block block = mc.theWorld.getBlock(x, y, z);
        canHarvest = ForgeHooks.canHarvestBlock(block, mc.thePlayer, mc.theWorld.getBlockMetadata(x, y, z));
        alpha = Math.min(alpha + deltaTicks * FADE_PER_TICK, MAX_ALPHA);
        float perTick = block.getPlayerRelativeBlockHardness(mc.thePlayer, mc.theWorld, x, y, z);
        if (damage + perTick >= 1f) {
            // The block breaks in the next tick
            alpha = progress = 1f;
        } else {
            progress = MathHelper.clamp_float(damage + partialTicks * perTick, 0f, 1f);
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
     * @return true while the line is visible, also while it fades out after the block was broken.
     */
    public static boolean isFading() {
        return alpha > 0;
    }

    /**
     * Draws the line on the outer row of the tooltip box. The arguments are the box geometry from {@link Tooltip}.
     */
    public static void draw(int x, int y, int w, int h) {
        if (!isEnabled() || alpha <= 0) return;

        int width = (int) ((w - 1) * progress);
        if (width <= 0) return;

        // Transparent as the box background
        float boxAlpha = (OverlayConfig.alpha >>> 24) / 255f;
        int base = canHarvest ? ColorUtils.breakProgress.getColor() : ColorUtils.breakProgressFailure.getColor();
        int color = ((int) (((base >>> 24) & 0xFF) * alpha * boxAlpha) << 24) | (base & 0xFFFFFF);

        boolean top = ConfigHandler.instance().getConfig(CFG_TOP, false);
        DisplayUtil.drawGradientRect(x + 1, top ? y : y + h, width, 1, color, color);
    }
}
