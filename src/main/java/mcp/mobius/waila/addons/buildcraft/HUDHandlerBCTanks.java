package mcp.mobius.waila.addons.buildcraft;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import org.apache.logging.log4j.Level;

import java.util.List;

public class HUDHandlerBCTanks implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final FluidTankInfo tank = this.getTank(accessor);
        final FluidStack stack = tank != null ? tank.fluid : null;


        String name = currenttip.get(0);
        if (stack != null && ConfigHandler.instance().getConfig("bc.tanktype"))
            name = name + " (" + stack.getFluid().getName() + ")";
        else if (stack == null && ConfigHandler.instance().getConfig("bc.tanktype"))
            name = name + " " + LangUtil.translateG("hud.msg.empty");
        currenttip.set(0, name);
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final FluidTankInfo tank = this.getTank(accessor);
        final FluidStack stack = tank != null ? tank.fluid : null;
        final int liquidAmount = stack != null ? stack.amount : 0;
        final int capacity = tank != null ? tank.capacity : 0;

        if (ConfigHandler.instance().getConfig("bc.tankamount"))
            currenttip.add(String.valueOf(liquidAmount) + "/" + String.valueOf(capacity) + " mB");

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    public FluidTankInfo getTank(final IWailaDataAccessor accessor) {
        FluidTankInfo tank = null;
        try {
            tank = ((FluidTankInfo[]) BCModule.TileTank_getTankInfo.invoke(BCModule.TileTank.cast(accessor.getTileEntity()), EnumFacing.DOWN))[0];
        } catch (final Exception e) {
            Waila.log.log(Level.ERROR, "[BC] Unhandled exception trying to access a tank for display !.\n" + String.valueOf(e));
            return null;
        }
        return tank;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        return tag;
    }

}
