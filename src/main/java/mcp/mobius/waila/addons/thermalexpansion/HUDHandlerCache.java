package mcp.mobius.waila.addons.thermalexpansion;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.cbcore.LangUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class HUDHandlerCache implements IWailaDataProvider {

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("thermalexpansion.cache"))
            return currenttip;
        try {
            ItemStack storedItem = null;
            if (accessor.getNBTData().hasKey("Item"))
                storedItem = readItemStack(accessor.getNBTData().getCompoundTag("Item"));

            String name = currenttip.get(0);
            String color = "";
            if (name.startsWith("\u00a7"))
                color = name.substring(0, 2);

            if (storedItem != null) {
                final String namex = String.valueOf(GameData.getItemRegistry().getNameForObject(storedItem.getItem()));
                name += String.format(" < " + SpecialChars.getRenderString("waila.stack", "1", namex, "0", String.valueOf(storedItem.getItemDamage())) + color + " %s >", storedItem.getDisplayName());
            } else
                name += " " + LangUtil.translateG("hud.msg.empty");

            currenttip.set(0, name);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("thermalexpansion.cache"))
            return currenttip;

        final NBTTagCompound tag = accessor.getNBTData();
        ItemStack storedItem = null;
        if (tag.hasKey("Item"))
            storedItem = readItemStack(tag.getCompoundTag("Item"));

        int stored = 0;
        int maxStored = 0;
        if (tag.hasKey("Stored"))
            stored = tag.getInteger("Stored");
        if (tag.hasKey("MaxStored"))
            maxStored = tag.getInteger("MaxStored");

        if (storedItem != null) {
            currenttip.add("Stored: " + stored + "/" + maxStored);
        } else
            currenttip.add("Capacity: " + maxStored);


        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        try {
            tag.setInteger("MaxStored", (Integer) ThermalExpansionModule.TileCache_getMaxStored.invoke(te));
            tag.setInteger("Stored", (Integer) ThermalExpansionModule.TileCache_getStored.invoke(te));
        } catch (final InvocationTargetException e) {
            e.printStackTrace();
        } catch (final IllegalAccessException e) {
            e.printStackTrace();
        }
        return tag;
    }

    public ItemStack readItemStack(final NBTTagCompound tag) {
        final ItemStack is = new ItemStack(Item.getItemById(tag.getShort("id")));
        is.setCount(tag.getInteger("Count"));
        is.setItemDamage(Math.max(0, tag.getShort("Damage")));
        if (tag.hasKey("tag", 10)) {
            //is.stackTagCompound = tag.getCompoundTag("tag"); //TODO
        }

        return is;
    }

}
