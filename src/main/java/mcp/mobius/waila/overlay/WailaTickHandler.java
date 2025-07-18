package mcp.mobius.waila.overlay;

import static mcp.mobius.waila.api.SpecialChars.ITALIC;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.utils.Constants;

public class WailaTickHandler {

    private Tooltip tooltip = null;
    private final MetaDataProvider handler = new MetaDataProvider();

    @SubscribeEvent
    public void tickRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            OverlayRenderer.renderOverlay(tooltip);
        }
    }

    @SubscribeEvent
    public void tickClient(TickEvent.ClientTickEvent event) {

        if (event.phase == TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
        if (world == null || player == null) {
            this.tooltip = null;
            RayTracing.instance().clear();
            return;
        }

        RayTracing.instance().fire();
        MovingObjectPosition target = RayTracing.instance().getTarget();

        List<String> currenttip;
        List<String> currenttipHead;
        List<String> currenttipBody;
        List<String> currenttipTail;
        if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            DataAccessorCommon accessor = DataAccessorCommon.instance;
            accessor.set(world, player, target);
            ItemStack targetStack = RayTracing.instance().getTargetStack(); // Here we get either the proper stack
            // or the override

            if (targetStack != null) {
                currenttip = new TipList<String, String>();
                currenttipHead = new TipList<String, String>();
                currenttipBody = new TipList<String, String>();
                currenttipTail = new TipList<String, String>();

                currenttipHead = handler.handleBlockTextData(
                        targetStack,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipHead,
                        Layout.HEADER);
                currenttipBody = handler
                        .handleBlockTextData(targetStack, world, player, target, accessor, currenttipBody, Layout.BODY);
                currenttipTail = handler.handleBlockTextData(
                        targetStack,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipTail,
                        Layout.FOOTER);

                if (ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTBLOCK, false)
                        && !currenttipBody.isEmpty()
                        && !accessor.getPlayer().isSneaking()) {
                    currenttipBody.clear();
                    currenttipBody.add(ITALIC + "Press shift for more data");
                }

                currenttip.addAll(currenttipHead);
                currenttip.addAll(currenttipBody);
                currenttip.addAll(currenttipTail);

                this.tooltip = new Tooltip(currenttip, targetStack);
            }
        } else if (target != null && target.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            DataAccessorCommon accessor = DataAccessorCommon.instance;
            accessor.set(world, player, target);

            Entity targetEnt = RayTracing.instance().getTargetEntity(); // This need to be replaced by the override
            // check.

            if (targetEnt != null) {
                currenttip = new TipList<String, String>();
                currenttipHead = new TipList<String, String>();
                currenttipBody = new TipList<String, String>();
                currenttipTail = new TipList<String, String>();

                currenttipHead = handler.handleEntityTextData(
                        targetEnt,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipHead,
                        Layout.HEADER);
                currenttipBody = handler
                        .handleEntityTextData(targetEnt, world, player, target, accessor, currenttipBody, Layout.BODY);
                currenttipTail = handler.handleEntityTextData(
                        targetEnt,
                        world,
                        player,
                        target,
                        accessor,
                        currenttipTail,
                        Layout.FOOTER);

                if (ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHIFTENTS, false)
                        && !currenttipBody.isEmpty()
                        && !accessor.getPlayer().isSneaking()) {
                    currenttipBody.clear();
                    currenttipBody.add(ITALIC + "Press shift for more data");
                }

                currenttip.addAll(currenttipHead);
                currenttip.addAll(currenttipBody);
                currenttip.addAll(currenttipTail);

                this.tooltip = new Tooltip(currenttip, false);
            }
        }

    }
}
