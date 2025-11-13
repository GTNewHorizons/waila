package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class TankFluidHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        if (!config.getConfig("thermalexpansion.fluidtype")) {
            return currenttip;
        }

        try {
            FluidStack fluid = (FluidStack) ThermalExpansionModule.TileTank_getTankFluid
                    .invoke(accessor.getTileEntity());
            String name = currenttip.get(0);

            if (fluid == null) {
                currenttip.set(0, name + " " + LangUtil.translateG("hud.msg.empty"));
            } else {
                currenttip.set(0, name + String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid)));
            }

        } catch (Exception e) {
            WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        if (!config.getConfig("thermalexpansion.fluidamount")) {
            return currenttip;
        }

        try {
            int amount = (Integer) ThermalExpansionModule.TileTank_getTankAmount.invoke(accessor.getTileEntity());
            int capacity = (Integer) ThermalExpansionModule.TileTank_getTankCapacity.invoke(accessor.getTileEntity());

            currenttip.add(String.format("%d / %d %s", amount, capacity, ConfigHandler.instance().fluidUnit));

        } catch (Exception e) {
            WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

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
