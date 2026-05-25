package com.mojang.takns.sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mojang.takns.ImageConverter;
import com.mojang.takns.Side;
import com.mojang.takns.units.buildings.Building;
import com.mojang.takns.units.MoveableUnit;

public class BuildIcons
{
    private static Side side;
    
    public static void setPreviewSide(Side side)
    {
        BuildIcons.side = side;
    }
    
    public static BufferedImage getBufferedImage(MoveableUnit vehicle)
    {
        vehicle.setSide(side);
        
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        vehicle.sprite.renderImageTo(g, 32, 42);
        g.dispose();
        
        return ImageConverter.convert(halfSize(image), 2);
    }

    public static BufferedImage getBufferedImage(Building building)
    {
        building.setSide(side);
        
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        building.sprite.renderImageTo(g, 32-(building.width-1)*8, 42-(building.height-1)*8);
        g.dispose();
        return ImageConverter.convert(halfSize(image), 2);
    }
    
    private static BufferedImage halfSize(BufferedImage in)
    {
        BufferedImage out = new BufferedImage(in.getWidth() / 2, in.getHeight() / 2+8, BufferedImage.TYPE_INT_ARGB_PRE);

        int[] inPixels = new int[in.getWidth() * in.getHeight()];
        in.getRGB(0, 0, in.getWidth(), in.getHeight(), inPixels, 0, in.getWidth());
        
        int[] outPixels = Voxels.halfSize(inPixels, in.getWidth(), in.getHeight(), 2);

        out.setRGB(0, 0, out.getWidth(), out.getHeight()-8, outPixels, 0, out.getWidth());

        return out;
    }
    
}