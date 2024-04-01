package mcp.mobius.waila.api;

import java.util.LinkedHashMap;

import net.minecraft.item.ItemStack;

public interface IWailaSummaryProvider {

    /* This interface is used to control the display data in the description screen */
    LinkedHashMap<String, String> getSummary(ItemStack stack, LinkedHashMap<String, String> currentSummary,
            IWailaConfigHandler config);
}
