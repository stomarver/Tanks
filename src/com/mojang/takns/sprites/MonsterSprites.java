package com.mojang.takns.sprites;

import java.awt.image.BufferedImage;
import java.util.Random;

import com.mojang.takns.ImageConverter;

public class MonsterSprites
{
    public static BufferedImage[] blob;
    public static BufferedImage blobShadow;

    public static void buildSprites(Random random)
    {
        buildBlob(random);
    }

    private static void buildBlob(Random random)
    {
        int rad = 8;
        int size = rad * 3;

        int[] pixels = new int[size * size];

        blob = new BufferedImage[2];
        for (int i = 0; i < 2; i++)
        {
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    double xx = x - size / 2 + 1;
                    double yy = y - size / 2 + 1;
                    if (i == 0 && y > size / 2) yy *= 2;
                    if (i == 1)
                    {
                        yy += rad / 4;
                        xx *= 1.2f;
                    }

                    int d0 = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);

                    int a = 0;
                    int r = 255;
                    int g = 255;
                    int b = 255;

                    if (d0 <= rad)
                    {
                        a = d0 * 170 / rad + 85;
                        r = 64 - d0 * 25 / rad;
                        g = 255 - d0*d0 * 90 / rad/rad;
                        b = 64 - d0 * 25 / rad;
                    }

                    xx = x - (size / 2 - rad / 2);
                    yy = y - (size / 2 - rad / 2);
                    if (i == 1)
                    {
                        yy += rad / 4;
                        xx *= 1.2f;
                    }

                    if (i == 0 && yy > 0) yy *= 2;

                    int d1 = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);
                    
                    if (a>0)
                    {
                        r = r*(rad*6-d1)/6/rad;
                        g = g*(rad*6-d1)/6/rad;
                        b = b*(rad*6-d1)/6/rad;
                    }

                    if (d1 <= rad / 8)
                    {
                        int l1 = 128;
                        int l2 = 256 - l1;
                        a = (a * l2 + 255 * l1) / 256;
                        r = (r * l2 + 255 * l1) / 256;
                        g = (g * l2 + 255 * l1) / 256;
                        b = (b * l2 + 255 * l1) / 256;
                    }

                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }


            blob[i] = ImageConverter.convert(size, size, pixels, 2);
        }
        for (int x = 0; x < size; x++)
        {
            for (int y = 0; y < size; y++)
            {
                double xx = x - size / 2 + 1;
                double yy = y - size / 2 + 1;
                yy *= 2;

                int d = (int) (Math.sqrt(xx * xx + yy * yy) - 0.5f);

                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;

                if (d <= rad)
                {
                    a = 64;
                    r = 0;
                    g = 0;
                    b = 0;
                }

                pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        blobShadow = ImageConverter.convert(size, size, pixels, 2);
    }
}