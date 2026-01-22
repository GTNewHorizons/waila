package mcp.mobius.waila.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.gui.truetyper.FontLoader;
import mcp.mobius.waila.gui.truetyper.TrueTypeFont;
import mcp.mobius.waila.handlers.HUDHandlerBlocks;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import mcp.mobius.waila.handlers.VanillaTooltipHandler;
import mcp.mobius.waila.handlers.nei.NEIHandler;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderAspectString;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderFluidBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderHealth;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderProgressBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderRFBar;
import mcp.mobius.waila.overlay.tooltiprenderers.TTRenderStack;
import mcp.mobius.waila.server.ProxyServer;
import mcp.mobius.waila.utils.LoadedMods;
import mcp.mobius.waila.utils.NumberFormatter;

public class ProxyClient extends ProxyServer {

    TrueTypeFont minecraftiaFont;

    public ProxyClient() {}

    @Override
    public void registerHandlers() {

        LangUtil.loadLangDir("waila");

        if (LoadedMods.NEI) {
            NEIHandler.register();
        } else {
            MinecraftForge.EVENT_BUS.register(new VanillaTooltipHandler());
        }

        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerBlocks(), Block.class);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerBlocks(), Block.class);
        ModuleRegistrar.instance().registerInfoIconProvider(new HUDHandlerBlocks(), Block.class);

        ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerEntities(), Entity.class);
        ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerEntities(), Entity.class);
        ModuleRegistrar.instance().registerTailProvider(new HUDHandlerEntities(), Entity.class);
        ModuleRegistrar.instance().registerInfoIconProvider(new HUDHandlerEntities(), Entity.class);

        ModuleRegistrar.instance().addConfig("General", "general.showents");
        ModuleRegistrar.instance().addConfig("General", "general.showhp");
        ModuleRegistrar.instance().addConfig("General", "general.showcrop");

        ModuleRegistrar.instance().registerTooltipRenderer("waila.health", new TTRenderHealth());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.stack", new TTRenderStack());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.progress", new TTRenderProgressBar());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.fluid", new TTRenderFluidBar());
        ModuleRegistrar.instance().registerTooltipRenderer("waila.rfenergy", new TTRenderRFBar());

        ModuleRegistrar.instance().registerTooltipRenderer("waila.tcaspect", new TTRenderAspectString());

        MinecraftForge.EVENT_BUS.register(new WorldUnloadEventHandler());

        ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
                .registerReloadListener(new NumberFormatterReloadListener());
    }

    @Override
    public Object getFont() {
        if (minecraftiaFont == null)
            minecraftiaFont = FontLoader.createFont(new ResourceLocation("waila", "fonts/Minecraftia.ttf"), 14, true);
        return this.minecraftiaFont;
    }

    public static class WorldUnloadEventHandler {

        @SubscribeEvent
        public void onWorldUnload(WorldEvent.Unload event) {
            if (event.world.isRemote) {
                DataAccessorCommon.instance = new DataAccessorCommon();
            }
        }
    }

    private static class NumberFormatterReloadListener implements IResourceManagerReloadListener {

        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {
            NumberFormatter.onResourcesReload();
        }
    }
}
