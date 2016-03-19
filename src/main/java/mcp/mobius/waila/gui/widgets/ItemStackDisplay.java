package mcp.mobius.waila.gui.widgets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.overlay.DisplayUtil;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackDisplay extends WidgetBase {

	ItemStack stack = null;
	
	public ItemStackDisplay(IWidget parent){
		this(parent, null);
	}

	public ItemStackDisplay(IWidget parent, ItemStack stack){
		super(parent);
		this.stack = stack;
		this.setSize(16, 16);
	}
	
	public void setStack(ItemStack stack){
		this.stack = stack;
	}
	
	public ItemStack getStack(){
		return this.stack;
	}
	
	@Override
	public void draw(Point pos) {
		if (this.stack == null) return;
		
		float scaleX = this.getSize().getX()/16.0f;
		float scaleY = this.getSize().getY()/16.0f;
		
		GL11.glPushMatrix();
		GL11.glScalef(scaleX, scaleY, 1.0f);
		
        RenderHelper.enableGUIStandardItemLighting();
		DisplayUtil.renderStack((int)(pos.getX()/scaleX), (int)(pos.getY()/scaleX), this.stack);
		GL11.glPopMatrix();
	}

}
