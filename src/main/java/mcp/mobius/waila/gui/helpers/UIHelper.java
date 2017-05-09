package mcp.mobius.waila.gui.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

public class UIHelper {

    public static void drawTexture(final int posX, final int posY, final int sizeX, final int sizeY) {
        UIHelper.drawTexture(posX, posY, sizeX, sizeY, 0, 0, 256, 256);
    }

    public static void drawTexture(final int posX, final int posY, final int sizeX, final int sizeY, final int texU, final int texV, final int texSizeU, final int texSizeV) {
        final float zLevel = 0.0F;
        final float f = 0.00390625F;

        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();
        t.begin(7, DefaultVertexFormats.POSITION_TEX);
        t.pos((double) (posX + 0), (double) (posY + sizeY), (double) zLevel).tex((double) ((float) texU * f), (double) ((float) (texV + texSizeV) * f)).endVertex();
        t.pos((double) (posX + sizeX), (double) (posY + sizeY), (double) zLevel).tex((double) ((float) (texU + texSizeU) * f), (double) ((float) (texV + texSizeV) * f)).endVertex();
        t.pos((double) (posX + sizeX), (double) (posY + 0), (double) zLevel).tex((double) ((float) (texU + texSizeU) * f), (double) ((float) texV * f)).endVertex();
        t.pos((double) (posX + 0), (double) (posY + 0), (double) zLevel).tex((double) ((float) texU * f), (double) ((float) texV * f)).endVertex();
        tessellator.draw();
    }

    public static void drawGradientRect(final int left, final int top, final int right, final int bottom, final int zLevel, final int startColor, final int endColor) {
        final float alpha1 = (float) (startColor >> 24 & 255) / 255.0F;
        final float red1 = (float) (startColor >> 16 & 255) / 255.0F;
        final float green1 = (float) (startColor >> 8 & 255) / 255.0F;
        final float blue1 = (float) (startColor & 255) / 255.0F;
        final float alpha2 = (float) (endColor >> 24 & 255) / 255.0F;
        final float red2 = (float) (endColor >> 16 & 255) / 255.0F;
        final float green2 = (float) (endColor >> 8 & 255) / 255.0F;
        final float blue2 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();
        t.begin(7, DefaultVertexFormats.POSITION_COLOR);
        t.pos((double) right, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1).endVertex();
        t.pos((double) left, (double) top, (double) zLevel).color(red1, green1, blue1, alpha1).endVertex();
        t.pos((double) left, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2).endVertex();
        t.pos((double) right, (double) bottom, (double) zLevel).color(red2, green2, blue2, alpha2).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void drawBillboard(final Vec3d pos, final float offX, final float offY, final float offZ, final double x1, final double y1, final double x2, final double y2, final int r, final int g, final int b, final int a, final double partialFrame) {
        UIHelper.drawBillboard((float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord, offX, offY, offZ, x1, y1, x2, y2, r, g, b, a, partialFrame);
    }

    public static void drawBillboard(final float posX, final float posY, final float posZ, final float offX, final float offY, final float offZ, final double x1, final double y1, final double x2, final double y2, final int r, final int g, final int b, final int a, final double partialFrame) {
        final Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        final float playerViewY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * (float) partialFrame;
        final float playerViewX = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * (float) partialFrame;

        UIHelper.drawBillboard(posX, posY, posZ, offX, offY, offZ, playerViewX, playerViewY * -1.0F, 0.0F, x1, y1, x2, y2, r, g, b, a);
    }

    public static void drawBillboard(final float posX, final float posY, final float posZ, final float offX, final float offY, final float offZ, final float rotX, final float rotY, final float rotZ, final double x1, final double y1, final double x2, final double y2, final int r, final int g, final int b, final int a) {
        final float f = 1.6F;
        final float f1 = 0.016666668F * f;
        GL11.glPushMatrix();

        GL11.glTranslatef(posX + offX, posY + offY, posZ + offZ);

        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);

        //GL11.glScalef(-f1, -f1, f1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        //GL11.glDisable(GL11.GL_TEXTURE_2D);
        drawRectangle(x1, y1, 0, x2, y2, 0, r, g, b, a);

        GL11.glPopMatrix();
    }

    public static void drawBillboardText(final String text, final Vec3d pos, final float offX, final float offY, final float offZ, final double partialFrame) {
        UIHelper.drawBillboardText(text, (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord, offX, offY, offZ, partialFrame);
    }

    public static void drawBillboardText(final String text, final float posX, final float posY, final float posZ, final float offX, final float offY, final float offZ, final double partialFrame) {
        final Entity player = Minecraft.getMinecraft().getRenderViewEntity();
        final float playerViewY = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * (float) partialFrame;
        final float playerViewX = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * (float) partialFrame;

        UIHelper.drawFloatingText(text, posX, posY, posZ, offX, offY, offZ, playerViewX, playerViewY * -1.0F, 0.0F);
    }

    public static void drawFloatingText(final String text, final Vec3d pos, final float offX, final float offY, final float offZ, final float rotX, final float rotY, final float rotZ) {
        UIHelper.drawFloatingText(text, (float) pos.xCoord, (float) pos.yCoord, (float) pos.zCoord, offX, offY, offZ, rotX, rotY, rotZ);
    }

    public static void drawFloatingText(final String text, final float posX, final float posY, final float posZ, final float offX, final float offY, final float offZ, final float rotX, final float rotY, final float rotZ) {

        if (text.isEmpty()) return;

        final FontRenderer fontRendererObj = Minecraft.getMinecraft().fontRendererObj;

        final float f = 1.6F;
        final float f1 = 0.016666668F * f;
        GL11.glPushMatrix();

        //GL11.glTranslatef((float)accessor.getPosition().blockX + 0.0F, (float)accessor.getPosition().blockY + 0.5F, (float)accessor.getPosition().blockZ);
        GL11.glTranslatef(posX + offX, posY + offY, posZ + offZ);

        //GL11.glTranslatef(posX, posY, posZ);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);
        //GL11.glTranslatef(offX, offY, offZ);

        GL11.glScalef(-f1, -f1, f1);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final byte b0 = 0;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        final int j = fontRendererObj.getStringWidth(text) / 2;
        drawRectangle((double) (-j - 1), (double) (8 + b0), 0.0, (double) (j + 1), (double) (-1 + b0), 0.0, 0, 0, 0, 64);

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontRendererObj.drawString(text, -fontRendererObj.getStringWidth(text) / 2, b0, 553648127);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontRendererObj.drawString(text, -fontRendererObj.getStringWidth(text) / 2, b0, -1);
        //GL11.glEnable(GL11.GL_LIGHTING);
        //GL11.glDisable(GL11.GL_BLEND);
        //GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
    }

    public static void drawRectangle(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final int r, final int g, final int b, final int a) {
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();

        t.begin(7, DefaultVertexFormats.POSITION_COLOR);
        t.pos(x1, y2, z1).color(r, g, b, a).endVertex();
        t.pos(x1, y1, z2).color(r, g, b, a).endVertex();
        t.pos(x2, y1, z2).color(r, g, b, a).endVertex();
        t.pos(x2, y2, z1).color(r, g, b, a).endVertex();
        tessellator.draw();
    }

    public static void drawRectangleEW(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2, final int r, final int g, final int b, final int a) {
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();

        t.begin(7, DefaultVertexFormats.POSITION_COLOR);
        t.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        t.pos(x1, y1, z2).color(r, g, b, a).endVertex();
        t.pos(x2, y2, z2).color(r, g, b, a).endVertex();
        t.pos(x2, y2, z1).color(r, g, b, a).endVertex();
        tessellator.draw();
    }
}