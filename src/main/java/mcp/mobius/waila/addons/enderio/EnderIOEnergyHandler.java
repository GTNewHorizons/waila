package mcp.mobius.waila.addons.enderio;

import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.cbcore.LangUtil;

public class EnderIOEnergyHandler implements IWailaDataProvider {

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

        if (!config.getConfig("enderio.energyhandler")) return currenttip;

        NBTTagCompound tag = accessor.getNBTData();
        if (tag == null) return currenttip;

        int energy = -1;
        int maxEnergy = -1;

        if (tag.hasKey("storedEnergyRF")) {
            energy = tag.getInteger("storedEnergyRF");
        }
        if (tag.hasKey("maxStoredRF")) {
            maxEnergy = tag.getInteger("maxStoredRF");
        }

        if (maxEnergy > 0 && energy >= 0) {
            if (config.getConfig("enderio.rfenergybar")) {
                currenttip.add(
                        SpecialChars
                                .getRenderString("waila.rfenergy", String.valueOf(energy), String.valueOf(maxEnergy)));
            }
        }

        if (tag.hasKey("EioRFt")) {
            int rft = tag.getInteger("EioRFt");
            if (rft > 0) {
                String outputStr = LangUtil.translateG("hud.msg.output"); // Слово "Выход"
                currenttip.add(
                        String.format(
                                "%s:%s\u00a7f%d\u00a7r RF/t",
                                outputStr,
                                SpecialChars.TAB + SpecialChars.ALIGNRIGHT,
                                rft));
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
            int energy = -1;
            int maxEnergy = -1;
            int rft = 0;

            if (EnderIOModule.TileCapBank != null && EnderIOModule.TileCapBank.isInstance(te)) {
                energy = (int) ((Number) EnderIOModule.TileCapBank_getEnergyStoredL.invoke(te)).longValue();
                maxEnergy = (int) ((Number) EnderIOModule.TileCapBank_getMaxEnergyStoredL.invoke(te)).longValue();
            } else {
                try {
                    Method getEnergy = te.getClass().getMethod("getEnergyStored", ForgeDirection.class);
                    Method getMaxEnergy = te.getClass().getMethod("getMaxEnergyStored", ForgeDirection.class);

                    if (getEnergy != null && getMaxEnergy != null) {
                        getEnergy.setAccessible(true);
                        getMaxEnergy.setAccessible(true);

                        energy = (Integer) getEnergy.invoke(te, ForgeDirection.UNKNOWN);
                        maxEnergy = (Integer) getMaxEnergy.invoke(te, ForgeDirection.UNKNOWN);
                    }
                } catch (Exception e) {
                    try {
                        Method getEnergyNoDir = te.getClass().getMethod("getEnergyStored");
                        Method getMaxEnergyNoDir = te.getClass().getMethod("getMaxEnergyStored");
                        if (getEnergyNoDir != null && getMaxEnergyNoDir != null) {
                            energy = (Integer) getEnergyNoDir.invoke(te);
                            maxEnergy = (Integer) getMaxEnergyNoDir.invoke(te);
                        }
                    } catch (Exception ignored) {}
                }

                if (EnderIOModule.AbstractGeneratorEntity != null
                        && EnderIOModule.AbstractGeneratorEntity.isInstance(te)) {
                    try {
                        boolean isActive = (Boolean) EnderIOModule.AbstractGeneratorEntity_isActive.invoke(te);
                        if (isActive) {
                            rft = (Integer) EnderIOModule.AbstractGeneratorEntity_getPowerUsePerTick.invoke(te);
                        }
                    } catch (Exception ignored) {}
                }
            }

            if (energy >= 0) tag.setInteger("storedEnergyRF", energy);
            if (maxEnergy > 0) tag.setInteger("maxStoredRF", maxEnergy);
            if (rft > 0) tag.setInteger("EioRFt", rft);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }
}
