package mcp.mobius.waila.addons.vanillamc;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.elements.IProbeInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.StatCollector;

public class HUDElementHandlerFurnace implements IProbeDataProvider {

    @Override
    public void addProveInfo(ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor data, IWailaConfigHandler config) {
        TileEntityFurnace furnace = (TileEntityFurnace) data.getTileEntity();
        int burnTime = data.getNBTData().getInteger("BurnTime") / 20;

        if (burnTime > 0) {
            probeInfo.text("Burntime" + ": "
                    + burnTime
                    + " "
                    + StatCollector.translateToLocal("Seconds Remaining"));
        }
    }
}
