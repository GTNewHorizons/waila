package mcp.mobius.waila.addons.openblocks;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;

public class TankFluidHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        if (!config.getConfig("openblocks.fluidamount")) return currenttip;

        IFluidHandler handler = (IFluidHandler) accessor.getTileEntity();
        if (handler == null) return currenttip;

        FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);
        if (tanks.length != 1) return currenttip;

        FluidStack fluid = tanks[0].fluid;
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
        if (!config.getConfig("openblocks.fluidamount")) return currenttip;

        IFluidHandler handler = (IFluidHandler) accessor.getTileEntity();
        if (handler == null) return currenttip;

        FluidTankInfo[] tanks = handler.getTankInfo(ForgeDirection.UNKNOWN);
        if (tanks.length != 1) return currenttip;

        int amount = tanks[0].fluid == null ? 0 : tanks[0].fluid.amount;
        currenttip.add(String.format("%,d / %,d %s", amount, tanks[0].capacity, ConfigHandler.instance().fluidUnit));

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

}
