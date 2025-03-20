package mcp.mobius.waila.addons.thaumcraft;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.AQUA;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerIAspectContainer implements IWailaDataProvider {

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

        if (!config.getConfig("thaumcraft.aspects")) return currenttip;

        NBTTagCompound tag = accessor.getNBTData();
        if (tag.hasKey("Aspects")) {
            NBTTagList taglist = tag.getTagList("Aspects", 10);

            List<String> unknownAspects = new ArrayList<>();

            int total = 0;

            for (int i = 0; i < taglist.tagCount(); i++) {
                NBTTagCompound subtag = taglist.getCompoundTagAt(i);

                String aspect = subtag.getString("key");
                int value = subtag.getInteger("value");
                total += value;
                String amount = String.valueOf(value);

                if (!aspect.equals("???"))
                    currenttip.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s", aspect, amount));
                else unknownAspects.add(String.format("%s" + TAB + ALIGNRIGHT + WHITE + "%s", aspect, amount));
            }

            String id = tag.getString("WailaID");

            if ((id.equals("TileNode") || id.equals("TileExtendedNode") || id.equals("TileJarNode"))
                    && (taglist.tagCount() > 10 || total > 300)) {
                currenttip.add(
                        0,
                        String.format(
                                AQUA + "%s" + TAB + ALIGNRIGHT + WHITE + "%s",
                                LangUtil.translateG("hud.msg.tcnodetotalsize"),
                                total));
            }

            currenttip.addAll(unknownAspects);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @SuppressWarnings("unchecked")
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {

        try {
            tag.setTag("Aspects", new NBTTagList());
            NBTTagList aspects = tag.getTagList("Aspects", 10);

            ItemStack headSlot = player.inventory.armorInventory[3];
            if (headSlot == null) return tag;
            boolean hasReveal = ThaumcraftModule.IGoggles.isInstance(headSlot.getItem());
            if (!hasReveal) return tag;

            Map<String, ?> knownAspects = (Map<String, ?>) ThaumcraftModule.CommonProxy_getKnownAspects
                    .invoke(ThaumcraftModule.Thaumcraft_proxy.get(null));
            Map<?, Integer> playerAspects = (Map<?, Integer>) ThaumcraftModule.AspectList_aspects
                    .get(knownAspects.get(player.getCommandSenderName()));
            Map<?, Integer> tileAspects = new LinkedHashMap<>();

            if (ThaumcraftModule.IAspectContainer.isInstance(te)) {
                tileAspects = (Map<?, Integer>) ThaumcraftModule.AspectList_aspects
                        .get(ThaumcraftModule.IAspectContainer_getAspects.invoke(te));
            } else if (ThaumcraftModule.TileAlchemyFurnace.isInstance(te)) {
                tileAspects = (Map<?, Integer>) ThaumcraftModule.AspectList_aspects
                        .get(ThaumcraftModule.TileAlchemyFurnace_aspects.get(te));
            }

            for (Object o : tileAspects.keySet()) {
                if (tileAspects.get(o) > 0) {
                    if (playerAspects.containsKey(o)) {
                        NBTTagCompound cmptag = new NBTTagCompound();
                        cmptag.setString("key", (String) ThaumcraftModule.Aspect_getName.invoke(o));
                        cmptag.setInteger("value", tileAspects.get(o));
                        aspects.appendTag(cmptag);
                    } else {
                        NBTTagCompound cmptag = new NBTTagCompound();
                        cmptag.setString("key", "???");
                        cmptag.setInteger("value", tileAspects.get(o));
                        aspects.appendTag(cmptag);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return tag;
    }

}
