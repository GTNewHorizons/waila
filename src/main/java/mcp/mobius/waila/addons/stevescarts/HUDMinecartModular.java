package mcp.mobius.waila.addons.stevescarts;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDMinecartModular implements IWailaEntityProvider {

    @Override
    public Entity getWailaOverride(final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        if (currenttip.get(0).contains("entity.StevesCarts.Minecart")) {
            currenttip.remove(0);
            currenttip.add(WHITE + "Modular Minecart");
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final Entity entity, List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("stevescarts.showall")) return currenttip;
        if (config.getConfig("stevescarts.shifttoggle") && !accessor.getPlayer().isSneaking()) {
            currenttip.add(ITALIC + "Press shift for more data");
            return currenttip;
        }

        Item ItemCartModule = null;
        try {
            ItemCartModule = (Item) StevesCartsModule.ItemCartModule.get(null);
        } catch (final Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getEntity().getClass().getName(), currenttip);
            return currenttip;
        }

        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("Modules")) {
            final byte[] metas = tag.getByteArray("Modules");

            for (int i = 0; i < metas.length; i++) {
                if (tag.hasKey("module" + String.valueOf(i) + "Fuel")) {

                    final int fuel = accessor.getNBTInteger(tag, "module" + String.valueOf(i) + "Fuel");
                    currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " [ " + WHITE + fuel + GRAY + " POW ]");

                } else if (tag.hasKey("module" + String.valueOf(i) + "Fluid")) {

                    final NBTTagCompound subtag = tag.getCompoundTag("module" + String.valueOf(i) + "Fluid");
                    if (subtag.hasKey("Amount")) {
                        final int amount = accessor.getNBTInteger(subtag, "Amount");
                        final String fluid = subtag.getString("FluidName");
                        currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " [ " + WHITE + String.valueOf(amount) + GRAY + " mB of " + WHITE + fluid + GRAY + " ]");
                    } else {
                        currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName() + TAB + ALIGNRIGHT + " < " + WHITE + "Empty" + GRAY + " > ");
                    }

                } else {

                    currenttip.add(new ItemStack(ItemCartModule, 1, metas[i]).getDisplayName());

                }
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final Entity te, final NBTTagCompound tag, final World world) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }
}
