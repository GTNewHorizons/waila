package mcp.mobius.waila.addons.projectred;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import org.apache.logging.log4j.Level;

public class ProjectRedModule {

    public static void register() {
        try {
            final Class ModClass = Class.forName("mrtjp.projectred.ProjectRedIntegration");
            Waila.log.log(Level.INFO, "ProjectRed|Integration mod found.");
        } catch (final ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[ProjectRed] ProjectRed|Integration mod not found.");
            return;
        }

        ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showio");
        ModuleRegistrar.instance().addConfigRemote("Project:Red", "pr.showdata");

        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_sgate");
        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_igate");
        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_tgate");
        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_bgate");
        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_agate");
        ModuleRegistrar.instance().registerBodyProvider(new HUDFMPGateLogic(), "pr_rgate");

        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_sgate");
        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_igate");
        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_tgate");
        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_bgate");
        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_agate");
        ModuleRegistrar.instance().registerDecorator(new HUDDecoratorRsGateLogic(), "pr_rgate");

        //ModuleRegistrar.instance().registerBlockDecorator(new HUDDecoratorRsGateLogic(), BlockMultipart);
        //ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerRsGateLogic(), BlockMultipart);
        //ModuleRegistrar.instance().registerSyncedNBTKey("*", BlockMultipart);
    }

}
