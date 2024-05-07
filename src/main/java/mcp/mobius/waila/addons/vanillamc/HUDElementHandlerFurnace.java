package mcp.mobius.waila.addons.vanillamc;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.NumberFormat;
import mcp.mobius.waila.api.ProbeMode;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.elements.IProbeInfo;
import mcp.mobius.waila.api.impl.elements.ItemStyle;
import mcp.mobius.waila.api.impl.elements.LayoutStyle;
import mcp.mobius.waila.api.impl.elements.ProgressStyle;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.StatCollector;

public class HUDElementHandlerFurnace implements IProbeDataProvider {

    @Override
    public void addProveInfo(ProbeMode probeMode, ItemStack itemStack, IProbeInfo probeInfo, IWailaDataAccessor data, IWailaConfigHandler config) {
        TileEntityFurnace furnace = (TileEntityFurnace) data.getTileEntity();

        int cookTime = data.getNBTData().getShort("CookTime");
        if (cookTime != 0) {
            cookTime = Math.round(cookTime / 20.0f);

            probeInfo.vertical().progress(cookTime, 10, new ProgressStyle()
                    .prefix("Smelting: ")
                    .suffix(" / 10 s")
                    .filledColor(0xFF4CBB17)
                    .alternateFilledColor(0xFF4CBB17)
                    .borderColor(0xFF555555)
                    .numberFormat(NumberFormat.COMMAS));
        }

        if (probeMode != ProbeMode.EXTENDED) {
            return;
        }

        int burnTime = data.getNBTData().getInteger("BurnTime") / 20;
        if (burnTime > 0) {
            probeInfo.horizontal()
                    .text("Burn")
                    .item(new ItemStack(Items.coal), new ItemStyle(8, 8))
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

        IProbeInfo itemSection = probeInfo.vertical(new LayoutStyle().borderColor(0xff00ffff));
        if(items[0] != null) {
            itemSection.horizontal()
                    .text("In: ")
                    .item(items[0], new ItemStyle(10, 10))
                    .text(" " + DisplayUtil.itemDisplayNameShort(items[0]) + " x" + items[0].stackSize);
        }

        if(items[2] != null) {
            itemSection.horizontal()
                    .text("Out: ")
                    .item(items[2], new ItemStyle(10, 10))
                    .text(" " + DisplayUtil.itemDisplayNameShort(items[2]) + " x" + items[2].stackSize);
        }

        if(items[1] != null) {
            itemSection.horizontal()
                    .text("Fuel: ")
                    .item(items[1], new ItemStyle(10, 10))
                    .text(" " + DisplayUtil.itemDisplayNameShort(items[1]) + " x" + items[1].stackSize);
        }
    }
}
