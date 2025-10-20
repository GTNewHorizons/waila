package mcp.mobius.waila.api;

/**
 * Interface for tooltip renderers that adjust their width depending on the width of the rest of the tooltip.
 * setMaxLineWidth is called by {@link mcp.mobius.waila.overlay.Tooltip} before every tooltip render to inform the
 * renderer of the width of the longest line in the tooltip.
 */
public interface IWailaVariableWidthTooltipRenderer extends IWailaTooltipRenderer {

    /**
     * Informs the renderer of what the length of the longest line is.</br>
     * Called by {@link mcp.mobius.waila.overlay.Tooltip} before every tooltip render.</br>
     *
     * @param width The width of the longest line in the tooltip.
     *
     */
    void setMaxLineWidth(int width);

    /**
     * Returns the width of the longest line in the current tooltip.</br>
     * With icons disabled the tooltip's background box is always 2 pixels wider to the right of the line than it is to
     * the left.</br>
     *
     * @return The width of the longest line in the tooltip
     *
     */
    int getMaxLineWidth();

}
