package mcp.mobius.waila.addons.thermalexpansion;

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

import java.util.List;


public class HUDHandlerISecureTile implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {

        if (config.getConfig("thermalexpansion.owner")) {

            final String owner = accessor.getNBTData().getString("Owner");
            final int iaccess = accessor.getNBTInteger(accessor.getNBTData(), "Access");

            if (owner.equals("[None]"))
                return currenttip;

            String access = "INVALID";
            switch (iaccess) {
                case 0:
                    access = LangUtil.translateG("hud.msg.public");
                    break;
                case 1:
                    access = LangUtil.translateG("hud.msg.restricted");
                    break;
                case 2:
                    access = LangUtil.translateG("hud.msg.private");
                    break;
            }

            currenttip.add(String.format("%s : \u00a7f%s\u00a7r [ %s ]", LangUtil.translateG("hud.msg.owner"), owner, access));

        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        try {
            final String owner = (String) ThermalExpansionModule.ISecureTile_getOwnerName.invoke(te);
            final int access = ((Enum) ThermalExpansionModule.ISecureTile_getAccess.invoke(te)).ordinal();

            tag.setString("Owner", owner);
            tag.setInteger("Access", access);

        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
