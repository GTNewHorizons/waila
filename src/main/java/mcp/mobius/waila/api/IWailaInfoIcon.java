package mcp.mobius.waila.api;

/**
 * Represents a single icon in the bottom right of the waila tooltip and is the icon equivalent of the line strings
 * returned by the getWailaBody/Head/Tail callbacks.</br>
 *
 * Info icons mirror line strings as much as possible and {@link IWailaDataProvider}s can provide them by overriding the
 * getWailaInfoIcon method.
 *
 * @author SuperSouper
 *
 */
public interface IWailaInfoIcon {

    /**
     * Returns the width of the icon.</br>
     * No padding is added if returns 0.
     *
     * @param accessor A global accessor for TileEntities and Entities
     * @return The width in pixels.
     */
    int getWidth(IWailaCommonAccessor accessor);

    /**
     * Draws the icon onto the tooltip.</br>
     * Position is already translated so that 0,0 is the top-left position of the icon.
     *
     * @param accessor A global accessor for TileEntities and Entities
     */
    void draw(IWailaCommonAccessor accessor);
}
