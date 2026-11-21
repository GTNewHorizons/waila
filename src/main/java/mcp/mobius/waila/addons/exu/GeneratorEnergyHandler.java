package mcp.mobius.waila.addons.exu;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;

public class GeneratorEnergyHandler implements IWailaDataProvider {

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

        if (!config.getConfig("extrautilites.energyhandler")) return currenttip;

        NBTTagCompound tag = accessor.getNBTData();
        if (!tag.hasKey("ExuEnergy") || !tag.hasKey("ExuMaxStorage")) return currenttip;

        int energy = tag.getInteger("ExuEnergy");
        int maxEnergy = tag.getInteger("ExuMaxStorage");

        if (maxEnergy > 0) {
            if (config.getConfig("extrautilites.rfenergybar")) {
                currenttip.add(
                        SpecialChars
                                .getRenderString("waila.rfenergy", String.valueOf(energy), String.valueOf(maxEnergy)));
            } else {
                currenttip.add(String.format("%s / %s RF", formatNumber(energy), formatNumber(maxEnergy)));
            }
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
        try {
            Object storageObj = ExtraUtilitiesModule.TileEntityGenerator_storage.get(te);
            if (storageObj != null) {
                int energy = ExtraUtilitiesModule.EnergyStoragePlus_energy.getInt(storageObj);
                int capacity = ExtraUtilitiesModule.EnergyStoragePlus_capacity.getInt(storageObj);

                tag.setInteger("ExuEnergy", energy);
                tag.setInteger("ExuMaxStorage", capacity);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }
}
