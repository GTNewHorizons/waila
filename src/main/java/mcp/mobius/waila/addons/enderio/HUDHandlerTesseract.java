package mcp.mobius.waila.addons.enderio;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.RESET;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDHandlerTesseract implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

        if (accessor.getPlayer().isSneaking()) {

            final String channel = "%s : %s%s%s %s";
            final String freq;
            final String frequser;
            String owner;

            if (config.getConfig("enderio.channel")) {

                if (accessor.getNBTData().hasKey("channelName")) {
                    freq = accessor.getNBTData().getString("channelName");

                    if (accessor.getNBTData().hasKey("channelUser"))
                        frequser = "[ " + WHITE + accessor.getNBTData().getString("channelUser") + RESET + " ]";
                    else
                        frequser = "[ " + WHITE + LangUtil.translateG("hud.msg.public") + RESET + " ]";

                } else {
                    freq = LangUtil.translateG("hud.msg.none");
                    frequser = "";
                }

                currenttip.add(String.format(channel, LangUtil.translateG("hud.msg.frequency"), TAB + WHITE, freq, RESET, frequser));
            }

            if (config.getConfig("enderio.owner")) {
                if (accessor.getNBTData().hasKey("owner"))
                    currenttip.add(String.format("%s : %s%s", LangUtil.translateG("hud.msg.owner"), TAB + WHITE, accessor.getNBTData().getString("owner")));
            }

        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
