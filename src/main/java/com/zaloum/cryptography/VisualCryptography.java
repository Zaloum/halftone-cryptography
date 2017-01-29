package com.zaloum.cryptography;

import com.zaloum.cryptography.image.BinaryImage;
import com.zaloum.cryptography.image.Share;

import java.util.Arrays;
import java.util.List;

public class VisualCryptography {

    /**
     * Encrypts an image into a list of shares, of which, when placed back together,
     * form a recognizable (but not perfect) black and white copy of the image.</p>
     * All shares must be present to {@link #decode} them.
     * @param image the source image
     * @return a list of shares
     * @throws NullPointerException if the image is null
     */
    public static List<Share> encode(BinaryImage image) {
        if (image == null) {
            throw new IllegalArgumentException("The source image is null.");
        }

        // Two shares initialized to all white
        final Share share1 = Share.forImage(image);
        final Share share2 = Share.forImage(image);

        final double n = 0.5;	// 50/50 chance of each pattern

        // We convert each pixel into 4 sub pixels in a 2x2 grid. If a pixel is black on the half-toned
        // source image, the corresponding union of the sub pixels on share1 and share 2
        for (int y=0;y<image.getHeight();y++) {
            for (int x=0;x<image.getWidth();x++) {
                if (image.isBlack(x, y)) {
                    if (Math.random() < n) {
                        share1.setDiagonal(x, y);
                        share2.setAntiDiagonal(x, y);
                    } else {
                        share1.setAntiDiagonal(x, y);
                        share2.setDiagonal(x, y);
                    }
                } else {
                    if (Math.random() < n) {
                        share1.setDiagonal(x, y);
                        share2.setDiagonal(x, y);
                    } else {
                        share1.setAntiDiagonal(x, y);
                        share2.setAntiDiagonal(x, y);
                    }
                }
            }
        }

        return Arrays.asList(share1, share2);
    }

    /**
     * Decrypts shares. All shares must be present or the result will be meaningless.
     * @param shares
     * @return the decrypted image.
     * @see Share#combine(List)
     */
    public static BinaryImage decode(List<Share> shares) {
        return Share.combine(shares);
    }
}
