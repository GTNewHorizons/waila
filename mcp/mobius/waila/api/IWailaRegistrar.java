package mcp.mobius.waila.api;

public interface IWailaRegistrar {
	/* Add a config option in the section modname with displayed text configtext and access key keyname */
	public void addConfig(String modname, String keyname, String configtext);
	public void addConfigRemote(String modname, String keyname, String configtext);	
	public void addConfig(String modname, String keyname);
	public void addConfigRemote(String modname, String keyname);	
	
	/* Register a stack overrider for the given blockID */
	public void registerStackProvider(IWailaDataProvider dataProvider, Class block);	
	
	/* Same thing, but works on a class hierarchy instead */
	public void registerHeadProvider (IWailaDataProvider dataProvider, Class block);
	public void registerBodyProvider (IWailaDataProvider dataProvider, Class block);
	public void registerTailProvider (IWailaDataProvider dataProvider, Class block);	
	
	/* The block decorators */
	public void registerBlockDecorator (IWailaBlockDecorator decorator, Class block);
	
	public void registerDocTextFile  (String filename);
	
	public void registerShortDataProvider (IWailaSummaryProvider dataProvider, Class item);
}
