package mcp.mobius.waila.api.impl;

import au.com.bytecode.opencsv.CSVReader;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaBlockDecorator;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.IWailaFMPDecorator;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import mcp.mobius.waila.api.IWailaSummaryProvider;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ModuleRegistrar implements IWailaRegistrar {

    private static ModuleRegistrar instance = null;

    public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> headBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> bodyBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> tailBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> stackBlockProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaDataProvider>> NBTDataProviders = new LinkedHashMap<Class, ArrayList<IWailaDataProvider>>();

    public LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>> blockClassDecorators = new LinkedHashMap<Class, ArrayList<IWailaBlockDecorator>>();

    public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> headEntityProviders = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> bodyEntityProviders = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> tailEntityProviders = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> overrideEntityProviders = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();
    public LinkedHashMap<Class, ArrayList<IWailaEntityProvider>> NBTEntityProviders = new LinkedHashMap<Class, ArrayList<IWailaEntityProvider>>();

    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> headFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> bodyFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();
    public LinkedHashMap<String, ArrayList<IWailaFMPProvider>> tailFMPProviders = new LinkedHashMap<String, ArrayList<IWailaFMPProvider>>();

    public LinkedHashMap<String, ArrayList<IWailaFMPDecorator>> FMPClassDecorators = new LinkedHashMap<String, ArrayList<IWailaFMPDecorator>>();

    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>> wikiDescriptions = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, String>>>();
    public LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>> summaryProviders = new LinkedHashMap<Class, ArrayList<IWailaSummaryProvider>>();

    public LinkedHashMap<String, String> IMCRequests = new LinkedHashMap<String, String>();

    public LinkedHashMap<String, IWailaTooltipRenderer> tooltipRenderers = new LinkedHashMap<String, IWailaTooltipRenderer>();

    private ModuleRegistrar() {
        instance = this;
    }

    public static ModuleRegistrar instance() {
        if (ModuleRegistrar.instance == null)
            ModuleRegistrar.instance = new ModuleRegistrar();
        return ModuleRegistrar.instance;
    }

    /* IMC HANDLING */
    public void addIMCRequest(final String method, final String modname) {
        this.IMCRequests.put(method, modname);
    }

    /* CONFIG HANDLING */
    @Override
    public void addConfig(final String modname, final String key, final String configname) {
        this.addConfig(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfigRemote(final String modname, final String key, final String configname) {
        this.addConfigRemote(modname, key, configname, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfig(final String modname, final String key) {
        this.addConfig(modname, key, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfigRemote(final String modname, final String key) {
        this.addConfigRemote(modname, key, Constants.CFG_DEFAULT_VALUE);
    }

    @Override
    public void addConfig(final String modname, final String key, final String configname, final boolean defvalue) {
        ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG(configname), defvalue);
    }

    @Override
    public void addConfigRemote(final String modname, final String key, final String configname, final boolean defvalue) {
        ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG(configname), defvalue);
    }

    @Override
    public void addConfig(final String modname, final String key, final boolean defvalue) {
        ConfigHandler.instance().addConfig(modname, key, LangUtil.translateG("option." + key), defvalue);
    }

    @Override
    public void addConfigRemote(final String modname, final String key, final boolean defvalue) {
        ConfigHandler.instance().addConfigServer(modname, key, LangUtil.translateG("option." + key), defvalue);
    }


    /* REGISTRATION METHODS */
    @Override
    public void registerHeadProvider(final IWailaDataProvider dataProvider, final Class block) {
        this.registerProvider(dataProvider, block, this.headBlockProviders);
    }

    @Override
    public void registerBodyProvider(final IWailaDataProvider dataProvider, final Class block) {
        this.registerProvider(dataProvider, block, this.bodyBlockProviders);
    }

    @Override
    public void registerTailProvider(final IWailaDataProvider dataProvider, final Class block) {
        this.registerProvider(dataProvider, block, this.tailBlockProviders);
    }

    @Override
    public void registerStackProvider(final IWailaDataProvider dataProvider, final Class block) {
        this.registerProvider(dataProvider, block, this.stackBlockProviders);
    }

    @Override
    public void registerNBTProvider(final IWailaDataProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.NBTDataProviders);
    }

    @Override
    public void registerHeadProvider(final IWailaEntityProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.headEntityProviders);
    }

    @Override
    public void registerBodyProvider(final IWailaEntityProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.bodyEntityProviders);
    }

    @Override
    public void registerTailProvider(final IWailaEntityProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.tailEntityProviders);
    }

    @Override
    public void registerNBTProvider(final IWailaEntityProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.NBTEntityProviders);
    }

    @Override
    public void registerHeadProvider(final IWailaFMPProvider dataProvider, final String name) {
        this.registerProvider(dataProvider, name, this.headFMPProviders);
    }

    @Override
    public void registerBodyProvider(final IWailaFMPProvider dataProvider, final String name) {
        this.registerProvider(dataProvider, name, this.bodyFMPProviders);
    }

    @Override
    public void registerTailProvider(final IWailaFMPProvider dataProvider, final String name) {
        this.registerProvider(dataProvider, name, this.tailFMPProviders);
    }

    @Override
    public void registerOverrideEntityProvider(final IWailaEntityProvider dataProvider, final Class entity) {
        this.registerProvider(dataProvider, entity, this.overrideEntityProviders);
    }

	/*
    @Override
	public void registerShortDataProvider(IWailaSummaryProvider dataProvider, Class item) {
		this.registerProvider(dataProvider, item, this.summaryProviders);	
	}
	*/

    @Override
    public void registerDecorator(final IWailaBlockDecorator decorator, final Class block) {
        this.registerProvider(decorator, block, this.blockClassDecorators);
    }

    @Override
    public void registerDecorator(final IWailaFMPDecorator decorator, final String name) {
        this.registerProvider(decorator, name, this.FMPClassDecorators);
    }

    private <T, V> void registerProvider(final T dataProvider, final V clazz, final LinkedHashMap<V, ArrayList<T>> target) {
        if (clazz == null || dataProvider == null)
            throw new RuntimeException(String.format("Trying to register a null provider or null block ! Please check the stacktrace to know what was the original registration method. [Provider : %s, Target : %s]", dataProvider.getClass().getName(), clazz));

        if (!target.containsKey(clazz))
            target.put(clazz, new ArrayList<T>());

        final ArrayList<T> providers = target.get(clazz);
        if (providers.contains(dataProvider)) return;

        target.get(clazz).add(dataProvider);
    }

    @Override
    public void registerTooltipRenderer(final String name, final IWailaTooltipRenderer renderer) {
        if (!this.tooltipRenderers.containsKey(name))
            this.tooltipRenderers.put(name, renderer);
        else
            Waila.log.warn(String.format("A renderer named %s already exists (Class : %s). Skipping new renderer.", name, renderer.getClass().getName()));
    }

	/* PROVIDER GETTERS */

    public Map<Integer, List<IWailaDataProvider>> getHeadProviders(final Object block) {
        return getProviders(block, this.headBlockProviders);
    }

    public Map<Integer, List<IWailaDataProvider>> getBodyProviders(final Object block) {
        return getProviders(block, this.bodyBlockProviders);
    }

    public Map<Integer, List<IWailaDataProvider>> getTailProviders(final Object block) {
        return getProviders(block, this.tailBlockProviders);
    }

    public Map<Integer, List<IWailaDataProvider>> getStackProviders(final Object block) {
        return getProviders(block, this.stackBlockProviders);
    }

    public Map<Integer, List<IWailaDataProvider>> getNBTProviders(final Object block) {
        return getProviders(block, this.NBTDataProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getHeadEntityProviders(final Object entity) {
        return getProviders(entity, this.headEntityProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getBodyEntityProviders(final Object entity) {
        return getProviders(entity, this.bodyEntityProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getTailEntityProviders(final Object entity) {
        return getProviders(entity, this.tailEntityProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getOverrideEntityProviders(final Object entity) {
        return getProviders(entity, this.overrideEntityProviders);
    }

    public Map<Integer, List<IWailaEntityProvider>> getNBTEntityProviders(final Object entity) {
        return getProviders(entity, this.NBTEntityProviders);
    }

    public Map<Integer, List<IWailaFMPProvider>> getHeadFMPProviders(final String name) {
        return getProviders(name, this.headFMPProviders);
    }

    public Map<Integer, List<IWailaFMPProvider>> getBodyFMPProviders(final String name) {
        return getProviders(name, this.bodyFMPProviders);
    }

    public Map<Integer, List<IWailaFMPProvider>> getTailFMPProviders(final String name) {
        return getProviders(name, this.tailFMPProviders);
    }

    public Map<Integer, List<IWailaSummaryProvider>> getSummaryProvider(final Object item) {
        return getProviders(item, this.summaryProviders);
    }

    public Map<Integer, List<IWailaBlockDecorator>> getBlockDecorators(final Object block) {
        return getProviders(block, this.blockClassDecorators);
    }

    public Map<Integer, List<IWailaFMPDecorator>> getFMPDecorators(final String name) {
        return getProviders(name, this.FMPClassDecorators);
    }

    public IWailaTooltipRenderer getTooltipRenderer(final String name) {
        return this.tooltipRenderers.get(name);
    }

    private <T> Map<Integer, List<T>> getProviders(final Object obj, final LinkedHashMap<Class, ArrayList<T>> target) {
        final Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
        Integer index = 0;

        for (final Class clazz : target.keySet()) {
            if (clazz.isInstance(obj))
                returnList.put(index, target.get(clazz));

            index++;
        }

        return returnList;
    }

    private <T> Map<Integer, List<T>> getProviders(final String name, final LinkedHashMap<String, ArrayList<T>> target) {
        final Map<Integer, List<T>> returnList = new TreeMap<Integer, List<T>>();
        returnList.put(0, target.get(name));
        return returnList;
    }
	
	/* HAS METHODS */

    public boolean hasStackProviders(final Object block) {
        return hasProviders(block, this.stackBlockProviders);
    }

    public boolean hasHeadProviders(final Object block) {
        return hasProviders(block, this.headBlockProviders);
    }

    public boolean hasBodyProviders(final Object block) {
        return hasProviders(block, this.bodyBlockProviders);
    }

    public boolean hasTailProviders(final Object block) {
        return hasProviders(block, this.tailBlockProviders);
    }

    public boolean hasNBTProviders(final Object block) {
        return hasProviders(block, this.NBTDataProviders);
    }

    public boolean hasHeadEntityProviders(final Object entity) {
        return hasProviders(entity, this.headEntityProviders);
    }

    public boolean hasBodyEntityProviders(final Object entity) {
        return hasProviders(entity, this.bodyEntityProviders);
    }

    public boolean hasTailEntityProviders(final Object entity) {
        return hasProviders(entity, this.tailEntityProviders);
    }

    public boolean hasOverrideEntityProviders(final Object entity) {
        return hasProviders(entity, this.overrideEntityProviders);
    }

    public boolean hasNBTEntityProviders(final Object entity) {
        return hasProviders(entity, this.NBTEntityProviders);
    }

    public boolean hasHeadFMPProviders(final String name) {
        return hasProviders(name, this.headFMPProviders);
    }

    public boolean hasBodyFMPProviders(final String name) {
        return hasProviders(name, this.bodyFMPProviders);
    }

    public boolean hasTailFMPProviders(final String name) {
        return hasProviders(name, this.tailFMPProviders);
    }

    public boolean hasBlockDecorator(final Object block) {
        return hasProviders(block, this.blockClassDecorators);
    }

    public boolean hasFMPDecorator(final String name) {
        return hasProviders(name, this.FMPClassDecorators);
    }

    private <T> boolean hasProviders(final Object obj, final LinkedHashMap<Class, ArrayList<T>> target) {
        for (final Class clazz : target.keySet())
            if (clazz.isInstance(obj))
                return true;
        return false;
    }

    private <T> boolean hasProviders(final String name, final LinkedHashMap<String, ArrayList<T>> target) {
        return target.containsKey(name);
    }

    public boolean hasSummaryProvider(final Class item) {
        return this.summaryProviders.containsKey(item);
    }
	
	/* ----------------- */
	/*
	@Override
	public void registerDocTextFile(String filename) {
		List<String[]> docData  = null;
		int    nentries = 0;
		
		
		try{
			docData = this.readFileAsString(filename);
		} catch (IOException e){
			Waila.log.log(Level.WARN, String.format("Error while accessing file %s : %s", filename, e));
			return;
		}

		for (String[] ss : docData){
			String modid  = ss[0];
			String name   = ss[1];
			String meta   = ss[2];
			String desc   = ss[5].replace('$', '\n');
			if (!(desc.trim().equals(""))){
				if (!this.wikiDescriptions.containsKey(modid))
					this.wikiDescriptions.put(modid, new LinkedHashMap <String, LinkedHashMap <String, String>>());
				if (!this.wikiDescriptions.get(modid).containsKey(name))
					this.wikiDescriptions.get(modid).put(name, new LinkedHashMap<String, String>());
				
				this.wikiDescriptions.get(modid).get(name).put(meta, desc);
				System.out.printf("Registered %s %s %s\n", modid, name, meta);
				nentries += 1;			
			}
		}
		
		
//		String[] sections = docData.split(">>>>");
//		for (String s : sections){
//			s.trim();
//			if (!s.equals("")){
//				try{
//					String name   = s.split("\r?\n",2)[0].trim();
//					String desc   = s.split("\r?\n",2)[1].trim();
//					if (!this.wikiDescriptions.containsKey(modid))
//						this.wikiDescriptions.put(modid, new LinkedHashMap <String, String>());
//					this.wikiDescriptions.get(modid).put(name, desc);
//					nentries += 1;
//				}catch (Exception e){
//					System.out.printf("%s\n", e);
//				}
//			}
//		}
		
		Waila.log.log(Level.INFO, String.format("Registered %s entries from %s", nentries, filename));
	}	
	*/

    public boolean hasDocTextModID(final String modid) {
        return this.wikiDescriptions.containsKey(modid);
    }

    public boolean hasDocTextItem(final String modid, final String item) {
        if (this.hasDocTextModID(modid))
            return this.wikiDescriptions.get(modid).containsKey(item);
        return false;
    }

    public boolean hasDocTextMeta(final String modid, final String item, final String meta) {
        if (this.hasDocTextItem(modid, item))
            return this.wikiDescriptions.get(modid).get(item).containsKey(meta);
        return false;
    }

    public LinkedHashMap<String, String> getDocText(final String modid, final String name) {
        return this.wikiDescriptions.get(modid).get(name);
    }

    public String getDocText(final String modid, final String name, final String meta) {
        return this.wikiDescriptions.get(modid).get(name).get(meta);
    }

    public boolean hasDocTextSpecificMeta(final String modid, final String name, final String meta) {
        for (final String s : this.getDocText(modid, name).keySet())
            if (s.equals(meta))
                return true;
        return false;
    }

    public String getDoxTextWildcardMatch(final String modid, final String name) {
        final Set<String> keys = this.wikiDescriptions.get(modid).keySet();
        for (final String s : keys) {
            String regexed = s;
            regexed = regexed.replace(".", "\\.");
            regexed = regexed.replace("*", ".*");

            if (name.matches(s))
                return s;
        }
        return null;
    }

    private List<String[]> readFileAsString(final String filePath) throws IOException {
//		URL fileURL   = this.getClass().getResource(filePath);
//		File filedata = new File(fileURL);
//		Reader paramReader = new InputStreamReader(this.getClass().getResourceAsStream(filePath));

        final InputStream in = getClass().getResourceAsStream(filePath);
        final BufferedReader input = new BufferedReader(new InputStreamReader(in));
        final CSVReader reader = new CSVReader(input);

        final List<String[]> myEntries = reader.readAll();
        reader.close();

        return myEntries;
		/*
		StringBuffer fileData = new StringBuffer();
        //BufferedReader reader = new BufferedReader(paramReader);
		
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=input.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        input.close();
        return fileData.toString();
        */
    }
}
