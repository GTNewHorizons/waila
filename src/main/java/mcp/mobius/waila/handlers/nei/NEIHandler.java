package mcp.mobius.waila.handlers.nei;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;

import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.overlay.RayTracing;
import mcp.mobius.waila.utils.Constants;

public class NEIHandler {

    public static void register() {
        if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_NEWFILTERS, true)) {
            API.addSearchProvider(new ModNameFilter());
            API.addSearchProvider(new OreDictFilter());
        }

        GuiContainerManager.addTooltipHandler(new TooltipHandlerWaila());

        // We mute the default keybind for displaying the tooltip
        NEIClientConfig.getSetting(Constants.BIND_NEI_SHOW).setIntValue(Keyboard.KEY_NONE);
        NEIClientConfig.getSetting(Constants.CFG_NEI_SHOW).setBooleanValue(false);

        GuiContainerManager.addInputHandler(new HandlerEnchants());
        API.addKeyBind(Constants.BIND_SCREEN_ENCH, Keyboard.KEY_I);
    }

    public static boolean firstInventory = true;

    public static void openRecipeGUI(boolean recipe) {
        Minecraft mc = Minecraft.getMinecraft();

        if ((RayTracing.instance().getTarget() != null)
                && (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)) {
            List<ItemStack> stacks = RayTracing.instance().getIdentifierItems();
            if (!stacks.isEmpty()) {
                mc.displayGuiScreen(new GuiInventory(mc.thePlayer));
                if (firstInventory) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception ignored) {}
                    firstInventory = false;
                }

                if (recipe) if (!GuiCraftingRecipe.openRecipeGui("item", stacks.get(0).copy())) {
                    ItemStack target = stacks.get(0).copy();
                    target.setItemDamage(0);
                    if (!GuiCraftingRecipe.openRecipeGui("item", target)) {
                        mc.thePlayer.addChatMessage(
                                new ChatComponentTranslation(
                                        "\u00a7f\u00a7o" + LangUtil.translateG("client.msg.norecipe")));
                        mc.displayGuiScreen(null);
                        mc.setIngameFocus();
                    }
                }

                if (!recipe) if (!GuiUsageRecipe.openRecipeGui("item", stacks.get(0).copy())) {
                    ItemStack target = stacks.get(0).copy();
                    target.setItemDamage(0);
                    if (!GuiUsageRecipe.openRecipeGui("item", target)) {
                        mc.thePlayer.addChatMessage(
                                new ChatComponentTranslation(
                                        "\u00a7f\u00a7o" + LangUtil.translateG("client.msg.nousage")));
                        mc.displayGuiScreen(null);
                        mc.setIngameFocus();
                    }
                }
            }
        }
    }
}
