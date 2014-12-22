package mcp.mobius.waila.addons.vanillamc;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.world.World;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.SpecialChars;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.cbcore.LangUtil;

public class HUDHandlerVanilla implements IWailaDataProvider {

	static Block mobSpawner    = Blocks.mob_spawner;
	static Block crops         = Blocks.wheat;
	static Block melonStem     = Blocks.melon_stem;
	static Block pumpkinStem   = Blocks.pumpkin_stem;
	static Block carrot        = Blocks.carrots;
	static Block potato        = Blocks.potatoes;
	static Block lever         = Blocks.lever;
	static Block repeaterIdle  = Blocks.unpowered_repeater;
	static Block repeaterActv  = Blocks.powered_repeater;
	static Block comparatorIdl = Blocks.unpowered_comparator;
	static Block comparatorAct = Blocks.powered_comparator;
	static Block redstone      = Blocks.redstone_wire;
	static Block jukebox       = Blocks.jukebox;
	static Block cocoa		   = Blocks.cocoa;
	static Block netherwart    = Blocks.nether_wart;
	static Block silverfish    = Blocks.monster_egg;
	static Block doubleplant   = Blocks.double_plant;
	static Block leave         = Blocks.leaves;
	static Block leave2        = Blocks.leaves2;	
	
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		Block block       = accessor.getBlock();
		
		if (block == silverfish && config.getConfig("vanilla.silverfish")){
			int metadata = accessor.getMetadata();
			switch(metadata){
			case 0:
				return new ItemStack(Blocks.stone);
			case 1:
				return new ItemStack(Blocks.cobblestone);
			case 2:
				return new ItemStack(Blocks.brick_block);
			default:
				return null;
			}
		}

		if (block == redstone){
			return new ItemStack(Items.redstone);
		}
		
		if (block == doubleplant && (accessor.getMetadata() & 8) != 0){
			
			int meta = accessor.getMetadata();// accessor.getWorld().getBlockState(accessor.getPosition());
			
			return new ItemStack(doubleplant, 0, meta);
		}
		
		if (block instanceof BlockRedstoneOre){
			return new ItemStack(Blocks.redstone_ore);
		}
		
		if (block == crops ){
			return new ItemStack(Items.wheat);
		}
		
		if ((block == leave || block == leave2) && (accessor.getMetadata() > 3)){
			return new ItemStack(block, 1, accessor.getMetadata() - 4);
		}
		
		return null;
		
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		Block block       = accessor.getBlock();
		
		/* Mob spawner handler */
		/*
		if (block == mobSpawner && accessor.getTileEntity() instanceof TileEntityMobSpawner && config.getConfig("vanilla.spawntype")){
			String name = currenttip.get(0);
			String mobname = ((TileEntityMobSpawner)accessor.getTileEntity()).func_145881_a().getEntityNameToSpawn();
			currenttip.set(0, String.format("%s (%s)", name, mobname));
		}
		*/

		if (block == redstone){
			String name = currenttip.get(0).replaceFirst(String.format(" %s", accessor.getMetadata()), "");
			currenttip.set(0, name);
		}		
		
		if (block == melonStem){
			currenttip.set(0, SpecialChars.WHITE + "Melon stem");
		}
	
		if (block == pumpkinStem){
			currenttip.set(0, SpecialChars.WHITE + "Pumpkin stem");
		}		
		
		return currenttip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block       = accessor.getBlock();
		/* Crops */
		boolean iscrop = crops.getClass().isInstance(block);	//Done to cover all inheriting mods
		if (config.getConfig("general.showcrop"))
			if (iscrop || block == melonStem || block == pumpkinStem || block == carrot || block == potato){
				float growthValue = (accessor.getMetadata() / 7.0F) * 100.0F;
				if (growthValue < 100.0)
					currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
				else
					currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
				return currenttip;
			}		

		if (block == cocoa && config.getConfig("general.showcrop")){
		
			float growthValue = ((accessor.getMetadata() >> 2) / 2.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (block == netherwart && config.getConfig("general.showcrop")){
			float growthValue = (accessor.getMetadata() / 3.0F) * 100.0F;
			if (growthValue < 100.0)
				currenttip.add(String.format("%s : %.0f %%", LangUtil.translateG("hud.msg.growth"), growthValue));
			else
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.growth"), LangUtil.translateG("hud.msg.mature")));
			return currenttip;
		}		
		
		if (config.getConfig("vanilla.leverstate"))
			if (block == lever){
				String redstoneOn = (accessor.getMetadata() & 8) == 0 ? LangUtil.translateG("hud.msg.off") : LangUtil.translateG("hud.msg.on");
				currenttip.add(String.format("%s : %s", LangUtil.translateG("hud.msg.state"), redstoneOn));
				return currenttip;				
			}				

		if (config.getConfig("vanilla.repeater"))
			if ((block == repeaterIdle) ||(block == repeaterActv)){
				int tick = (accessor.getMetadata() >> 2) + 1 ;
				if (tick == 1)
					currenttip.add(String.format("%s : %s tick", LangUtil.translateG("hud.msg.delay"), tick));
				else
					currenttip.add(String.format("%s : %s ticks", LangUtil.translateG("hud.msg.delay"), tick));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.comparator"))
			if ((block == comparatorIdl) ||(block == comparatorAct)){
				String mode = ((accessor.getMetadata() >> 2) & 1) == 0 ? LangUtil.translateG("hud.msg.comparator") : LangUtil.translateG("hud.msg.substractor");
				//int outputSignal = ((TileEntityComparator)entity).func_96100_a();
				currenttip.add("Mode : " + mode);
				//currenttip.add(String.format("Out : %s", outputSignal));
				return currenttip;				
			}		

		if (config.getConfig("vanilla.redstone"))
			if (block == redstone){
				currenttip.add(String.format("%s : %s" , LangUtil.translateG("hud.msg.power"), accessor.getMetadata()));
				return currenttip;				
			}	
		
		/*
		if (config.getConfig("vanilla.jukebox"))
			if (block == jukebox){
				NBTTagCompound tag = accessor.getNBTData();
				Item record = null;
				if (tag.hasKey("Record")){
					record = Item.itemsList[accessor.getNBTInteger(tag, "Record")];
					currenttip.add(((ItemRecord)record).getRecordTitle());					
				} else {
					currenttip.add(LangUtil.translateG("hud.msg.empty"));
				}
			}
		*/
		
		return currenttip;
	}	

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,	IWailaConfigHandler config) {
		return currenttip;
	}	
	
	@Override
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		if (te != null)
			te.writeToNBT(tag);
		return tag;
	}	
	
	public static void register(){
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.spawntype");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.leverstate");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.repeater");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.comparator");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.redstone");
		ModuleRegistrar.instance().addConfig("VanillaMC", "vanilla.silverfish");
		ModuleRegistrar.instance().addConfigRemote("VanillaMC", "vanilla.jukebox");
		
		IWailaDataProvider provider = new HUDHandlerVanilla();
		
		ModuleRegistrar.instance().registerStackProvider(provider, silverfish.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, redstone.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, doubleplant.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, BlockRedstoneOre.class);
		ModuleRegistrar.instance().registerStackProvider(provider, crops.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, leave.getClass());
		ModuleRegistrar.instance().registerStackProvider(provider, leave2.getClass());		
		
		//ModuleRegistrar.instance().registerStackProvider(provider, Block.class);
		ModuleRegistrar.instance().registerHeadProvider(provider, mobSpawner.getClass());
		ModuleRegistrar.instance().registerHeadProvider(provider, melonStem.getClass());
		ModuleRegistrar.instance().registerHeadProvider(provider, pumpkinStem.getClass());
		
		ModuleRegistrar.instance().registerBodyProvider(provider, crops.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, melonStem.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, pumpkinStem.getClass());
		//ModuleRegistrar.instance().registerBodyProvider(provider, carrot.getClass());
		//ModuleRegistrar.instance().registerBodyProvider(provider, potato.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, lever.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, repeaterIdle.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, repeaterActv.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, comparatorIdl.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, comparatorAct.getClass());
		ModuleRegistrar.instance().registerHeadProvider(provider, redstone.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, redstone.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, jukebox.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, cocoa.getClass());
		ModuleRegistrar.instance().registerBodyProvider(provider, netherwart.getClass());			
		
		ModuleRegistrar.instance().registerNBTProvider(provider, mobSpawner.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, crops.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, melonStem.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, pumpkinStem.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, carrot.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, potato.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, lever.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, repeaterIdle.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, repeaterActv.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, comparatorIdl.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, comparatorAct.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, redstone.getClass());		
		ModuleRegistrar.instance().registerNBTProvider(provider, jukebox.getClass());		
		ModuleRegistrar.instance().registerNBTProvider(provider, cocoa.getClass());
		ModuleRegistrar.instance().registerNBTProvider(provider, netherwart.getClass());		
		ModuleRegistrar.instance().registerNBTProvider(provider, silverfish.getClass());	
		
		//ModuleRegistrar.instance().registerDocTextFile("/mcp/mobius/waila/addons/vanillamc/WikiData.csv");
		
		//ExternalModulesHandler.instance().registerBlockDecorator(new HUDDecoratorVanilla(), repeaterIdle);
	}


	
}
