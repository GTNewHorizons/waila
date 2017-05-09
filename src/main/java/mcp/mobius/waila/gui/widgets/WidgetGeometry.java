package mcp.mobius.waila.gui.widgets;

import mcp.mobius.waila.gui.interfaces.CType;
import mcp.mobius.waila.gui.interfaces.IWidget;
import mcp.mobius.waila.gui.interfaces.WAlign;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.Point;


//public class WidgetGeometry implements Cloneable{
public class WidgetGeometry {

    //public enum Align {LEFT, CENTER, RIGHT, TOP, BOTTOM};


    double x = -1;
    double y = -1;
    double sx = -1;
    double sy = -1;

    CType posType;
    CType sizeType;

    boolean fracPosX = false;
    boolean fracPosY = false;
    boolean fracSizeX = false;
    boolean fracSizeY = false;

    WAlign alignX;
    WAlign alignY;

    public class PointDouble {
        double x;
        double y;

        public PointDouble(final double x, final double y) {
            this.x = x;
            this.y = y;
        }

        ;

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public String toString() {
            return String.format("PointDouble : %.5f %.5f", this.x, this.y);
        }
    }

    public WidgetGeometry(final double x, final double y, final double sx, final double sy, final CType fracPos, final CType fracSize) {
        this(x, y, sx, sy, fracPos, fracSize, WAlign.LEFT, WAlign.TOP);
    }

    public WidgetGeometry(final double x, final double y, final double sx, final double sy, final CType fracPos, final CType fracSize, final WAlign alignX, final WAlign alignY) {
        this.x = x;
        this.y = y;
        this.sx = sx;
        this.sy = sy;
        this.posType = fracPos;
        this.sizeType = fracSize;

        switch (fracPos) {
            case REL_X:
                this.fracPosX = true;
                break;
            case RELXY:
                this.fracPosX = true;
                this.fracPosY = true;
                break;
            case REL_Y:
                this.fracPosY = true;
                break;
            default:
                break;

        }

        switch (fracSize) {
            case REL_X:
                this.fracSizeX = true;
                break;
            case RELXY:
                this.fracSizeX = true;
                this.fracSizeY = true;
                break;
            case REL_Y:
                this.fracSizeY = true;
                break;
            default:
                break;

        }

        this.alignX = alignX;
        this.alignY = alignY;
    }

    public void setPos(final double x, final double y, final boolean fracX, final boolean fracY) {
        this.x = x;
        this.y = y;
        this.fracPosX = fracX;
        this.fracPosY = fracY;
    }

    public void setSize(final double sx, final double sy, final boolean fracSizeX, final boolean fracSizeY) {
        this.sx = sx;
        this.sy = sy;
        this.fracSizeX = fracSizeX;
        this.fracSizeY = fracSizeY;
    }

    public PointDouble getRawPos() {
        return new PointDouble(this.x, this.y);
    }

    public Point getUnalignedPos(final IWidget parent) {
        int x = -1;
        if (this.fracPosX)
            x = MathHelper.ceil(parent.getPos().getX() + parent.getSize().getX() * this.x / 100D);
        if (!this.fracPosX && parent != null)
            x = parent.getPos().getX() + (int) this.x;
        if (!this.fracPosX && parent == null)
            x = (int) this.x;

        int y = -1;
        if (this.fracPosY)
            y = MathHelper.ceil(parent.getPos().getY() + parent.getSize().getY() * this.y / 100D);
        if (!this.fracPosY && parent != null)
            y = parent.getPos().getY() + (int) this.y;
        if (!this.fracPosY && parent == null)
            y = (int) this.y;

        return new Point(x, y);
    }

    public Point getPos(final IWidget parent) {

        int x = -1;
        if (this.fracPosX)
            x = MathHelper.ceil(parent.getPos().getX() + parent.getSize().getX() * this.x / 100D);
        if (!this.fracPosX && parent != null)
            x = parent.getPos().getX() + (int) this.x;
        if (!this.fracPosX && parent == null)
            x = (int) this.x;

        int y = -1;
        if (this.fracPosY)
            y = MathHelper.ceil(parent.getPos().getY() + parent.getSize().getY() * this.y / 100D);
        if (!this.fracPosY && parent != null)
            y = parent.getPos().getY() + (int) this.y;
        if (!this.fracPosY && parent == null)
            y = (int) this.y;


        if (this.alignX == WAlign.CENTER)
            x -= this.getSize(parent).getX() / 2;

        if (this.alignY == WAlign.CENTER)
            y -= this.getSize(parent).getY() / 2;

        if (this.alignX == WAlign.RIGHT)
            x -= this.getSize(parent).getX() - 1;

        if (this.alignY == WAlign.BOTTOM)
            y -= this.getSize(parent).getY() - 1;

        return new Point(x, y);
    }

    public Point getSize(final IWidget parent) {
        int sx = -1;
        if (this.fracSizeX)
            sx = MathHelper.ceil(parent.getSize().getX() * this.sx / 100D);
        if (!this.fracSizeX)
            sx = (int) this.sx;

        int sy = -1;
        if (this.fracSizeY)
            sy = MathHelper.ceil(parent.getSize().getY() * this.sy / 100D);
        if (!this.fracSizeY)
            sy = (int) this.sy;

        return new Point(sx, sy);
    }

    public String toString() {
        return String.format("Geometry : [%s %s] [%s %s] [%s %s] [%s %s]", this.x, this.y, this.sx, this.sy, this.posType, this.sizeType, this.alignX, this.alignY);
    }

    //@Override
    //public WidgetGeometry clone() throws CloneNotSupportedException{
    //	return (WidgetGeometry)super.clone();
    //}
}
