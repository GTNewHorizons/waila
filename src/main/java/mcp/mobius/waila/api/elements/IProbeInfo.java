package mcp.mobius.waila.api.elements;

import net.minecraft.item.ItemStack;

public interface IProbeInfo {

    IProbeInfo text(String text);

    IProbeInfo vertical();

    IProbeInfo horizontal();

    IProbeInfo item(ItemStack stack, IItemStyle style);

    IProbeInfo item(ItemStack stack);
}
