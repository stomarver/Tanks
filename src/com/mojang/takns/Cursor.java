package com.mojang.takns;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Cursor
{
    public BufferedImage image;
    
    public Cursor()
    {
        int[] xs = new int[4];
        int[] ys = new int[4];
        
        xs[0] = 0;
        ys[0] = 0;

        xs[1] = 7;
        ys[1] = 7;
        xs[2] = 0;
        ys[2] = 10;
        
        image = new BufferedImage(8, 11, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        g.setColor(Color.WHITE);
        g.fillPolygon(xs, ys, 3);
        g.setColor(Color.BLACK);
        g.drawPolygon(xs, ys, 3);
        g.dispose();
        
        image = ImageConverter.convert(image, 1);
    }
}