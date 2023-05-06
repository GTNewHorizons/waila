package mcp.mobius.waila.network;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import mcp.mobius.waila.Waila;

public class NetworkHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        Waila.log.info(String.format("Player %s connected. Sending ping", event.player));
        WailaPacketHandler.INSTANCE.sendTo(new Message0x00ServerPing(), (EntityPlayerMP) event.player);
    }
}
