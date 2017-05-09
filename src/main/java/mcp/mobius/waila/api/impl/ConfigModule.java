package mcp.mobius.waila.api.impl;

import java.util.HashMap;

public class ConfigModule {

    String modName;
    HashMap<String, String> options;

    public ConfigModule(final String _modName) {
        this.modName = _modName;
        this.options = new HashMap<String, String>();
    }

    public ConfigModule(final String _modName, final HashMap<String, String> _options) {
        this.modName = _modName;
        this.options = _options;
    }

    public void addOption(final String key, final String name) {
        this.options.put(key, name);
    }

}
