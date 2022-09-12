package mcp.mobius.waila.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.gui.screens.config.ScreenConfig;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;

public class KeyEvent {

	public static KeyBinding key_cfg;
	public static KeyBinding key_show;
	public static KeyBinding key_liquid;
	public static KeyBinding key_recipe;
	public static KeyBinding key_usage;

	public KeyEvent() {
		ClientRegistry.registerKeyBinding(KeyEvent.key_cfg = new KeyBinding(Constants.BIND_WAILA_CFG, Keyboard.KEY_NUMPAD0, "Waila"));
		ClientRegistry.registerKeyBinding(KeyEvent.key_show = new KeyBinding(Constants.BIND_WAILA_SHOW, Keyboard.KEY_NUMPAD1, "Waila"));
		ClientRegistry.registerKeyBinding(KeyEvent.key_liquid = new KeyBinding(Constants.BIND_WAILA_LIQUID, Keyboard.KEY_NUMPAD2, "Waila"));
		ClientRegistry.registerKeyBinding(KeyEvent.key_recipe = new KeyBinding(Constants.BIND_WAILA_RECIPE, Keyboard.KEY_NUMPAD3, "Waila"));
		ClientRegistry.registerKeyBinding(KeyEvent.key_usage = new KeyBinding(Constants.BIND_WAILA_USAGE, Keyboard.KEY_NUMPAD4, "Waila"));
	}

	@SubscribeEvent
	public void onKeyEvent(KeyInputEvent event) {
		if (key_cfg.isPressed()) {
			Minecraft mc = Minecraft.getMinecraft();
			if (mc.currentScreen == null) {
				mc.displayGuiScreen(new ScreenConfig(null));
			}
		} else if (key_show.isPressed() && ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
			boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, !status);
		} else if (key_show.isPressed() && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)) {
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true);
		} else if (key_liquid.isPressed()) {
			boolean status = ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true);
			ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, !status);
		} else if (key_recipe.isPressed()) {
			if (Loader.isModLoaded("NotEnoughItems")) {
				try {
					Class.forName("mcp.mobius.waila.handlers.nei.NEIHandler").getDeclaredMethod("openRecipeGUI", boolean.class).invoke(null, true);
				} catch (Exception ignored) {
				}
			}
		} else if (key_usage.isPressed()) {
			if (Loader.isModLoaded("NotEnoughItems")) {
				try {
					Class.forName("mcp.mobius.waila.handlers.nei.NEIHandler").getDeclaredMethod("openRecipeGUI", boolean.class).invoke(null, false);
				} catch (Exception ignored) {
				}
			}
		}
	}

}
