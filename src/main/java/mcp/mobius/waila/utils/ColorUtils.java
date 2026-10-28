package mcp.mobius.waila.utils;

import com.gtnewhorizon.gtnhlib.color.ColorResource;

public class ColorUtils {

    private static final ColorResource.Factory color = new ColorResource.Factory("waila");

    public static final ColorResource
    // spotless:off
            fuelFurnaceBarTop       = color.argb("fuelFurnaceBarTop",    "0xFFFF8800"),
            fuelFurnaceBarBottom    = color.argb("fuelFurnaceBarBottom", "0xFFFFCC00"),
            progressBarTop          = color.argb("progressBarTop",       "0xFFFF0000"),
            progressBarBottom       = color.argb("progressBarBottom",    "0xFF8B0000"),
            breakProgress           = color.argb("breakProgress",        "0xFFFFFFFF"),
            breakProgressFailure    = color.argb("breakProgressFailure", "0xFFFF4444");
    // spotless:on

}
