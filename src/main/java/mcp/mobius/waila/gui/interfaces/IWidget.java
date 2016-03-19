package mcp.mobius.waila.gui.interfaces;

import org.lwjgl.util.Point;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public interface IWidget {
	// Should all the coordinates, sizes, etc be % or absolute values ?
	// Should we allow 2 methods for an absolute referencing (screen pos) and relative one (parent pos) ?
	
	IWidget getParent();
	void    setParent(IWidget parent);
	
	IWidget setGeometry(WidgetGeometry geom);
	WidgetGeometry getGeometry();

	IWidget setPos(double x, double y);
	IWidget setPos(double x, double y, boolean fracX, boolean fracY);
	IWidget setSize(double sx, double sy);
	IWidget setSize(double sx, double sy, boolean fracX, boolean fracY);
	IWidget adjustSize();
	
	Point getPos();
	Point getSize();
	int   getRight();
	int   getLeft();
	int   getTop();
	int   getBottom();
	
	void  draw();
	void  draw(Point pos);
	void  show();
	void  hide();
	void  setAlpha(float alpha);
	float getAlpha();
	boolean shouldRender();
	
	IWidget addWidget(String name, IWidget widget);
	IWidget addWidget(String name, IWidget widget, RenderPriority priority);	
	IWidget getWidget(String name);
	IWidget delWidget(String name);	
	IWidget getWidgetAtCoordinates(double posX, double posY);
	
	boolean isWidgetAtCoordinates(double posx, double posy);
	
	void handleMouseInput();
	
	void onMouseClick(MouseEvent event);
	void onMouseDrag(MouseEvent event);
	void onMouseMove(MouseEvent event);
	void onMouseRelease(MouseEvent event);
	void onMouseWheel(MouseEvent event);
	void onMouseEnter(MouseEvent event);
	void onMouseLeave(MouseEvent event);
	
	void emit(Signal signal, Object... params);
	void onWidgetEvent(IWidget srcwidget, Signal signal, Object... params);
	
	//boolean  onMouseEnter();
	//boolean  onMouseMoved();
	//boolean  onMouseLeave();
	
}
