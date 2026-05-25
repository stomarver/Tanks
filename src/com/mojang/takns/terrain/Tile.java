package com.mojang.takns.terrain;

import java.awt.image.BufferedImage;

import com.mojang.takns.ImageConverter;

public class Tile
{
    public BufferedImage image;
    public Terrain terrain;

    public Tile(int[] pixels, int width, int height, Terrain terrain)
    {
        boolean hasAlpha = false;
        for (int i = 0; i < width * height && !hasAlpha; i++)
            if ((pixels[i] & 0xff000000) != 0xff000000) hasAlpha = true;
        

        BufferedImage image = ImageConverter.convert(width, height, pixels, hasAlpha?2:0);

        this.image = image;
        this.terrain = terrain;
    }
}