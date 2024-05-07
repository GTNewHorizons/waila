package mcp.mobius.waila.api.elements;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.ProbeMode;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IProbeDataProvider {

    void addProveInfo(ProbeMode probeMode, ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor accessor,
                      IWailaConfigHandler config);
}
