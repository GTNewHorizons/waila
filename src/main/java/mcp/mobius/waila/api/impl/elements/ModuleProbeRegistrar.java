package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.elements.IProbeRegistrar;

import java.util.*;

public class ModuleProbeRegistrar implements IProbeRegistrar {

    private static ModuleProbeRegistrar instance = null;

    public LinkedHashMap<Class<?>, ArrayList<IProbeDataProvider>> probeProviders = new LinkedHashMap<>();

    public LinkedHashMap<Class, HashSet<String>> syncedNBTKeys = new LinkedHashMap<>();

    public LinkedHashMap<String, String> IMCRequests = new LinkedHashMap<>();

    private ModuleProbeRegistrar() {
        instance = this;
    }

    public static ModuleProbeRegistrar instance() {
        if (ModuleProbeRegistrar.instance == null) ModuleProbeRegistrar.instance = new ModuleProbeRegistrar();
        return ModuleProbeRegistrar.instance;
    }

    /* IMC HANDLING */
    public void addIMCRequest(String method, String modname) {
        this.IMCRequests.put(method, modname);
    }

    @Override
    public void registerProbeProvider(IProbeDataProvider dataProvider, Class<?> block) {
        this.registerProvider(dataProvider, block, this.probeProviders);
    }

    private void registerProvider(IProbeDataProvider dataProvider, Class<?> clazz, LinkedHashMap<Class<?>, ArrayList<IProbeDataProvider>> target) {
        if (clazz == null || dataProvider == null) throw new RuntimeException(
                "Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method."
        );

        if (!target.containsKey(clazz)) target.put(clazz, new ArrayList<>());

        ArrayList<IProbeDataProvider> providers = target.get(clazz);
        if (providers.contains(dataProvider)) return;

        target.get(clazz).add(dataProvider);
    }

    public boolean hasProviders(Object obj) {
        for (Class clazz : probeProviders.keySet()) if (clazz.isInstance(obj)) return true;
        return false;
    }

    public List<IProbeDataProvider> getProviders(Object obj) {
        List<IProbeDataProvider> returnList = new ArrayList<>();
        Integer index = 0;

        for (Class clazz : probeProviders.keySet()) {
            if (clazz.isInstance(obj)) returnList.addAll(index, probeProviders.get(clazz));

            index++;
        }

        return returnList;
    }
}
