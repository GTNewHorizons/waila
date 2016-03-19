package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.nbt.NBTTagCompound;


public class Message0x02TENBTData extends SimpleChannelInboundHandler<Message0x02TENBTData> implements IWailaMessage {
	
	NBTTagCompound tag;
	
	public Message0x02TENBTData(){}	
	
	public Message0x02TENBTData(NBTTagCompound tag){
		this.tag = tag;
	}	
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		WailaPacketHandler.INSTANCE.writeNBT(target, tag);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage msg) {
		try{
			this.tag = WailaPacketHandler.INSTANCE.readNBT(dat);
		} catch (Exception e){
    		WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x02TENBTData msg) throws Exception {
		DataAccessorCommon.instance.setNBTData(msg.tag);
	}
}

