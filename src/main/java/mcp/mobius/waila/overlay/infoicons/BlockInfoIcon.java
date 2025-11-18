package mcp.mobius.waila.overlay.infoicons;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaInfoIcon;
import mcp.mobius.waila.api.impl.ConfigHandler;

/**
 * Draws a fake semi-transparent block that is facing the exact same direction as the block that the player is looking
 * at.</br>
 * Texture and UV can be specified in the constructor(s).</br>
 * Overlays can be added with the withSideOverlay method.</br>
 * </br>
 *
 * Example usage:</br>
 * currentIcons.add(new BlockInfoIcon(new ResourceLocation("textures/blocks/stone_slab_top.png"), 0d, 1d, 0d, 1d)
 * .withSideOverlay(ForgeDirection.EAST, new ResourceLocation("waila", "textures/o.png"))
 * .withSideOverlay(ForgeDirection.NORTH, new ResourceLocation("waila", "textures/x.png")));</br>
 * </br>
 *
 * Intended to be as simple visually as possible so it can be read while making it very small. Only supports using the
 * same texture for all the faces of the block.</br>
 * </br>
 *
 * Made specifically for GregTech to show where machines are facing.
 *
 * @author SuperSouper
 *
 */
public class BlockInfoIcon implements IWailaInfoIcon {

    private final ResourceLocation texture;

    private final double minU;
    private final double maxU;
    private final double minV;
    private final double maxV;

    protected List<SideOverlay> sideOverlays = new ArrayList<>();

    float size = (float) (3.3 * ConfigHandler.infoIconHeight) / 8;

    public BlockInfoIcon(Block block) {
        this(block, 0, 0);
    }

    public BlockInfoIcon(Block block, int side, int meta) {
        this(
                TextureMap.locationBlocksTexture,
                block.getIcon(side, meta).getMinU(),
                block.getIcon(side, meta).getMaxU(),
                block.getIcon(side, meta).getMinV(),
                block.getIcon(side, meta).getMaxV());
    }

    public BlockInfoIcon(IIcon iIcon) {
        this(TextureMap.locationBlocksTexture, iIcon.getMinU(), iIcon.getMaxU(), iIcon.getMinV(), iIcon.getMaxV());
    }

    public BlockInfoIcon(ResourceLocation texture, double minU, double maxU, double minV, double maxV) {
        this.texture = texture;
        this.minU = minU;
        this.maxU = maxU;
        this.minV = minV;
        this.maxV = maxV;
    }

    @FunctionalInterface
    public interface FaceOverlayDrawer {

        void draw(double size, double minU, double maxU, double minV, double maxV);
    }

    private class SideOverlay {

        private final ResourceLocation overlayTexture;
        private final FaceOverlayDrawer overlayDrawerFront;
        private final FaceOverlayDrawer overlayDrawerBack;
        private final double size;
        private final double overlayMinU;
        private final double overlayMaxU;
        private final double overlayMinV;
        private final double overlayMaxV;

        SideOverlay(ResourceLocation overlayTexture, double size, FaceOverlayDrawer overlayDrawerFront,
                FaceOverlayDrawer overlayDrawerBack, double overlayMinU, double overlayMaxU, double overlayMinV,
                double overlayMaxV) {
            this.overlayTexture = overlayTexture;
            this.overlayDrawerFront = overlayDrawerFront;
            this.overlayDrawerBack = overlayDrawerBack;
            this.size = size;
            this.overlayMinU = overlayMinU;
            this.overlayMaxU = overlayMaxU;
            this.overlayMinV = overlayMinV;
            this.overlayMaxV = overlayMaxV;
        }

        protected void drawFront() {
            Minecraft.getMinecraft().renderEngine.bindTexture(overlayTexture);
            GL11.glColor4f(1F, 1F, 1F, 1F);
            tessellator.startDrawingQuads();
            overlayDrawerFront.draw(size, overlayMinU, overlayMaxU, overlayMinV, overlayMaxV);
            tessellator.draw();
        }

        protected void drawBack() {
            Minecraft.getMinecraft().renderEngine.bindTexture(overlayTexture);
            GL11.glColor4f(1F, 1F, 1F, 0.25F);
            tessellator.startDrawingQuads();
            overlayDrawerBack.draw(size, overlayMinU, overlayMaxU, overlayMinV, overlayMaxV);
            tessellator.draw();
        }
    }

    public BlockInfoIcon withSideOverlay(ForgeDirection side, ResourceLocation texture) {
        return withSideOverlay(side, texture, 0d, 1d, 0d, 1d);
    }

    public BlockInfoIcon withSideOverlay(ForgeDirection side, ResourceLocation texture, double overlayMinU,
            double overlayMaxU, double overlayMinV, double overlayMaxV) {
        FaceOverlayDrawer overlayDrawerFront;
        FaceOverlayDrawer overlayDrawerBack;
        switch (side) {
            case DOWN:
                overlayDrawerFront = this::drawBottomFaceFront;
                overlayDrawerBack = this::drawBottomFaceBack;
                break;
            case UP:
                overlayDrawerFront = this::drawTopFaceFront;
                overlayDrawerBack = this::drawTopFaceBack;
                break;
            case NORTH:
                overlayDrawerFront = this::drawNorthFaceFront;
                overlayDrawerBack = this::drawNorthFaceBack;
                break;
            case SOUTH:
                overlayDrawerFront = this::drawSouthFaceFront;
                overlayDrawerBack = this::drawSouthFaceBack;
                break;
            case WEST:
                overlayDrawerFront = this::drawWestFaceFront;
                overlayDrawerBack = this::drawWestFaceBack;
                break;
            case EAST:
                overlayDrawerFront = this::drawEastFaceFront;
                overlayDrawerBack = this::drawEastFaceBack;
                break;
            default:
                throw new IllegalArgumentException("Invalid side " + side);
        }

        sideOverlays.add(
                new SideOverlay(
                        texture,
                        size,
                        overlayDrawerFront,
                        overlayDrawerBack,
                        overlayMinU,
                        overlayMaxU,
                        overlayMinV,
                        overlayMaxV));

        return this;
    }

    @Override
    public int getWidth(IWailaCommonAccessor accessor) {
        return (int) Math.ceil(ConfigHandler.infoIconHeight * 1.33);
    }

    @Override
    public void draw(IWailaCommonAccessor accessor) {
        Minecraft mc = Minecraft.getMinecraft();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glEnable(GL11.GL_POLYGON_SMOOTH);

        GL11.glTranslatef((float) getWidth(accessor) / 2, (float) ConfigHandler.infoIconHeight / 2, 0);

        // Calculate the rotation
        float x1 = (float) mc.thePlayer.posX;
        float y1 = (float) mc.thePlayer.posY;
        float z1 = (float) mc.thePlayer.posZ;
        float x2 = accessor.getPosition().blockX + 0.5f;
        float y2 = accessor.getPosition().blockY + 0.5f;
        float z2 = accessor.getPosition().blockZ + 0.5f;

        float hx = x2 - x1;
        float hy = y2 - y1;
        float hz = z2 - z1;

        // Don't ask me how this math works, chatgpt wrote it.
        double deg = Math.atan2(hx, hz);
        double deg2 = Math.acos(Math.max(-1.0, Math.min(1.0, hy / Math.sqrt(hx * hx + hy * hy + hz * hz))))
                + Math.PI * 0.55;
        // Convert Euler angles to quaternion
        float cy = (float) Math.cos(deg * 0.5); // Z rotation
        float sy = (float) Math.sin(deg * 0.5);
        float cr = (float) Math.cos(-deg2 * 0.5); // Y rotation
        float sr = (float) Math.sin(-deg2 * 0.5);

        float w = cy * cr;
        float x = cy * sr;
        float y = sy * cr;
        float z = sy * sr;

        // Convert quaternion to rotation matrix and apply
        float angle = (float) (2.0 * Math.acos(w));
        float scale = (float) Math.sqrt(1.0 - w * w);
        // DisplayUtil.drawString(mc.thePlayer.rotationYaw + "", 0, 100, 1, true);
        if (scale < 0.001f) {
            GL11.glRotatef(angle * 57.2958f, 1.0F, 0.0F, 0.0F);
        } else {
            GL11.glRotatef(angle * 57.2958f, x / scale, y / scale, z / scale);
        }

        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(texture);
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA(255, 255, 255, (int) (255 * 0.85));
        drawTopFaceFront(size, minU, maxU, minV, maxV);
        drawNorthFaceFront(size, minU, maxU, minV, maxV);
        drawBottomFaceFront(size, minU, maxU, minV, maxV);
        drawSouthFaceFront(size, minU, maxU, minV, maxV);
        drawEastFaceFront(size, minU, maxU, minV, maxV);
        drawWestFaceFront(size, minU, maxU, minV, maxV);

        tessellator.setColorRGBA(255, 255, 255, (int) (255 * 0.25));
        drawTopFaceBack(size, minU, maxU, minV, maxV);
        drawBottomFaceBack(size, minU, maxU, minV, maxV);
        drawNorthFaceBack(size, minU, maxU, minV, maxV);
        drawSouthFaceBack(size, minU, maxU, minV, maxV);
        drawEastFaceBack(size, minU, maxU, minV, maxV);
        drawWestFaceBack(size, minU, maxU, minV, maxV);
        tessellator.draw();

        for (SideOverlay sideOverlay : sideOverlays) {
            sideOverlay.drawBack();
        }

        for (SideOverlay sideOverlay : sideOverlays) {
            sideOverlay.drawFront();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_POLYGON_SMOOTH);

    }

    Tessellator tessellator = Tessellator.instance;

    // bottom face
    protected void drawBottomFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(size, -size, size, maxU, maxV);
        tessellator.addVertexWithUV(size, -size, -size, maxU, minV);
        tessellator.addVertexWithUV(-size, -size, -size, minU, minV);
        tessellator.addVertexWithUV(-size, -size, size, minU, maxV);
    }

    // top face
    protected void drawTopFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, size, size, minU, maxV);
        tessellator.addVertexWithUV(-size, size, -size, minU, minV);
        tessellator.addVertexWithUV(size, size, -size, maxU, minV);
        tessellator.addVertexWithUV(size, size, size, maxU, maxV);
    }

    // north (z-negative)
    protected void drawNorthFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, -size, -size, maxU, maxV);
        tessellator.addVertexWithUV(size, -size, -size, minU, maxV);
        tessellator.addVertexWithUV(size, size, -size, minU, minV);
        tessellator.addVertexWithUV(-size, size, -size, maxU, minV);
    }

    // south (z-positive)
    protected void drawSouthFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(size, size, size, maxU, minV);
        tessellator.addVertexWithUV(size, -size, size, maxU, maxV);
        tessellator.addVertexWithUV(-size, -size, size, minU, maxV);
        tessellator.addVertexWithUV(-size, size, size, minU, minV);
    }

    // west (x-negative) face
    protected void drawEastFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, -size, size, maxU, maxV);
        tessellator.addVertexWithUV(-size, -size, -size, minU, maxV);
        tessellator.addVertexWithUV(-size, size, -size, minU, minV);
        tessellator.addVertexWithUV(-size, size, size, maxU, minV);
    }

    // east (x-positive) face
    protected void drawWestFaceFront(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(size, size, size, minU, minV);
        tessellator.addVertexWithUV(size, size, -size, maxU, minV);
        tessellator.addVertexWithUV(size, -size, -size, maxU, maxV);
        tessellator.addVertexWithUV(size, -size, size, minU, maxV);
    }

    // bottom face
    protected void drawBottomFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, -size, size, minU, maxV);
        tessellator.addVertexWithUV(-size, -size, -size, minU, minV);
        tessellator.addVertexWithUV(size, -size, -size, maxU, minV);
        tessellator.addVertexWithUV(size, -size, size, maxU, maxV);
    }

    // top face
    protected void drawTopFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(size, size, size, maxU, maxV);
        tessellator.addVertexWithUV(size, size, -size, maxU, minV);
        tessellator.addVertexWithUV(-size, size, -size, minU, minV);
        tessellator.addVertexWithUV(-size, size, size, minU, maxV);
    }

    // north (z-negative)
    protected void drawNorthFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, size, -size, maxU, minV);
        tessellator.addVertexWithUV(size, size, -size, minU, minV);
        tessellator.addVertexWithUV(size, -size, -size, minU, maxV);
        tessellator.addVertexWithUV(-size, -size, -size, maxU, maxV);
    }

    // south (z-positive)
    protected void drawSouthFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, size, size, minU, minV);
        tessellator.addVertexWithUV(-size, -size, size, minU, maxV);
        tessellator.addVertexWithUV(size, -size, size, maxU, maxV);
        tessellator.addVertexWithUV(size, size, size, maxU, minV);
    }

    // west (x-negative) face
    protected void drawEastFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(-size, size, size, maxU, minV);
        tessellator.addVertexWithUV(-size, size, -size, minU, minV);
        tessellator.addVertexWithUV(-size, -size, -size, minU, maxV);
        tessellator.addVertexWithUV(-size, -size, size, maxU, maxV);
    }

    // east (x-positive) face
    protected void drawWestFaceBack(double size, double minU, double maxU, double minV, double maxV) {
        tessellator.addVertexWithUV(size, -size, size, minU, maxV);
        tessellator.addVertexWithUV(size, -size, -size, maxU, maxV);
        tessellator.addVertexWithUV(size, size, -size, maxU, minV);
        tessellator.addVertexWithUV(size, size, size, minU, minV);
    }

}
