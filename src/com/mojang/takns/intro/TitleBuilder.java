package com.mojang.takns.intro;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import com.mojang.takns.Takns;
import com.mojang.takns.World;
import com.mojang.takns.gui.Text;
import com.mojang.takns.sprites.Voxels;
import com.mojang.takns.terrain.Tiles;

public class TitleBuilder
{
    private int width = Takns.SCREEN_WIDTH;
    private int height = Takns.SCREEN_HEIGHT;
    
    private double angle = 0.2;
    private double angle2 = -0.8;
    private double cutOff = 0.30;
    private double xOffset = 100;
    private double yOffset = 100;
    
    String[] firstParts = {
            "The building", "The creation",
            "The fall", "The rise",
            "The era", "The age",
            "The weapons", "The plans", "The tale",
            "The end", "The dawn", "The rule"
    };

    String[] secondParts = {
            "a dynasty", "mankind",
            "a species", "a social construct",
            "robots", "machines", "androids",
            "a corporation", "two cities",
            "a madman", "a fat man"
    };

    String titleString = "Takns";

    public BufferedImage buildTitleImage()
    {
        Random random = new Random();
        String subString = firstParts[random.nextInt(firstParts.length)];
        subString += " of ";
        subString += secondParts[random.nextInt(secondParts.length)];
        World world = new World(random);
        world.init(true);
        
        angle2 = -random.nextDouble();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int[] pixels = new int[width * height];

        double sin = Math.sin(angle);
        double cos = Math.cos(angle);

        int[][] tileImages = new int[256][];

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (y > height * cutOff)
                {
                    double d = (4000*height/240) / (y - height * cutOff);

                    int fog = (int) (2000 / (Math.sqrt(d)));
                    if (fog > 256) fog = 256;

                    double xo = (x - width / 2.0) / width * 2 * d;
                    double yo = d;

                    double xp = cos * xo - sin * yo + xOffset;
                    double yp = cos * yo + sin * xo + yOffset;

                    int X = ((int) xp) & (64 * 16 - 1);
                    int Y = ((int) yp) & (64 * 16 - 1);

                    int xTile = X / 16;
                    int yTile = Y / 16;

                    X -= xTile * 16;
                    Y -= yTile * 16;

                    int tile = world.map.tiles[xTile + yTile * 64];
                    if (tileImages[tile] == null)
                    {
                        tileImages[tile] = new int[16 * 16];
                        Tiles.tiles[tile].image.getRGB(0, 0, 16, 16, tileImages[tile], 0, 16);
                    }
                    int color = tileImages[tile][X + Y * 16];

                    int r = (color >> 16) & 0xff;
                    int g = (color >> 8) & 0xff;
                    int b = (color) & 0xff;

                    r = r * fog / 256;
                    g = g * fog / 256;
                    b = b * fog / 256;
                    pixels[x + y * width] = (255 << 24) | (r << 16) | (g << 8) | b;
                }
                else
                {
                    int fog = (int) (256 - (y - height * cutOff));
                    int r = 0;
                    int g = 0;
                    int b = fog;
                    pixels[x + y * width] = (255 << 24) | (r << 16) | (g << 8) | b;
                }
            }
        }

        double sin2 = Math.sin(angle2);
        double cos2 = Math.cos(angle2);

        for (int y = 0; y < height; y++)
        {
            for (int l = 0; l < 2; l++)
            {
                for (int z = 0; z < 8; z++)
                {
                    for (int x = 0; x < width; x++)
                    {
                        if (y > height * cutOff)
                        {
                            double d = (2000*height/240) / (y - height * cutOff);

                            int fog = 255;
                            if (fog > 256) fog = 256;

                            double xo = (x - width / 2.0) / width * 2 * d;
                            double yo = d;

                            double xp = cos * xo - sin * yo + 18;
                            double yp = cos * yo + sin * xo - 13;

                            if (l == 1)
                            {
                                xp -= 8;
                                yp -= 8;

                                double xx = cos2 * xp - sin2 * yp;
                                double yy = cos2 * yp + sin2 * xp;

                                xp = xx + 8;
                                yp = yy + 8;
                            }

                            int X = ((int) xp);
                            int Y = ((int) yp);

                            if (X >= 0 && X < 16 && Y >= 0 && Y < 16)
                            {
                                boolean above = false;
                                if (z < 7)
                                {
                                    if (X >= 0 && Y >= 0 && X < 16 && Y < 16 && (Voxels.titleImageVoxels[l][z + 1][X + Y * 16] & 0xff000000) != 0) above = true;
                                }

                                int color = Voxels.titleImageVoxels[l][z][X + Y * 16];
                                if (color != 0)
                                {
                                    int col = 200;

                                    int hh0 = (int) (height*2/3 * (z + l * 4) / d);
                                    int hh1 = (int) (height*2/3 * (z + 1 + l * 4) / d);

                                    for (int yy = y - hh0; yy >= y - hh1; yy--)
                                    {
                                        if (yy == y - hh1)
                                        {
                                            if (!above) col = 255;
                                        }

                                        int r = (color >> 16) & 0xff;
                                        int g = (color >> 8) & 0xff;
                                        int b = (color) & 0xff;
                                        r = r * fog / 256 * col / 256;
                                        g = g * fog / 256 * col / 256;
                                        b = b * fog / 256 * col / 256;
                                        if (yy >= 0 && yy < height) pixels[x + yy * width] = (255 << 24) | (r << 16) | (g << 8) | b;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        image.setRGB(0, 0, width, height, pixels, 0, width);
        
        Graphics2D gr = image.createGraphics();
        AffineTransform tf = gr.getTransform();
        gr.translate(width/2, 40);
        gr.scale(4, 4);
        
        Text.drawString(titleString, gr, (-titleString.length()*6)/2, -5);
        gr.setTransform(tf);
        Text.drawString(subString, gr, (width-subString.length()*6)/2, 40+10);
        gr.dispose();

        return image;
    }

    public static void main(String[] args) throws Exception
    {
        long pre = System.currentTimeMillis();
        BufferedImage image = new TitleBuilder().buildTitleImage();
        long post = System.currentTimeMillis();
        System.out.println((post - pre) + "ms to generate title image");
        ImageIO.write(image, "png", new File("title.png"));
    }
}