package mcp.mobius.waila.addons.exu;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class HUDHandlerDrum implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		
		if (!config.getConfig("extrautilities.fluidamount")) return currenttip;
		
		int amount = 0;
		
		NBTTagCompound subtag = accessor.getNBTData().getCompoundTag("tank");
		if (subtag.hasKey("Amount"))
			amount = accessor.getNBTInteger(subtag, "Amount");
		
		IFluidHandler handler = (IFluidHandler)accessor.getTileEntity();
		if (handler == null) return currenttip;
		
		FluidTankInfo[] tanks = handler.getTankInfo(EnumFacing.DOWN);
		if (tanks.length != 1) return currenttip;
		
		currenttip.add(String.format("%d / %d mB", amount, tanks[0].capacity));
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
}
