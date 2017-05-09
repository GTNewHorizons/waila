package mcp.mobius.waila.addons.stevescarts;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.ALIGNRIGHT;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.TAB;
import static mcp.mobius.waila.api.SpecialChars.WHITE;


public class HUDLiquidManager implements IWailaDataProvider {

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
    public List<String> getWailaBody(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
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
                currenttip.add("Tanks : " + WHITE + "Shared");
                break;
            case 1:
                currenttip.add("Tanks : " + WHITE + "By side");
                break;
            case 2:
                currenttip.add("Tanks : " + WHITE + "By color");
                break;
        }

        final int toCart = accessor.getNBTInteger(tag, "tocart");
        final int doReturn = accessor.getNBTInteger(tag, "doReturn");

        for (int i = 0; i < 4; i++) {
            final int target = accessor.getNBTInteger(tag, "target" + String.valueOf(i));
            final int color = accessor.getNBTInteger(tag, "color" + String.valueOf(i));
            final int amount = accessor.getNBTInteger(tag, "amount" + String.valueOf(i));

            String fluidName = "<Empty>";
            int fluidAmount = 0;
            if (tag.hasKey("Fluid" + i)) {
                fluidName = tag.getCompoundTag("Fluid" + i).getString("FluidName");
                fluidAmount = tag.getCompoundTag("Fluid" + i).getInteger("Amount");
            }

            final String fluidString = fluidAmount == 0 ? "<Empty>" : String.format("%s mB of %s", fluidAmount, fluidName);

            if (color == 5) continue;


            final String direction = (toCart & (1 << i)) != 0 ? "Load" : "Unload";
            final String shouldReturn = (doReturn & (1 << i)) != 0 ? "Ret." : "Cont.";
            final String sAmount = amount == 0 ? "All" : String.valueOf(this.getMaxAmountBuckets(amount)) + " mB";

            //String selection =  (String) StevesCartsModule.GetSelectionName.invoke(StevesCartsModule.CargoItemSelection.cast(itemSelection.get(target)));
            currenttip.add(String.format("Side %s %s[ %s ]%s[ %s , %s]%s[ %s ]", WHITE + colors[color] + GRAY,
                    TAB + ALIGNRIGHT, WHITE + fluidString + GRAY,
                    TAB + ALIGNRIGHT, WHITE + direction + GRAY,
                    TAB + ALIGNRIGHT + WHITE + shouldReturn + GRAY,
                    TAB + ALIGNRIGHT, WHITE + sAmount + GRAY));


        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final ItemStack itemStack, final List<String> currenttip, final IWailaDataAccessor accessor, final IWailaConfigHandler config) {
        return currenttip;
    }

    public int getMaxAmountBuckets(final int id) {
        switch (id) {
            case 1:
                return 250;
            case 2:
                return 500;
            case 3:
                return 750;
            case 4:
                return 1000;
            case 5:
                return 2000;
            case 6:
                return 3000;
            case 7:
                return 5000;
            case 8:
                return 7500;
            case 9:
                return 10000;
            case 10:
                return 15000;
            default:
                return 0;
        }
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final TileEntity te, final NBTTagCompound tag, final World world, final BlockPos pos) {
        if (te != null)
            te.writeToNBT(tag);
        return tag;
    }
}
