package mcp.mobius.waila.api.elements;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IProbeDataProvider {

    void addProveInfo(ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor accessor,
                              IWailaConfigHandler config);
}
