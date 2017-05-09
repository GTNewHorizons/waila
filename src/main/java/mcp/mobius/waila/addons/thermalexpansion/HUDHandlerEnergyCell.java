package mcp.mobius.waila.addons.thermalexpansion;

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

public class HUDHandlerEnergyCell implements IWailaDataProvider {

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
        if (!config.getConfig("thermalexpansion.energycell")) return currenttip;

        final int energyReceive = accessor.getNBTInteger(accessor.getNBTData(), "Recv");
        final int energySend = accessor.getNBTInteger(accessor.getNBTData(), "Send");

        currenttip.add(String.format("%s/%s : %d / %d RF/t", LangUtil.translateG("hud.msg.in"), LangUtil.translateG("hud.msg.out"), energyReceive, energySend));

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        try {
            final int recv = ThermalExpansionModule.TileEnergyCell_Recv.getInt(te);
            final int send = ThermalExpansionModule.TileEnergyCell_Send.getInt(te);
            tag.setInteger("Recv", recv);
            tag.setInteger("Send", send);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
