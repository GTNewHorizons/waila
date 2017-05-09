package mcp.mobius.waila.addons.thaumcraft;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDHandlerIAspectContainer implements IWailaDataProvider {

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

        if (!config.getConfig("thaumcraft.aspects")) return currenttip;

        final NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("Aspects")) {
            final NBTTagList taglist = tag.getTagList("Aspects", 10);

            final List<String> unknownAspects = new ArrayList<String>();

            for (int i = 0; i < taglist.tagCount(); i++) {
                final NBTTagCompound subtag = taglist.getCompoundTagAt(i);

                final String aspect = Character.toUpperCase(subtag.getString("key").charAt(0)) + subtag.getString("key").substring(1);
                final String amount = String.valueOf(subtag.getInteger("value"));

                if (!aspect.equals("???"))
                    currenttip.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s", aspect, amount));
                else
                    unknownAspects.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s", aspect, amount));
            }

            currenttip.addAll(unknownAspects);
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
            tag.setTag("Aspects", new NBTTagList());
            final NBTTagList aspects = tag.getTagList("Aspects", 10);

            final ItemStack headSlot = player.inventory.armorInventory.get(3);
            if (headSlot == null) return tag;
            final boolean hasReveal = ThaumcraftModule.IGoggles.isInstance(headSlot.getItem());
            if (!hasReveal) return tag;

            final HashMap knownAspects = (HashMap) ThaumcraftModule.CommonProxy_getKnownAspects.invoke(ThaumcraftModule.Thaumcraft_proxy.get(null));
            final LinkedHashMap playerAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(knownAspects.get(player.getName()));
            LinkedHashMap tileAspects = new LinkedHashMap();

            if (ThaumcraftModule.IAspectContainer.isInstance(te)) {
                tileAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(ThaumcraftModule.IAspectContainer_getAspects.invoke(te));
            } else if (ThaumcraftModule.TileAlchemyFurnace.isInstance(te)) {
                tileAspects = (LinkedHashMap) ThaumcraftModule.AspectList_aspects.get(ThaumcraftModule.TileAlchemyFurnace_aspects.get(te));
            }

            for (final Object o : tileAspects.keySet()) {
                if ((Integer) tileAspects.get(o) > 0) {
                    if (playerAspects.containsKey(o)) {
                        final NBTTagCompound cmptag = new NBTTagCompound();
                        cmptag.setString("key", (String) ThaumcraftModule.Aspect_tag.get(o));
                        cmptag.setInteger("value", (Integer) tileAspects.get(o));
                        aspects.appendTag(cmptag);
                    } else {
                        final NBTTagCompound cmptag = new NBTTagCompound();
                        cmptag.setString("key", "???");
                        cmptag.setInteger("value", (Integer) tileAspects.get(o));
                        aspects.appendTag(cmptag);
                    }
                }
            }

            //for (int i = 0; i < aspects.tagCount(); i++){
            //	System.out.printf("%s : %s\n", aspects.getCompoundTagAt(i).getString("key"), aspects.getCompoundTagAt(i).getString("value"));
            //}


        } catch (final Exception e) {
            throw new RuntimeException(e);
        }

        return tag;
    }

}
