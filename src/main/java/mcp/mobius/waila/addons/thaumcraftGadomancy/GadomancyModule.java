package mcp.mobius.waila.addons.thaumcraftGadomancy;

import java.lang.reflect.Field;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.addons.thaumcraft.ThaumcraftModule;

public class GadomancyModule {

    public static Class<?> RegisteredEnchantments = null;
    public static Field RegisteredEnchantments_revealer = null;

    public static int revealerEffectId;

    public static void register() {
        try {
            RegisteredEnchantments = Class.forName("makeo.gadomancy.common.registration.RegisteredEnchantments");
            RegisteredEnchantments_revealer = RegisteredEnchantments.getDeclaredField("revealer");
            Object revealer = RegisteredEnchantments_revealer.get(null);

            revealerEffectId = ((Enchantment) revealer).effectId;

            ThaumcraftModule.isGoggles = stack -> ThaumcraftModule.IGoggles.isInstance(stack.getItem())
                    || EnchantmentHelper.getEnchantmentLevel(revealerEffectId, stack) > 0;
        } catch (ClassNotFoundException e) {
            Waila.log.log(Level.INFO, "[Thaumcraft Gadomancy] Gadomancy mod not found.");
            return;
        } catch (Exception e) {
            Waila.log.log(Level.WARN, "[Thaumcraft Gadomancy] Unhandled exception. {}", e);
            return;
        }
        Waila.log.log(Level.INFO, "Gadomancy mod found.");

    }

}
