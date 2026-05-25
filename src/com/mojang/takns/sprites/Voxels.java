package com.mojang.takns.sprites;

import java.awt.image.BufferedImage;
import java.util.Random;

import com.mojang.takns.ImageConverter;

public class Voxels
{
    private static final float[] SIN = { 0.0f, 0.38f, 0.71f, 0.92f, 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, };
    private static final float[] COS = { 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, 0.0f, 0.38f, 0.71f, 0.92f, };

    public static int[][][] titleImageVoxels = new int[2][][];

    public static BufferedImage[] turret;
    public static BufferedImage[] turretShadow;

    public static BufferedImage[][] tankBase;
    public static BufferedImage[] baseShadow;

    public static BufferedImage[][] carBase;
    public static BufferedImage[] carShadow;

    public static BufferedImage[][][] harvesterBase;
    public static BufferedImage[] harvesterShadow;

    public static BufferedImage[] hqBase;
    public static BufferedImage hqShadow;

    public static BufferedImage[] wallTurretBase;
    public static BufferedImage wallTurretShadow;

    public static BufferedImage[] walls;
    public static BufferedImage[] wallShadows;

    private static final int SIDES = 2;

    public static final int[] SIDE_COLORS = { 0xffc00000, 0xff4040e0, 0xff00e000, 0xffe000e0 };
    public static final int[] SIDE_COLORS2 = { 0xffff0000, 0xff0000ff, 0xff00ff00, 0xffff00ff };

    public static boolean reduced = false;

    public static void buildVoxels(Random random)
    {
        buildTurrets();
        buildTanks();

        if (!reduced)
        {
            buildCars();
            buildHarvesters();
            
            buildHqs();
            buildWallTurrets();
            buildWalls();
        }
    }

    private static void buildWalls()
    {
        walls = new BufferedImage[16];
        wallShadows = new BufferedImage[16];

        for (int i = 0; i < 16; i++)
        {
            int[][] layers = new int[16][16 * 16];
            if (i != 5 && i != 10)
                fillBlock(layers, 16, 4, 4, 0, 8, 8, 10, 0xff909090);

            if ((i & 0x01) != 0)
                fillBlock(layers, 16, 6, 0, 0, 4, 8, 8, 0xff606060);
            if ((i & 0x02) != 0)
                fillBlock(layers, 16, 0, 6, 0, 8, 4, 8, 0xff606060);
            if ((i & 0x04) != 0)
                fillBlock(layers, 16, 6, 8, 0, 4, 8, 8, 0xff606060);
            if ((i & 0x08) != 0)
                fillBlock(layers, 16, 8, 6, 0, 8, 4, 8, 0xff606060);

            walls[i] = voxelizeDir(layers, 16, 0);
            wallShadows[i] = buildShadowDir(layers, 16, 0);
        }
    }

    private static void buildWallTurrets()
    {
        int[][] layers = new int[16][16 * 16];

        wallTurretBase = new BufferedImage[SIDES];
        for (int i = 0; i < SIDES; i++)
        {
            fillBlock(layers, 16, 2, 2, 0, 12, 12, 10, SIDE_COLORS[i]);
            fillBlock(layers, 16, 6, 0, 0, 4, 16, 8, 0xff606060);
            fillBlock(layers, 16, 0, 6, 0, 16, 4, 8, 0xff606060);

            wallTurretBase[i] = voxelizeDir(layers, 16, 0);
            if (i == 0) wallTurretShadow = buildShadowDir(layers, 16, 0);
        }
    }
    
    private static void buildHqs()
    {
        int[][] layers = new int[16][32 * 32];

        hqBase = new BufferedImage[SIDES];
        for (int i = 0; i < SIDES; i++)
        {
            fillBlock(layers, 32, 1, 1, 0, 30, 30, 6, 0xff606060);
            fillBlock(layers, 32, 2, 2, 0, 28, 28, 6, 0);
            fillBlock(layers, 32, 7, 2, 0, 8, 30, 6, 0);

            fillBlock(layers, 32, 0, 0, 0, 3, 3, 7, 0xff606060);
            fillBlock(layers, 32, 29, 0, 0, 3, 3, 7, 0xff606060);
            fillBlock(layers, 32, 0, 29, 0, 3, 3, 7, 0xff606060);
            fillBlock(layers, 32, 29, 29, 0, 3, 3, 7, 0xff606060);

            fillBlock(layers, 32, 4, 5, 0, 32 - 10, 10, 10, SIDE_COLORS[i]);
            fillBlock(layers, 32, 6, 5, 0, 10, 18, 8, SIDE_COLORS[i]);
            fillBlock(layers, 32, 7, 7, 0, 1, 1, 14, 0xffffffff);
            fillBlock(layers, 32, 9, 7, 0, 1, 1, 13, 0xffffffff);

            fillBlock(layers, 32, 26, 26, 12, 6, 1, 4, SIDE_COLORS[i]);
            fillBlock(layers, 32, 26, 26, 0, 1, 1, 16, 0xffd0d0a0);

            hqBase[i] = voxelizeDir(layers, 32, 0);
            if (i == 0) hqShadow = buildShadowDir(layers, 32, 0);
        }
    }

    private static void buildTurrets()
    {
        int[][] layers = new int[8][16 * 16];

        fillBlock(layers, 16, 6, 0, 0, 4, 8, 3, 0xff505060);
        fillBlock(layers, 16, 7, 0, 1, 2, 8, 1, 0xff000000);
        fillBlock(layers, 16, 4, 8, 0, 8, 8, 3, 0xffb0b0c0);

        titleImageVoxels[1] = layers;

        turret = voxelize(layers, 16, 16);
        turretShadow = buildShadows(layers, 16, 16);
    }

    private static void buildTanks()
    {
        tankBase = new BufferedImage[SIDES][];
        for (int i = 0; i < SIDES; i++)
        {
            int[][] layers = new int[8][16 * 16];
            fillBlock(layers, 16, 3, 1, 1, 10, 14, 3, SIDE_COLORS[i]);

            fillBlock(layers, 16, 1, 2, 0, 2, 14, 4, 0xff808080);
            fillBlock(layers, 16, 13, 2, 0, 2, 14, 4, 0xff808080);
            fillBlock(layers, 16, 1, 0, 2, 2, 2, 2, 0xff808080);
            fillBlock(layers, 16, 13, 0, 2, 2, 2, 2, 0xff808080);

            if (i == 0) titleImageVoxels[0] = layers;

            tankBase[i] = voxelize(layers, 16, 16);
            if (i == 0) baseShadow = buildShadows(layers, 16, 16);
        }
    }

    private static void buildHarvesters()
    {
        harvesterBase = new BufferedImage[3][SIDES][];
        for (int i = 0; i < SIDES; i++)
        {
            for (int m = 0; m < 3; m++)
            {
                int[][] layers = new int[8][16 * 16];
                fillBlock(layers, 16, 3, 0, 1, 10, 15, 3, SIDE_COLORS[i]);
                fillBlock(layers, 16, 3, 4 - 3, 1 + 3, 10, 8, 1, 0xff303030);
                fillBlock(layers, 16, 3, 4, 1, 10, 8 + 4, 6, SIDE_COLORS[i]);

                fillBlock(layers, 16, 4, 8, 1, 8, 8, 6, 0);
                if (m == 0)
                    fillBlock(layers, 16, 4, 8, 1, 8, 8, 3, 0xff505050);
                else if (m == 1)
                    fillBlock(layers, 16, 4, 8, 1, 8, 8, 4, 0xff805080);
                else
                    fillBlock(layers, 16, 4, 8, 1, 8, 8, 6, 0xffe080e0);

                fillBlock(layers, 16, 1, 2, 0, 2, 4, 3, 0xff808080);
                fillBlock(layers, 16, 13, 2, 0, 2, 4, 3, 0xff808080);

                fillBlock(layers, 16, 1, 7, 0, 2, 4, 3, 0xff808080);
                fillBlock(layers, 16, 13, 7, 0, 2, 4, 3, 0xff808080);

                fillBlock(layers, 16, 1, 12, 0, 2, 4, 3, 0xff808080);
                fillBlock(layers, 16, 13, 12, 0, 2, 4, 3, 0xff808080);

                harvesterBase[m][i] = voxelize(layers, 16, 16);
                if (i == 0 && m == 0) harvesterShadow = buildShadows(layers, 16, 16);
            }
        }
    }

    private static void buildCars()
    {
        carBase = new BufferedImage[SIDES][];
        for (int i = 0; i < SIDES; i++)
        {
            int[][] layers = new int[8][16 * 16];
            fillBlock(layers, 16, 3, 1, 1, 10, 14, 3, SIDE_COLORS[i]);
            fillBlock(layers, 16, 3, 10 - 3, 1 + 3, 10, 6, 1, 0xff303030);
            fillBlock(layers, 16, 3, 10, 1, 10, 6, 4, SIDE_COLORS[i]);

            fillBlock(layers, 16, 5, 0, 4, 2, 6, 1, 0xff606060);
            fillBlock(layers, 16, 9, 0, 4, 2, 6, 1, 0xff606060);

            fillBlock(layers, 16, 1, 1, 0, 2, 4, 3, 0xff808080);
            fillBlock(layers, 16, 13, 1, 0, 2, 4, 3, 0xff808080);

            fillBlock(layers, 16, 1, 11, 0, 2, 5, 4, 0xff808080);
            fillBlock(layers, 16, 13, 11, 0, 2, 5, 4, 0xff808080);

            carBase[i] = voxelize(layers, 16, 16);
            if (i == 0) carShadow = buildShadows(layers, 16, 16);
        }
    }

    private static void fillBlock(int[][] pixels, int size, int x0, int y0, int z0, int xs, int ys, int zs, int color)
    {
        for (int z = z0; z < z0 + zs; z++)
            for (int y = y0; y < y0 + ys; y++)
                for (int x = x0; x < x0 + xs; x++)
                {
                    pixels[z][x + y * size] = color;
                }
    }

    public static BufferedImage[] voxelize(int[][] layers, int size, int dirs)
    {
        if (reduced) return null;

        BufferedImage[] images = new BufferedImage[dirs];
        for (int dir = 0; dir < dirs; dir++)
        {
            images[dir] = voxelizeDir(layers, size, dir);
        }
        return images;
    }

    private static BufferedImage voxelizeDir(int[][] layers, int size, int dir)
    {
        if (reduced) return null;

        int scale = 1;
        int newSize = size * 3 / 2 * scale;
        float sin = SIN[dir];
        float cos = COS[dir];

        int imgWidth = newSize;
        int imgHeight = newSize + layers.length * scale;
        int[] pixels = new int[imgWidth * imgHeight];
        int yo = layers.length * scale;

        for (int ll = 0; ll < layers.length * scale; ll++)
        {
            int l = ll / scale;
            int[] input = layers[l];
            for (int y = 0; y < newSize; y++)
            {
                for (int x = 0; x < newSize; x++)
                {
                    float _x = (x - (newSize / 2f - 0.5f * scale)) / scale;
                    float _y = (y - (newSize / 2f - 0.5f * scale)) / scale;

                    int xi = (int) ((cos * _x + sin * _y + (size / 2f - 0.5f)));
                    int yi = (int) ((cos * _y - sin * _x + (size / 2f - 0.5f)));

                    boolean above = false;
                    boolean diagonalAbove = false;
                    if (l < layers.length - 1)
                    {
                        if (xi >= 0 && yi >= 0 && xi < size && yi < size && (layers[l + 1][xi + yi * size] & 0xff000000) != 0) above = true;
                        float _x2 = (x - (newSize / 2f - 0.5f) - 1 * scale) / scale;
                        float _y2 = (y - (newSize / 2f - 0.5f) - 1 * scale) / scale;
                        int xi2 = (int) ((cos * _x2 + sin * _y2 + (size / 2f - 0.5f * scale)));
                        int yi2 = (int) ((cos * _y2 - sin * _x2 + (size / 2f - 0.5f * scale)));
                        if (xi2 >= 0 && yi2 >= 0 && xi2 < size && yi2 < size && (layers[l + 1][xi2 + yi2 * size] & 0xff000000) != 0) diagonalAbove = true;
                    }
                    int color = 255;
                    if (above) color = 200;
                    if (diagonalAbove) color = 128;

                    if (xi >= 0 && yi >= 0 && xi < size && yi < size && (input[xi + yi * size] & 0xff000000) != 0)
                    {
                        int a = 255;
                        int r = (input[xi + yi * size] >> 16) & 0xff;
                        int g = (input[xi + yi * size] >> 8) & 0xff;
                        int b = (input[xi + yi * size]) & 0xff;

                        r = r * color / 255;
                        g = g * color / 255;
                        b = b * color / 255;
                        pixels[x + (y + yo - ll) * imgWidth] = (a << 24) | (r << 16) | (g << 8) | b;
                    }
                }
            }
        }

        BufferedImage res;
        if (scale == 2)
        {
            int[] p2 = halfSize(pixels, imgWidth, imgHeight, scale);
            res = new BufferedImage(imgWidth / scale, imgHeight / scale, BufferedImage.TYPE_INT_ARGB_PRE);
            res.setRGB(0, 0, imgWidth / scale, imgHeight / scale, p2, 0, imgWidth / scale);
            res = ImageConverter.convert(res, 1);
        }
        else
        {
            res = ImageConverter.convert(imgWidth, imgHeight, pixels, 1);
        }

        return res;
    }

    public static int[] halfSize(int[] pixels, int width, int height, int scale)
    {
        if (reduced) return null;

        int[] result = new int[(width / scale) * (height / scale)];
        for (int x = 0; x < width; x += scale)
        {
            for (int y = 0; y < height; y += scale)
            {
                int r = 0;
                int g = 0;
                int b = 0;
                int a = 0;
                int samples = 0;
                for (int xx = x; xx < x + scale; xx++)
                    for (int yy = y; yy < y + scale; yy++)
                    {
                        int aa = (pixels[xx + yy * width] >> 24) & 0xff;
                        int rr = (pixels[xx + yy * width] >> 16) & 0xff;
                        int gg = (pixels[xx + yy * width] >> 8) & 0xff;
                        int bb = (pixels[xx + yy * width]) & 0xff;
                        a += aa;
                        if (aa > 0)
                        {
                            r += rr;
                            g += gg;
                            b += bb;
                            samples++;
                        }
                    }

                int col = 0;
                if (samples > 0)
                {
                    a /= samples;
                    r /= samples;
                    g /= samples;
                    b /= samples;
                    col = (a << 24) | (r << 16) | (g << 8) | b;
                }

                result[(x / scale) + (y / scale) * (width / scale)] = col;
            }
        }
        return result;
    }

    public static BufferedImage[] buildShadows(int[][] layers, int size, int dirs)
    {
        if (reduced) return null;

        BufferedImage[] images = new BufferedImage[dirs];
        for (int dir = 0; dir < dirs; dir++)
        {
            images[dir] = buildShadowDir(layers, size, dir);
        }
        return images;
    }

    public static BufferedImage buildShadowDir(int[][] layers, int size, int dir)
    {
        if (reduced) return null;

        int newSize = size * 3 / 2;
        float sin = SIN[dir];
        float cos = COS[dir];

        int imgWidth = newSize + layers.length / 2;
        int imgHeight = newSize + layers.length;
        int[] pixels = new int[imgWidth * imgHeight];

        for (int l = 0; l < layers.length; l++)
        {
            int[] input = layers[l];
            for (int y = 0; y < newSize; y++)
            {
                for (int x = 0; x < newSize; x++)
                {
                    float _x = x - (newSize / 2f - 0.5f);
                    float _y = y - (newSize / 2f - 0.5f);

                    int xi = (int) (cos * _x + sin * _y + (size / 2f - 0.5f));
                    int yi = (int) (cos * _y - sin * _x + (size / 2f - 0.5f));
                    if (xi >= 0 && yi >= 0 && xi < size && yi < size && (input[xi + yi * size] & 0xff000000) != 0)
                    {
                        pixels[(x + l / 2) + (y + l) * imgWidth] = 0x40000000;
                    }
                }
            }
        }

        BufferedImage res = ImageConverter.convert(imgWidth, imgHeight, pixels, 2);
        return res;
    }
}