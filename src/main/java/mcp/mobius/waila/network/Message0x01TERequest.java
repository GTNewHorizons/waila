package mcp.mobius.waila.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Message0x01TERequest extends SimpleChannelInboundHandler<Message0x01TERequest> implements IWailaMessage {
	
	private static Field classToNameMap = null;
	
	static{
		try{
			classToNameMap = TileEntity.class.getDeclaredField("classToNameMap");
			classToNameMap.setAccessible(true);
		} catch (Exception e){
			
			try{
				classToNameMap = TileEntity.class.getDeclaredField("field_145853_j");
				classToNameMap.setAccessible(true);
			} catch (Exception f){
				throw new RuntimeException(f);
			}
			
		}
	}
	
	public int dim;
	public int posX;
	public int posY;
	public int posZ;	
	
	public Message0x01TERequest(){}	
	
	public Message0x01TERequest(TileEntity ent){
		this.dim  = ent.getWorld().provider.getDimensionId();
		this.posX = ent.getPos().getX();
		this.posY = ent.getPos().getY();
		this.posZ = ent.getPos().getZ();
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
		target.writeInt(dim);
		target.writeInt(posX);
		target.writeInt(posY);
		target.writeInt(posZ);
	}

	@Override
	public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
		try{
			Message0x01TERequest msg = (Message0x01TERequest)rawmsg;
			msg.dim  = dat.readInt();
			msg.posX = dat.readInt();
			msg.posY = dat.readInt();
			msg.posZ = dat.readInt();
		
		}catch (Exception e){
			WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
		}		
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message0x01TERequest msg) throws Exception {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        World           world  = DimensionManager.getWorld(msg.dim);
        TileEntity      entity = world.getTileEntity(new BlockPos(msg.posX, msg.posY, msg.posZ));
        Block           block  = world.getBlockState(new BlockPos(msg.posX, msg.posY, msg.posZ)).getBlock();
        
        if (entity != null){
        	try{
        		NBTTagCompound tag  = new NBTTagCompound();
        		boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(block);
        		boolean hasNBTEnt   = ModuleRegistrar.instance().hasNBTProviders(entity);

        		if (hasNBTBlock || hasNBTEnt){
        			tag.setInteger("x", msg.posX);
            		tag.setInteger("y", msg.posY);
            		tag.setInteger("z", msg.posZ);
            		tag.setString ("id", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));
            		
            		EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;
            		
        			for (IWailaDataProvider provider : ModuleRegistrar.instance().getNBTProviders(block)){
        				try{
        					tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
        				} catch (AbstractMethodError ame){
        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
        				}
        			}
        			
        			for (IWailaDataProvider provider : ModuleRegistrar.instance().getNBTProviders(entity)){
        				try{        				
        					tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
        				} catch (AbstractMethodError ame){
        					tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
        				}       					
        			}
        			
        			tag.setInteger("WailaX", msg.posX);
            		tag.setInteger("WailaY", msg.posY);
            		tag.setInteger("WailaZ", msg.posZ);
            		tag.setString ("WailaID", (String)((HashMap)classToNameMap.get(null)).get(entity.getClass()));        		
            		
        			ctx.writeAndFlush(new Message0x02TENBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);        			
        		}
        	}catch(Throwable e){
        		e.printStackTrace();
        		WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        	}
        }
		
		
	}
}

