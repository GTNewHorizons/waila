package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.item.ItemStack;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerTesseract implements IWailaDataProvider {

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
		
		if (config.getConfig("thermalexpansion.tesssendrecv")){
			String send = String.format("%s : ", LangUtil.translateG("hud.msg.send"));
			String recv = String.format("%s : ", LangUtil.translateG("hud.msg.recv"));
			String item = String.format("\u00a7a%s ", LangUtil.translateG("hud.msg.item"));
			String fluid= String.format("\u00a79%s ", LangUtil.translateG("hud.msg.fluid"));
			String energ= String.format("\u00a7c%s ", LangUtil.translateG("hud.msg.energ"));
			
			
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Item.Mode")){
			case 0:
				send += item;
				break;
			case 1:
				recv += item;
				break;
			case 2:
				send += item;
				recv += item;
				break;
			}
	
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Fluid.Mode")){
			case 0:
				send += fluid;
				break;
			case 1:
				recv += fluid;
				break;
			case 2:
				send += fluid;
				recv += fluid;
				break;
			}
			
			switch (accessor.getNBTInteger(accessor.getNBTData(), "Energy.Mode")){
			case 0:
				send += energ;
				break;
			case 1:
				recv += energ;
				break;
			case 2:
				send += energ;
				recv += energ;
				break;
			}		
			
			if (!send.equals(String.format("%s : ", LangUtil.translateG("hud.msg.send"))))
				currenttip.add(send);
			
			if (!send.equals(String.format("%s : ", LangUtil.translateG("hud.msg.recv"))))		
				currenttip.add(recv);
		}
		
		if (config.getConfig("thermalexpansion.tessfreq"))
			currenttip.add(String.format("%s : %d",LangUtil.translateG("hud.msg.frequency"), accessor.getNBTInteger(accessor.getNBTData(), "Frequency")));		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

}
