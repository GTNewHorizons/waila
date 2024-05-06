package mcp.mobius.waila.api.elements;

public interface IProbeRegistrar {

    void registerProbeProvider(IProbeDataProvider dataProvider, Class<?> block);
}
