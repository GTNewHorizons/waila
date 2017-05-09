package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.HashSet;
import java.util.List;

public class Message0x01TERequest extends SimpleChannelInboundHandler<Message0x01TERequest> implements IWailaMessage {

//    private static Field classToNameMap = null;
//
//    static {
//        try {
//            classToNameMap = TileEntity.class.getDeclaredField("classToNameMap");
//            classToNameMap.setAccessible(true);
//        } catch (Exception e) {
//
//            try {
//                classToNameMap = TileEntity.class.getDeclaredField("field_145853_j");
//                classToNameMap.setAccessible(true);
//            } catch (Exception f) {
//                throw new RuntimeException(f);
//            }
//
//        }
//    }

    public int dim;
    public int posX;
    public int posY;
    public int posZ;
    public HashSet<String> keys = new HashSet<String>();

//    private Map<Class, String> entityMap = Maps.newHashMap();

    public Message0x01TERequest() {
    }

    public Message0x01TERequest(final TileEntity ent, final HashSet<String> keys) {
        this.dim = ent.getWorld().provider.getDimension();
        this.posX = ent.getPos().getX();
        this.posY = ent.getPos().getY();
        this.posZ = ent.getPos().getZ();
        this.keys = keys;
    }

    @Override
    public void encodeInto(final ChannelHandlerContext ctx, final IWailaMessage msg, final ByteBuf target) throws Exception {
        target.writeInt(dim);
        target.writeInt(posX);
        target.writeInt(posY);
        target.writeInt(posZ);
        target.writeInt(this.keys.size());

        for (final String key : keys)
            WailaPacketHandler.INSTANCE.writeString(target, key);
    }

    @Override
    public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IWailaMessage rawmsg) {
        try {
            final Message0x01TERequest msg = (Message0x01TERequest) rawmsg;
            msg.dim = dat.readInt();
            msg.posX = dat.readInt();
            msg.posY = dat.readInt();
            msg.posZ = dat.readInt();

            final int nkeys = dat.readInt();

            for (int i = 0; i < nkeys; i++)
                this.keys.add(WailaPacketHandler.INSTANCE.readString(dat));

        } catch (final Exception e) {
            WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message0x01TERequest msg) throws Exception {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        final World world = DimensionManager.getWorld(msg.dim);
        final BlockPos pos = new BlockPos(msg.posX, msg.posY, msg.posZ);
        final TileEntity entity = world.getTileEntity(pos);
        final Block block = world.getBlockState(pos).getBlock();

        if (entity != null) {
            try {
                NBTTagCompound tag = new NBTTagCompound();
                final boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(block);
                final boolean hasNBTEnt = ModuleRegistrar.instance().hasNBTProviders(entity);

                if (hasNBTBlock || hasNBTEnt) {
                    tag.setInteger("x", msg.posX);
                    tag.setInteger("y", msg.posY);
                    tag.setInteger("z", msg.posZ);
//                    tag.setString("id", (String) ((HashMap) classToNameMap.get(null)).get(entity.getClass()));
                    tag.setString("id", entity.getClass().getName());

                    final EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;

                    for (final List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(block).values()) {
                        for (final IWailaDataProvider provider : providersList) {
                            try {
                                tag = provider.getNBTData(player, entity, tag, world, new BlockPos(msg.posX, msg.posY, msg.posZ));
                            } catch (final AbstractMethodError ame) {
                                tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                            } catch (final NoSuchMethodError nsm) {
                                tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                            }
                        }
                    }


                    for (final List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(entity).values()) {
                        for (final IWailaDataProvider provider : providersList) {
                            try {
                                tag = provider.getNBTData(player, entity, tag, world, new BlockPos(msg.posX, msg.posY, msg.posZ));
                            } catch (final AbstractMethodError ame) {
                                tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                            } catch (final NoSuchMethodError nsm) {
                                tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                            }
                        }
                    }

                } else {
                    entity.writeToNBT(tag);
                    tag = NBTUtil.createTag(tag, msg.keys);
                }

                tag.setInteger("WailaX", msg.posX);
                tag.setInteger("WailaY", msg.posY);
                tag.setInteger("WailaZ", msg.posZ);
//                tag.setString("WailaID", (String) ((HashMap) classToNameMap.get(null)).get(entity.getClass()));
                tag.setString("WailaID", entity.getClass().getName());

                WailaPacketHandler.INSTANCE.sendTo(new Message0x02TENBTData(tag), WailaPacketHandler.getPlayer(ctx));
                //ctx.writeAndFlush(new Message0x02TENBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } catch (final Throwable e) {
                WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
            }
        }


    }
}

