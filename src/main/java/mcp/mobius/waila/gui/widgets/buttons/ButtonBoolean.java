package mcp.mobius.waila.gui.widgets.buttons;

import mcp.mobius.waila.gui.events.MouseEvent;
import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import mcp.mobius.waila.gui.widgets.LabelFixedFont;
import mcp.mobius.waila.gui.widgets.WidgetGeometry;

public class ButtonBoolean extends ButtonBase {

	protected boolean state     = false;
	
	public ButtonBoolean(IWidget parent, String textFalse, String textTrue){
		super(parent);
		
		this.addWidget("LabelFalse", new LabelFixedFont(this, textFalse));
		this.getWidget("LabelFalse").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
		this.addWidget("LabelTrue",  new LabelFixedFont(this, textTrue));
		this.getWidget("LabelTrue").hide();
		this.getWidget("LabelTrue").setGeometry(new WidgetGeometry(50.0D, 50.0D, 100.0D, 20.0D, CType.RELXY, CType.ABSXY, WAlign.CENTER, WAlign.CENTER));		
	}
	
	@Override
	public void onMouseClick(MouseEvent event){
		super.onMouseClick(event);
		
		if (event.button == 0)
			this.state = !this.state;
		
		if (this.state){
			this.getWidget("LabelTrue").show();
			this.getWidget("LabelFalse").hide();
		} else {
			this.getWidget("LabelTrue").hide();
			this.getWidget("LabelFalse").show();			
		}
	}
}
