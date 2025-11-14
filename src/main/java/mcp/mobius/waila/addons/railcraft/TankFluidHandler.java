package mcp.mobius.waila.addons.railcraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.NumberFormatter;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class TankFluidHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        if (!config.getConfig("railcraft.fluidamount")) return currenttip;

        IFluidTank tank = this.getTank(accessor.getTileEntity());
        if (tank == null) return currenttip;

        FluidStack fluid = tank.getFluid();
        String name = currenttip.get(0);

        if (fluid == null) {
            currenttip.set(0, name + " " + LangUtil.translateG("hud.msg.empty"));
        } else {
            currenttip.set(0, name + String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid)));
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        if (!config.getConfig("railcraft.fluidamount")) return currenttip;

        IFluidTank tank = this.getTank(accessor.getTileEntity());
        if (tank == null) return currenttip;

        FluidStack fluid = tank.getFluid();
        int amount = fluid == null ? 0 : fluid.amount;

        currenttip.add(
                String.format(
                        "%s / %s %s",
                        NumberFormatter.format(amount),
                        NumberFormatter.format(tank.getCapacity()),
                        ConfigHandler.instance().fluidUnit));

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        return tag;
    }

    private FluidTank getTank(TileEntity tileEntity) {
        try {
            Object tank = RailcraftModule.TileTankBase_getTank.invoke(tileEntity);

            int capacity = RailcraftModule.StandardTank_capacity.getInt(tank);
            Object renderData = RailcraftModule.StandardTank_renderData.get(tank);

            Fluid fluid = (Fluid) RailcraftModule.StandardTank_renderDataFluid.get(renderData);
            int amount = RailcraftModule.StandardTank_renderDataAmount.getInt(renderData);

            return new FluidTank(fluid, amount, capacity);
        } catch (Exception e) {
            WailaExceptionHandler.handleErr(e, tileEntity.getClass().getName(), null);
            return null;
        }
    }

}
