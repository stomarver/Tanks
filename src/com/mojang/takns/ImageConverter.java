package com.mojang.takns;

import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class ImageConverter
{
    private ImageConverter()
    {
    }
    
    private static GraphicsConfiguration gc;
    
    public static void init(GraphicsConfiguration gc)
    {
        ImageConverter.gc = gc;
    }
    
    public static BufferedImage convert(BufferedImage img, int transparency)
    {
        BufferedImage image = null;
        if (transparency==0)
            image = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.OPAQUE);
        else if (transparency==1)
            image = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.BITMASK);
        else if (transparency==2)
            image = gc.createCompatibleImage(img.getWidth(), img.getHeight(), Transparency.TRANSLUCENT);
        else
            throw new IllegalArgumentException("!!!!");
        
        image.getRaster().setRect(img.getRaster());
        
        return image;
    }

    public static BufferedImage convert(int width, int height, int[] pixels, int transparency)
    {
        BufferedImage image = new BufferedImage(width, height, transparency!=0 ? BufferedImage.TYPE_INT_ARGB_PRE : BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, pixels, 0, width);
        return convert(image, transparency);
    }
}