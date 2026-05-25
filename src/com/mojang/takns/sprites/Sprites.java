package com.mojang.takns.sprites;

import java.awt.image.BufferedImage;
import java.util.Random;

import com.mojang.takns.ImageConverter;
import com.mojang.takns.NoiseMap;
import com.mojang.takns.terrain.*;

public class Sprites
{
    public static BufferedImage[][] debris;
    public static BufferedImage[] whiteSmoke;
    public static BufferedImage[] fireSmoke;
    public static BufferedImage[] shadow;
    public static BufferedImage[] bullet;
    public static BufferedImage[] crater;

    public static void buildSprites(Random random)
    {
        buildDebris(random);
        buildWhiteSmoke(random);
        buildFireSmoke(random);
        buildShadow(random);
        buildBullet(random);
        buildCrater(random);
    }

    private static void buildDebris(Random random)
    {
        int size = 16;
        int[] layer = new NoiseMap(random).getNoise(5, 3);
        int[] pixels = new int[size * size];

        debris = new BufferedImage[Terrain.terrainCount][8];
        for (int t = 0; t < Terrain.terrainCount; t++)
        {
            int color = Terrain.terrains[t].color;
            for (int i = 0; i < 8; i++)
            {
                for (int x = 0; x < size; x++)
                {
                    for (int y = 0; y < size; y++)
                    {
                        int xx = x - size / 2;
                        int yy = y - size / 2;

                        int a = (layer[x + y * 32] + 80) * 4;
                        a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
                        if (a > 255) a = 255;
                        if (a < 0) a = 0;
                        int r = 255 - (x + y) * 80 / (size * 2);
                        int g = 255 - (x + y) * 80 / (size * 2);
                        int b = 255 - (x + y) * 80 / (size * 2);

                        r += (r * ((color >> 16) & 0xff) / 255)*4;
                        g += (g * ((color >> 8) & 0xff) / 255)*4;
                        b += (b * ((color) & 0xff) / 255)*4;
                        r/=7;
                        g/=7;
                        b/=7;
                        pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }

                debris[t][i] = ImageConverter.convert(size, size, pixels, 2);
            }
        }
    }

    private static void buildFireSmoke(Random random)
    {
        int size = 16;

        int[] pixels = new int[size * size];
        fireSmoke = new BufferedImage[8];
        for (int i = 0; i < 8; i++)
        {
            int[] layer = new NoiseMap(random).getNoise(5, 3);
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    int xx = x - size / 2;
                    int yy = y - size / 2;

                    int radius = size / 2 * (i + 1) / 5 + 2;
                    int a = 130 - (int) (Math.sqrt(xx * xx + yy * yy) * 155 / radius);
                    if (a > 255) a = 255;
                    a = a-layer[x+y*32];
                    if (a > 255) a = 255;
                    if (a < 0) a = 0;

                    int r = 155 - (x + y) * 100 / (size * 2) + y * y * 250 / (size * size);
                    int g = (155 - (x + y) * 100 / (size * 2))/4 + y * y * 150 / (size * size);
                    int b = (155 - (x + y) * 100 / (size * 2))/4;
                    
                    int col = (a-16)*2;
                    if (col>255) col = 255;
                    if (col<0) col = 0;
                    a = a*4;
                    a = a*(7-i)/8;
                    if (a>155) a = 155;
                    r = r*(255-col)/255;
                    g = g*(255-col)/255;
                    b = b*(255-col)/255;
                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            fireSmoke[i] = ImageConverter.convert(size, size, pixels, 2);
        }
    }
    
    private static void buildWhiteSmoke(Random random)
    {
        int size = 16;

        int[] pixels = new int[size * size];
        whiteSmoke = new BufferedImage[8];
        for (int i = 0; i < 8; i++)
        {
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    int xx = x - size / 2;
                    int yy = y - size / 2;

                    int radius = size / 2 * (i + 1) / 5 + 2;
                    int a = 155 - (int) (Math.sqrt(xx * xx + yy * yy) * 155 / radius);
                    a = a * (7 - i) / 7;
                    if (a > 255) a = 255;
                    if (a < 0) a = 0;

                    int r = 255 - (x + y) * 100 / (size * 2) + y * y * 50 / (size * size);
                    int g = 255 - (x + y) * 100 / (size * 2);
                    int b = 255 - (x + y) * 100 / (size * 2);
                    
                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            whiteSmoke[i] = ImageConverter.convert(size, size, pixels, 2);
        }
    }

    private static void buildShadow(Random random)
    {
        int size = 16;

        int[] pixels = new int[size * size];
        shadow = new BufferedImage[8];
        for (int i = 0; i < 8; i++)
        {
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    int xx = x - size / 2;
                    int yy = (y - size / 2) * 2;

                    int a = 255;
                    a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
                    if (a > 40) a = 40;
                    if (a < 0) a = 0;
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            shadow[i] = ImageConverter.convert(size, size, pixels, 2);
        }
    }

    private static void buildBullet(Random random)
    {
        int size = 16;

        int[] pixels = new int[size * size];
        bullet = new BufferedImage[8];
        for (int i = 0; i < 8; i++)
        {
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    int xx = x - size / 2;
                    int yy = (y - size / 2);

                    int a = 255;
                    a -= (int) (Math.sqrt(xx * xx + yy * yy) * 315 / (size / 2)) + i * 32;
                    a = a * 10;

                    xx = (x + 3) - size / 2;
                    yy = (y + 3) - size / 2;
                    int dd = (int) (Math.sqrt(xx * xx + yy * yy) * 255 / (size * 2));
                    if (a > 255) a = 255;
                    if (a < 0) a = 0;
                    int r = 255 - dd;
                    int g = 255 - dd;
                    int b = 0;

                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            bullet[i] = ImageConverter.convert(size, size, pixels, 2);
        }
    }

    private static void buildCrater(Random random)
    {
        int size = 16;

        int[] pixels = new int[size * size];
        crater = new BufferedImage[8];
        for (int i = 0; i < 8; i++)
        {
            int[] layer = new NoiseMap(new Random(random.nextLong())).getNoise(5, 2);
            for (int x = 0; x < size; x++)
            {
                for (int y = 0; y < size; y++)
                {
                    int xx = x - size / 2;
                    float yy = (y - size / 2) * 1.5f;

                    int a = (layer[x + y * 32] + 90) * 2;
                    a -= (int) (Math.sqrt(xx * xx + yy * yy) * 200 / (size / 2));

                    if (a > 200) a = 200;
                    if (a < 0) a = 0;
                    int r = 0;
                    int g = 0;
                    int b = 0;

                    pixels[x + y * size] = (a << 24) | (r << 16) | (g << 8) | b;
                }
            }

            crater[i] = ImageConverter.convert(size, size, pixels, 2);
        }
    }
}