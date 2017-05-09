package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.api.ITaggedList;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerIEnergyHandler implements IWailaDataProvider {

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

        if (!config.getConfig("thermalexpansion.energyhandler")) return currenttip;
        if (!accessor.getNBTData().hasKey("Energy")) return currenttip;

        final int energy = accessor.getNBTInteger(accessor.getNBTData(), "Energy");
        final int maxEnergy = accessor.getNBTInteger(accessor.getNBTData(), "MaxStorage");
        try {
            if ((maxEnergy != 0) && (((ITaggedList) currenttip).getEntries("RFEnergyStorage").size() == 0)) {
                ((ITaggedList) currenttip).add(String.format("%d / %d RF", energy, maxEnergy), "RFEnergyStorage");
            }
        } catch (final Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        try {
            Integer energy = -1;
            Integer maxsto = -1;
            if (ThermalExpansionModule.IEnergyInfo.isInstance(te)) {
                energy = (Integer) ThermalExpansionModule.IEnergyInfo_getCurStorage.invoke(te);
                maxsto = (Integer) ThermalExpansionModule.IEnergyInfo_getMaxStorage.invoke(te);
            } else if (ThermalExpansionModule.IEnergyProvider.isInstance(te)) {
                energy = (Integer) ThermalExpansionModule.IEnergyProvider_getCurStorage.invoke(te, EnumFacing.DOWN);
                maxsto = (Integer) ThermalExpansionModule.IEnergyProvider_getMaxStorage.invoke(te, EnumFacing.DOWN);
            } else if (ThermalExpansionModule.IEnergyReceiver.isInstance(te)) {
                energy = (Integer) ThermalExpansionModule.IEnergyReceiver_getCurStorage.invoke(te, EnumFacing.DOWN);
                maxsto = (Integer) ThermalExpansionModule.IEnergyReceiver_getMaxStorage.invoke(te, EnumFacing.DOWN);
            }

            tag.setInteger("Energy", energy);
            tag.setInteger("MaxStorage", maxsto);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return tag;
    }

}
