package mcp.mobius.waila.overlay.tooltiprenderers;

import mcp.mobius.waila.api.IWailaCommonAccessor;
import mcp.mobius.waila.api.IWailaTooltipRenderer;
import mcp.mobius.waila.overlay.DisplayUtil;
import mcp.mobius.waila.overlay.IconUI;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

/**
 * Custom renderer for health bars.
 * Syntax : {waila.health, nheartperline, health, maxhealth}
 */
public class TTRenderHealth implements IWailaTooltipRenderer {

    @Override
    public Dimension getSize(final String[] params, final IWailaCommonAccessor accessor) {
        final float maxhearts = Float.valueOf(params[0]);
        final float health = Float.valueOf(params[1]);
        final float maxhealth = Float.valueOf(params[2]);

        final int heartsPerLine = (int) (Math.min(maxhearts, Math.ceil(maxhealth)));
        final int nlines = (int) (Math.ceil(maxhealth / maxhearts));

        return new Dimension(8 * heartsPerLine, 10 * nlines - 2);
    }

    @Override
    public void draw(final String[] params, final IWailaCommonAccessor accessor) {
        final float maxhearts = Float.valueOf(params[0]);
        final float health = Float.valueOf(params[1]);
        final float maxhealth = Float.valueOf(params[2]);

        final int nhearts = MathHelper.ceil(maxhealth);
        final int heartsPerLine = (int) (Math.min(maxhearts, Math.ceil(maxhealth)));

        int offsetX = 0;
        int offsetY = 0;

        for (int iheart = 1; iheart <= nhearts; iheart++) {


            if (iheart <= MathHelper.floor(health)) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.HEART);
                offsetX += 8;
            }

            if ((iheart > health) && (iheart < health + 1)) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.HHEART);
                offsetX += 8;
            }

            if (iheart >= health + 1) {
                DisplayUtil.renderIcon(offsetX, offsetY, 8, 8, IconUI.EHEART);
                offsetX += 8;
            }

            if (iheart % heartsPerLine == 0) {
                offsetY += 10;
                offsetX = 0;
            }

        }
    }
}
