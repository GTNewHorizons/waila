package mcp.mobius.waila.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.HashSet;
import java.util.List;

public class Message0x03EntRequest extends SimpleChannelInboundHandler<Message0x03EntRequest> implements IWailaMessage {

    public int dim;
    public int id;
    public HashSet<String> keys = new HashSet<String>();

    public Message0x03EntRequest() {
    }

    public Message0x03EntRequest(final Entity ent, final HashSet<String> keys) {
        this.dim = ent.world.provider.getDimension();
        this.id = ent.getEntityId();
        this.keys = keys;
    }

    @Override
    public void encodeInto(final ChannelHandlerContext ctx, final IWailaMessage msg, final ByteBuf target) throws Exception {
        target.writeInt(dim);
        target.writeInt(id);
        target.writeInt(this.keys.size());

        for (final String key : keys)
            WailaPacketHandler.INSTANCE.writeString(target, key);
    }

    @Override
    public void decodeInto(final ChannelHandlerContext ctx, final ByteBuf dat, final IWailaMessage rawmsg) {
        try {
            final Message0x03EntRequest msg = (Message0x03EntRequest) rawmsg;
            msg.dim = dat.readInt();
            msg.id = dat.readInt();


            final int nkeys = dat.readInt();

            for (int i = 0; i < nkeys; i++)
                this.keys.add(WailaPacketHandler.INSTANCE.readString(dat));

        } catch (final Exception e) {
            WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
        }
    }

    @Override
    protected void channelRead0(final ChannelHandlerContext ctx, final Message0x03EntRequest msg) throws Exception {
        final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        final World world = DimensionManager.getWorld(msg.dim);
        final Entity entity = world.getEntityByID(msg.id);

        if (entity != null) {
            try {
                NBTTagCompound tag = new NBTTagCompound();

                final EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER).get()).playerEntity;

                if (ModuleRegistrar.instance().hasNBTEntityProviders(entity)) {
                    for (final List<IWailaEntityProvider> providersList : ModuleRegistrar.instance().getNBTEntityProviders(entity).values()) {
                        for (final IWailaEntityProvider provider : providersList) {
                            try {
                                tag = provider.getNBTData(player, entity, tag, world);
                            } catch (final AbstractMethodError ame) {
                                tag = AccessHelper.getNBTData(provider, entity, tag);
                            }
                        }
                    }

                } else {
                    entity.writeToNBT(tag);
                    tag = NBTUtil.createTag(tag, msg.keys);
                }

                tag.setInteger("WailaEntityID", entity.getEntityId());

                WailaPacketHandler.INSTANCE.sendTo(new Message0x04EntNBTData(tag), WailaPacketHandler.getPlayer(ctx));
                //ctx.writeAndFlush(new Message0x04EntNBTData(tag)).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            } catch (final Throwable e) {
                WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
            }
        }
    }

}
