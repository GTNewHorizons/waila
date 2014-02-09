package mcp.mobius.waila.api.impl;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import mcp.mobius.waila.api.IWailaDataAccessor;

public class DataAccessor implements IWailaDataAccessor {

	public World world;
	public EntityPlayer player;
	public MovingObjectPosition mop;
	public Vec3 renderingvec = null;
	public Block block;
	//public int blockID;
	public int metadata;
	public TileEntity entity;
	public NBTTagCompound remoteNbt = null;
	public long timeLastUpdate = System.currentTimeMillis();
	public double partialFrame;
	
	public static DataAccessor instance = new DataAccessor();

	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop) {
		this.set(_world, _player, _mop, null, 0.0);
	}
	
	public void set(World _world, EntityPlayer _player, MovingObjectPosition _mop, EntityLivingBase viewEntity, double partialTicks) {
		this.world    = _world;
		this.player   = _player;
		this.mop      = _mop;
		//this.blockID  = world.getBlockId(_mop.blockX, _mop.blockY, _mop.blockZ);
		//this.block    = Block.blocksList[this.blockID];
		this.block    = world.getBlock(_mop.blockX, _mop.blockY, _mop.blockZ);
		this.metadata = world.getBlockMetadata(_mop.blockX, _mop.blockY, _mop.blockZ);
		this.entity   = world.getTileEntity(_mop.blockX, _mop.blockY, _mop.blockZ);
		
		if (viewEntity != null){
			double px = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
			double py = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
			double pz = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;		
			this.renderingvec = Vec3.createVectorHelper(_mop.blockX - px, _mop.blockY - py, _mop.blockZ - pz);
			this.partialFrame = partialTicks;
		}
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
	public Block getBlock() {
		return this.block;
	}

	/*
	@Override
	public int getBlockID() {
		return this.blockID;
	}
	*/	
	
	@Override
	public int getMetadata() {
		return this.metadata;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	/*
	public void setBlockID(int val) {
		this.blockID = val;
	}
	*/	
	
	public void setMetadata(int val) {
		this.metadata = val;
	}	
	
	@Override
	public TileEntity getTileEntity() {
		return this.entity;
	}

	@Override
	public MovingObjectPosition getPosition() {
		return this.mop;
	}

	@Override
	public NBTTagCompound getNBTData() {
		if (this.entity == null) return null;
		
		if (this.isTagCorrect(this.remoteNbt))
			return this.remoteNbt;

		NBTTagCompound tag = new NBTTagCompound();
		this.entity.writeToNBT(tag);
		return tag;
	}

	@Override
	public int getNBTInteger(NBTTagCompound tag, String keyname){
		NBTBase subtag = tag.getTag(keyname);
		if (subtag instanceof NBTTagInt)
			return tag.getInteger(keyname);
		if (subtag instanceof NBTTagShort)
			return tag.getShort(keyname);
		if (subtag instanceof NBTTagByte)
			return tag.getByte(keyname);

		return 0;
	}
	
	private boolean isTagCorrect(NBTTagCompound tag){
		if (tag == null){
			this.timeLastUpdate = System.currentTimeMillis() - 250;
			return false;
		}
		
		int x = tag.getInteger("x");
		int y = tag.getInteger("y");
		int z = tag.getInteger("z");
		
		if (x == this.mop.blockX && y == this.mop.blockY && z == this.mop.blockZ)
			return true;
		else {
			this.timeLastUpdate = System.currentTimeMillis() - 250;			
			return false;
		}
	}

	@Override
	public Vec3 getRenderingPosition() {
		return this.renderingvec;
	}

	@Override
	public double getPartialFrame() {
		return this.partialFrame;
	}
	
	@Override
	public ForgeDirection getSide(){
		int side = this.getPosition().sideHit;
		switch (side){
		case 0:
			return ForgeDirection.DOWN;
		case 1:
			return ForgeDirection.UP;
		case 2:
			return ForgeDirection.EAST;
		case 3:
			return ForgeDirection.WEST;
		case 4:
			return ForgeDirection.NORTH;
		case 5:
			return ForgeDirection.SOUTH;
		}
		return ForgeDirection.UNKNOWN;
	}
	
}
