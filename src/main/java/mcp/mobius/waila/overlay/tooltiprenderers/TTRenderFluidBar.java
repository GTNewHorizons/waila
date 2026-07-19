package mcp.mobius.waila.overlay.tooltiprenderers;

import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatFluid;
import static com.gtnewhorizon.gtnhlib.util.numberformatting.NumberFormatUtil.formatNumber;

import java.awt.Dimension;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import gtPlusPlus.core.util.minecraft.FluidUtils;
import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaVariableWidthTooltipRenderer;
import mcp.mobius.waila.cbcore.LangUtil;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.utils.LoadedMods;

public class TTRenderFluidBar implements IWailaVariableWidthTooltipRenderer {

    int maxStringW;

    private final Consumer<String> bindColor;
    private static final int height = 12;

    public TTRenderFluidBar() {
        if (LoadedMods.GT5U) {
            bindColor = (fluidName) -> {
                FluidStack tFStack = FluidUtils.getWildcardFluidStack(fluidName, 1000);
                if (tFStack == null) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    return;
                }
                Fluid tFluid = tFStack.getFluid();
                if (tFluid == null) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    return;
                }
                // duplicate code to prevent extra stack allocations
                int tColor = tFluid.getColor();
                float r = ((tColor >>> 16) & 0xFF) / (float) 0xFF;
                float g = ((tColor >>> 8) & 0xFF) / (float) 0xFF;
                float b = (tColor & 0xFF) / (float) 0xFF;
                GL11.glColor4f(r, g, b, 1.0F);
            };
        } else {
            bindColor = (fluidName) -> {
                Fluid tFluid = FluidRegistry.getFluid(fluidName);
                if (tFluid == null) {
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    return;
                }
                // duplicate code to prevent extra stack allocations
                int tColor = tFluid.getColor();
                float r = ((tColor >>> 16) & 0xFF) / (float) 0xFF;
                float g = ((tColor >>> 8) & 0xFF) / (float) 0xFF;
                float b = (tColor & 0xFF) / (float) 0xFF;
                GL11.glColor4f(r, g, b, 1.0F);
            };
        }

    }

    @Override
    public Dimension getSize(String[] params, IWailaCommonAccessor accessor) {
        boolean isEmpty = (params[0].equals("EMPTYFLUID") && params[1].equals("EMPTYFLUID"));
        int displayWidth = DisplayUtil.getDisplayWidth(
                buildDisplayText(
                        isEmpty ? 0 : Double.parseDouble(params[2]),
                        Double.parseDouble(params[3]),
                        params[1],
                        isEmpty));

        return new Dimension(displayWidth + 4, height);
    }

    public static final ResourceLocation gradient = new ResourceLocation("waila", "textures/gradient.png");

    @Override
    public void draw(String[] params, IWailaCommonAccessor accessor) {
        String fluidName = params[0];
        String localizedName = params[1];
        double amount = Double.parseDouble(params[2]);
        double capacity = Double.parseDouble(params[3]);
        Tessellator tessellator = Tessellator.instance;
        boolean isEmpty = fluidName.equals("EMPTYFLUID") && localizedName.equals("EMPTYFLUID");

        Minecraft mc = Minecraft.getMinecraft();
        if (!isEmpty) {
            IIcon icon = FluidRegistry.getFluid(fluidName).getIcon();
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            bindColor.accept(fluidName);

            tessellator.startDrawingQuads();
            // Intentionally draw 2 pixels taller than needed than cover with the border to make the texture more
            // visible
            int i = (int) ((double) (maxStringW - 2) * (amount / Math.max(capacity, amount)));
            int j = 0;
            for (; i > height; i = i - height) {
                drawRectFromIcon(tessellator, 1 + (j * height), 0, 0, icon, height, height);
                j++;
            }
            if (i > 0) DisplayUtil.drawRect(
                    tessellator,
                    1 + (j * height),
                    0,
                    0,
                    i,
                    height,
                    icon.getMinU(),
                    icon.getMinV(),
                    icon.getMinU() + ((icon.getMaxU() - icon.getMinU()) * ((double) i / height)),
                    icon.getMaxV());
            tessellator.draw();
        }

        if (!isEmpty) {
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glColor4f(1F, 1F, 1F, 0.70F);
            mc.getTextureManager().bindTexture(gradient);
            tessellator.startDrawingQuads();
            DisplayUtil.drawRect(tessellator, 1, 0, 0, maxStringW - 2, height - 1, 0, 0, 1, 1);
            tessellator.draw();
        } else {
            Gui.drawRect(1, 0, maxStringW - 1, height - 1, 0x1A575656);
        }

        DisplayUtil.drawThickBeveledBox(0, 0, maxStringW, height, 1, 0xFF505050, 0xFF505050, -1);

        DisplayUtil.drawString(
                buildDisplayText(amount, capacity, localizedName, isEmpty),
                2,
                2,
                isEmpty ? 0xFFDDDDDD : 0xFFFFFFFF,
                true);
    }

    public String buildDisplayText(double amount, double capacity, String fluidName, boolean isEmpty) {
        return isEmpty ? String.format("%s / %s", LangUtil.translateG("hud.msg.empty"), formatFluid(capacity))
                : String.format("%s / %s %s", formatNumber(amount), formatFluid(capacity), fluidName);
    }

    public static void drawRectFromIcon(Tessellator tessellator, int x, int y, double z, IIcon icon, int width,
            int height) {
        DisplayUtil.drawRect(
                tessellator,
                x,
                y,
                z,
                width,
                height,
                icon.getMinU(),
                icon.getMinV(),
                icon.getMaxU(),
                icon.getMaxV());
    }

    @Override
    public void setMaxLineWidth(int width) {
        maxStringW = width;
    }

    @Override
    public int getMaxLineWidth() {
        return maxStringW;
    }
}
