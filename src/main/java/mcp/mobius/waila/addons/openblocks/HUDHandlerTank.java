package mcp.mobius.waila.addons.openblocks;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerTank implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("openblocks.fluidamount")) return currenttip;

//		IFluidHandler handler = (IFluidHandler)accessor.getTileEntity();
//		if (handler == null) return currenttip;
//
//		FluidTankInfo[] tanks = handler.getTankInfo(EnumFacing.DOWN);
//		if (tanks.length != 1) return currenttip;
//
//		FluidStack  fluid = tanks[0].fluid;
//		String name       = currenttip.get(0);
//
//		try{
//			name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
//		} catch (NullPointerException f){
//			name += " " + LangUtil.translateG("hud.msg.empty");
//		}
//
//		currenttip.set(0, name);

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("openblocks.fluidamount")) return currenttip;

//        IFluidHandler handler = (IFluidHandler) accessor.getTileEntity();
//        if (handler == null) return currenttip;
//
//        FluidTankInfo[] tanks = handler.getTankInfo(EnumFacing.DOWN);
//        if (tanks.length != 1) return currenttip;
//
//        if (tanks[0].fluid != null)
//            currenttip.add(String.format("%d / %d mB", tanks[0].fluid.amount, tanks[0].capacity));
//        else
//            currenttip.add(String.format("0 / %d mB", tanks[0].capacity));

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
