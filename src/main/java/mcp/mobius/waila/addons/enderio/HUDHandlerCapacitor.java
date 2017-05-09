package mcp.mobius.waila.addons.enderio;

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

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.RESET;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDHandlerCapacitor implements IWailaDataProvider {

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

        if (accessor.getPlayer().isSneaking()) {
            try {

                final String maxIOStr = LangUtil.translateG("hud.msg.maxio");
                final String inputStr = LangUtil.translateG("hud.msg.input");
                final String outputStr = LangUtil.translateG("hud.msg.output");

                if (config.getConfig("enderio.storage")) {
                    final Integer maxEnergyStored = (Integer) EnderIOModule.TCB_getMaxEnergyStored.invoke(accessor.getTileEntity());
                    final Double energyStored = (Double) EnderIOModule.TCB_getEnergyStored.invoke(accessor.getTileEntity());
                    currenttip.add(String.format("%s%.1f%s / %s%d%s RF", WHITE, energyStored * 10, RESET, WHITE, maxEnergyStored * 10, RESET));
                }

                if (config.getConfig("enderio.inout")) {
                    final Integer maxIO = (Integer) EnderIOModule.TCB_getMaxIO.invoke(accessor.getTileEntity());
                    final Integer maxInput = (Integer) EnderIOModule.TCB_getMaxInput.invoke(accessor.getTileEntity());
                    final Integer maxOutput = (Integer) EnderIOModule.TCB_getMaxOutput.invoke(accessor.getTileEntity());
                    currenttip.add(String.format("%s : %s%d%sRF/t ", maxIOStr, TAB + ALIGNRIGHT + WHITE, maxIO * 10, TAB + ALIGNRIGHT));
                    currenttip.add(String.format("%s : %s%d%sRF/t ", inputStr, TAB + ALIGNRIGHT + WHITE, maxInput * 10, TAB + ALIGNRIGHT));
                    currenttip.add(String.format("%s : %s%d%sRF/t ", outputStr, TAB + ALIGNRIGHT + WHITE, maxOutput * 10, TAB + ALIGNRIGHT));
                }


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
