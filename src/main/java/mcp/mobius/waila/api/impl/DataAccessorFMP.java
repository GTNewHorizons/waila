package mcp.mobius.waila.api.impl;

import mcp.mobius.waila.api.IWailaFMPAccessor;
import mcp.mobius.waila.utils.NBTUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class DataAccessorFMP implements IWailaFMPAccessor {
	
	String id;
	World world;
	EntityPlayer player;
	RayTraceResult mop;
	Vec3d renderingvec = null;
	TileEntity entity;
	NBTTagCompound partialNBT = null;	
	NBTTagCompound remoteNBT  = null;
	long timeLastUpdate = System.currentTimeMillis();
	double partialFrame;
	
	public static DataAccessorFMP instance = new DataAccessorFMP();
	
	public void set(World _world, EntityPlayer _player, RayTraceResult _mop, NBTTagCompound _partialNBT, String id) {
		this.set(_world, _player, _mop, _partialNBT, id, null, 0.0);
	}
	
	public void set(World _world, EntityPlayer _player, RayTraceResult _mop, NBTTagCompound _partialNBT, String id, Vec3d renderVec, double partialTicks) {
		this.world      = _world;
		this.player     = _player;
		this.mop        = _mop;
		this.entity     = world.getTileEntity(_mop.getBlockPos());
		this.partialNBT = _partialNBT;
		this.id         = id;
		this.renderingvec = renderVec;
		this.partialFrame = partialTicks;
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
		return this.entity;
	}

	@Override
	public RayTraceResult getPosition() {
		return this.mop;
	}

	@Override
	public NBTTagCompound getNBTData() {
		return this.partialNBT;
	}

	@Override
	public NBTTagCompound getFullNBTData() {
		if (this.entity == null) return null;
		
		if (this.isTagCorrect(this.remoteNBT))
			return this.remoteNBT;

		NBTTagCompound tag = new NBTTagCompound();
		this.entity.writeToNBT(tag);
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
	public Vec3d getRenderingPosition() {
		return this.renderingvec;
	}

	@Override
	public String getID() {
		return this.id;
	}		
	
	private boolean isTagCorrect(NBTTagCompound tag){
		if (tag == null){
			this.timeLastUpdate = System.currentTimeMillis() - 250;
			return false;
		}
		
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		
		if (x == this.mop.getBlockPos().getX() && y == this.mop.getBlockPos().getY() && z == this.mop.getBlockPos().getZ())
			return true;
		else {
			this.timeLastUpdate = System.currentTimeMillis() - 250;			
			return false;
		}
	}
}
