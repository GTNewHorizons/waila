package mcp.mobius.waila.handlers;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaFMPDecorator;
import mcp.mobius.waila.api.impl.DataAccessorFMP;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.Level;

import java.util.List;

public class DecoratorFMP implements IWailaBlockDecorator {

    @Override
    public void decorateBlock(final ItemStack itemStack, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        final NBTTagList list = accessor.getNBTData().getTagList("parts", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            final NBTTagCompound subtag = list.getCompoundTagAt(i);
            final String id = subtag.getString("id");

            if (ModuleRegistrar.instance().hasFMPDecorator(id)) {
                DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id, accessor.getRenderingPosition(), accessor.getPartialFrame());

                for (final List<IWailaFMPDecorator> providersList : ModuleRegistrar.instance().getFMPDecorators(id).values())
                    for (final IWailaFMPDecorator provider : providersList)
                        provider.decorateBlock(itemStack, DataAccessorFMP.instance, config);
            }
        }
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

        ModuleRegistrar.instance().registerDecorator(new DecoratorFMP(), BlockMultipart);
    }

}
