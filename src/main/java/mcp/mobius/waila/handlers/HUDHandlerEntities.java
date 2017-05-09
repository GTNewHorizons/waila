package mcp.mobius.waila.handlers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaEntityAccessor;
import mcp.mobius.waila.api.IWailaEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;

import java.util.List;

import static mcp.mobius.waila.api.SpecialChars.BLUE;
import static mcp.mobius.waila.api.SpecialChars.GRAY;
import static mcp.mobius.waila.api.SpecialChars.ITALIC;
import static mcp.mobius.waila.api.SpecialChars.WHITE;
import static mcp.mobius.waila.api.SpecialChars.getRenderString;

public class HUDHandlerEntities implements IWailaEntityProvider {

    public static int nhearts = 20;
    public static float maxhpfortext = 40.0f;

    @Override
    public Entity getWailaOverride(final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        try {
            currenttip.add(WHITE + entity.getName());
        } catch (final Exception e) {
            currenttip.add(WHITE + "Unknown");
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        if (!config.getConfig("general.showhp")) return currenttip;

        if (entity instanceof EntityLivingBase) {
            final String hptip = "";

            nhearts = nhearts <= 0 ? 20 : nhearts;

            final float health = ((EntityLivingBase) entity).getHealth() / 2.0f;
            final float maxhp = ((EntityLivingBase) entity).getMaxHealth() / 2.0f;

            if (((EntityLivingBase) entity).getMaxHealth() > maxhpfortext)
                currenttip.add(String.format("HP : " + WHITE + "%.0f" + GRAY + " / " + WHITE + "%.0f", ((EntityLivingBase) entity).getHealth(), ((EntityLivingBase) entity).getMaxHealth()));

            else {
                currenttip.add(getRenderString("waila.health", String.valueOf(nhearts), String.valueOf(health), String.valueOf(maxhp)));
            }
        }

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(final Entity entity, final List<String> currenttip, final IWailaEntityAccessor accessor, final IWailaConfigHandler config) {
        try {
            currenttip.add(BLUE + ITALIC + getEntityMod(entity));
        } catch (final Exception e) {
            currenttip.add(BLUE + ITALIC + "Unknown");
        }
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(final EntityPlayerMP player, final Entity te, final NBTTagCompound tag, final World world) {
        return tag;
    }

    private static String getEntityMod(final Entity entity) {
        String modName = "";
        try {
            final EntityRegistration er = EntityRegistry.instance().lookupModSpawn(entity.getClass(), true);
            final ModContainer modC = er.getContainer();
            modName = modC.getName();
        } catch (final NullPointerException e) {
            modName = "Minecraft";
        }
        return modName;
    }
}