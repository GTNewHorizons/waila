package mcp.mobius.waila.api.elements;

public interface ILayoutStyle {

    ILayoutStyle borderColor(Integer var1);

    ILayoutStyle spacing(int var1);

    ILayoutStyle alignment(ElementAlignment var1);

    Integer getBorderColor();

    int getSpacing();

    ElementAlignment getAlignment();
}
