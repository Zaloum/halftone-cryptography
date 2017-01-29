package com.zaloum.cryptography.image.effects;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 * Dithers an image using the Floyd–Steinberg algorithm resulting in an image that is restricted to only black and
 * white pixels, but producing a basic optical illusion where the human eye perceives shades of gray.
 */
public class Halftone {

    private static final int THRESHOLD = 128;

    /**
     * Dithers the image using the Floyd–Steinberg algorithm.
     * @param srcImage
     * @return
     */
    public static BufferedImage image(BufferedImage srcImage) {
        final int width  = srcImage.getWidth();
        final int height = srcImage.getHeight();

        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        WritableRaster inputRaster = srcImage.copyData(null);
        WritableRaster outputRaster = outputImage.getRaster();

        for (int y=0;y<height;y++) {
            for (int x=0;x<width;x++) {
                int value = inputRaster.getSample(x, y, 0);
                int error;

                if (value < THRESHOLD) {
                    outputRaster.setSample(x, y, 0, 0);
                    error = value;
                } else {
                    outputRaster.setSample(x, y, 0, 1);
                    error = value - 255;
                }

                // Distribute the error to adjacent pixels that we have not altered yet.
                if (x > 0 && y > 0 && x < (width - 1) && y < (height - 1)) {
                    inputRaster.setSample(x + 1, y, 0, clamp(inputRaster.getSample(x + 1, y, 0) + 7.0 / 16 * error));
                    inputRaster.setSample(x + 1, y + 1, 0, clamp(inputRaster.getSample(x + 1, y + 1, 0) + 1.0 / 16 * error));
                    inputRaster.setSample(x, y + 1, 0, clamp(inputRaster.getSample(x, y + 1, 0) + 5.0 / 16 * error));
                    inputRaster.setSample(x - 1, y + 1, 0, clamp(inputRaster.getSample(x - 1, y + 1, 0) + 3.0 / 16 * error));
                }
            }
        }

        return outputImage;
    }

    private static int clamp(double val) {
        return (int)Math.max(0, Math.min(255, Math.round(val)));
    }
}
