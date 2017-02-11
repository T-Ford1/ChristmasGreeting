/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.Font;
import java.util.Random;
import java.util.Scanner;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JOptionPane;

/**
 *
 * @author ford.terrell
 */
public class Main {

    private static final Random random = new Random();

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        File f = new File("./");
        File[] list = f.listFiles();
        ArrayList<BufferedImage> images = new ArrayList<>();
        String[] formats = ImageIO.getReaderFormatNames();
        for (File e : list) {
            for (String format : formats) {
                if (!e.getName().contains("savedImage") && e.getName().endsWith("." + format)) {
                    images.add(ImageIO.read(e));
                }
            }
        }
        List<String> names = javafx.scene.text.Font.getFontNames();
        Scanner s = new Scanner(new File("res/xmaswords.txt"));
        ArrayList<String> phrases = new ArrayList<>();
        ArrayList<BufferedImage> borders = new ArrayList<>();

        int files = new Scanner(new File("res/files.txt")).nextInt();
        for (int i = 1; i <= files; i++) {
            borders.add(ImageIO.read(new File("res/b" + i + ".png")));
        }

        while (s.hasNextLine()) {
            phrases.add(s.nextLine());
        }
        while (!images.isEmpty()) {
            BufferedImage orig = images.get(0);
            BufferedImage border = borders.get(random.nextInt(borders.size()));

            BufferedImage toEdit = new BufferedImage(orig.getWidth(), orig.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = toEdit.getGraphics();
            g.drawImage(border, 0, 0, toEdit.getWidth(), toEdit.getHeight(), null);
            for (int y = 0; y < orig.getHeight(); y++) {
                for (int x = 0; x < orig.getWidth(); x++) {
                    Color toOvl = new Color(toEdit.getRGB(x, y));
                    if (toOvl.getRed() == 255 && toOvl.getGreen() == 0 && toOvl.getBlue() == 255) {
                        toEdit.setRGB(x, y, orig.getRGB(x, y));
                    }
                }
            }

            String phrase = phrases.get(random.nextInt(phrases.size()));
            int width = (toEdit.getWidth() - 100) / phrase.length() + 50;
            int height = (int) ((double) toEdit.getHeight() * width / toEdit.getWidth());
            Font font = new Font(names.get(new Random().nextInt(names.size())), width, height);
            g.setFont(font);
            if (random.nextBoolean()) {
                g.setColor(Color.red);
            } else {
                g.setColor(Color.green);
            }
            int xPos = toEdit.getWidth() / 2 - width * phrase.length() / 5;
            int yPos = toEdit.getHeight() / 2 + height / 2;
            g.drawString(phrase, xPos, yPos);

            int i = JOptionPane.showConfirmDialog(null, "Select This Image?", "Card Maker", 0, 0, new MyIcon(toEdit));
            if (i == 0) {
                File out = new File("savedImage.png");
                int j = 2;
                while (out.exists()) {
                    out = new File("savedImage" + j + ".png");
                    j++;
                }
                ImageIO.write(toEdit, "png", out);
                images.remove(orig);
            } else if (i == -1) {
                return;
            } else {
                images.remove(0);
                images.add(orig);
            }
        }
    }

    static class MyIcon implements Icon {

        private final Color c;
        private final BufferedImage b;
        private final boolean img;

        protected MyIcon(Color c) {
            this.c = c;
            b = null;
            img = false;
        }

        protected MyIcon(BufferedImage b) {
            this.b = b;
            c = null;
            img = true;
        }

        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (img) {
                g.drawImage(b, x, y, getIconWidth(), getIconHeight(), c);
            } else {
                g.setColor(this.c);
                g.fillRect(x, y, x + getIconWidth(), y + getIconHeight());
            }
        }

        public int getIconWidth() {
            if (img) {
                return Math.min(400, b.getWidth());
            }
            return 32;
        }

        public int getIconHeight() {
            if (img) {
                return Math.min(400, b.getHeight());
            }
            return 32;
        }
    }
}
