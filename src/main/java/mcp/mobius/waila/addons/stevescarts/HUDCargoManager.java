package mcp.mobius.waila.addons.stevescarts;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;

public class HUDCargoManager implements IWailaDataProvider {

    private static String colors[] = {"NA", "Red", "Blue", "Yellow", "Green", "Dis."};
    private static String sides[] = {"Yellow", "Blue", "Green", "Red"};

    @Override
    public ItemStack getWailaStack(final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final ItemStack itemStack, List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("stevescarts.showall")) return currenttip;
        if (config.getConfig("stevescarts.shifttoggle") && !accessor.getPlayer().isSneaking()) {
            currenttip.add(ITALIC + "Press shift for more data");
            return currenttip;
        }

        final NBTTagCompound tag = accessor.getNBTData();

        if (config.getConfig("stevescarts.colorblind")) {
            final int side = accessor.getSide().ordinal() - 2;
            if (side >= 0)
                currenttip.add("Looking at side " + WHITE + sides[side]);
        }

        final int layout = accessor.getNBTInteger(tag, "layout");
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
            final ArrayList<Object> itemSelection = (ArrayList<Object>) StevesCartsModule.ItemSelections.get(accessor.getTileEntity());

            final int toCart = accessor.getNBTInteger(tag, "tocart");
            final int doReturn = accessor.getNBTInteger(tag, "doReturn");

            for (int i = 0; i < 4; i++) {
                final int target = accessor.getNBTInteger(tag, "target" + String.valueOf(i));
                final int color = accessor.getNBTInteger(tag, "color" + String.valueOf(i));
                final int amount = accessor.getNBTInteger(tag, "amount" + String.valueOf(i));
                if (color == 5) continue;


                final String direction = (toCart & (1 << i)) != 0 ? "Load" : "Unload";
                final String shouldReturn = (doReturn & (1 << i)) != 0 ? "Ret." : "Cont.";
                final String sAmount = amount == 0 ? "All" : String.valueOf(this.getAmount(amount) + " " + this.getAmountType(amount));

                final String selection = (String) StevesCartsModule.GetSelectionName.invoke(StevesCartsModule.CargoItemSelection.cast(itemSelection.get(target)));
                currenttip.add(String.format("Side %s %s[ %s ]%s[ %s , %s]%s[ %s ]", WHITE + colors[color] + GRAY,
                        TAB + ALIGNRIGHT, WHITE + selection + GRAY,
                        TAB + ALIGNRIGHT, WHITE + direction + GRAY,
                        TAB + ALIGNRIGHT + WHITE + shouldReturn + GRAY,
                        TAB + ALIGNRIGHT, WHITE + sAmount + GRAY));
            }

        } catch (final Exception e) {
            currenttip = WailaExceptionHandler.handleErr(e, accessor.getTileEntity().getClass().getName(), currenttip);
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    public int getAmount(final int id) {
        switch (id) {
            case 1:
                return 1;

            case 2:
                return 3;

            case 3:
                return 8;

            case 4:
                return 16;

            case 5:
                return 32;

            case 6:
                return 64;

            case 7:
                return 1;

            case 8:
                return 2;

            case 9:
                return 3;

            case 10:
                return 5;

            default:
                return 0;
        }
    }

    //0 - MAX
    //1 - Items
    //2 - Stacks
    public String getAmountType(final int id) {
        if (id <= 6) {
            return "I";
        } else {
            return "S";
        }
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }

}
