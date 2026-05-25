package com.mojang.takns;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import com.mojang.takns.units.Unit;

public class FogOfWar
{
    private static final int VISIBLE_COLOR = 0x00000000;
    private static final int SHADE_COLOR = 0x80000000;
    private static final int HIDDEN_COLOR = 0xff000000;

    public int[] litCorners = new int[65 * 65];
    public int[] staticLitCorners = new int[65 * 65];

    public int[] lightMap = new int[64 * 64];
    public int[] revealMap = new int[64 * 64];
    public int[] minimapPixels = new int[64 * 64];

    public BufferedImage minimapImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB_PRE);
    boolean hasDirectRaster = false;

    public FogOfWar()
    {
        try
        {
            minimapPixels = ((DataBufferInt) minimapImage.getRaster().getDataBuffer()).getData();
            hasDirectRaster = true;
        }
        catch (Exception e)
        {
            System.out.println("No direct raster access for fog of war: "+e);
        }
    }

    public void tick()
    {
        for (int i = 0; i < 65 * 65; i++)
        {
            litCorners[i] &= 1;
            if (staticLitCorners[i] != 0) litCorners[i] = 3;
        }
    }

    public void prepareRender()
    {
        for (int y = 0; y < 64; y++)
        {
            for (int x = 0; x < 64; x++)
            {
                int tile = 0;
                int tile2 = 0;
                for (int i = 0; i < 4; i++)
                {
                    if ((litCorners[(x + (i & 1) + (y + (i >> 1)) * 65)] & 1) != 0)
                    {
                        tile += 1 << i;
                    }
                    if ((litCorners[(x + (i & 1) + (y + (i >> 1)) * 65)] & 2) != 0)
                    {
                        tile2 += 1 << i;
                    }
                }

                lightMap[x + y * 64] = tile2;
                revealMap[x + y * 64] = tile;
                minimapPixels[x + y * 64] = tile2 != 0 ? VISIBLE_COLOR : tile == 0 ? HIDDEN_COLOR : SHADE_COLOR;
            }
        }
    }

    public void buildImage()
    {
        if (!hasDirectRaster) minimapImage.setRGB(0, 0, 64, 64, minimapPixels, 0, 64);
    }

    public void revealStatic(int x, int y, int r)
    {
        float rr = r * 0.95f;
        for (int xx = x - r; xx <= x + r; xx++)
        {
            if (xx >= 0 && xx < 65)
            {
                for (int yy = y - r; yy <= y + r; yy++)
                {
                    if (yy >= 0 && yy < 65)
                    {
                        int _x = xx - x;
                        int _y = yy - y;
                        if (xx < x) _x--;
                        if (yy < y) _y--;
                        int rad = _x * _x + _y * _y;
                        if (rad < rr * rr)
                        {
                            staticLitCorners[xx + yy * 65]++;
                        }
                    }
                }
            }
        }
    }

    public void unRevealStatic(int x, int y, int r)
    {
        float rr = r * 0.95f;
        for (int xx = x - r; xx <= x + r; xx++)
        {
            if (xx >= 0 && xx < 65)
            {
                for (int yy = y - r; yy <= y + r; yy++)
                {
                    if (yy >= 0 && yy < 65)
                    {
                        int _x = xx - x;
                        int _y = yy - y;
                        if (xx < x) _x--;
                        if (yy < y) _y--;
                        int rad = _x * _x + _y * _y;
                        if (rad < rr * rr)
                        {
                            staticLitCorners[xx + yy * 65]--;
                        }
                    }
                }
            }
        }
    }

    public void reveal(int x, int y, int r)
    {
        float rr = r * 0.95f;
        for (int xx = x - r; xx <= x + r; xx++)
        {
            if (xx >= 0 && xx < 65)
            {
                for (int yy = y - r; yy <= y + r; yy++)
                {
                    if (yy >= 0 && yy < 65)
                    {
                        int _x = xx - x;
                        int _y = yy - y;
                        if (xx < x) _x--;
                        if (yy < y) _y--;
                        int rad = _x * _x + _y * _y;
                        if (rad < rr * rr)
                        {
                            litCorners[xx + yy * 65] = 3;
                        }
                    }
                }
            }
        }
    }

    public boolean isVisible(Sprite sprite)
    {
        int xCorner = (sprite.x + sprite.xo + 16) / 16;
        int yCorner = (sprite.y + sprite.yo - sprite.z + 16) / 16;
        if (xCorner < 0 || yCorner < 0 || xCorner > 64 || yCorner > 64) return false;
        return litCorners[xCorner + yCorner * 65] == 3;
    }

    public boolean isVisible(Unit unit)
    {
        int xCorner = (int) (unit.x + 16) / 16;
        int yCorner = (int) (unit.y - unit.z + 16) / 16;
        if (xCorner < 0 || yCorner < 0 || xCorner > 64 || yCorner > 64) return false;
        return litCorners[xCorner + yCorner * 65] == 3;
    }

    public boolean isRevealed(int x, int y)
    {
        if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
        return revealMap[x + y * 64] != 0;
    }

    public boolean isLit(int x, int y)
    {
        if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
        return lightMap[x + y * 64] != 0;
    }
}