package mcp.mobius.waila.api.elements;

import net.minecraft.item.ItemStack;

public interface IProbeInfo {

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
