package com.mojang.takns.terrain;

import java.util.*;

import com.mojang.takns.NoiseMap;

public class Tiles
{
    public static final int METHOD_NONE = 0;
    public static final int METHOD_DARKEN = 1;
    public static final int METHOD_SHORE = 2;
    public static final int METHOD_NOISE = 3;
    public static final int METHOD_LERP = 4;
    private Random random;

    public static final int TYPE_WATER = 0;
    public static final int TYPE_WATER_TO_SAND = 0;
    public static final int TYPE_SAND = 15;
    public static final int TYPE_SAND_TO_GRASS = 15;
    public static final int TYPE_GRASS = 30;

    public static final int TYPE_GRASS_DECORATORS = 32;
    public static final int TYPE_SAND_DECORATORS = 40;

    public static final int TYPE_SLAB = 64;
    public static final int TYPE_GEMS = 65;

    public static Tile[] tiles = new Tile[256];

    public Tiles()
    {
        this(new Random());
    }

    public Tiles(Random random)
    {
        this.random = random;
    }

    public void createTiles()
    {
        int[] water = generateWater();
        int[] sand = generateSand();
        int[] grass = generateGrass();
        int[] black = generateSolid(255, 0, 0, 0);
        int[] fog = generateSolid(140, 0, 0, 0);
        int[] transparent = generateSolid(0, 0, 0, 0);
        int[] slab = generateSlab();

        createTiles(TYPE_WATER_TO_SAND, water, METHOD_SHORE, Terrain.TERRAIN_WATER, 192 + 16, sand, METHOD_DARKEN, Terrain.TERRAIN_SHORE);
        createTiles(TYPE_SAND_TO_GRASS, sand, METHOD_NOISE, Terrain.TERRAIN_SAND, 64 - 16, grass, METHOD_DARKEN, Terrain.TERRAIN_GRASS);


        createTiles(256 - 32, fog, METHOD_LERP, Terrain.TERRAIN_NONE, 192, transparent, METHOD_NONE, Terrain.TERRAIN_NONE);
        createTiles(256 - 16, black, METHOD_LERP, Terrain.TERRAIN_NONE, 192, transparent, METHOD_NONE, Terrain.TERRAIN_NONE);

        createGems(TYPE_GEMS, Terrain.TERRAIN_GEM);
        createSandDecorators(TYPE_SAND_DECORATORS, Terrain.TERRAIN_SAND);
        createGrassDecorators(TYPE_GRASS_DECORATORS, Terrain.TERRAIN_GRASS);

        createTile(TYPE_SLAB, slab, Terrain.TERRAIN_SLAB);
    }

    public void createSandDecorators(int offset, Terrain terrain)
    {
        for (int i = 0; i < 8; i++)
        {
            int[] pixels = generateSand();

            for (int j = 0; j < i / 4 + 1; j++)
            {
                int r = random.nextInt(8) + 4;
                int x = random.nextInt(17 - r);
                int y = random.nextInt(17 - r);

                for (int xx = x; xx < x + r; xx++)
                    for (int yy = y; yy < y + r; yy++)
                    {
                        float xd = (xx - (x + r / 2.0f));
                        float yd = (yy - (y + r / 2.0f));
                        float d = (xd * xd + yd * yd);

                        float xd2 = ((xx - 1) - (x + r / 2.0f));
                        float yd2 = ((yy - 1) - (y + r / 2.0f));
                        float d2 = (xd2 * xd2 + yd2 * yd2);

                        if (d < r * r / 4.0f)
                        {
                            if (d > (r - 3) * (r - 3) / 4.0f)
                            {
                                float dd = d;
                                d = d2;
                                d2 = dd;
                            }

                            if (d < d2)
                                lerp(pixels, (xx) + (yy) * 16, 0xff808040, (int) (64 * d / (r * r / 4.0f)) + 16);
                            else
                                lerp(pixels, (xx) + (yy) * 16, 0xffffffcf, (int) (64 * d2 / (r * r / 4.0f)) + 16);
                        }
                    }
            }

            tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
        }
    }

    public void createGems(int offset, Terrain terrain)
    {
        for (int i = 0; i < 8; i++)
        {
            int[] pixels = generateSand();

            int gemColor = lerp(0xffdd5dec, 0xff707070, (7 - i) * 24);
            int highlightColor = lerp(0xffffffff, gemColor, (7 - i) * 16);
            int shadeColor = lerp(gemColor, 0xff000000, 64);

            for (int y = 0; y < 14; y++)
            {
                for (int x = 0; x < 14; x++)
                {
                    if (random.nextInt(64) < i + 3)
                    {
                        lerp(pixels, (x + 0) + (y + 0) * 16, highlightColor, 255);
                        lerp(pixels, (x + 1) + (y + 0) * 16, gemColor, 255);
                        lerp(pixels, (x + 0) + (y + 1) * 16, shadeColor, 255);
                        lerp(pixels, (x + 1) + (y + 1) * 16, shadeColor, 255);
                        lerp(pixels, (x + 2) + (y + 1) * 16, 0xff000000, 32);
                        lerp(pixels, (x + 1) + (y + 2) * 16, 0xff000000, 32);
                        lerp(pixels, (x + 2) + (y + 2) * 16, 0xff000000, 32);
                    }
                }
            }

            tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
        }
    }

    public void createGrassDecorators(int offset, Terrain terrain)
    {
        for (int i = 0; i < 8; i++)
        {
            int[] pixels = generateGrass();

            for (int j = 0; j < (i + 1) * 4; j++)
            {
                int x = random.nextInt(15);
                int y = random.nextInt(14);

                int flowerColor = 0xffc0ffc0;
                lerp(pixels, (x + 0) + (y + 0) * 16, 0xffcfffcf, 64);
                lerp(pixels, (x + 0) + (y + 1) * 16, flowerColor, 64);
                lerp(pixels, (x + 0) + (y + 2) * 16, 0xff000000, 16);
                lerp(pixels, (x + 1) + (y + 2) * 16, 0xff000000, 16);
            }

            tiles[i + offset] = new Tile(pixels, 16, 16, terrain);
        }
    }

    public int[] generateSolid(int a, int r, int g, int b)
    {
        int[] pixels = new int[16 * 16];

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
            }
        }
        return pixels;
    }

    public int[] generateGrass()
    {
        int[] pixels = new int[16 * 16];

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                int c = random.nextInt(32);
                c = c * random.nextInt(256) / 255;
                int a = 255;
                int r = ((Terrain.TERRAIN_GRASS.color >> 16) & 0xff) + c;
                int g = ((Terrain.TERRAIN_GRASS.color >> 8) & 0xff) + c;
                int b = ((Terrain.TERRAIN_GRASS.color >> 0) & 0xff) + c;

                pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
                if (random.nextInt(16) == 0 && x > 1)
                {
                    pixels[x + y * 16 - 1] = (a << 24) | (r << 16) | (g << 8) | (b);
                    pixels[x + y * 16 - 2] = (a << 24) | (r << 16) | (g << 8) | (b);
                }
            }
        }
        return pixels;
    }

    public int[] generateSand()
    {
        int[] pixels = new int[16 * 16];

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                int c = random.nextInt(16);
                int a = 255;
                int r = ((Terrain.TERRAIN_SAND.color >> 16) & 0xff) + c;
                int g = ((Terrain.TERRAIN_SAND.color >> 8) & 0xff) + c;
                int b = ((Terrain.TERRAIN_SAND.color >> 0) & 0xff) + c;

                pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
            }
        }
        return pixels;
    }

    public int[] generateWater()
    {
        int[] pixels = new int[16 * 16];
        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                int a = 255;
                int c = random.nextInt(16);
                int r = ((Terrain.TERRAIN_WATER.color >> 16) & 0xff) + c;
                int g = ((Terrain.TERRAIN_WATER.color >> 8) & 0xff) + c;
                int b = ((Terrain.TERRAIN_WATER.color >> 0) & 0xff) + c;

                pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
            }
        }
        return pixels;
    }

    public int[] generateSlab()
    {
        int[] pixels = new int[16 * 16];
        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                int a = 255;
                int c = random.nextInt(8);
                if ((x & 7) == 0 || (y & 7) == 0)
                {
                    c -= 20;
                }
                if ((x & 7) == 7 || (y & 7) == 7)
                {
                    c += 20;
                }
                int r = 200 - (c + (x&7) + (y&7));
                int g = 200 - (c + (x&7) + (y&7));
                int b = 200 - (c + (x&7) + (y&7));

                pixels[x + y * 16] = (a << 24) | (r << 16) | (g << 8) | (b);
            }
        }
        return pixels;
    }

    public void createTile(int offset, int[] pixels, Terrain terrain)
    {
        tiles[offset] = new Tile(pixels, 16, 16, terrain);
    }

    public void createTiles(int offset, int[] source, int method0, Terrain firstTerrain, int limit, int[] dest, int method1, Terrain otherTerrain)
    {
        int[] hm = new NoiseMap(random).getNoise(4, 1);

        for (int i = 0; i < 16; i++)
        {
            int h0 = ((i >> 0) & 1) * (256 + 64);
            int h1 = ((i >> 1) & 1) * (256 + 64);
            int h2 = ((i >> 2) & 1) * (256 + 64);
            int h3 = ((i >> 3) & 1) * (256 + 64);

            int[] pixels = new int[16 * 16];

            for (int y = 15; y >= 0; y--)
            {
                int hh0 = h0 + (h2 - h0) * y / 16;
                int hh1 = h1 + (h3 - h1) * y / 16;

                for (int x = 0; x < 16; x++)
                {
                    int hh = hh0 + (hh1 - hh0) * x / 16 - 32;
                    hh += hm[x + y * 16] / 3;
                    int color, color2, p, m;

                    if (hh < limit)
                    {
                        p = hh * 256 / limit;
                        color = source[x + y * 16];
                        color2 = dest[x + y * 16];
                        m = method0;
                    }
                    else
                    {
                        p = 255 - ((hh - limit) * 256 / (256 - limit));
                        color = dest[x + y * 16];
                        color2 = source[x + y * 16];
                        m = method1;
                    }

                    switch (m)
                    {
                        case METHOD_DARKEN:
                            color = lerp(color, 0xff000000, p / 2 - 64);
                            break;
                        case METHOD_SHORE:
                            if (p > 128) color = lerp(color, lerp(color2, 0xff000000, 128), p - 64);
                            break;
                        case METHOD_NOISE:
                            color = lerp(color, lerp(color2, 0xff000000, 64), random.nextInt(255) > p + 64 ? 0 : (p + 64));
                            break;
                        case METHOD_LERP:
                            color = lerp(color, color2, p);
                            break;
                    }

                    pixels[x + y * 16] = color;
                }
            }

            tiles[i + offset] = new Tile(pixels, 16, 16, i == 0 ? firstTerrain : otherTerrain);
        }
    }

    public void lerp(int[] pixels, int pos, int to, int a)
    {
        pixels[pos] = lerp(pixels[pos], to, a);
    }

    public int lerp(int from, int to, int a)
    {
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        int aSource = (from >> 24) & 0xff;
        int rSource = (from >> 16) & 0xff;
        int gSource = (from >> 8) & 0xff;
        int bSource = (from) & 0xff;

        int aDest = (to >> 24) & 0xff;
        int rDest = (to >> 16) & 0xff;
        int gDest = (to >> 8) & 0xff;
        int bDest = (to) & 0xff;

        int aRes = aSource + (aDest - aSource) * a / 255;
        int rRes = rSource + (rDest - rSource) * a / 255;
        int gRes = gSource + (gDest - gSource) * a / 255;
        int bRes = bSource + (bDest - bSource) * a / 255;

        return (aRes << 24) | (rRes << 16) | (gRes << 8) | bRes;
    }
}