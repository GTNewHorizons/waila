package mcp.mobius.waila.addons.thermaldynamics;

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
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

/**
 * Created by Lordmau5 on 28.02.2015.
 */
public class HUDHandlerDuct implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("thermaldynamics.fluiductsFluid")) return currenttip;

        final FluidStack fluid = FluidStack.loadFluidStackFromNBT(accessor.getNBTData());

        String name = "";

        try {
            name += String.format(" < %s >", fluid.getFluid().getLocalizedName(fluid));
        } catch (final NullPointerException f) {
            name += " " + LangUtil.translateG("hud.msg.empty");
        }

        currenttip.add(name);
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("thermaldynamics.fluiductsAmount")) return currenttip;

        int amount = 0;

        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("Amount"))
            amount = accessor.getNBTInteger(tag, "Amount");

        currenttip.add(String.format(" %d / 1000 mB", amount));

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
