package mcp.mobius.waila.addons.stevescarts;

import static mcp.mobius.waila.api.SpecialChars.*;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;

public class HUDLiquidManager implements IWailaDataProvider {

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
                currenttip.add("Tanks : " + WHITE + "Shared");
                break;
            case 1:
                currenttip.add("Tanks : " + WHITE + "By side");
                break;
            case 2:
                currenttip.add("Tanks : " + WHITE + "By color");
                break;
        }

        int toCart = accessor.getNBTInteger(tag, "tocart");
        int doReturn = accessor.getNBTInteger(tag, "doReturn");

        for (int i = 0; i < 4; i++) {
            int color = accessor.getNBTInteger(tag, "color" + i);
            int amount = accessor.getNBTInteger(tag, "amount" + i);

            String fluidName = "<Empty>";
            int fluidAmount = 0;
            if (tag.hasKey("Fluid" + i)) {
                fluidName = tag.getCompoundTag("Fluid" + i).getString("FluidName");
                fluidAmount = tag.getCompoundTag("Fluid" + i).getInteger("Amount");
            }

            String fluidString = fluidAmount == 0 ? "<Empty>" : String.format("%s mB of %s", fluidAmount, fluidName);

            if (color == 5) continue;

            String direction = (toCart & (1 << i)) != 0 ? "Load" : "Unload";
            String shouldReturn = (doReturn & (1 << i)) != 0 ? "Ret." : "Cont.";
            String sAmount = amount == 0 ? "All" : this.getMaxAmountBuckets(amount) + " mB";

            currenttip.add(
                    String.format(
                            "Side %s %s[ %s ]%s[ %s , %s]%s[ %s ]",
                            WHITE + colors[color] + GRAY,
                            TAB + ALIGNRIGHT,
                            WHITE + fluidString + GRAY,
                            TAB + ALIGNRIGHT,
                            WHITE + direction + GRAY,
                            TAB + ALIGNRIGHT + WHITE + shouldReturn + GRAY,
                            TAB + ALIGNRIGHT,
                            WHITE + sAmount + GRAY));

        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    public int getMaxAmountBuckets(int id) {
        return switch (id) {
            case 1 -> 250;
            case 2 -> 500;
            case 3 -> 750;
            case 4 -> 1000;
            case 5 -> 2000;
            case 6 -> 3000;
            case 7 -> 5000;
            case 8 -> 7500;
            case 9 -> 10000;
            case 10 -> 15000;
            default -> 0;
        };
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        if (te != null) te.writeToNBT(tag);
        return tag;
    }
}
