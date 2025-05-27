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

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import mcp.mobius.waila.api.impl.TipList;
import mcp.mobius.waila.cbcore.Layout;
import mcp.mobius.waila.client.KeyEvent;
import mcp.mobius.waila.utils.Constants;

public class WailaTickHandler {

    public Tooltip tooltip = null;
    public MetaDataProvider handler = new MetaDataProvider();
    private final Minecraft mc = Minecraft.getMinecraft();

    private static WailaTickHandler _instance;

    private WailaTickHandler() {}

    public static WailaTickHandler instance() {
        if (_instance == null) _instance = new WailaTickHandler();
        return _instance;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickRender(TickEvent.RenderTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            OverlayRenderer.renderOverlay();
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void tickClient(TickEvent.ClientTickEvent event) {

        if (!Keyboard.isKeyDown(KeyEvent.key_show.getKeyCode())
                && !ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_MODE, false)
                && ConfigHandler.instance()
                        .getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false)) {
            ConfigHandler.instance().setConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, false);
        }

        World world = mc.theWorld;
        EntityPlayer player = mc.thePlayer;
        if (world != null && player != null) {
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
                    currenttipBody = handler.handleBlockTextData(
                            targetStack,
                            world,
                            player,
                            target,
                            accessor,
                            currenttipBody,
                            Layout.BODY);
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
                    currenttipBody = handler.handleEntityTextData(
                            targetEnt,
                            world,
                            player,
                            target,
                            accessor,
                            currenttipBody,
                            Layout.BODY);
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
}
