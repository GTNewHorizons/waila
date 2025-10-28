package mcp.mobius.waila.api;

public interface IWailaInfoIcon {

    // Padding to the left of the icon must be included in the width.
    int getWidth(IWailaCommonAccessor accessor);

    void draw(IWailaCommonAccessor accessor);
}
