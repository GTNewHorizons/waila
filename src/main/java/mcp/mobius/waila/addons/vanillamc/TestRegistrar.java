package mcp.mobius.waila.addons.vanillamc;

import net.minecraft.block.BlockFurnace;

import cpw.mods.fml.common.event.FMLInterModComms;
import mcp.mobius.waila.api.elements.IProbeRegistrar;

// example class for registering IMC
public class TestRegistrar {

    public static void callbackRegister(IProbeRegistrar register) {
        register.registerProbeProvider(new HUDElementHandlerFurnace(), BlockFurnace.class);
    }

    public static void init() {
        FMLInterModComms.sendMessage("Waila", "elementregister", TestRegistrar.class.getName() + ".callbackRegister");
    }
}
