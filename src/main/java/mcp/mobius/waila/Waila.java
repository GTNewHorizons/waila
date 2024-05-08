package mcp.mobius.waila;

import java.lang.reflect.Field;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.waila.addons.vanillamc.TestRegistrar;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.api.impl.elements.ModuleProbeRegistrar;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.commands.CommandDumpHandlers;
import mcp.mobius.waila.network.NetworkHandler;
import mcp.mobius.waila.network.WailaPacketHandler;
import mcp.mobius.waila.overlay.DecoratorRenderer;
import mcp.mobius.waila.overlay.OverlayConfig;
import mcp.mobius.waila.overlay.WailaTickHandler;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.ModIdentification;

@Mod(
        modid = "Waila",
        name = "Waila",
        version = Tags.GRADLETOKEN_VERSION,
        dependencies = "after:NotEnoughItems@[1.0.4.0,)",
        acceptableRemoteVersions = "*")
public class Waila {

    // The instance of your mod that Forge uses.
    @Instance("Waila")
    public static Waila instance;

    @SidedProxy(clientSide = "mcp.mobius.waila.client.ProxyClient", serverSide = "mcp.mobius.waila.server.ProxyServer")
    public static ProxyServer proxy;
    public static Logger log = LogManager.getLogger("Waila");
    public boolean serverPresent = false;
    private final ArtifactVersion minimumClientJoinVersion = new DefaultArtifactVersion("1.7.3");

    /* INIT SEQUENCE */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigHandler.instance().loadDefaultConfig(event);
        OverlayConfig.updateColors();
        MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());
        WailaPacketHandler.INSTANCE.ordinal();
    }

    @EventHandler
    public void initialize(FMLInitializationEvent event) {
        try {
            Field eBus = FMLModContainer.class.getDeclaredField("eventBus");
            eBus.setAccessible(true);
            EventBus FMLbus = (EventBus) eBus.get(FMLCommonHandler.instance().findContainerFor(this));
            FMLbus.register(this);
        } catch (Throwable ignored) {}

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new DecoratorRenderer());
            FMLCommonHandler.instance().bus().register(new KeyEvent());
            FMLCommonHandler.instance().bus().register(WailaTickHandler.instance());

        }
        FMLCommonHandler.instance().bus().register(new NetworkHandler());

        TestRegistrar.init();

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.registerHandlers();
        ModIdentification.init();
    }

    @Subscribe
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.registerMods();
        proxy.registerIMCs();
    }

    @EventHandler
    public void processIMC(FMLInterModComms.IMCEvent event) {
        for (IMCMessage imcMessage : event.getMessages()) {
            if (!imcMessage.isStringMessage()) continue;

            if (imcMessage.key.equalsIgnoreCase("addconfig")) {
                String[] params = imcMessage.getStringValue().split("\\$\\$");
                if (params.length != 3) {
                    Waila.log.warn(
                            String.format(
                                    "Error while parsing config option from [ %s ] for %s",
                                    imcMessage.getSender(),
                                    imcMessage.getStringValue()));
                    continue;
                }
                Waila.log.info(
                        String.format(
                                "Receiving config request from [ %s ] for %s",
                                imcMessage.getSender(),
                                imcMessage.getStringValue()));
                ConfigHandler.instance().addConfig(params[0], params[1], params[2]);
            }

            if (imcMessage.key.equalsIgnoreCase("register")) {
                Waila.log.info(
                        String.format(
                                "Receiving registration request from [ %s ] for method %s",
                                imcMessage.getSender(),
                                imcMessage.getStringValue()));
                ModuleRegistrar.instance().addIMCRequest(imcMessage.getStringValue(), imcMessage.getSender());
            }

            if (imcMessage.key.equalsIgnoreCase("elementregister")) {
                Waila.log.info(
                        String.format(
                                "Receiving registration request from [ %s ] for method %s",
                                imcMessage.getSender(),
                                imcMessage.getStringValue()));
                ModuleProbeRegistrar.instance().addIMCRequest(imcMessage.getStringValue(), imcMessage.getSender());
            }
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDumpHandlers());
    }

    /**
     * Block any clients older than 1.7.3 to ensure the vanilla.show_invisible_players property is respected
     */
    @SuppressWarnings("unused")
    @NetworkCheckHandler
    public boolean checkModList(Map<String, String> versions, Side side) {
        if (side == Side.CLIENT && versions.containsKey("Waila")) {
            return minimumClientJoinVersion.compareTo(new DefaultArtifactVersion(versions.get("Waila"))) <= 0;
        }
        return true;
    }
}
