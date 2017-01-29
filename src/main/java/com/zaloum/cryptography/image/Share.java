package com.zaloum.cryptography.image;

import java.awt.image.BufferedImage;
import java.util.List;

public class Share extends BinaryImage {

    /**
     * Constructs and returns a new share for the original image.
     * @param originalImage
     * @return
     * @NullPointerException if the original image is null
     */
    public static Share forImage(BinaryImage originalImage) {
        if (originalImage == null) {
            throw new NullPointerException("The original image was null.");
        }
        return new Share(originalImage.getWidth(), originalImage.getHeight());
    }

    /**
     * Constructs a new share from an existing image.
     * @param srcImage
     */
    public Share(BufferedImage srcImage) {
        super(srcImage);
    }

    /**
     * Constructs a new share that is a copy of the source share.
     * @param srcShare
     * @throws NullPointerException if the source image is null
     */
    public Share(Share srcShare) {
        super(srcShare);
    }

    /**
     * Constructs a new share with the specified dimensions.
     * @param width
     * @param height
     */
    public Share(final int width, final int height) {
        super(width * 2, height * 2);
    }

    /**
     * Sets the sub pixels that correspond to the pixel at x, y to:
     * <table border="1">
     * 	<tr><td>0</td><td>1</td></tr>
     *  <tr><td>1</td><td>0</td></tr>
     * </table>
     * @param x
     * @param y
     */
    public void setDiagonal(int x, int y) {
        x *= 2;
        y *= 2;
        srcData[flatten(x, y)]               = BinaryImage.BLACK;
        srcData[flatten(x + 1, y)]        = BinaryImage.WHITE;
        srcData[flatten(x, y + 1)]        = BinaryImage.WHITE;
        srcData[flatten(x + 1, y + 1)] = BinaryImage.BLACK;
    }

    /**
     * Sets the sub pixels that correspond to the pixel at x, y to:
     * <table border="1">
     * 	<tr><td>1</td><td>0</td></tr>
     *  <tr><td>0</td><td>1</td></tr>
     * </table>
     * @param x
     * @param y
     */
    public void setAntiDiagonal(int x, int y) {
        x *= 2;
        y *= 2;
        srcData[flatten(x, y)]               = BinaryImage.WHITE;
        srcData[flatten(x + 1, y)] 	     = BinaryImage.BLACK;
        srcData[flatten(x, y + 1)] 	     = BinaryImage.BLACK;
        srcData[flatten(x + 1, y + 1)] = BinaryImage.WHITE;
    }

    /**
     * Combines this Share with another. Alters the original share.
     * @param other
     * @throws NullPointerException if other is null
     */
    public void combine(Share other) {
        if (other == null) {
            throw new NullPointerException("The share must not be null");
        }

        for (int j = 0; j < srcData.length; j++) {
            srcData[j] ^= other.getPixel(j);
        }
    }

    /**
     * Combines shares on top of each other.
     * @param shares
     * @return the resulting image
     * @throws NullPointerException if shares is null
     * @throws IllegalArgumentException if shares is empty, or if a share has different dimensions from any other
     */
    public static BinaryImage combine(List<Share> shares) {
        if (shares == null) {
            throw new NullPointerException("Shares must not be null.");
        }
        if (shares.size() == 0) {
            throw new IllegalArgumentException("There must be at least one share.");
        }
        if (shares.size() == 1) {
            // There's nothing to combine.
            return shares.get(0);
        }

        Share share = shares.get(0);

        final Share output = new Share(share);

        for (int i = 1; i < shares.size(); i++) {
            share = shares.get(i);
            if (share.getWidth() != output.getWidth() || share.getHeight() != output.getHeight()) {
                throw new IllegalArgumentException("The shares must all have the same dimensions.");
            }

            for (int j = 0; j < output.srcData.length; j++) {
                output.srcData[j] ^= share.getPixel(j);
            }
        }
        return output;
    }
}
