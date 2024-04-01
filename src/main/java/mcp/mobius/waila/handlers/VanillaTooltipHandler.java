package mcp.mobius.waila.handlers;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.utils.ModIdentification;

public class VanillaTooltipHandler {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tooltipEvent(ItemTooltipEvent event) {
        String canonicalName = ModIdentification.nameFromStack(event.itemStack);
        if (canonicalName != null && !canonicalName.isEmpty()) event.toolTip.add("\u00a79\u00a7o" + canonicalName);
    }
}
