package mcp.mobius.waila.addons.ic2;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;
import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.TAB;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class HUDHandlerTEGenerator implements IWailaDataProvider {

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
            NBTTagCompound tag = accessor.getNBTData();
            String storedStr = LangUtil.translateG("hud.msg.stored");
            String outputStr = LangUtil.translateG("hud.msg.output");

            if (tag.hasKey("isTransformer")) {
                int mode = tag.getInteger("transMode");
                int tier = tag.getInteger("transTier");
                double energy = tag.getDouble("transEnergy");
                boolean hasRedstone = tag.getBoolean("hasRedstone");

                int lowV = (int) (32 * Math.pow(4, tier - 1));
                int highV = lowV * 4;

                boolean isStepUp = false;
                String modeLabel = "";

                if (mode == 0) {
                    if (hasRedstone) {
                        isStepUp = true;
                        modeLabel = "§aUp (Redstone)§r";
                    } else {
                        isStepUp = false;
                        modeLabel = "§Down (Auto)§r";
                    }
                } else if (mode == 1) {
                    isStepUp = false;
                    modeLabel = "§bFixed Down§r";
                } else if (mode == 2) {
                    isStepUp = true;
                    modeLabel = "§aFixed Up§r";
                }

                if (ConfigHandler.instance().getConfig("ic2.transformer.mode")) {
                    currenttip.add(String.format("Mode:%s%s", TAB + ALIGNRIGHT, modeLabel));
                }
                String transformStr = isStepUp ? (lowV + " §8->§f " + highV) : (highV + " §8->§f " + lowV);
                if (ConfigHandler.instance().getConfig("ic2.transformer.inout")) {
                    currenttip.add(String.format("Voltage:%s§f%s EU/t", TAB + ALIGNRIGHT, transformStr));
                }

                int maxBuf = isStepUp ? lowV : highV;
                if (ConfigHandler.instance().getConfig("ic2.storage")) {
                    long currentBuf = Math.round(Math.min(energy, maxBuf));

                    if (ConfigHandler.instance().getConfig("ic2.euenergybar")) {
                        currenttip.add(
                                SpecialChars.getRenderString(
                                        "waila.euenergy",
                                        String.valueOf(currentBuf),
                                        String.valueOf(maxBuf)));
                    } else {
                        currenttip.add(
                                String.format(
                                        "%s (Buffer):%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU",
                                        storedStr,
                                        TAB + ALIGNRIGHT,
                                        formatNumber(currentBuf),
                                        formatNumber(maxBuf)));
                    }
                }

                return currenttip;
            }

            double storage = tag.getDouble("storage");
            int production = tag.getInteger("production");
            long maxStorage = tag.getLong("maxStorage");

            /* EU Storage */
            if (ConfigHandler.instance().getConfig("ic2.storage")) {
                if (maxStorage > 0) {
                    long currentStorage = Math.round(Math.min(storage, maxStorage));

                    if (ConfigHandler.instance().getConfig("ic2.euenergybar")) {
                        currenttip.add(
                                SpecialChars.getRenderString(
                                        "waila.euenergy",
                                        String.valueOf(currentStorage),
                                        String.valueOf(maxStorage)));
                    } else {
                        currenttip.add(
                                String.format(
                                        "%s:%s\u00a7f%d\u00a7r / \u00a7f%d\u00a7r EU",
                                        storedStr,
                                        TAB + ALIGNRIGHT,
                                        formatNumber(currentStorage),
                                        formatNumber(maxStorage)));
                    }
                }
            }

            if (ConfigHandler.instance().getConfig("ic2.outputeu")) {
                if (production > 0) {
                    currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", outputStr, TAB + ALIGNRIGHT, production));
                } else if (tag.hasKey("output")) {
                    int output = tag.getInteger("output");
                    if (output > 0) {
                        currenttip.add(String.format("%s%s\u00a7f%d\u00a7r EU/t", outputStr, TAB + ALIGNRIGHT, output));
                    }
                }
            }

        } catch (Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
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
            double storage = -1;
            long production = -1;
            long maxStorage = -1;
            int output = -1;

            if (IC2Module.TileTransformer != null && IC2Module.TileTransformer.isInstance(te)) {
                int mode = IC2Module.TileTransformer_mode.getInt(te);
                int tier = IC2Module.TileTransformer_tier.getInt(te);
                double energy = IC2Module.TileTransformer_energy.getDouble(te);
                boolean hasRedstone = world.isBlockIndirectlyGettingPowered(x, y, z);

                tag.setBoolean("isTransformer", true);
                tag.setInteger("transMode", mode);
                tag.setInteger("transTier", tier);
                tag.setDouble("transEnergy", energy);
                tag.setBoolean("hasRedstone", hasRedstone);
                return tag;
            }

            if (IC2Module.TileBaseGenerator.isInstance(te)) {
                storage = IC2Module.TileBaseGenerator_storage.getDouble(te);
                production = IC2Module.TileBaseGenerator_production.getInt(te);
                maxStorage = IC2Module.TileBaseGenerator_maxStorage.getShort(te);
            }

            else if (IC2Module.TileGeoGenerator.isInstance(te)) {
                storage = IC2Module.TileGeoGenerator_storage.getDouble(te);
                production = IC2Module.TileGeoGenerator_production.getInt(te);
                maxStorage = IC2Module.TileGeoGenerator_maxStorage.getShort(te);
            }

            else if (IC2Module.TileKineticGenerator.isInstance(te)) {
                storage = IC2Module.TileKineticGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(IC2Module.TileKineticGenerator_production.getDouble(te));
                maxStorage = IC2Module.TileKineticGenerator_maxStorage.getInt(te);
            }

            else if (IC2Module.TileSemifluidGenerator.isInstance(te)) {
                storage = IC2Module.TileSemifluidGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(IC2Module.TileSemifluidGenerator_production.getDouble(te));
                maxStorage = IC2Module.TileSemifluidGenerator_maxStorage.getShort(te);
            }

            else if (IC2Module.TileStirlingGenerator.isInstance(te)) {
                storage = IC2Module.TileStirlingGenerator_storage.getDouble(te);
                production = MathHelper.floor_double_long(IC2Module.TileStirlingGenerator_production.getDouble(te));
                maxStorage = IC2Module.TileStirlingGenerator_maxStorage.getShort(te);
            }

            else if (IC2Module.TileElectricBlock != null && IC2Module.TileElectricBlock.isInstance(te)) {
                storage = IC2Module.TileElectricBlock_energy.getDouble(te);

                Object maxStorageObj = IC2Module.TileElectricBlock_maxStorage.get(te);
                if (maxStorageObj instanceof Number) {
                    maxStorage = ((Number) maxStorageObj).longValue();
                }

                Object outputObj = IC2Module.TileElectricBlock_output.get(te);
                if (outputObj instanceof Number) {
                    output = ((Number) outputObj).intValue();
                }

                production = 0;
            }

            tag.setDouble("storage", storage);
            tag.setLong("production", production);
            tag.setLong("maxStorage", maxStorage);

            if (output > 0) {
                tag.setInteger("output", output);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tag;
    }

}
