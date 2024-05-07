package mcp.mobius.waila.api.impl.elements;

import mcp.mobius.waila.api.NumberFormat;
import mcp.mobius.waila.api.elements.IProgressStyle;

public class ProgressStyle implements IProgressStyle {
    private int borderColor = -1;
    private int backgroundColor = -16777216;
    private int filledColor = -5592406;
    private int alternatefilledColor = -5592406;
    private boolean showText = true;
    private String prefix = "";
    private String suffix = "";
    private int width = 100;
    private int height = 12;
    private boolean lifeBar = false;
    private boolean armorBar = false;
    private NumberFormat numberFormat;

    public ProgressStyle() {
        this.numberFormat = NumberFormat.COMPACT;
    }

    public ProgressStyle borderColor(int c) {
        this.borderColor = c;
        return this;
    }

    public ProgressStyle backgroundColor(int c) {
        this.backgroundColor = c;
        return this;
    }

    public ProgressStyle filledColor(int c) {
        this.filledColor = c;
        return this;
    }

    public ProgressStyle alternateFilledColor(int c) {
        this.alternatefilledColor = c;
        return this;
    }

    public ProgressStyle showText(boolean b) {
        this.showText = b;
        return this;
    }

    public ProgressStyle numberFormat(NumberFormat f) {
        this.numberFormat = f;
        return this;
    }

    public ProgressStyle prefix(String prefix) {
        this.prefix = prefix;
        return this;
    }

    public ProgressStyle suffix(String suffix) {
        this.suffix = suffix;
        return this;
    }

    public ProgressStyle width(int w) {
        this.width = w;
        return this;
    }

    public ProgressStyle height(int h) {
        this.height = h;
        return this;
    }

    public IProgressStyle lifeBar(boolean b) {
        this.lifeBar = b;
        return this;
    }

    public IProgressStyle armorBar(boolean b) {
        this.armorBar = b;
        return this;
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getFilledColor() {
        return this.filledColor;
    }

    public int getAlternatefilledColor() {
        return this.alternatefilledColor;
    }

    public boolean isShowText() {
        return this.showText;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isLifeBar() {
        return this.lifeBar;
    }

    public boolean isArmorBar() {
        return this.armorBar;
    }
}
