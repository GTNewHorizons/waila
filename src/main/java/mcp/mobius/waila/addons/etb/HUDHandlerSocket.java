package mcp.mobius.waila.addons.etb;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class HUDHandlerSocket implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("etb.displaydata")) return currenttip;

        try {
            final int[] sides = (int[]) ETBModule.Socket_sides.get(accessor.getTileEntity());
            final Object[] configs = (Object[]) ETBModule.Socket_configs.get(accessor.getTileEntity());
            final Item module = (Item) ETBModule.module.get(null);


            for (int s = 0; s < 6; s++) {
                if (sides[s] != 0) {

                    final int tank = (Integer) ETBModule.SC_tank.get(configs[s]);
                    final int inventory = (Integer) ETBModule.SC_inventory.get(configs[s]);
                    final boolean[] rsControl = (boolean[]) ETBModule.SC_rsControl.get(configs[s]);
                    final boolean[] rsLatch = (boolean[]) ETBModule.SC_rsLatch.get(configs[s]);


                    final ItemStack stack = new ItemStack(module, 1, sides[s]);
                    String tipstr = String.format("%-5s : %s ", EnumFacing.values()[s], stack.getDisplayName());

                    String configstr = "[ ";

                    if (tank != -1)
                        configstr += "\u00a79" + String.valueOf(tank) + " ";

                    if (inventory != -1)
                        configstr += "\u00a7a" + String.valueOf(inventory) + " ";

                    if (rsControl[0] || rsControl[1] || rsControl[2]) {
                        configstr += "\u00a7c";
                        configstr += rsControl[0] ? "1" : "0";
                        configstr += rsControl[1] ? "1" : "0";
                        configstr += rsControl[2] ? "1" : "0";
                        configstr += " ";
                    }

                    if (rsLatch[0] || rsLatch[1] || rsLatch[2]) {
                        configstr += "\u00a75";
                        configstr += rsLatch[0] ? "1" : "0";
                        configstr += rsLatch[1] ? "1" : "0";
                        configstr += rsLatch[2] ? "1" : "0";
                        configstr += " ";
                    }

                    configstr += "\u00a7r]";

                    if (!configstr.equals("[ \u00a7r]"))
                        tipstr += " " + configstr;

							/*
							if ((tank != -1) && (inventory != -1))
								tipstr += String.format("[ \u00a79%d \u00a7a%d \u00a7r]", tank, inventory);
							else if (tank != -1)
								tipstr += String.format("[ \u00a79%d \u00a7r]", tank);
							else if (inventory != -1)
								tipstr += String.format("[ \u00a7a%d \u00a7r]", inventory);
							*/

                    currenttip.add(tipstr);
                }
            }


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
