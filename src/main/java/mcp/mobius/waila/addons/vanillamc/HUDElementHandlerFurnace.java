package mcp.mobius.waila.addons.vanillamc;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.elements.IProbeInfo;
import mcp.mobius.waila.api.impl.elements.ItemStyle;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.StatCollector;

public class HUDElementHandlerFurnace implements IProbeDataProvider {

    @Override
    public void addProveInfo(ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor data, IWailaConfigHandler config) {
        TileEntityFurnace furnace = (TileEntityFurnace) data.getTileEntity();
        int burnTime = data.getNBTData().getInteger("BurnTime") / 20;

        if (burnTime > 0) {
            probeInfo.horizontal()
                    .text("Burntime")
                    .item(new ItemStack(Blocks.fire), new ItemStyle(8, 8))
                    .text(": " + burnTime + " " + StatCollector.translateToLocal("Seconds Remaining"));
        }
    }
}
