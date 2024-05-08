package mcp.mobius.waila.api.impl.elements;

import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.DefaultProbeInfoProvider;
import mcp.mobius.waila.api.ProbeMode;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.network.Message0x01TERequest;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class MetaProbeDataProvider {

    public ProbeInfo handleBlockElementData(ItemStack itemStack, World world, EntityPlayer player,
            MovingObjectPosition mop, DataAccessorCommon accessor) {
        Block block = accessor.getBlock();
        ProbeInfo probeInfo = new ProbeInfo();
        DefaultProbeInfoProvider.addStandardBlockInfo(itemStack, probeInfo, accessor, ConfigHandler.instance());

        if (!ModuleProbeRegistrar.instance().hasProviders(block)
                && !ModuleProbeRegistrar.instance().hasProviders(accessor.getTileEntity())) {
            return probeInfo;
        }

        ProbeMode probeMode = player.isSneaking() ? ProbeMode.EXTENDED
                : player.capabilities.isCreativeMode ? ProbeMode.DEBUG : ProbeMode.NORMAL;

        if (accessor.getTileEntity() != null && Waila.instance.serverPresent
                && accessor.isTimeElapsed(250)
                && ConfigHandler.instance().showTooltip()
                && !ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_FORCE_LEGACY_MODE, false)) {
            accessor.resetTimer();
            HashSet<String> keys = new HashSet<>();

            if (ModuleProbeRegistrar.instance().hasProviders(block)
                    || ModuleProbeRegistrar.instance().hasProviders(accessor.getTileEntity()))
                WailaPacketHandler.INSTANCE
                        .sendToServer(new Message0x01TERequest(accessor.getTileEntity(), keys, true));
        } else if (accessor.getTileEntity() != null && !Waila.instance.serverPresent
                && accessor.isTimeElapsed(250)
                && ConfigHandler.instance().showTooltip()) {

                    try {
                        NBTTagCompound tag = new NBTTagCompound();
                        accessor.getTileEntity().writeToNBT(tag);
                        accessor.setNBTData(tag);
                    } catch (Exception e) {
                        WailaExceptionHandler.handleErr(e, this.getClass().getName(), null);
                    }
                }

        /* Lookup by class (for blocks) */
        if (ModuleProbeRegistrar.instance().hasProviders(block)) {
            List<IProbeDataProvider> probeDataProviders = ModuleProbeRegistrar.instance().getProviders(block);
            for (IProbeDataProvider probeDataProvider : probeDataProviders) {
                probeDataProvider.addProbeInfo(probeMode, itemStack, probeInfo, accessor, ConfigHandler.instance());
            }
        }

        /* Lookup by class (for tileentities) */
        if (ModuleProbeRegistrar.instance().hasProviders(accessor.getTileEntity())) {
            List<IProbeDataProvider> probeDataProviders = ModuleProbeRegistrar.instance()
                    .getProviders(accessor.getTileEntity());
            for (IProbeDataProvider probeDataProvider : probeDataProviders) {
                probeDataProvider.addProbeInfo(probeMode, itemStack, probeInfo, accessor, ConfigHandler.instance());
            }
        }

        return probeInfo;
    }

    // append legacy Tooltip into new ProbeInfo if possible
    public ProbeInfo appendTooltipData(ProbeInfo probeInfo, List<String> tooltip, ItemStack itemStack,
            DataAccessorCommon accessor) {
        if (tooltip.isEmpty()) {
            return probeInfo;
        }

        for (String s : tooltip) {
            probeInfo.text(s);
        }

        return probeInfo;
    }
}
