package mcp.mobius.waila.api.impl;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.utils.NBTUtil;

public class DataAccessorFMP implements IWailaFMPAccessor {

    public static final DataAccessorFMP instance = new DataAccessorFMP();

    private String id;
    private World world;
    private EntityPlayer player;
    private MovingObjectPosition mop;
    private Vec3 renderingvec;
    private TileEntity tileEntity;
    private NBTTagCompound partialNBT;
    private double partialFrame;

    private DataAccessorFMP() {}

    public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, NBTTagCompound _partialNBT,
            String id) {
        this.set(_world, _player, _mop, _partialNBT, id, null, 0.0);
    }

    public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, NBTTagCompound _partialNBT,
            String id, Vec3 renderVec, double partialTicks) {
        this.world = _world;
        this.player = _player;
        this.mop = _mop;
        this.tileEntity = world.getTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
        this.partialNBT = _partialNBT;
        this.id = id;
        this.renderingvec = renderVec;
        this.partialFrame = partialTicks;
    }

    public void reset() {
        id = null;
        world = null;
        player = null;
        mop = null;
        renderingvec = null;
        tileEntity = null;
        partialNBT = null;
        partialFrame = 0.0D;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public EntityPlayer getPlayer() {
        return this.player;
    }

    @Override
    public TileEntity getTileEntity() {
        return this.tileEntity;
    }

    @Override
    public MovingObjectPosition getPosition() {
        return this.mop;
    }

    @Override
    public NBTTagCompound getNBTData() {
        return this.partialNBT;
    }

    @Override
    public NBTTagCompound getFullNBTData() {
        if (this.tileEntity == null) return null;
        NBTTagCompound tag = new NBTTagCompound();
        this.tileEntity.writeToNBT(tag);
        return tag;
    }

    @Override
    public int getNBTInteger(NBTTagCompound tag, String keyname) {
        return NBTUtil.getNBTInteger(tag, keyname);
    }

    @Override
    public double getPartialFrame() {
        return this.partialFrame;
    }

    @Override
    public Vec3 getRenderingPosition() {
        return this.renderingvec;
    }

    @Override
    public String getID() {
        return this.id;
    }

}
