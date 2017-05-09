package mcp.mobius.waila.addons.enderstorage;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerStorage implements IWailaDataProvider {

    private static String[] colors = {
            LangUtil.translateG("hud.msg.white"),
            LangUtil.translateG("hud.msg.orange"),
            LangUtil.translateG("hud.msg.magenta"),
            LangUtil.translateG("hud.msg.lblue"),
            LangUtil.translateG("hud.msg.yellow"),
            LangUtil.translateG("hud.msg.lime"),
            LangUtil.translateG("hud.msg.pink"),
            LangUtil.translateG("hud.msg.gray"),
            LangUtil.translateG("hud.msg.lgray"),
            LangUtil.translateG("hud.msg.cyan"),
            LangUtil.translateG("hud.msg.purple"),
            LangUtil.translateG("hud.msg.blue"),
            LangUtil.translateG("hud.msg.brown"),
            LangUtil.translateG("hud.msg.green"),
            LangUtil.translateG("hud.msg.red"),
            LangUtil.translateG("hud.msg.black")
    };

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (config.getConfig("enderstorage.colors")) {
            try {

                final int freq = EnderStorageModule.TileFrequencyOwner_Freq.getInt(accessor.getTileEntity());
                final int freqLeft = (Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 0);
                final int freqCenter = (Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 1);
                final int freqRight = (Integer) EnderStorageModule.GetColourFromFreq.invoke(null, freq, 2);

                if (!EnderStorageModule.TileEnderTank.isInstance(accessor.getTileEntity()))
                    currenttip.add(String.format("%s/%s/%s", colors[freqLeft], colors[freqCenter], colors[freqRight]));
                else
                    currenttip.add(String.format("%s/%s/%s", colors[freqRight], colors[freqCenter], colors[freqLeft]));


            } catch (final Exception e) {
                currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
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
