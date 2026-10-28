package mcp.mobius.waila.handlers;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;

/**
 * Shows "X items in Y of Z slots" for every block and entity that is an {@link IInventory}.
 * <p>
 * The server always adds the summary to the NBT answer of a {@link IInventory} target (see
 * {@link #writeSummary(Object, World, int, int, int, NBTTagCompound)}). This is not done with an NBT provider, because
 * an NBT provider would stop the synced NBT keys of other mods for all inventories.
 * <p>
 * If the client only asked because of the summary, the answer has only the summary and {@link #TAG_SUMMARY_ONLY}. The
 * client then adds the summary to its own NBT of the target, so that other mods still get the same NBT as before.
 */
public class HUDHandlerInventory implements IWailaDataProvider, IWailaEntityProvider {

    public static final String CFG_SHOW = "general.showinventory";

    private static final String TAG_ITEMS = "WailaInvItems";
    private static final String TAG_USED = "WailaInvUsed";
    private static final String TAG_SLOTS = "WailaInvSlots";
    public static final String TAG_SUMMARY_ONLY = "WailaInvOnly";

    /**
     * @return true if the client must ask the server for the summary of this target.
     */
    public static boolean shouldRequest(Object target) {
        return target instanceof IInventory && ConfigHandler.instance().getConfig(CFG_SHOW, true);
    }

    /**
     * Server side: adds the item count, the used slots and the total slots of the target to the tag.
     */
    public static void writeSummary(Object target, World world, int x, int y, int z, NBTTagCompound tag) {
        if (!(target instanceof IInventory)) return;
        try {
            IInventory inv = (IInventory) target;

            // Count both halves of a double chest. The chest updates its neighbours every tick.
            if (target instanceof TileEntityChest chest) {
                TileEntityChest other = chest.adjacentChestXNeg != null ? chest.adjacentChestXNeg
                        : chest.adjacentChestXPos != null ? chest.adjacentChestXPos
                                : chest.adjacentChestZNeg != null ? chest.adjacentChestZNeg : chest.adjacentChestZPos;
                if (other != null) inv = new InventoryLargeChest("", chest, other);
            }

            long items = 0;
            int used = 0;
            int slots = inv.getSizeInventory();
            for (int i = 0; i < slots; i++) {
                ItemStack stack = inv.getStackInSlot(i);
                if (stack == null || stack.getItem() == null || stack.stackSize <= 0) continue;
                items += stack.stackSize;
                used++;
            }

            tag.setLong(TAG_ITEMS, items);
            tag.setInteger(TAG_USED, used);
            tag.setInteger(TAG_SLOTS, slots);
        } catch (Throwable e) {
            // Do not lose the data of the other providers because of one bad inventory
            WailaExceptionHandler.handleErr(e, "HUDHandlerInventory | " + target.getClass().getName(), null);
        }
    }

    /**
     * Client side: adds the values of a summary only answer to the local NBT of the target.
     */
    public static NBTTagCompound mergeSummary(NBTTagCompound remote, NBTTagCompound local) {
        for (Object key : remote.func_150296_c()) local.setTag((String) key, remote.getTag((String) key).copy());
        local.removeTag(TAG_SUMMARY_ONLY);
        return local;
    }

    private static void addLine(NBTTagCompound tag, List<String> currenttip, IWailaConfigHandler config) {
        if (tag == null || !config.getConfig(CFG_SHOW, true) || !tag.hasKey(TAG_SLOTS)) return;
        int slots = tag.getInteger(TAG_SLOTS);
        if (slots <= 0) return;
        long items = tag.getLong(TAG_ITEMS);
        int used = tag.getInteger(TAG_USED);
        String key = items == 1 ? "hud.msg.inventory.single" : "hud.msg.inventory";
        currenttip.add(String.format(LangUtil.translateG(key), formatNumber(items), used, slots));
    }

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
        addLine(accessor.getNBTData(), currenttip, config);
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x,
            int y, int z) {
        return tag;
    }

    @Override
    public Entity getWailaOverride(IWailaEntityAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
            IWailaConfigHandler config) {
        addLine(accessor.getNBTData(), currenttip, config);
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(Entity entity, List<String> currenttip, IWailaEntityAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, Entity ent, NBTTagCompound tag, World world) {
        return tag;
    }

    public static void register() {
        HUDHandlerInventory provider = new HUDHandlerInventory();
        ModuleRegistrar.instance().addConfig("General", CFG_SHOW, true);
        ModuleRegistrar.instance().registerBodyProvider((IWailaDataProvider) provider, IInventory.class);
        ModuleRegistrar.instance().registerBodyProvider((IWailaEntityProvider) provider, IInventory.class);
    }
}
