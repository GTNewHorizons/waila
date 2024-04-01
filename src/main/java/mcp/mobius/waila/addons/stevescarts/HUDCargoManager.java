package mcp.mobius.waila.addons.stevescarts;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class HUDCargoManager implements IWailaDataProvider {

    private static String[] colors = { "NA", "Red", "Blue", "Yellow", "Green", "Dis." };
    private static String[] sides = { "Yellow", "Blue", "Green", "Red" };

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
        if (!config.getConfig("stevescarts.showall")) return currenttip;
        if (config.getConfig("stevescarts.shifttoggle") && !accessor.getPlayer().isSneaking()) {
            currenttip.add(ITALIC + "Press shift for more data");
            return currenttip;
        }

        NBTTagCompound tag = accessor.getNBTData();

        if (config.getConfig("stevescarts.colorblind")) {
            int side = accessor.getSide().ordinal() - 2;
            if (side >= 0) currenttip.add("Looking at side " + WHITE + sides[side]);
        }

        int layout = accessor.getNBTInteger(tag, "layout");
        switch (layout) {
            case 0:
                currenttip.add("Slots : " + WHITE + "Shared");
                break;
            case 1:
                currenttip.add("Slots : " + WHITE + "By side");
                break;
            case 2:
                currenttip.add("Slots : " + WHITE + "By color");
                break;
        }

        try {
            ArrayList<Object> itemSelection = (ArrayList<Object>) StevesCartsModule.ItemSelections
                    .get(accessor.getTileEntity());

            int toCart = accessor.getNBTInteger(tag, "tocart");
            int doReturn = accessor.getNBTInteger(tag, "doReturn");

            for (int i = 0; i < 4; i++) {
                int target = accessor.getNBTInteger(tag, "target" + i);
                int color = accessor.getNBTInteger(tag, "color" + i);
                int amount = accessor.getNBTInteger(tag, "amount" + i);
                if (color == 5) continue;

                String direction = (toCart & (1 << i)) != 0 ? "Load" : "Unload";
                String shouldReturn = (doReturn & (1 << i)) != 0 ? "Ret." : "Cont.";
                String sAmount = amount == 0 ? "All" : this.getAmount(amount) + " " + this.getAmountType(amount);

                String selection = (String) StevesCartsModule.GetSelectionName
                        .invoke(StevesCartsModule.CargoItemSelection.cast(itemSelection.get(target)));
                currenttip.add(
                        String.format(
                                "Side %s %s[ %s ]%s[ %s , %s]%s[ %s ]",
                                WHITE + colors[color] + GRAY,
                                TAB + ALIGNRIGHT,
                                WHITE + selection + GRAY,
                                TAB + ALIGNRIGHT,
                                WHITE + direction + GRAY,
                                TAB + ALIGNRIGHT + WHITE + shouldReturn + GRAY,
                                TAB + ALIGNRIGHT,
                                WHITE + sAmount + GRAY));
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

    public int getAmount(int id) {
        return switch (id) {
            case 1, 7 -> 1;
            case 2, 9 -> 3;
            case 3 -> 8;
            case 4 -> 16;
            case 5 -> 32;
            case 6 -> 64;
            case 8 -> 2;
            case 10 -> 5;
            default -> 0;
        };
    }

    // 0 - MAX
    // 1 - Items
    // 2 - Stacks
    public String getAmountType(int id) {
        if (id <= 6) {
            return "I";
        } else {
            return "S";
        }
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }

}
