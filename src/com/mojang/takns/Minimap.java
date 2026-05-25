package com.mojang.takns;

import java.awt.image.*;

import com.mojang.takns.terrain.Tiles;

public class Minimap
{
    public int[] pixels;
    public BufferedImage image;
    private World world;
    private boolean hasDirectRaster = false;
    
    public Minimap(World world)
    {
        this.world = world;
        image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        try
        {
            pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            hasDirectRaster = true;
        }
        catch (Exception e)
        {
            pixels = new int[64*64];
            System.out.println("No direct raster access for minimap: "+e);
        }
        
        world.map.setMinimap(this);
        
        updateArea(0, 0, 64, 64);
        
        for (int y=0; y<64; y++)
            for (int x=0; x<64; x++)
            {
                pixels[x+y*64] = Tiles.tiles[world.map.tiles[x+y*64]].terrain.color;
            }

        buildImage();
    }
    
    public void updateArea(int x1, int y1, int w, int h)
    {
        for (int y=y1; y<y1+h; y++)
            for (int x=x1; x<x1+w; x++)
            {
                pixels[x+y*64] = Tiles.tiles[world.map.tiles[x+y*64]].terrain.color;
            }

        if (!hasDirectRaster)
            image.setRGB(x1, y1, w, h, pixels, x1+y1*64, 64);
    }
    
    public void buildImage()
    {
        if (!hasDirectRaster)
            image.setRGB(0, 0, 64, 64, pixels, 0, 64);
    }
}