package mcp.mobius.waila.gui.widgets.movable;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.helpers.UIHelper;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.Signal;
import mcp.mobius.waila.gui.widgets.WidgetBase;
import net.minecraft.util.ResourceLocation;


// A movable picture setup especially for centered relative positions.
	
public class PictureMovableRC extends WidgetBase {
		
	private double offsetX, offsetY;
	protected ResourceLocation texture;
	
	public PictureMovableRC(IWidget parent, String uri) {
		super(parent);
		this.texture = new ResourceLocation(uri);
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		this.offsetX = event.x - this.geom.getUnalignedPos(this.parent).getX();
		this.offsetY = event.y - this.geom.getUnalignedPos(this.parent).getY();
		//System.out.printf("%s %s\n", this.offsetX, this.offsetY);
	}
	
	@Override	
	public void onMouseDrag(MouseEvent event){
		double newX = event.x - this.offsetX ;
		double newY = event.y - this.offsetY ;

		newX = Math.max(newX, this.parent.getLeft());
		newY = Math.max(newY, this.parent.getTop());

		newX = Math.min(newX, this.parent.getRight());
		newY = Math.min(newY, this.parent.getBottom());		

		this.setPos(((newX - this.parent.getLeft()) / this.parent.getSize().getX()) * 100.0, ((newY - this.parent.getTop())/ this.parent.getSize().getY()) * 100.0);
		
		//System.out.printf("%s\n", this.parent.getGeometry());
		//System.out.printf("%s %s\n", this.parent.getPos(), this.parent.getSize());
		
		this.emit(Signal.DRAGGED, this.getPos());
	}
	
	@Override
	public void draw(Point pos) {
		this.saveGLState();
		
		GL11.glPushMatrix();
		this.texManager.bindTexture(texture);
		UIHelper.drawTexture(pos.getX(), pos.getY(), this.getSize().getX(), this.getSize().getY());
		GL11.glPopMatrix();

		this.loadGLState();
	}	
}

