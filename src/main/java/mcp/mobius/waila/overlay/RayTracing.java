package mcp.mobius.waila.overlay;

import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaEntityProvider;
import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.api.impl.DataAccessorCommon;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RayTracing {

    private static RayTracing _instance;

    private RayTracing() {
    }

    public static RayTracing instance() {
        if (_instance == null)
            _instance = new RayTracing();
        return _instance;
    }

    private RayTraceResult target = null;
    private ItemStack targetStack = null;
    private Entity targetEntity = null;
    private Minecraft mc = Minecraft.getMinecraft();

    public void fire() {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY) {
            this.target = mc.objectMouseOver;
            this.targetStack = null;
            return;
        }

        final Entity viewpoint = mc.getRenderViewEntity();
        if (viewpoint == null) return;

        this.target = this.rayTrace(viewpoint, mc.playerController.getBlockReachDistance(), 0);

        if (this.target == null) return;
    }

    public RayTraceResult getTarget() {
        return this.target;
    }

    public ItemStack getTargetStack() {
        if (this.target.typeOfHit == RayTraceResult.Type.BLOCK)
            this.targetStack = this.getIdentifierStack();
        else
            this.targetStack = null;

        return this.targetStack;
    }

    public Entity getTargetEntity() {
        if (this.target.typeOfHit == RayTraceResult.Type.ENTITY)
            this.targetEntity = this.getIdentifierEntity();
        else
            this.targetEntity = null;

        return this.targetEntity;
    }

    public RayTraceResult rayTrace(final Entity entity, final double par1, final float par3) {
        final Vec3d vec3 = entity.getPositionEyes(par3);
        final Vec3d vec31 = entity.getLook(par3);
        final Vec3d vec32 = vec3.addVector(vec31.xCoord * par1, vec31.yCoord * par1, vec31.zCoord * par1);

        //if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
        if (ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_LIQUID, true))
            return entity.world.rayTraceBlocks(vec3, vec32, true);
        else
            return entity.world.rayTraceBlocks(vec3, vec32, false);
    }

    public ItemStack getIdentifierStack() {
        final World world = mc.world;
        final ArrayList<ItemStack> items = this.getIdentifierItems();

        if (items.isEmpty())
            return null;

        Collections.sort(items, new Comparator<ItemStack>() {
            @Override
            public int compare(final ItemStack stack0, final ItemStack stack1) {
                return stack1.getItemDamage() - stack0.getItemDamage();
            }
        });

        return items.get(0);
    }

    public Entity getIdentifierEntity() {
        final ArrayList<Entity> ents = new ArrayList<Entity>();

        if (this.target == null)
            return null;

        if (ModuleRegistrar.instance().hasOverrideEntityProviders(this.target.entityHit)) {
            for (final List<IWailaEntityProvider> listProviders : ModuleRegistrar.instance().getOverrideEntityProviders(this.target.entityHit).values()) {
                for (final IWailaEntityProvider provider : listProviders) {
                    ents.add(provider.getWailaOverride(DataAccessorCommon.instance, ConfigHandler.instance()));
                }
            }
        }

        if (ents.size() > 0)
            return ents.get(0);
        else
            return this.target.entityHit;
    }

    public ArrayList<ItemStack> getIdentifierItems() {
        final ArrayList<ItemStack> items = new ArrayList<ItemStack>();

        if (this.target == null)
            return items;

        final EntityPlayer player = mc.player;
        final World world = mc.world;
        final BlockPos pos = target.getBlockPos();

        //int   blockID         = world.getBlockId(x, y, z);
        //Block mouseoverBlock  = Block.blocksList[blockID];
        final Block mouseoverBlock = world.getBlockState(pos).getBlock();
        final TileEntity tileEntity = world.getTileEntity(pos);
        if (mouseoverBlock == null) return items;

        if (ModuleRegistrar.instance().hasStackProviders(mouseoverBlock)) {
            for (final List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(mouseoverBlock).values()) {
                for (final IWailaDataProvider provider : providersList) {
                    final ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack != null) {

                        if (providerStack.getItem() == null)
                            return new ArrayList<ItemStack>();

                        items.add(providerStack);
                    }
                }
            }
        }

        if (tileEntity != null && ModuleRegistrar.instance().hasStackProviders(tileEntity)) {
            for (final List<IWailaDataProvider> providersList : ModuleRegistrar.instance().getStackProviders(tileEntity).values()) {

                for (final IWailaDataProvider provider : providersList) {
                    final ItemStack providerStack = provider.getWailaStack(DataAccessorCommon.instance, ConfigHandler.instance());
                    if (providerStack != null) {

                        if (providerStack.getItem() == null)
                            return new ArrayList<ItemStack>();

                        items.add(providerStack);
                    }
                }
            }
        }

        if (items.size() > 0)
            return items;

        if (world.getTileEntity(pos) == null) {
            try {
                final ItemStack block = new ItemStack(mouseoverBlock, 1, mouseoverBlock.getMetaFromState(world.getBlockState(pos)));

                //System.out.printf("%s %s %s\n", block, block.getDisplayName(), block.getItemDamage());

                if (block.getItem() != null)
                    items.add(block);
                //else
                //	items.add(new ItemStack(new ItemBlock(mouseoverBlock)));
                //else
                //	items.add(new ItemStack(Item.getItemFromBlock(mouseoverBlock)));


            } catch (final Exception e) {
            }
        }

        if (items.size() > 0)
            return items;

        try {
            final ItemStack pick = mouseoverBlock.getPickBlock(mouseoverBlock.getDefaultState(), target, world, pos, player);//(this.target, world, pos, player);
            if (pick != null)
                items.add(pick);
        } catch (final Exception e) {
        }

        if (items.size() > 0)
            return items;           

        /*
        try
        {
            items.addAll(mouseoverBlock.getBlockDropped(world, x, y, z, world.getBlockMetadata(x, y, z), 0));
        }
        catch(Exception e){}
        
        if(items.size() > 0)
            return items;         
        */

        if (mouseoverBlock instanceof IShearable) {
            final IShearable shearable = (IShearable) mouseoverBlock;
            if (shearable.isShearable(new ItemStack(Items.SHEARS), world, pos)) {
                items.addAll(shearable.onSheared(new ItemStack(Items.SHEARS), world, pos, 0));
            }
        }

        if (items.size() == 0)
            items.add(0, new ItemStack(mouseoverBlock, 1, mouseoverBlock.getMetaFromState(world.getBlockState(pos))));

        return items;
    }

}
