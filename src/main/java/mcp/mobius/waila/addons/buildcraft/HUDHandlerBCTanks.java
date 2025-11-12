package mcp.mobius.waila.addons.buildcraft;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.LoadedMods;

public class HUDHandlerBCTanks implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        if (!ConfigHandler.instance().getConfig("bc.tanktype") || LoadedMods.WAILA_PLUGINS) {
            return currenttip;
        }

        FluidTankInfo tank = this.getTank(accessor);
        FluidStack stack = tank != null ? tank.fluid : null;
        String name = currenttip.get(0);

        if (stack != null) {
            currenttip.set(0, name + " (" + stack.getFluid().getName() + ")");
        } else {
            currenttip.set(0, name + " " + LangUtil.translateG("hud.msg.empty"));
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {

        if (!ConfigHandler.instance().getConfig("bc.tankamount") || LoadedMods.WAILA_PLUGINS) {
            return currenttip;
        }

        FluidTankInfo tank = this.getTank(accessor);
        FluidStack stack = tank != null ? tank.fluid : null;
        int liquidAmount = stack != null ? stack.amount : 0;
        int capacity = tank != null ? tank.capacity : 0;

        currenttip.add(liquidAmount + "/" + capacity + " mB");

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    public FluidTankInfo getTank(IWailaDataAccessor accessor) {
        FluidTankInfo tank;
        try {
            tank = ((FluidTankInfo[]) BCModule.TileTank_getTankInfo
                    .invoke(BCModule.TileTank.cast(accessor.getTileEntity()), ForgeDirection.UNKNOWN))[0];
        } catch (Exception e) {
            Waila.log.log(Level.ERROR, "[BC] Unhandled exception trying to access a tank for display !.\n" + e);
            return null;
        }
        return tank;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        return tag;
    }

}
