package mcp.mobius.waila.addons.thermalexpansion;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class TankModeHandler implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        try {
            if (config.getConfig("thermalexpansion.tankmode")) {
                Byte mode = (Byte) ThermalExpansionModule.TileTank_mode.get(accessor.getTileEntity());
                if (mode == 0) {
                    currenttip.add(
                            String.format(
                                    "%s : \u00a7a%s",
                                    LangUtil.translateG("hud.msg.mode"),
                                    LangUtil.translateG("hud.msg.input")));
                } else if (mode == 1) {
                    currenttip.add(
                            String.format(
                                    "%s : \u00a7c%s",
                                    LangUtil.translateG("hud.msg.mode"),
                                    LangUtil.translateG("hud.msg.output")));
                } else {
                    currenttip.add(String.format("Mode : Unknown (%d)", mode));
                }
            }

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
