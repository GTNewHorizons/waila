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

public class HUDHandlerTesseract implements IWailaDataProvider {

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

        if (config.getConfig("thermalexpansion.tesssendrecv")) {
            String send = String.format("%s : ", LangUtil.translateG("hud.msg.send"));
            String recv = String.format("%s : ", LangUtil.translateG("hud.msg.recv"));
            final String item = String.format("\u00a7a%s ", LangUtil.translateG("hud.msg.item"));
            final String fluid = String.format("\u00a79%s ", LangUtil.translateG("hud.msg.fluid"));
            final String energ = String.format("\u00a7c%s ", LangUtil.translateG("hud.msg.energ"));


            switch (accessor.getNBTInteger(accessor.getNBTData(), "Item.Mode")) {
                case 0:
                    send += item;
                    break;
                case 1:
                    recv += item;
                    break;
                case 2:
                    send += item;
                    recv += item;
                    break;
            }

            switch (accessor.getNBTInteger(accessor.getNBTData(), "Fluid.Mode")) {
                case 0:
                    send += fluid;
                    break;
                case 1:
                    recv += fluid;
                    break;
                case 2:
                    send += fluid;
                    recv += fluid;
                    break;
            }

            switch (accessor.getNBTInteger(accessor.getNBTData(), "Energy.Mode")) {
                case 0:
                    send += energ;
                    break;
                case 1:
                    recv += energ;
                    break;
                case 2:
                    send += energ;
                    recv += energ;
                    break;
            }

            if (!send.equals(String.format("%s : ", LangUtil.translateG("hud.msg.send"))))
                currenttip.add(send);

            if (!send.equals(String.format("%s : ", LangUtil.translateG("hud.msg.recv"))))
                currenttip.add(recv);
        }

        if (config.getConfig("thermalexpansion.tessfreq"))
            currenttip.add(String.format("%s : %d", LangUtil.translateG("hud.msg.frequency"), accessor.getNBTInteger(accessor.getNBTData(), "Frequency")));

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        try {
            final byte modeItem = ThermalExpansionModule.TileTesseract_Item.getByte(te);
            final byte modeFluid = ThermalExpansionModule.TileTesseract_Fluid.getByte(te);
            final byte modeEnergy = ThermalExpansionModule.TileTesseract_Energy.getByte(te);
            tag.setByte("Item.Mode", modeItem);
            tag.setByte("Fluid.Mode", modeFluid);
            tag.setByte("Energy.Mode", modeEnergy);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
