package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.network.Message0x01TERequest;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MetaDataProvider {

    private Map<Integer, List<IWailaDataProvider>> headBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();
    private Map<Integer, List<IWailaDataProvider>> bodyBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();
    private Map<Integer, List<IWailaDataProvider>> tailBlockProviders = new TreeMap<Integer, List<IWailaDataProvider>>();

    private Map<Integer, List<IWailaEntityProvider>> headEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();
    private Map<Integer, List<IWailaEntityProvider>> bodyEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();
    private Map<Integer, List<IWailaEntityProvider>> tailEntityProviders = new TreeMap<Integer, List<IWailaEntityProvider>>();

    private Class prevBlock = null;
    private Class prevTile = null;

    public ItemStack identifyBlockHighlight(final World world, final EntityPlayer player, final RayTraceResult mop, final DataAccessorCommon accessor) {
        final Block block = accessor.getBlock();
        final int blockID = accessor.getBlockID();

        if (ModuleRegistrar.instance().hasStackProviders(block)) {
            for (final List<IWailaDataProvider> providerList : ModuleRegistrar.instance().getStackProviders(block).values()) {
                for (final IWailaDataProvider dataProvider : providerList) {
                    try {
                        final ItemStack retval = dataProvider.getWailaStack(accessor, ConfigHandler.instance());
                        if (retval != null)
                            return retval;
                    } catch (final Throwable e) {
                        WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), null);
                    }
                }
            }
        }
        return null;
    }

    public List<String> handleBlockTextData(final ItemStack itemStack, final World world, final EntityPlayer player, final RayTraceResult mop, final DataAccessorCommon accessor, List<String> currenttip, final Layout layout) {
        final Block block = accessor.getBlock();

        if (accessor.getTileEntity() != null && Waila.instance.serverPresent && accessor.isTimeElapsed(250) && ConfigHandler.instance().showTooltip()) {
            accessor.resetTimer();
            final HashSet<String> keys = new HashSet<String>();

            if (keys.size() != 0 || ModuleRegistrar.instance().hasNBTProviders(block) || ModuleRegistrar.instance().hasNBTProviders(accessor.getTileEntity()))
                WailaPacketHandler.INSTANCE.sendToServer(new Message0x01TERequest(accessor.getTileEntity(), keys));

        } else if (accessor.getTileEntity() != null && !Waila.instance.serverPresent && accessor.isTimeElapsed(250) && ConfigHandler.instance().showTooltip()) {

            try {
                final NBTTagCompound tag = new NBTTagCompound();
                accessor.getTileEntity().writeToNBT(tag);
                accessor.setNBTData(tag);
            } catch (final Exception e) {
                WailaExceptionHandler.handleErr(e, this.getClass().getName(), null);
            }
        }

        headBlockProviders.clear();
        bodyBlockProviders.clear();
        tailBlockProviders.clear();

		/* Lookup by class (for blocks)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadProviders(block))
            headBlockProviders.putAll(ModuleRegistrar.instance().getHeadProviders(block));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyProviders(block))
            bodyBlockProviders.putAll(ModuleRegistrar.instance().getBodyProviders(block));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailProviders(block))
            tailBlockProviders.putAll(ModuleRegistrar.instance().getTailProviders(block));

		
		/* Lookup by class (for tileentities)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadProviders(accessor.getTileEntity()))
            headBlockProviders.putAll(ModuleRegistrar.instance().getHeadProviders(accessor.getTileEntity()));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyProviders(accessor.getTileEntity()))
            bodyBlockProviders.putAll(ModuleRegistrar.instance().getBodyProviders(accessor.getTileEntity()));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailProviders(accessor.getTileEntity()))
            tailBlockProviders.putAll(ModuleRegistrar.instance().getTailProviders(accessor.getTileEntity()));

		/* Apply all collected providers */
        if (layout == Layout.HEADER)
            for (final List<IWailaDataProvider> providersList : headBlockProviders.values()) {
                for (final IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaHead(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.BODY)
            for (final List<IWailaDataProvider> providersList : bodyBlockProviders.values()) {
                for (final IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaBody(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }
        if (layout == Layout.FOOTER)
            for (final List<IWailaDataProvider> providersList : tailBlockProviders.values()) {
                for (final IWailaDataProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaTail(itemStack, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }
        return currenttip;
    }

    public List<String> handleEntityTextData(final Entity entity, final World world, final EntityPlayer player, final RayTraceResult mop, final DataAccessorCommon accessor, List<String> currenttip, final Layout layout) {

        if (accessor.getEntity() != null && Waila.instance.serverPresent && accessor.isTimeElapsed(250)) {
            accessor.resetTimer();

        } else if (accessor.getEntity() != null && !Waila.instance.serverPresent && accessor.isTimeElapsed(250)) {

            try {
                final NBTTagCompound tag = new NBTTagCompound();
                accessor.getEntity().writeToNBT(tag);
                accessor.remoteNbt = tag;
            } catch (final Exception e) {
                WailaExceptionHandler.handleErr(e, this.getClass().getName(), null);
            }
        }

        headEntityProviders.clear();
        bodyEntityProviders.clear();
        tailEntityProviders.clear();
		
		/* Lookup by class (for entities)*/
        if (layout == Layout.HEADER && ModuleRegistrar.instance().hasHeadEntityProviders(entity))
            headEntityProviders.putAll(ModuleRegistrar.instance().getHeadEntityProviders(entity));

        else if (layout == Layout.BODY && ModuleRegistrar.instance().hasBodyEntityProviders(entity))
            bodyEntityProviders.putAll(ModuleRegistrar.instance().getBodyEntityProviders(entity));

        else if (layout == Layout.FOOTER && ModuleRegistrar.instance().hasTailEntityProviders(entity))
            tailEntityProviders.putAll(ModuleRegistrar.instance().getTailEntityProviders(entity));

		/* Apply all collected providers */
        if (layout == Layout.HEADER)
            for (final List<IWailaEntityProvider> providersList : headEntityProviders.values()) {
                for (final IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaHead(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.BODY)
            for (final List<IWailaEntityProvider> providersList : bodyEntityProviders.values()) {
                for (final IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaBody(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        if (layout == Layout.FOOTER)
            for (final List<IWailaEntityProvider> providersList : tailEntityProviders.values()) {
                for (final IWailaEntityProvider dataProvider : providersList)
                    try {
                        currenttip = dataProvider.getWailaTail(entity, currenttip, accessor, ConfigHandler.instance());
                    } catch (final Throwable e) {
                        currenttip = WailaExceptionHandler.handleErr(e, dataProvider.getClass().toString(), currenttip);
                    }
            }

        return currenttip;
    }
}
