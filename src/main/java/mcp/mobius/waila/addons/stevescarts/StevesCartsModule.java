package mcp.mobius.waila.addons.stevescarts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StevesCartsModule {

    public static Class<?> CargoItemSelection = null;

    public static Field ItemCartModule = null; // Items.modules
    public static Field ItemSelections = null; // TileEntityCargo.itemSelections

    public static Method GetSelectionName = null; // CargoItemSelection.getName

    public static void register() {}

}
