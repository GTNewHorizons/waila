package mcp.mobius.waila.addons.stevescarts;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class StevesCartsModule {

    public static Class TileEntityCartAssembler = null;
    public static Class TileEntityCargo = null;
    public static Class TileEntityLiquid = null;
    public static Class MinecartModular = null;
    public static Class StevesItems = null;
    public static Class CargoItemSelection = null;

    public static Field ItemCartComponent = null; // Items.component
    public static Field ItemCartModule = null; // Items.modules
    public static Field ItemSelections = null; // TileEntityCargo.itemSelections

    public static Method GetSelectionName = null; // CargoItemSelection.getName

    public static void register() {}

}
