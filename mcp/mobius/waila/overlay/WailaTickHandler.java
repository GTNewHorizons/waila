package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.Layout;
import mcp.mobius.waila.api.impl.MetaDataProvider;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockWithMetadata;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class WailaTickHandler implements ITickHandler {

    //public static LangUtil lang = LangUtil.loadLangDir("waila");
	
	private int ticks = 0;
	public ItemStack identifiedHighlight = new ItemStack(Block.dirt);
	public List<String> currenttip       = new ArrayList<String>(); 	
	public MetaDataProvider handler    = new MetaDataProvider();
	private Minecraft mc = Minecraft.getMinecraft();
	
	private static WailaTickHandler _instance;
	private WailaTickHandler(){}
	
	public static WailaTickHandler instance(){
		if(_instance == null)
			_instance = new WailaTickHandler();
		return _instance;
	}
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
	}

	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {
		if(type.contains(TickType.RENDER)){
			OverlayRenderer.instance().renderOverlay(); 
		}
		
		if(type.contains(TickType.CLIENT)){
			World world                 = mc.theWorld;
			EntityPlayer player         = mc.thePlayer;			
			if (world != null && player != null){
				RayTracing.instance().fire();
				MovingObjectPosition target = RayTracing.instance().getTarget();
				
				if (target != null){
					DataAccessor accessor = DataAccessor.instance;
					accessor.set(world, player, target);
					ItemStack targetStack = RayTracing.instance().getTargetStack();
					
					if (targetStack != null){
						this.currenttip.clear();
						
						//this.identifiedHighlight = handler.identifyHighlight(world, player, target);
						this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.HEADER);
						this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.BODY);
						this.currenttip      = handler.handleTextData(targetStack, world, player, target, accessor, currenttip, Layout.FOOTER);
					}
				}
			}
		}		
	}

    @Override
    public EnumSet<TickType> ticks() 
    {
        return EnumSet.of(TickType.RENDER, TickType.CLIENT);
    }

    @Override
    public String getLabel() 
    {
        return "Waila Tick Handler";
    }

}
