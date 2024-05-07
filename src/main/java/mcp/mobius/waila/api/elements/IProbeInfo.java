package mcp.mobius.waila.api.elements;

import net.minecraft.item.ItemStack;

public interface IProbeInfo {

    /**
     * Create a default layout style for the horizontal or vertical elements
     */
    ILayoutStyle defaultLayoutStyle();

    /**
     * Create a default style for the progress bar
     */
    IProgressStyle defaultProgressStyle();

    /**
     * Create a default style for the text element
     */
    ITextStyle defaultTextStyle();

    /**
     * Create a default style for the item element
     */
    IItemStyle defaultItemStyle();

    IProbeInfo text(String text, ITextStyle style);

    IProbeInfo text(String text);

    IProbeInfo vertical(ILayoutStyle style);

    IProbeInfo vertical();

    IProbeInfo horizontal(ILayoutStyle style);

    IProbeInfo horizontal();

    IProbeInfo item(ItemStack stack, IItemStyle style);

    IProbeInfo item(ItemStack stack);

    IProbeInfo progress(int var1, int var2, IProgressStyle var3);

    IProbeInfo progress(int var1, int var2);

    IProbeInfo progress(long var1, long var3, IProgressStyle var5);

    IProbeInfo progress(long var1, long var3);
}
