package mcp.mobius.waila.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.elements.IProbeDataProvider;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.api.impl.elements.ModuleProbeRegistrar;
import mcp.mobius.waila.utils.AccessHelper;
import mcp.mobius.waila.utils.NBTUtil;
import mcp.mobius.waila.utils.WailaExceptionHandler;

public class Message0x01TERequest extends SimpleChannelInboundHandler<Message0x01TERequest> implements IWailaMessage {

    private static final Field classToNameMap;

    static {
        try {
            classToNameMap = ReflectionHelper.findField(TileEntity.class, "classToNameMap", "field_145853_j");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public int dim;
    public int posX;
    public int posY;
    public int posZ;
    public HashSet<String> keys = new HashSet<>();
    public boolean useNewAPI;

    public Message0x01TERequest() {}

    public Message0x01TERequest(TileEntity ent, HashSet<String> keys, boolean useNewAPI) {
        this.dim = ent.getWorldObj().provider.dimensionId;
        this.posX = ent.xCoord;
        this.posY = ent.yCoord;
        this.posZ = ent.zCoord;
        this.keys = keys;
        this.useNewAPI = useNewAPI;
    }

    @Override
    public void encodeInto(ChannelHandlerContext ctx, IWailaMessage msg, ByteBuf target) throws Exception {
        target.writeInt(dim);
        target.writeInt(posX);
        target.writeInt(posY);
        target.writeInt(posZ);
        target.writeInt(this.keys.size());

        for (String key : keys) WailaPacketHandler.INSTANCE.writeString(target, key);

        target.writeBoolean(useNewAPI);
    }

    @Override
    public void decodeInto(ChannelHandlerContext ctx, ByteBuf dat, IWailaMessage rawmsg) {
        try {
            Message0x01TERequest msg = (Message0x01TERequest) rawmsg;
            msg.dim = dat.readInt();
            msg.posX = dat.readInt();
            msg.posY = dat.readInt();
            msg.posZ = dat.readInt();

            int nkeys = dat.readInt();

            for (int i = 0; i < nkeys; i++) this.keys.add(WailaPacketHandler.INSTANCE.readString(dat));

            msg.useNewAPI = dat.readBoolean();

        } catch (Exception e) {
            WailaExceptionHandler.handleErr(e, this.getClass().toString(), null);
        }
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message0x01TERequest msg) throws Exception {
        World world = DimensionManager.getWorld(msg.dim);
        if (world == null) return;
        TileEntity entity = world.getTileEntity(msg.posX, msg.posY, msg.posZ);
        Block block = world.getBlock(msg.posX, msg.posY, msg.posZ);
        if (entity == null) return;
        try {
            NBTTagCompound tag = new NBTTagCompound();
            boolean hasProbeBlock = ModuleProbeRegistrar.instance().hasProviders(block);
            boolean hasProbeEntity = ModuleProbeRegistrar.instance().hasProviders(entity);

            boolean hasNBTBlock = ModuleRegistrar.instance().hasNBTProviders(block);
            boolean hasNBTEnt = ModuleRegistrar.instance().hasNBTProviders(entity);

            if ((hasProbeBlock || hasProbeEntity) && msg.useNewAPI) {
                tag.setInteger("x", msg.posX);
                tag.setInteger("y", msg.posY);
                tag.setInteger("z", msg.posZ);
                tag.setString("id", (String) ((HashMap) classToNameMap.get(null)).get(entity.getClass()));

                EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER)
                        .get()).playerEntity;

                for (IProbeDataProvider provider : ModuleProbeRegistrar.instance().getProviders(block)) {
                    try {
                        tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                    } catch (AbstractMethodError | NoSuchMethodError ame) {
                        // tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                    }
                }

                for (IProbeDataProvider provider : ModuleProbeRegistrar.instance().getProviders(entity)) {
                    try {
                        tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                    } catch (AbstractMethodError | NoSuchMethodError ame) {
                        // tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                    }
                }

            }

            // We will try to use old tooltips regardless of the mode
            if ((hasNBTBlock || hasNBTEnt)) {
                tag.setInteger("x", msg.posX);
                tag.setInteger("y", msg.posY);
                tag.setInteger("z", msg.posZ);
                tag.setString("id", (String) ((HashMap) classToNameMap.get(null)).get(entity.getClass()));

                EntityPlayerMP player = ((NetHandlerPlayServer) ctx.channel().attr(NetworkRegistry.NET_HANDLER)
                        .get()).playerEntity;

                for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(block)
                        .values()) {
                    for (IWailaDataProvider provider : providersList) {
                        try {
                            tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                        } catch (AbstractMethodError | NoSuchMethodError ame) {
                            tag = AccessHelper.getNBTData(provider, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                        }
                    }
                }

                for (List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getNBTProviders(entity)
                        .values()) {
                    for (IWailaDataProvider provider : providersList) {
                        try {
                            tag = provider.getNBTData(player, entity, tag, world, msg.posX, msg.posY, msg.posZ);
                        } catch (AbstractMethodError | NoSuchMethodError ame) {
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
            tag.setString("WailaID", (String) ((HashMap) classToNameMap.get(null)).get(entity.getClass()));

            WailaPacketHandler.INSTANCE.sendTo(new Message0x02TENBTData(tag), WailaPacketHandler.getPlayer(ctx));
        } catch (Throwable e) {
            WailaExceptionHandler.handleErr(e, entity.getClass().toString(), null);
        }

    }
}
