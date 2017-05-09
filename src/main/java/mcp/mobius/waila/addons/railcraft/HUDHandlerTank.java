package mcp.mobius.waila.addons.railcraft;

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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;

public class HUDHandlerTank implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("railcraft.fluidamount")) return currenttip;
        try {
            final IFluidTank tank = (IFluidTank) RailcraftModule.ITankTile_getTank.invoke(RailcraftModule.ITankTile.cast(accessor.getTileEntity()));
            if (tank == null) return currenttip;

            final FluidStack fluid = tank.getFluid();

            String name = currenttip.get(0);

            try {
                name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
            } catch (final NullPointerException f) {
                name += " " + LangUtil.translateG("hud.msg.empty");
            }

            currenttip.set(0, name);

        } catch (final Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("railcraft.fluidamount")) return currenttip;

        try {
            final IFluidTank tank = (IFluidTank) RailcraftModule.ITankTile_getTank.invoke(RailcraftModule.ITankTile.cast(accessor.getTileEntity()));
            if (tank == null) return currenttip;

            final FluidStack fluid = tank.getFluid();
            if (fluid != null)
                currenttip.add(String.format("%d / %d mB", fluid.amount, tank.getInfo().capacity));
            else
                currenttip.add(String.format("0 / %d mB", tank.getInfo().capacity));

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
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
