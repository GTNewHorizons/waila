package mcp.mobius.waila.addons.thermaldynamics;

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
import mcp.mobius.waila.cbcore.LangUtil;

/**
 * Created by Lordmau5 on 28.02.2015.
 */
public class FluiductFluidHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        if (!config.getConfig("thermaldynamics.fluiductsFluid")) {
            return currenttip;
        }

        NBTTagCompound tag = accessor.getNBTData();
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
        int capacity = tag.getInteger("capacity");

        if (capacity == 0) {
            return currenttip;
        }

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

        if (!config.getConfig("thermaldynamics.fluiductsAmount")) {
            return currenttip;
        }

        NBTTagCompound tag = accessor.getNBTData();
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("fluid"));
        int amount = fluid == null ? 0 : fluid.amount;
        int capacity = tag.getInteger("capacity");

        if (capacity == 0) {
            return currenttip;
        }

        currenttip.add(String.format("%d / %d mB", amount, capacity));

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
        if (te instanceof IFluidHandler fluidHandler) {
            FluidTankInfo[] infos = fluidHandler.getTankInfo(ForgeDirection.UNKNOWN);
            if (infos.length != 1) {
                return tag;
            }

            FluidTankInfo info = infos[0];

            if (info.fluid != null) {
                NBTTagCompound fluidNBT = info.fluid.writeToNBT(new NBTTagCompound());
                tag.setTag("fluid", fluidNBT);
            }

            tag.setInteger("capacity", info.capacity);
        }

        return tag;
    }

}
