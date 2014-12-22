package mcp.mobius.waila.overlay;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.cbcore.GuiDraw;
import mcp.mobius.waila.cbcore.StackHandler;
import mcp.mobius.waila.utils.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.util.MovingObjectPosition;

public class OverlayRenderer {

	protected static boolean hasAlphaTest;
	protected static boolean hasBlending;
	protected static boolean hasLight;
	protected static boolean hasDepthTest;
	protected static boolean hasLight0;
	protected static boolean hasLight1;
	protected static boolean hasRescaleNormal;
	protected static boolean hasColorMaterial;
	protected static int     boundTexIndex;   	
	protected static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
	
    public static void renderOverlay()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if(!(mc.currentScreen == null &&
             mc.theWorld != null &&
             Minecraft.isGuiEnabled() &&
             !mc.gameSettings.keyBindPlayerList.isKeyDown() &&
             ConfigHandler.instance().getConfig(Configuration.CATEGORY_GENERAL, Constants.CFG_WAILA_SHOW, true) &&
             RayTracing.instance().getTarget()      != null))
        	return;
    
        if (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && RayTracing.instance().getTargetStack() != null)
        {
            renderOverlay(WailaTickHandler.instance().tooltip);
        }

        if (RayTracing.instance().getTarget().typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY)
        {
        	renderOverlay(WailaTickHandler.instance().tooltip);       	
        }
    }		
    
    public static void renderOverlay(Tooltip tooltip)
    {
    	//TrueTypeFont font = (TrueTypeFont)mod_Waila.proxy.getFont();
    	GL11.glPushMatrix();
    	saveGLState();
    	
    	GL11.glScalef(OverlayConfig.scale, OverlayConfig.scale, 1.0f);
    	
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        
        drawTooltipBox(tooltip.x, tooltip.y, tooltip.w, tooltip.h, OverlayConfig.bgcolor, OverlayConfig.gradient1, OverlayConfig.gradient2);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA,GL11.GL_ONE_MINUS_SRC_ALPHA);     
		tooltip.drawStrings();
        GL11.glDisable(GL11.GL_BLEND);

        tooltip.drawIcons();        


        if (tooltip.hasIcon)
        	RenderHelper.enableGUIStandardItemLighting();

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        
        if (tooltip.hasIcon && tooltip.stack != null && tooltip.stack.getItem() != null){
        	Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(tooltip.stack, tooltip.x+5, tooltip.y+tooltip.h/2-8);
            //StackHandler.drawItem(tooltip.x+5, tooltip.y+tooltip.h/2-8, tooltip.stack);
        }

        loadGLState();
    	GL11.glPopMatrix();  
    }    
    
    public static void saveGLState(){
    	hasAlphaTest  = GL11.glGetBoolean(GL11.GL_ALPHA_TEST);
		hasBlending   = GL11.glGetBoolean(GL11.GL_BLEND);
		hasLight      = GL11.glGetBoolean(GL11.GL_LIGHTING);
		hasLight0     = GL11.glGetBoolean(GL11.GL_LIGHT0);
		hasLight1     = GL11.glGetBoolean(GL11.GL_LIGHT1);
		hasDepthTest     = GL11.glGetBoolean(GL11.GL_DEPTH_TEST);
		hasRescaleNormal = GL11.glGetBoolean(GL12.GL_RESCALE_NORMAL);
		hasColorMaterial = GL11.glGetBoolean(GL11.GL_COLOR_MATERIAL);
    	boundTexIndex    = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);  
    	GL11.glPushAttrib(GL11.GL_CURRENT_BIT);
    }
    
    public static void loadGLState(){
    	if (hasAlphaTest)     GL11.glEnable(GL11.GL_ALPHA_TEST);      else GL11.glDisable(GL11.GL_ALPHA_TEST);
    	if (hasBlending)      GL11.glEnable(GL11.GL_BLEND);      else GL11.glDisable(GL11.GL_BLEND);
    	if (hasLight)         GL11.glEnable(GL11.GL_LIGHTING);   else GL11.glDisable(GL11.GL_LIGHTING);
    	if (hasLight0)        GL11.glEnable(GL11.GL_LIGHT0);     else GL11.glDisable(GL11.GL_LIGHT0);
    	if (hasLight1)        GL11.glEnable(GL11.GL_LIGHT1);     else GL11.glDisable(GL11.GL_LIGHT1);
    	if (hasDepthTest)     GL11.glEnable(GL11.GL_DEPTH_TEST); else GL11.glDisable(GL11.GL_DEPTH_TEST);
    	if (hasRescaleNormal) GL11.glEnable(GL12.GL_RESCALE_NORMAL); else GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    	if (hasColorMaterial) GL11.glEnable(GL11.GL_COLOR_MATERIAL); else GL11.glDisable(GL11.GL_COLOR_MATERIAL);
    	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
    	GL11.glPopAttrib();
    	//GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }    
    
    public static void drawTooltipBox(int x, int y, int w, int h, int bg, int grad1, int grad2)
    {
        //int bg = 0xf0100010;
    	GuiDraw.drawGradientRect(x + 1, y, w - 1, 1, bg, bg);
    	GuiDraw.drawGradientRect(x + 1, y + h, w - 1, 1, bg, bg);
    	GuiDraw.drawGradientRect(x + 1, y + 1, w - 1, h - 1, bg, bg);//center
    	GuiDraw.drawGradientRect(x, y + 1, 1, h - 1, bg, bg);
    	GuiDraw.drawGradientRect(x + w, y + 1, 1, h - 1, bg, bg);
        //int grad1 = 0x505000ff;
        //int grad2 = 0x5028007F;
    	GuiDraw.drawGradientRect(x + 1, y + 2, 1, h - 3, grad1, grad2);
    	GuiDraw.drawGradientRect(x + w - 1, y + 2, 1, h - 3, grad1, grad2);
        
    	GuiDraw.drawGradientRect(x + 1, y + 1, w - 1, 1, grad1, grad1);
    	GuiDraw.drawGradientRect(x + 1, y + h - 1, w - 1, 1, grad2, grad2);
    }    
    
    public static void drawTexturedModalRect(int x, int y, int u, int v, int w, int h, int tw, int th)
    {
        float f  = 0.00390625F;
        float f1 = 0.00390625F;
        float zLevel = 0.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer renderer  = tessellator.getWorldRenderer();
        renderer.startDrawingQuads();
        renderer.setColorOpaque_F(1, 1, 1);        
        renderer.addVertexWithUV(x + 0, y + h, zLevel, (u + 0)  * f, (v + th) * f1);
        renderer.addVertexWithUV(x + w, y + h, zLevel, (u + tw) * f, (v + th) * f1);
        renderer.addVertexWithUV(x + w, y + 0, zLevel, (u + tw) * f, (v + 0)  * f1);
        renderer.addVertexWithUV(x + 0, y + 0, zLevel, (u + 0)  * f, (v + 0)  * f1);
        tessellator.draw();
    }    
    
    public static void renderIcon(int x, int y, int sx, int sy, IconUI icon){
    	Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
    	
    	if (icon == null)
    		return;
    	
    	if (icon.bu != -1)
    		drawTexturedModalRect(x, y, icon.bu, icon.bv, sx, sy, icon.bsu, icon.bsv);
    	drawTexturedModalRect(x, y, icon.u, icon.v, sx, sy, icon.su, icon.sv);
    }
}
