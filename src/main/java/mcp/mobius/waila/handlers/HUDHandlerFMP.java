package mcp.mobius.waila.handlers;

import java.util.List;

import org.apache.logging.log4j.Level;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaFMPProvider;
import mcp.mobius.waila.api.impl.DataAccessorFMP;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

public class HUDHandlerFMP implements IWailaDataProvider {

	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++){
			NBTTagCompound subtag = list.getCompoundTagAt(i);
			String id = subtag.getString("id");

			if (ModuleRegistrar.instance().hasHeadFMPProviders(id)){
				DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);
				
				for (List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getHeadFMPProviders(id).values()){
					for (IWailaFMPProvider provider : providersList)
						currenttip = provider.getWailaHead(itemStack, currenttip, DataAccessorFMP.instance, config);
				}
			}
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++){
			NBTTagCompound subtag = list.getCompoundTagAt(i);
			String id = subtag.getString("id");

			if (ModuleRegistrar.instance().hasBodyFMPProviders(id)){
				DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);
				
				for (List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getBodyFMPProviders(id).values()){
					for (IWailaFMPProvider provider : providersList)
						currenttip = provider.getWailaBody(itemStack, currenttip, DataAccessorFMP.instance, config);
				}
			}
		}
		
		return currenttip;
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		NBTTagList list = accessor.getNBTData().getTagList("parts", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++){
			NBTTagCompound subtag = list.getCompoundTagAt(i);
			String id = subtag.getString("id");

			if (ModuleRegistrar.instance().hasTailFMPProviders(id)){
				DataAccessorFMP.instance.set(accessor.getWorld(), accessor.getPlayer(), accessor.getMOP(), subtag, id);
				
				for (List<IWailaFMPProvider> providersList : ModuleRegistrar.instance().getTailFMPProviders(id).values()){
					for (IWailaFMPProvider provider : providersList)
						currenttip = provider.getWailaTail(itemStack, currenttip, DataAccessorFMP.instance, config);
				}
			}
		}
		
		return currenttip;
	}

	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}
	
	public static void register(){
		Class BlockMultipart = null;
		try{
			BlockMultipart = Class.forName("codechicken.multipart.BlockMultipart");
		} catch (ClassNotFoundException e){
			Waila.log.log(Level.WARN, "[FMP] Class not found. " + e);
			return;
		} catch (Exception e){
			Waila.log.log(Level.WARN, "[FMP] Unhandled exception." + e);
			return;			
		}

		ModuleRegistrar.instance().registerHeadProvider(new HUDHandlerFMP(), BlockMultipart);		
		ModuleRegistrar.instance().registerBodyProvider(new HUDHandlerFMP(), BlockMultipart);		
		ModuleRegistrar.instance().registerTailProvider(new HUDHandlerFMP(), BlockMultipart);
		ModuleRegistrar.instance().registerNBTProvider (new HUDHandlerFMP(), BlockMultipart);
		
		Waila.log.log(Level.INFO, "Forge Multipart found and dedicated handler registered");
		
	}
}
