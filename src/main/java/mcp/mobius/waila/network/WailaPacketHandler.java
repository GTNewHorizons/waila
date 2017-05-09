package mcp.mobius.waila.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.FMLEmbeddedChannel;
import net.minecraftforge.fml.common.network.FMLIndexedMessageToMessageCodec;
import net.minecraftforge.fml.common.network.FMLOutboundHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.EnumMap;

public enum WailaPacketHandler {
    INSTANCE;

    public EnumMap<Side, FMLEmbeddedChannel> channels;

    private WailaPacketHandler() {
        this.channels = NetworkRegistry.INSTANCE.newChannel("Waila", new WailaCodec());
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            addClientHandlers();
            addServerHandlers();
        } else {
            addServerHandlers();
        }

    }

    private void addClientHandlers() {
        final FMLEmbeddedChannel channel = this.channels.get(Side.CLIENT);
        final String codec = channel.findChannelHandlerNameForType(WailaCodec.class);

        channel.pipeline().addAfter(codec, "ServerPing", new Message0x00ServerPing());
        channel.pipeline().addAfter("ServerPing", "TENBTData", new Message0x02TENBTData());
        channel.pipeline().addAfter("TENBTData", "EntNBTData", new Message0x04EntNBTData());
    }

    private void addServerHandlers() {
        final FMLEmbeddedChannel channel = this.channels.get(Side.SERVER);
        final String codec = channel.findChannelHandlerNameForType(WailaCodec.class);

        channel.pipeline().addAfter(codec, "TERequest", new Message0x01TERequest());
        channel.pipeline().addAfter("TERequest", "EntRequest", new Message0x03EntRequest());
    }

    private class WailaCodec extends FMLIndexedMessageToMessageCodec<IWailaMessage> {
        public WailaCodec() {
            addDiscriminator(0, Message0x00ServerPing.class);
            addDiscriminator(1, Message0x01TERequest.class);
            addDiscriminator(2, Message0x02TENBTData.class);
            addDiscriminator(3, Message0x03EntRequest.class);
            addDiscriminator(4, Message0x04EntNBTData.class);
        }

        @Override
        public void encodeInto(final ChannelHandlerContext ctx, final IWailaMessage msg, final ByteBuf target) throws Exception {
            msg.encodeInto(ctx, msg, target);
        }

        @Override
        public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IWailaMessage msg) {
            msg.decodeInto(ctx, dat, msg);
        }
    }

    public void sendTo(final IWailaMessage message, final EntityPlayerMP player) {
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
        channels.get(Side.SERVER).attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
        channels.get(Side.SERVER).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void sendToServer(final IWailaMessage message) {
        channels.get(Side.CLIENT).attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
        channels.get(Side.CLIENT).writeAndFlush(message).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

    public void writeNBT(final ByteBuf target, final NBTTagCompound tag) throws IOException {
        if (tag == null)
            target.writeShort(-1);
        else {
            target.writeShort(0);
            CompressedStreamTools.writeCompressed(tag, new ByteBufOutputStream(target));
        }
    }

    public NBTTagCompound readNBT(final ByteBuf dat) throws IOException {
        final short short1 = dat.readShort();
        if (short1 < 0)
            return null;
        else {
            return CompressedStreamTools.readCompressed(new ByteBufInputStream(dat));
        }
    }

    public void writeString(final ByteBuf buffer, final String data) throws IOException {
        final byte[] abyte = data.getBytes(Charsets.UTF_8);
        buffer.writeShort(abyte.length);
        buffer.writeBytes(abyte);
    }

    public String readString(final ByteBuf buffer) throws IOException {
        final int j = buffer.readShort();
        final String s = new String(buffer.readBytes(j).array(), Charsets.UTF_8);
        return s;
    }

    public static EntityPlayerMP getPlayer(final ChannelHandlerContext ctx) {
        return ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
    }
}
