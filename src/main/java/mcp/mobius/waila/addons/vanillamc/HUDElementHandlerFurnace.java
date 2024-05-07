package mcp.mobius.waila.addons.vanillamc;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.NumberFormat;
import mcp.mobius.waila.api.ProbeMode;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.elements.IProbeInfo;
import mcp.mobius.waila.api.elements.ITextStyle;
import mcp.mobius.waila.overlay.DisplayUtil;

/**
 * Example class for testing various elements
 */
public class HUDElementHandlerFurnace implements IProbeDataProvider {

    @Override
    public void addProbeInfo(ProbeMode probeMode, ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor data,
            IWailaConfigHandler config) {
        TileEntityFurnace furnace = (TileEntityFurnace) data.getTileEntity();

        int cookTime = data.getNBTData().getShort("CookTime");
        if (cookTime != 0) {
            cookTime = Math.round(cookTime / 20.0f);

            probeInfo.vertical().progress(
                    cookTime,
                    10,
                    probeInfo.defaultProgressStyle().text("Smelting: " + cookTime + " / 10 s").filledColor(0xFF4CBB17)
                            .alternateFilledColor(0xFF4CBB17).borderColor(0xFF555555)
                            .numberFormat(NumberFormat.COMMAS));
        }

        if (probeMode != ProbeMode.EXTENDED) {
            return;
        }

        int burnTime = data.getNBTData().getInteger("BurnTime") / 20;
        if (burnTime > 0) {
            probeInfo.horizontal().text("Burn")
                    .item(new ItemStack(Blocks.fire), probeInfo.defaultItemStyle().width(8).height(8))
                    .text(": " + burnTime + " " + "Seconds Remaining");
        }

        ItemStack[] items = new ItemStack[3];
        NBTTagList itemsTag = data.getNBTData().getTagList("Items", 10);

        for (int i = 0; i < itemsTag.tagCount(); i++) {
            NBTTagCompound itemTag = itemsTag.getCompoundTagAt(i);
            byte slot = itemTag.getByte("Slot");

            if (slot >= 0 && slot < items.length) {
                items[slot] = ItemStack.loadItemStackFromNBT(itemTag);
            }
        }

        ITextStyle itemPaddedStyle = probeInfo.defaultTextStyle().vPadding(2);
        IProbeInfo itemSection = probeInfo.vertical(probeInfo.defaultLayoutStyle().borderColor(0xff00ffff));
        if (items[0] != null) {
            itemSection.horizontal().text("In: ", itemPaddedStyle)
                    .item(items[0], probeInfo.defaultItemStyle().width(10).height(10)).text(
                            " " + DisplayUtil.itemDisplayNameShort(items[0]) + " x" + items[0].stackSize,
                            itemPaddedStyle);
        }

        if (items[2] != null) {
            itemSection.horizontal().text("Out: ", itemPaddedStyle)
                    .item(items[2], probeInfo.defaultItemStyle().width(10).height(10)).text(
                            " " + DisplayUtil.itemDisplayNameShort(items[2]) + " x" + items[2].stackSize,
                            itemPaddedStyle);
        }

        if (items[1] != null) {
            itemSection.horizontal().text("Fuel: ", itemPaddedStyle)
                    .item(items[1], probeInfo.defaultItemStyle().width(10).height(10)).text(
                            " " + DisplayUtil.itemDisplayNameShort(items[1]) + " x" + items[1].stackSize,
                            itemPaddedStyle);
        }
    }
}
