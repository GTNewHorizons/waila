package mcp.mobius.waila.addons.thermalexpansion;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.WailaExceptionHandler;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDHandlerIEnergyHandler implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		
		if (!config.getConfig("thermalexpansion.energyhandler")) return currenttip;
		if (!accessor.getNBTData().hasKey("Energy")) return currenttip;
		
		int energy = accessor.getNBTInteger(accessor.getNBTData(), "Energy");
		try {
			Integer maxEnergy = (Integer)ThermalExpansionModule.IEnergyHandler_getMaxStorage.invoke(accessor.getTileEntity(), ForgeDirection.UNKNOWN);
			if (maxEnergy != 0)
				currenttip.add(String.format("%d / %d RF", energy, maxEnergy));			
		} catch (Exception e){    
			currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
		} 
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
