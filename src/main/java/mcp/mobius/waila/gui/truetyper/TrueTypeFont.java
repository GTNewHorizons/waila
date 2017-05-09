package mcp.mobius.waila.gui.truetyper;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * TrueTyper: Open Source TTF implementation for Minecraft.
 * Modified from Slick2D - under BSD Licensing -  http://slick.ninjacave.com/license/
 * <p>
 * Copyright (c) 2013 - Slick2D
 * <p>
 * All rights reserved.
 */

public class TrueTypeFont {
    public final static int
            ALIGN_LEFT = 0,
            ALIGN_RIGHT = 1,
            ALIGN_CENTER = 2;
    /**
     * Array that holds necessary information about the font characters
     */
    private FloatObject[] charArray = new FloatObject[256];
    /**
     * Map of user defined font characters (Character <-> IntObject)
     */
    private Map customChars = new HashMap();
    /**
     * Boolean flag on whether AntiAliasing is enabled or not
     */
    protected boolean antiAlias;
    /**
     * Font's size
     */
    private float fontSize = 0;
    /**
     * Font's height
     */
    private float fontHeight = 0;
    /**
     * Texture used to cache the font 0-255 characters
     */
    private int fontTextureID;
    /**
     * Default font texture width
     */
    private int textureWidth = 1024;
    /**
     * Default font texture height
     */
    private int textureHeight = 1024;
    /**
     * A reference to Java's AWT Font that we create our font texture from
     */
    protected Font font;
    /**
     * The font metrics for our Java AWT font
     */
    private FontMetrics fontMetrics;


    private int correctL = 9, correctR = 8;

    private class FloatObject {
        public float width;
        public float height;
        public float storedX;
        public float storedY;
    }


    public TrueTypeFont(final Font font, final boolean antiAlias, final char[] additionalChars) {
        this.font = font;
        this.fontSize = font.getSize() + 3;
        this.antiAlias = antiAlias;

        createSet(additionalChars);
        System.out.println("TrueTypeFont loaded: " + font + " - AntiAlias = " + antiAlias);
        fontHeight -= 1;
        if (fontHeight <= 0) fontHeight = 1;
    }

    public TrueTypeFont(final Font font, final boolean antiAlias) {
        this(font, antiAlias, null);
    }

    public void setCorrection(final boolean on) {
        if (on) {
            correctL = 2;
            correctR = 1;
        } else {
            correctL = 0;
            correctR = 0;
        }
    }

    private BufferedImage getFontImage(final char ch) {
        // Create a temporary image to extract the character's size
        final BufferedImage tempfontImage = new BufferedImage(1, 1,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = (Graphics2D) tempfontImage.getGraphics();
        if (antiAlias == true) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        g.setFont(font);
        fontMetrics = g.getFontMetrics();
        float charwidth = fontMetrics.charWidth(ch) + 8;

        if (charwidth <= 0) {
            charwidth = 7;
        }
        float charheight = fontMetrics.getHeight() + 3;
        if (charheight <= 0) {
            charheight = fontSize;
        }

        // Create another image holding the character we are creating
        final BufferedImage fontImage;
        fontImage = new BufferedImage((int) charwidth, (int) charheight,
                BufferedImage.TYPE_INT_ARGB);
        final Graphics2D gt = (Graphics2D) fontImage.getGraphics();
        if (antiAlias == true) {
            gt.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        gt.setFont(font);

        gt.setColor(Color.WHITE);
        final int charx = 3;
        final int chary = 1;
        gt.drawString(String.valueOf(ch), (charx), (chary)
                + fontMetrics.getAscent());

        return fontImage;

    }

    private void createSet(final char[] customCharsArray) {
        // If there are custom chars then I expand the font texture twice
        if (customCharsArray != null && customCharsArray.length > 0) {
            textureWidth *= 2;
        }

        // In any case this should be done in other way. Texture with size 512x512
        // can maintain only 256 characters with resolution of 32x32. The texture
        // size should be calculated dynamicaly by looking at character sizes.

        try {

            final BufferedImage imgTemp = new BufferedImage(textureWidth, textureHeight, BufferedImage.TYPE_INT_ARGB);
            final Graphics2D g = (Graphics2D) imgTemp.getGraphics();

            g.setColor(new Color(0, 0, 0, 1));
            g.fillRect(0, 0, textureWidth, textureHeight);

            float rowHeight = 0;
            float positionX = 0;
            float positionY = 0;

            final int customCharsLength = (customCharsArray != null) ? customCharsArray.length : 0;

            for (int i = 0; i < 256 + customCharsLength; i++) {

                // get 0-255 characters and then custom characters
                final char ch = (i < 256) ? (char) i : customCharsArray[i - 256];

                BufferedImage fontImage = getFontImage(ch);

                final FloatObject newIntObject = new FloatObject();

                newIntObject.width = fontImage.getWidth();
                newIntObject.height = fontImage.getHeight();

                if (positionX + newIntObject.width >= textureWidth) {
                    positionX = 0;
                    positionY += rowHeight;
                    rowHeight = 0;
                }

                newIntObject.storedX = positionX;
                newIntObject.storedY = positionY;

                if (newIntObject.height > fontHeight) {
                    fontHeight = newIntObject.height;
                }

                if (newIntObject.height > rowHeight) {
                    rowHeight = newIntObject.height;
                }

                // Draw it here
                g.drawImage(fontImage, (int) positionX, (int) positionY, null);

                positionX += newIntObject.width;

                if (i < 256) { // standard characters
                    charArray[i] = newIntObject;
                } else { // custom characters
                    customChars.put(new Character(ch), newIntObject);
                }

                fontImage = null;
            }

            fontTextureID = loadImage(imgTemp);


            //.getTexture(font.toString(), imgTemp);

        } catch (final Exception e) {
            System.err.println("Failed to create font.");
            e.printStackTrace();
        }
    }

    private void drawQuad(final float drawX, final float drawY, final float drawX2, final float drawY2,
                          final float srcX, final float srcY, final float srcX2, final float srcY2) {
        final float DrawWidth = drawX2 - drawX;
        final float DrawHeight = drawY2 - drawY;
        final float TextureSrcX = srcX / textureWidth;
        final float TextureSrcY = srcY / textureHeight;
        final float SrcWidth = srcX2 - srcX;
        final float SrcHeight = srcY2 - srcY;
        final float RenderWidth = (SrcWidth / textureWidth);
        final float RenderHeight = (SrcHeight / textureHeight);
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();

        //t.setColorRGBA_F(0f, 0f, 0f, 1f);

        t.pos((double) drawX, (double) drawY, 0).tex((double) ((float) TextureSrcX), (double) ((float) TextureSrcY)).endVertex();
        //GL11.glTexCoord2f(TextureSrcX, TextureSrcY);
        //GL11.glVertex2f(drawX, drawY);

        t.pos((double) drawX, (double) (drawY + DrawHeight), 0).tex((double) ((float) TextureSrcX), (double) ((float) (TextureSrcY + RenderHeight))).endVertex();
        //GL11.glTexCoord2f(TextureSrcX, TextureSrcY + RenderHeight);
        //GL11.glVertex2f(drawX, drawY + DrawHeight);

        t.pos((double) drawX + DrawWidth, (double) (drawY + DrawHeight), 0).tex((double) ((float) (TextureSrcX + RenderWidth)), (double) ((float) (TextureSrcY + RenderHeight))).endVertex();
        //GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY + RenderHeight);
        //GL11.glVertex2f(drawX + DrawWidth, drawY + DrawHeight);

        t.pos((double) (drawX + DrawWidth), (double) drawY, 0).tex((double) ((float) (TextureSrcX + RenderWidth)), (double) ((float) TextureSrcY)).endVertex();
        //GL11.glTexCoord2f(TextureSrcX + RenderWidth, TextureSrcY);
        //GL11.glVertex2f(drawX + DrawWidth, drawY);
    }

    public float getWidth(final String whatchars) {
        float totalwidth = 0;
        FloatObject floatObject = null;
        int currentChar = 0;
        float lastWidth = -10f;
        for (int i = 0; i < whatchars.length(); i++) {
            currentChar = whatchars.charAt(i);
            if (currentChar < 256) {
                floatObject = charArray[currentChar];
            } else {
                floatObject = (FloatObject) customChars.get(new Character((char) currentChar));
            }

            if (floatObject != null) {
                totalwidth += floatObject.width / 2;
                lastWidth = floatObject.width;
            }
        }
        //System.out.println("Size: "+totalwidth);
        return this.fontMetrics.stringWidth(whatchars);
        //return (totalwidth);
    }

    public float getHeight() {
        return fontHeight;
    }

    public float getHeight(final String HeightString) {
        return fontHeight;
    }

    public float getLineHeight() {
        return fontHeight;
    }

    public void drawString(final float x, final float y, final String whatchars, final float scaleX, final float scaleY, float... rgba) {
        if (rgba.length == 0) rgba = new float[]{1f, 1f, 1f, 1f};
        drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, ALIGN_LEFT, rgba);
    }

    public void drawString(final float x, final float y, final String whatchars, final float scaleX, final float scaleY, final int format, float... rgba) {
        if (rgba.length == 0) rgba = new float[]{1f, 1f, 1f, 1f};

        drawString(x, y, whatchars, 0, whatchars.length() - 1, scaleX, scaleY, format, rgba);
    }


    public void drawString(final float x, final float y, final String whatchars, final int startIndex, final int endIndex, final float scaleX, final float scaleY, final int format, float... rgba) {
        if (rgba.length == 0) rgba = new float[]{1f, 1f, 1f, 1f};
        GL11.glPushMatrix();
        GL11.glScalef(scaleX, scaleY, 1.0f);

        FloatObject floatObject = null;
        int charCurrent;

        float totalwidth = 0;
        int i = startIndex;
        int d;
        final int c;
        float startY = 0;

        switch (format) {
            case ALIGN_RIGHT: {
                d = -1;
                c = correctR;

                while (i < endIndex) {
                    if (whatchars.charAt(i) == '\n') startY -= fontHeight;
                    i++;
                }
                break;
            }
            case ALIGN_CENTER: {
                for (int l = startIndex; l <= endIndex; l++) {
                    charCurrent = whatchars.charAt(l);
                    if (charCurrent == '\n') break;
                    if (charCurrent < 256) {
                        floatObject = charArray[charCurrent];
                    } else {
                        floatObject = (FloatObject) customChars.get(new Character((char) charCurrent));
                    }
                    totalwidth += floatObject.width - correctL;
                }
                totalwidth /= -2;
            }
            case ALIGN_LEFT:
            default: {
                d = 1;
                c = correctL;
                break;
            }

        }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, fontTextureID);
        final Tessellator tessellator = Tessellator.getInstance();
        final VertexBuffer t = tessellator.getBuffer();
        t.begin(7, DefaultVertexFormats.POSITION_COLOR);
        //	GL11.glBegin(GL11.GL_QUADS);
        if (rgba.length == 4)
            t.putColorRGB_F(rgba[0], rgba[1], rgba[2], (int) rgba[3]);
        while (i >= startIndex && i <= endIndex) {

            charCurrent = whatchars.charAt(i);
            if (charCurrent < 256) {
                floatObject = charArray[charCurrent];
            } else {
                floatObject = (FloatObject) customChars.get(new Character((char) charCurrent));
            }

            if (floatObject != null) {
                if (d < 0) totalwidth += (floatObject.width - c) * d;
                if (charCurrent == '\n') {
                    startY -= fontHeight * d;
                    totalwidth = 0;
                    if (format == ALIGN_CENTER) {
                        for (int l = i + 1; l <= endIndex; l++) {
                            charCurrent = whatchars.charAt(l);
                            if (charCurrent == '\n') break;
                            if (charCurrent < 256) {
                                floatObject = charArray[charCurrent];
                            } else {
                                floatObject = (FloatObject) customChars.get(new Character((char) charCurrent));
                            }
                            totalwidth += floatObject.width - correctL;
                        }
                        totalwidth /= -2;
                    }
                    //if center get next lines total width/2;
                } else {
                    drawQuad((totalwidth + floatObject.width) + x / scaleX,
                            startY + y / scaleY,
                            totalwidth + x / scaleX,
                            (startY + floatObject.height) + y / scaleY,
                            floatObject.storedX + floatObject.width,
                            floatObject.storedY + floatObject.height,
                            floatObject.storedX,
                            floatObject.storedY);
                    if (d > 0) totalwidth += (floatObject.width - c) * d;
                }
                i += d;

            }
        }
        tessellator.draw();
        //	GL11.glEnd();

        GL11.glPopMatrix();
    }

    public static int loadImage(final BufferedImage bufferedImage) {
        try {
            final short width = (short) bufferedImage.getWidth();
            final short height = (short) bufferedImage.getHeight();
            //textureLoader.bpp = bufferedImage.getColorModel().hasAlpha() ? (byte)32 : (byte)24;
            final int bpp = (byte) bufferedImage.getColorModel().getPixelSize();
            final ByteBuffer byteBuffer;
            final DataBuffer db = bufferedImage.getData().getDataBuffer();
            if (db instanceof DataBufferInt) {
                final int[] intI = ((DataBufferInt) (bufferedImage.getData().getDataBuffer())).getData();
                final byte[] newI = new byte[intI.length * 4];
                for (int i = 0; i < intI.length; i++) {
                    final byte[] b = intToByteArray(intI[i]);
                    final int newIndex = i * 4;

                    newI[newIndex] = b[1];
                    newI[newIndex + 1] = b[2];
                    newI[newIndex + 2] = b[3];
                    newI[newIndex + 3] = b[0];
                }

                byteBuffer = ByteBuffer.allocateDirect(
                        width * height * (bpp / 8))
                        .order(ByteOrder.nativeOrder())
                        .put(newI);
            } else {
                byteBuffer = ByteBuffer.allocateDirect(
                        width * height * (bpp / 8))
                        .order(ByteOrder.nativeOrder())
                        .put(((DataBufferByte) (bufferedImage.getData().getDataBuffer())).getData());
            }
            byteBuffer.flip();


            final int internalFormat = GL11.GL_RGBA8;
            final int format = GL11.GL_RGBA;
            final IntBuffer textureId = BufferUtils.createIntBuffer(1);
            GL11.glGenTextures(textureId);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId.get(0));

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);


            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_NEAREST);

            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_LINEAR);
            //GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST_MIPMAP_NEAREST);

            GL11.glTexEnvf(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL11.GL_MODULATE);

            GLU.gluBuild2DMipmaps(GL11.GL_TEXTURE_2D, internalFormat, width, height, format, GL11.GL_UNSIGNED_BYTE, byteBuffer);
            return textureId.get(0);

        } catch (final Exception e) {
            throw new RuntimeException(e);
            //System.exit(-1);
        }
    }

    public static boolean isSupported(final String fontname) {
        final Font[] font = getFonts();
        for (int i = font.length - 1; i >= 0; i--) {
            if (font[i].getName().equalsIgnoreCase(fontname))
                return true;
        }
        return false;
    }

    public static Font[] getFonts() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    }

    public static byte[] intToByteArray(final int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }

    public void destroy() {
        final IntBuffer scratch = BufferUtils.createIntBuffer(1);
        scratch.put(0, fontTextureID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glDeleteTextures(scratch);
    }
}