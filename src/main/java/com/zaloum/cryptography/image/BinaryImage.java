package com.zaloum.cryptography.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * An image that has only two possible values for each pixel.
 */
public class BinaryImage {

    public static final byte WHITE = 1;
    public static final byte BLACK = 0;

    protected final byte[] srcData;
    private final int width;
    private final int height;

    /**
     * Constructs a new image from an existing {@link BufferedImage}.
     * @param srcImage
     * @throws NullPointerException if the source image is null
     * @throws IllegalArgumentException if the source image type is not {@link BufferedImage#TYPE_BYTE_BINARY}
     */
    public BinaryImage(BufferedImage srcImage) {
        if (srcImage == null) {
            throw new NullPointerException("The source image is null.");
        }
        if (srcImage.getType() != BufferedImage.TYPE_BYTE_BINARY) {
            throw new IllegalArgumentException("The image type must be BufferedImage.TYPE_BYTE_BINARY.");
        }
        this.width = srcImage.getWidth();
        this.height = srcImage.getHeight();
        this.srcData = (byte[])srcImage.getRaster()
                                       .getDataElements(0, 0, width, height, null);
    }

    /**
     * Constructs a new image that is a copy of the source image.
     * @param srcImage
     * @throws NullPointerException if the source image is null
     */
    public BinaryImage(BinaryImage srcImage) {
        if (srcImage == null) {
            throw new NullPointerException("The source image is null.");
        }
        this.width = srcImage.width;
        this.height = srcImage.height;
        this.srcData = srcImage.getData();
    }

    /**
     * Constructs a new image with the specified dimensions and sets all of the
     * pixels to {@link #WHITE}.
     * @param width
     * @param height
     */
    public BinaryImage(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.srcData = new byte[width * height];
        setUniversal();
    }

    public BufferedImage toBufferedImage() {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = out.getRaster();
        raster.setDataElements(0, 0, width, height, srcData);
        return out;
    }

    public BufferedImage toBufferedImage(Color color1, Color color2) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                out.setRGB(x, y, srcData[flatten(x, y)] == WHITE ? color1.getRGB() : color2.getRGB());
            }
        }
        return out;
    }

    /**
     * Sets all pixels to {@link #WHITE}.
     */
    public void setUniversal() {
        Arrays.fill(srcData, WHITE);
    }

    /**
     * Sets all pixels to {@link #BLACK}.
     */
    public void setEmpty() {
        Arrays.fill(srcData, BLACK);
    }

    public boolean isWhite(int x, int y) {
        return getPixel(x, y) == WHITE;
    }

    public boolean isBlack(int x, int y) {
        return getPixel(x, y) == BLACK;
    }

    public byte getPixel(int x, int y) {
        return srcData[flatten(x, y)];
    }

    public byte getPixel(int i) {
        return srcData[i];
    }

    public void setPixel(int x, int y, byte pixel) {
        srcData[flatten(x, y)] = pixel;
    }

    public void setPixel(int i, byte pixel) {
        srcData[i] = pixel;
    }

    /**
     * Returns a copy of the array backing this image.
     * @return
     */
    public byte[] getData() {
        return Arrays.copyOf(srcData, srcData.length);
    }

    byte[] getRawData() {
        return srcData;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected int flatten(int x, int y) {
        return y * width + x;
    }
}
