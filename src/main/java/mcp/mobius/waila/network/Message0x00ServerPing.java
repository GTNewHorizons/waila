package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.Constants;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraftforge.common.config.ConfigCategory;

import java.util.HashMap;

public class Message0x00ServerPing extends SimpleChannelInboundHandler<Message0x00ServerPing> implements IWailaMessage {

    HashMap<String, Boolean> forcedKeys = new HashMap<String, Boolean>();

    public Message0x00ServerPing() {
        final ConfigCategory serverForcingCfg = ConfigHandler.instance().config.getCategory(Constants.CATEGORY_SERVER);

        for (final String key : serverForcingCfg.keySet()) {
            if (serverForcingCfg.get(key).getBoolean(false)) {
                forcedKeys.put(key, ConfigHandler.instance().getConfig(key));
            }
        }
    }

    @Override
    public void encodeInto(final ChannelHandlerContext ctx, final IWailaMessage msg, final ByteBuf target) throws Exception {
        target.writeShort(this.forcedKeys.keySet().size());
        for (final String key : forcedKeys.keySet()) {
            WailaPacketHandler.INSTANCE.writeString(target, key);
            target.writeBoolean(this.forcedKeys.get(key));
        }
    }

    @Override
    public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IWailaMessage rawmsg) {
        try {
            final Message0x00ServerPing msg = (Message0x00ServerPing) rawmsg;
            final int nkeys = dat.readShort();
            for (int i = 0; i < nkeys; i++) {
                this.forcedKeys.put(WailaPacketHandler.INSTANCE.readString(dat), dat.readBoolean());
            }
        } catch (final Exception e) {
            WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
        }

    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message0x00ServerPing msg) throws Exception {
        Waila.log.info("Received server authentication msg. Remote sync will be activated");
        Waila.instance.serverPresent = true;

        for (final String key : msg.forcedKeys.keySet())
            Waila.log.info(String.format("Received forced key config %s : %s", key, msg.forcedKeys.get(key)));

        ConfigHandler.instance().forcedConfigs = msg.forcedKeys;
    }
}

