package mcp.mobius.waila.overlay;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import mcp.mobius.waila.api.impl.ConfigHandler;
import mcp.mobius.waila.utils.WailaExceptionHandler;

/**
 * Draws a small turning 3D model of the block in the world as the tooltip icon. Based on {@code GuiBlockDraw} of WDMla
 * (https://github.com/GTNewHorizons/WDMla), which is based on the {@code WorldSceneRenderer} of BlockRenderer6343.
 * <p>
 * The block is drawn from the real world, so the icon shows the same state (facing, connections, TESR content) as the
 * block that the player looks at.
 */
public final class BlockIconRenderer {

    public static final String CFG_SHOW = "general.showblockicon3d";

    /** Degrees per second */
    private static final float TURN_SPEED = 45f;
    private static final float PITCH = 30f;

    private static final RenderBlocks renderBlocks = new RenderBlocks();
    private static final Set<Class<?>> failedClasses = new HashSet<>();

    private BlockIconRenderer() {}

    public static boolean isEnabled() {
        return ConfigHandler.instance().getConfig(CFG_SHOW, true);
    }

    /**
     * Draws the block at (bx, by, bz) in the square with the top left corner (x, y) and the side length size.
     *
     * @return false if nothing was drawn, then the caller must draw the normal item icon.
     */
    public static boolean render(int x, int y, int size, int bx, int by, int bz) {
        final Minecraft mc = Minecraft.getMinecraft();
        final World world = mc.theWorld;
        if (world == null) return false;

        Block block = world.getBlock(bx, by, bz);
        if (block == null || block.isAir(world, bx, by, bz) || failedClasses.contains(block.getClass())) return false;

        TileEntity tile = world.getTileEntity(bx, by, bz);
        TileEntityRendererDispatcher tesr = TileEntityRendererDispatcher.instance;
        boolean hasTesr = tile != null && tesr.hasSpecialRenderer(tile);
        if (block.getRenderType() == -1 && !hasTesr) return false;
        // A double chest is drawn as one large model by one of its halves, use the item icon for it
        if (tile instanceof TileEntityChest chest && (chest.adjacentChestXNeg != null || chest.adjacentChestXPos != null
                || chest.adjacentChestZNeg != null
                || chest.adjacentChestZPos != null))
            return false;

        float angle = (Minecraft.getSystemTime() % 360000L) / 1000f * TURN_SPEED;
        // A turning cube is at most about 1.6 blocks wide or high on the screen with this pitch
        float scale = size / 1.6f;

        Tessellator tessellator = Tessellator.instance;

        int matrixDepth = GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH);
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            // Some models are larger than one block, do not draw over the text
            ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
            float factor = res.getScaleFactor() * OverlayConfig.scale;
            int margin = 3;
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                    (int) ((x - margin) * factor),
                    (int) (mc.displayHeight - (y + size + margin) * factor),
                    (int) ((size + 2 * margin) * factor),
                    (int) ((size + 2 * margin) * factor));

            GL11.glTranslatef(x + size / 2f, y + size / 2f, 150f);
            GL11.glScalef(scale, -scale, scale);
            GL11.glRotatef(PITCH, 1f, 0f, 0f);
            GL11.glRotatef(angle, 0f, 1f, 0f);
            GL11.glTranslated(-bx - 0.5d, -by - 0.5d, -bz - 0.5d);

            GL11.glColor4f(1f, 1f, 1f, 1f);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Normal block model. The face shading of RenderBlocks gives the 3D look, so no GL lighting here.
            if (block.getRenderType() != -1) {
                RenderHelper.disableStandardItemLighting();
                GL11.glDisable(GL11.GL_LIGHTING);
                mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                renderBlocks.blockAccess = world;
                renderBlocks.renderAllFaces = true;
                tessellator.startDrawingQuads();
                try {
                    for (int pass = 0; pass < 2; pass++) {
                        if (!block.canRenderInPass(pass)) continue;
                        ForgeHooksClient.setRenderPass(pass);
                        renderBlocks.renderBlockByRenderType(block, bx, by, bz);
                    }
                } finally {
                    ForgeHooksClient.setRenderPass(-1);
                    tessellator.draw();
                    tessellator.setTranslation(0, 0, 0);
                }
            }

            // Special renderer (chests, engines, tanks, ...)
            if (hasTesr) {
                RenderHelper.enableStandardItemLighting();
                for (int pass = 0; pass < 2; pass++) {
                    if (!tile.shouldRenderInPass(pass)) continue;
                    ForgeHooksClient.setRenderPass(pass);
                    GL11.glColor4f(1f, 1f, 1f, 1f);
                    tesr.renderTileEntityAt(tile, bx, by, bz, 0f);
                }
                ForgeHooksClient.setRenderPass(-1);
                RenderHelper.disableStandardItemLighting();
            }
            return true;
        } catch (Throwable e) {
            failedClasses.add(block.getClass());
            WailaExceptionHandler.handleErr(e, "BlockIconRenderer | " + block.getClass().getName(), null);
            return false;
        } finally {
            renderBlocks.blockAccess = null;
            GL11.glPopAttrib();
            // A renderer that failed can leave a pushed matrix behind
            while (GL11.glGetInteger(GL11.GL_MODELVIEW_STACK_DEPTH) > matrixDepth + 1) GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }
}
