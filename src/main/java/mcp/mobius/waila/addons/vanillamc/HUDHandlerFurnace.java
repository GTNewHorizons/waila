package mcp.mobius.waila.addons.vanillamc;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.List;

public class HUDHandlerFurnace implements IWailaDataProvider {

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
        final int cookTime = accessor.getNBTData().getShort("CookTime");
        final NBTTagList tag = accessor.getNBTData().getTagList("Items", 10);

        String renderStr = "";
        {
            final ItemStack stack = new ItemStack(tag.getCompoundTagAt(0));
            final String name = String.valueOf(GameData.getItemRegistry().getNameForObject(stack.getItem()));
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()));
        }
        {
            final ItemStack stack = new ItemStack(tag.getCompoundTagAt(1));
            final String name = String.valueOf(GameData.getItemRegistry().getNameForObject(stack.getItem()));
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()));
        }

        renderStr += SpecialChars.getRenderString("waila.progress", String.valueOf(cookTime), String.valueOf(200));

        {
            final ItemStack stack = new ItemStack(tag.getCompoundTagAt(2));
            final String name = String.valueOf(GameData.getItemRegistry().getNameForObject(stack.getItem()));
            renderStr += SpecialChars.getRenderString("waila.stack", "1", name, String.valueOf(stack.getCount()), String.valueOf(stack.getItemDamage()));
        }

        currenttip.add(renderStr);

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
        return tag;
    }

    public static void register() {
        //ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);
        //ModuleRegistrar.instance().registerNBTProvider(new HUDHandlerFurnace(), TileEntityFurnace.class);
    }
}
