package mcp.mobius.waila.addons.projectred;

import java.util.List;

import net.minecraft.item.ItemStack;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.NBTUtil;

public class HUDFMPWires implements IWailaFMPProvider {

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,
            IWailaConfigHandler config) {
        if (!config.getConfig("pr.showsignal")) return currenttip;

        ITaggedList<String, String> tagList = (ITaggedList<String, String>) currenttip;
        int signal = ((NBTUtil.getNBTInteger(accessor.getNBTData(), "signal") & 0xff) + 16) / 17;
        if (tagList.getEntries("signal").isEmpty()) {
            tagList.add(String.format("%s : %s", LangUtil.translateG("hud.msg.power"), signal), "signal");
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }
}
