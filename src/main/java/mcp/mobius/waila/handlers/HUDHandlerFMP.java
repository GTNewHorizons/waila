package mcp.mobius.waila.handlers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.api.impl.DataAccessorFMP;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.Level;

import java.util.List;

public class HUDHandlerFMP implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound subtag = list.getCompoundTagAt(i);
            final String id = subtag.getString("id");

            if (ModuleRegistrar.instance().hasHeadFMPProviders(id)) {
                DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);

                for (final List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getHeadFMPProviders(id).values()) {
                    for (final IWailaFMPProvider provider : providersList)
                        currenttip = provider.getWailaHead(itemStack, currenttip, DataAccessorFMP.instance, config);
                }
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound subtag = list.getCompoundTagAt(i);
            final String id = subtag.getString("id");

            if (ModuleRegistrar.instance().hasBodyFMPProviders(id)) {
                DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);

                for (final List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getBodyFMPProviders(id).values()) {
                    for (final IWailaFMPProvider provider : providersList)
                        currenttip = provider.getWailaBody(itemStack, currenttip, DataAccessorFMP.instance, config);
                }
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound subtag = list.getCompoundTagAt(i);
            final String id = subtag.getString("id");

            if (ModuleRegistrar.instance().hasTailFMPProviders(id)) {
                DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);

                for (final List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getTailFMPProviders(id).values()) {
                    for (final IWailaFMPProvider provider : providersList)
                        currenttip = provider.getWailaTail(itemStack, currenttip, DataAccessorFMP.instance, config);
                }
            }
        }

        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

    public static void register() {
        Class BlockMultipart = null;
        try {
            BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.WARN, "[FMP] Class not found. " + e);
            return;
        } catch (final Exception e) {
            Waila.log.log(Level.WARN, "[FMP] Unhandled exception." + e);
            return;
        }

        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerFMP(), BlockMultipart);
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerFMP(), BlockMultipart);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerFMP(), BlockMultipart);
        ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerFMP(), BlockMultipart);

        Waila.log.log(Level.INFO, "Forge Multipart found and dedicated handler registered");

    }
}
