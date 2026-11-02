package mcp.mobius.waila.overlay;

import java.util.Arrays;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import com.gtnewhorizons.dashboard.api.HudBounds;
import com.gtnewhorizons.dashboard.api.HudElement;
import com.gtnewhorizons.dashboard.api.HudRegistry;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Lets players move, resize and hide the info box in Dashboard's HUD editor. The only class using Dashboard, so only
 * call it while Dashboard is loaded.
 */
@SideOnly(Side.CLIENT)
public final class DashboardHud {

    private static final long SAMPLE_REUSE_MILLIS = 1000;

    private static final HudElement INFO_BOX = new HudElement("waila:info_box", DashboardHud::getBounds)
            .setOwnSettings("dashboard.element.waila.info_box.own_settings", "waila.keybind.wailaconfig");

    private static Tooltip sample;
    private static long lastSampleTime;

    private DashboardHud() {}

    public static void register() {
        INFO_BOX.setPreview((screenWidth, screenHeight) -> OverlayRenderer.drawPreview(getSample()));
        HudRegistry.register(INFO_BOX);
    }

    /**
     * The same sample is kept while the editor is open, since a box drawn by another mod may start over for every new
     * one. A new one is made after a break, so it follows changed Waila settings.
     */
    private static Tooltip getSample() {
        long now = System.currentTimeMillis();
        if (sample == null || now - lastSampleTime > SAMPLE_REUSE_MILLIS) {
            sample = createSample();
        }
        lastSampleTime = now;
        return sample;
    }

    /** Stone, like looking at a block. */
    private static Tooltip createSample() {
        ItemStack stone = new ItemStack(Blocks.stone);
        return new Tooltip(
                Arrays.asList(
                        EnumChatFormatting.WHITE + stone.getDisplayName(),
                        EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC + "Minecraft"),
                stone);
    }

    private static HudBounds getBounds(int screenWidth, int screenHeight) {
        if (sample == null) {
            sample = createSample();
        }
        float scale = OverlayConfig.scale;
        return new HudBounds(
                Math.round(sample.x * scale),
                Math.round(sample.y * scale),
                Math.round((sample.w + 1) * scale),
                Math.round((sample.h + 1) * scale));
    }
}
