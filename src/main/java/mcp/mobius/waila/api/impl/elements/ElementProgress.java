package mcp.mobius.waila.api.impl.elements;

import java.text.DecimalFormat;

import mcp.mobius.waila.api.NumberFormat;
import mcp.mobius.waila.api.elements.IElement;
import mcp.mobius.waila.api.elements.IProgressStyle;
import mcp.mobius.waila.overlay.DisplayUtil;

public class ElementProgress implements IElement {

    private final long current;
    private final long max;
    private final IProgressStyle style;

    public ElementProgress(long current, long max, IProgressStyle style) {
        this.current = current;
        this.max = max;
        this.style = style;
    }

    private static DecimalFormat dfCommas = new DecimalFormat("###,###");

    /**
     * If the suffix starts with 'm' we can possibly drop that
     */
    public static String format(long in, NumberFormat style, String suffix) {
        switch (style) {
            case FULL:
                return Long.toString(in) + suffix;
            case COMPACT: {
                int unit = 1000;
                if (in < unit) {
                    return Long.toString(in) + " " + suffix;
                }
                int exp = (int) (Math.log(in) / Math.log(unit));
                char pre;
                if (suffix.startsWith("m")) {
                    suffix = suffix.substring(1);
                    if (exp - 2 >= 0) {
                        pre = "kMGTPE".charAt(exp - 2);
                        return String.format("%.1f %s", in / Math.pow(unit, exp), pre) + suffix;
                    } else {
                        return String.format("%.1f %s", in / Math.pow(unit, exp), suffix);
                    }
                } else {
                    pre = "kMGTPE".charAt(exp - 1);
                    return String.format("%.1f %s", in / Math.pow(unit, exp), pre) + suffix;
                }
            }
            case COMMAS:
                return dfCommas.format(in) + suffix;
            case NONE:
                return suffix;
        }
        return Long.toString(in);
    }

    @Override
    public void render(int x, int y) {
        render(style, current, max, x, y, getWidth(), getHeight());
    }

    private static void render(IProgressStyle style, long current, long max, int x, int y, int w, int h) {
        DisplayUtil.drawThickBeveledBox(
                x,
                y,
                x + w,
                y + h,
                1,
                style.getBorderColor(),
                style.getBorderColor(),
                style.getBackgroundColor());
        if (current > 0L && max > 0L) {
            int dx = (int) Math.min(current * (long) (w - 2) / max, (long) (w - 2));
            if (style.getFilledColor() == style.getAlternatefilledColor()) {
                if (dx > 0) {
                    DisplayUtil.drawThickBeveledBox(
                            x + 1,
                            y + 1,
                            x + dx + 1,
                            y + h - 1,
                            1,
                            style.getFilledColor(),
                            style.getFilledColor(),
                            style.getFilledColor());
                }
            } else {
                for (int xx = x + 1; xx <= x + dx + 1; ++xx) {
                    int color = (xx & 1) == 0 ? style.getFilledColor() : style.getAlternatefilledColor();
                    DisplayUtil.drawVerticalLine(xx, y + 1, y + h - 1, color);
                }
            }
        }

        if (style.isShowText()) {
            DisplayUtil.drawString(style.getText(), x + 3, y + 2, style.getTextColor(), style.isTextShadow());
        }
    }

    @Override
    public int getWidth() {
        if (style.isLifeBar()) {
            if (current * 4 >= style.getWidth()) {
                return 100;
            } else {
                return (int) (current * 4 + 2);
            }
        }
        return style.getWidth();
    }

    @Override
    public int getHeight() {
        return style.getHeight();
    }
}
