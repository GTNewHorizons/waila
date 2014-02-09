package mcp.mobius.waila.overlay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mcp.mobius.waila.Constants;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessor;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

public class RayTracing {

	private static RayTracing _instance;
	private RayTracing(){}	
	public static RayTracing instance(){
		if(_instance == null)
			_instance = new RayTracing();
		return _instance;
	}
	
	private MovingObjectPosition target      = null;
	private ItemStack            targetStack = null;
	private Minecraft            mc          = Minecraft.getMinecraft();
	
	public void fire(){
		if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY){
			this.target = mc.objectMouseOver;
			this.targetStack = null;
			return;
		}
		
		EntityLivingBase viewpoint = mc.renderViewEntity;
		if (viewpoint == null) return;
			
		this.target      = this.rayTrace(viewpoint, 4.0, 0);
		
		if (this.target == null) return;
	}
	
	public MovingObjectPosition getTarget(){ 
		return this.target;
	}
	
	public ItemStack getTargetStack(){
		if (this.target.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK)
			this.targetStack = this.getIdentifierStack();
		else
			this.targetStack = null;		
		
		return this.targetStack;
	}
	
    public MovingObjectPosition rayTrace(EntityLivingBase entity, double par1, float par3)
    {
        Vec3 vec3  = entity.getPosition(par3);
        Vec3 vec31 = entity.getLook(par3);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);
        
        if (ConfigHandler.instance().getConfig(Constants.CFG_WAILA_LIQUID))
        	return entity.worldObj.rayTraceBlocks(vec3, vec32, true);
        else
        	return entity.worldObj.rayTraceBlocks(vec3, vec32, false);
    }	
	
	public ItemStack getIdentifierStack(){
        World world = mc.theWorld;
        ArrayList<ItemStack> items = this.getIdentifierItems();
        if (items.isEmpty())
            return null;
        
        Collections.sort(items, new Comparator<ItemStack>()
        {
            @Override
            public int compare(ItemStack stack0, ItemStack stack1)
            {
                return stack1.getItemDamage() - stack0.getItemDamage();
            }
        });

        return items.get(0);		
	}
	
    public ArrayList<ItemStack> getIdentifierItems()
    {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        
    	if (this.target == null)
    		return items;
    	
    	EntityPlayer player = mc.thePlayer;
    	World world = mc.theWorld; 
    	
        int x = this.target.blockX;
        int y = this.target.blockY;
        int z = this.target.blockZ;
        Block mouseoverBlock = world.getBlock(x, y, z);
        if (mouseoverBlock == null) return items;

        if (ModuleRegistrar.instance().hasStackProviders(mouseoverBlock))
        	items.add(ModuleRegistrar.instance().getStackProviders(mouseoverBlock).get(0).getWailaStack(DataAccessor.instance, ConfigHandler.instance()));
        
        if(items.size() > 0)
            return items;

        if (world.getTileEntity(x, y, z) == null){
	        try{
	        	ItemStack block = new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z));
	        	items.add(block);
	        } catch(Exception e){
	        }
        }

        if(items.size() > 0)
            return items;

        try{
        ItemStack pick = mouseoverBlock.getPickBlock(this.target, world, x, y, z);
        if(pick != null)
            items.add(pick);
        }catch(Exception e){}
        
        if(items.size() > 0)
            return items;           

        try
        {
            //items.addAll(mouseoverBlock.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        	items.addAll(mouseoverBlock.getDrops(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        }
        catch(Exception e){}
        
        if(items.size() > 0)
            return items;         
        
        if(mouseoverBlock instanceof IShearable)
        {
            IShearable shearable = (IShearable)mouseoverBlock;
            if(shearable.isShearable(new ItemStack(Items.shears), world, x, y, z))
            {
                items.addAll(shearable.onSheared(new ItemStack(Items.shears), world, x, y, z, 0));
            }
        }
        
        if(items.size() == 0)
           items.add(0, new ItemStack(mouseoverBlock, 1, world.getBlockMetadata(x, y, z)));
        
        return items;
    }
    
}
