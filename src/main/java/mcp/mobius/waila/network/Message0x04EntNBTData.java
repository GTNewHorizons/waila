package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.nbt.NBTTagCompound;

public class Message0x04EntNBTData extends SimpleChannelInboundHandler<Message0x04EntNBTData> implements IWailaMessage {

    NBTTagCompound tag;

    public Message0x04EntNBTData() {
    }

    public Message0x04EntNBTData(final NBTTagCompound tag) {
        this.tag = tag;
    }

    @Override
    public void encodeInto(final ChannelHandlerContext ctx, final IWailaMessage msg, final ByteBuf target) throws Exception {
        WailaPacketHandler.INSTANCE.writeNBT(target, tag);
    }

    @Override
    public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IWailaMessage msg) {
        try {
            this.tag = WailaPacketHandler.INSTANCE.readNBT(dat);
        } catch (final Exception e) {
            WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message0x04EntNBTData msg) throws Exception {
        DataAccessorCommon.instance.setNBTData(msg.tag);
    }

}
