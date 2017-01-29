package com.zaloum.cryptography;

import com.zaloum.cryptography.image.BinaryImage;
import com.zaloum.cryptography.image.Share;
import com.zaloum.cryptography.image.effects.Halftone;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    enum Args {
        ENCODE,
        DECODE,
        OUTPUT_PATH,
        OUTPUT_FORMAT,
        COLORS
    }

    static final String DEFAULT_SHARE_NAME = "share";
    static final String DEFAULT_DECODE_NAME = "decoded_image";
    static final String DEFAULT_FORMAT = "png";

    static Map<Args, String> argList = new HashMap<>();

    public static void main(String[] args) {
        try {
            parseArgs(args);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        try {
            if (argList.containsKey(Args.ENCODE)) {
                encode();
            } else if (argList.containsKey(Args.DECODE)) {
                decode();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void encode() throws IOException {
        String input = argList.get(Args.ENCODE);

        BufferedImage image = ImageIO.read(new File(input));
        image = Halftone.image(image);

        List<Share> shares = VisualCryptography.encode(new BinaryImage(image));

        String output = argList.containsKey(Args.OUTPUT_PATH) ? argList.get(Args.OUTPUT_PATH) : DEFAULT_SHARE_NAME;
        String format = argList.containsKey(Args.OUTPUT_FORMAT) ? argList.get(Args.OUTPUT_FORMAT) : DEFAULT_FORMAT;

        int i = 0;
        for (Share share : shares) {
            File file = new File(output + "-" + i++ + "." + format);
            ImageIO.write(share.toBufferedImage(), format, file);
            System.out.println("Share created at " + file.getAbsolutePath());
        }
    }

    static void decode() throws IOException {
        String[] inputs = argList.get(Args.DECODE).split(" ");

        Color color1 = Color.BLACK;
        Color color2 = Color.WHITE;
        if (argList.containsKey(Args.COLORS)) {
            String[] colors = argList.get(Args.COLORS).split(" ");
            color1 = parseColor(colors[0]);
            color2 = parseColor(colors[1]);
        }

        Share share = new Share(ImageIO.read(new File(inputs[0])));

        for (int i = 1; i < inputs.length; i++) {
            share.combine(new Share(ImageIO.read(new File(inputs[i]))));
        }

        BufferedImage output = share.toBufferedImage(color1, color2);
        output = scale(output, 0.5, 0.5);

        String name = argList.containsKey(Args.OUTPUT_PATH) ? argList.get(Args.OUTPUT_PATH) : DEFAULT_DECODE_NAME;
        String format = argList.containsKey(Args.OUTPUT_FORMAT) ? argList.get(Args.OUTPUT_FORMAT) : DEFAULT_FORMAT;

        File file = new File(name + "." + format);
        ImageIO.write(output, format, file);
        System.out.println("Decoded image saved to " + file.getAbsolutePath());
    }

    static BufferedImage scale(BufferedImage img, double sx, double sy) {
        BufferedImage output = new BufferedImage((int)(img.getWidth() * sx), (int)(img.getHeight() * sy), img.getType());

        Graphics2D g2d = (Graphics2D)output.getGraphics();
        g2d.scale(sx, sy);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return output;
    }

    static void parseArgs(String[] args) {
        String arg = "";
        for (int i = 0; i < args.length; i++) {
            try {
                arg = args[i];
                if ("-encode".equals(arg)) {
                    argList.put(Args.ENCODE, args[++i]);
                } else if ("-decode".equals(arg)) {
                    String paths = "";
                    i++;
                    while (i < args.length && !args[i].startsWith("-")) {
                        paths += args[i++] + " ";
                    }
                    i--;
                    argList.put(Args.DECODE, paths);
                } else if ("-format".equals(arg)) {
                    argList.put(Args.OUTPUT_FORMAT, args[++i]);
                } else if ("-out".equals(arg)) {
                    argList.put(Args.OUTPUT_PATH, args[++i]);
                } else if ("-colors".equals(arg)) {
                    argList.put(Args.COLORS, args[++i] + " " + args[++i]);
                } else {
                    throw new IllegalArgumentException("Unsupported arg '" + arg +"'.");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new IllegalArgumentException("Incorrect number of tokens while parsing arg '" + arg + "'.");
            }

        }
    }

    static Color parseColor(String color) {
        if (color.matches("\\d+")) {
            return new Color(Integer.parseInt(color));
        } else {
            try {
                return (Color)Color.class.getField(color.toUpperCase()).get(null);
            } catch(NoSuchFieldException e) {
                throw new IllegalArgumentException("The color'" + color + "' does not exist.");
            } catch (IllegalAccessException|SecurityException e) {
                e.printStackTrace();
                return Color.BLACK;
            }
        }
    }
}
