package mcp.mobius.waila.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;

public class AccessHelper {

    public static Field getDeclaredField(String classname, String fieldname) {

        try {
            Class<?> class_ = Class.forName(classname);
            Field field_ = class_.getDeclaredField(fieldname);
            field_.setAccessible(true);
            return field_;
        } catch (NoSuchFieldException e) {
            Waila.log.warn(String.format("== Field %s %s not found !\n", classname, fieldname));
            return null;
        } catch (SecurityException e) {
            Waila.log.warn(String.format("== Field %s %s security exception !\n", classname, fieldname));
            return null;
        } catch (ClassNotFoundException e) {
            Waila.log.warn(String.format("== Class %s not found !\n", classname));
            return null;
        }
    }

    public static Object getField(String classname, String fieldname, Object instance) {

        try {
            Class<?> class_ = Class.forName(classname);
            Field field_ = class_.getDeclaredField(fieldname);
            field_.setAccessible(true);
            return field_.get(instance);
        } catch (NoSuchFieldException e) {
            Waila.log.warn(String.format("== Field %s %s not found !\n", classname, fieldname));
            return null;
        } catch (SecurityException e) {
            Waila.log.warn(String.format("== Field %s %s security exception !\n", classname, fieldname));
            return null;
        } catch (ClassNotFoundException e) {
            Waila.log.warn(String.format("== Class %s not found !\n", classname));
            return null;
        } catch (IllegalArgumentException | IllegalAccessException e) {
            Waila.log.warn(String.format("== %s\n", e));
            return null;
        }
    }

    public static Object getFieldExcept(String classname, String fieldname, Object instance) throws Exception {
        Class<?> class_ = Class.forName(classname);
        Field field_ = class_.getDeclaredField(fieldname);
        field_.setAccessible(true);
        return field_.get(instance);
    }

    public static Block getBlock(String classname, String fieldname) {
        Field field_ = getDeclaredField(classname, fieldname);
        try {
            return (Block) field_.get(Block.class);
        } catch (Exception e) {
            System.out.printf("%s\n", e);
            Waila.log.warn(String.format("== ERROR GETTING BLOCK %s %s\n", classname, fieldname));
            return null;
        }
    }

    public static Item getItem(String classname, String fieldname) {
        Field field_ = getDeclaredField(classname, fieldname);
        try {
            return (Item) field_.get(Item.class);
        } catch (Exception e) {
            System.out.printf("%s\n", e);
            Waila.log.warn(String.format("== ERROR GETTING ITEM %s %s\n", classname, fieldname));
            return null;
        }
    }

    public static ArrayList<IRecipe> getCraftingRecipes(ItemStack stack) {
        ArrayList<IRecipe> recipes = new ArrayList<>();

        for (IRecipe recipe : CraftingManager.getInstance().getRecipeList()) {
            if (recipe != null && recipe.getRecipeOutput() != null) {
                if (recipe.getRecipeOutput().isItemEqual(stack)) recipes.add(recipe);
            }
        }

        return recipes;
    }

    public static void cleanCraftingRecipes(ItemStack stack) {
        for (IRecipe recipe : getCraftingRecipes(stack)) {
            CraftingManager.getInstance().getRecipeList().remove(recipe);
        }
    }

    public static NBTTagCompound getNBTData(IWailaDataProvider provider, TileEntity entity, NBTTagCompound tag,
            World world, int x, int y, int z) throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method getNBTData = provider.getClass().getMethod(
                "getNBTData",
                TileEntity.class,
                NBTTagCompound.class,
                World.class,
                int.class,
                int.class,
                int.class);
        return (NBTTagCompound) getNBTData.invoke(provider, entity, tag, world, x, y, z);
    }

    public static NBTTagCompound getNBTData(IWailaEntityProvider provider, Entity entity, NBTTagCompound tag)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        Method getNBTData = provider.getClass().getMethod("getNBTData", Entity.class, NBTTagCompound.class);
        return (NBTTagCompound) getNBTData.invoke(provider, entity, tag);
    }
}
