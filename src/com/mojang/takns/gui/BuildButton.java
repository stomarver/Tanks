package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mojang.takns.gui.buttons.*;

public class BuildButton extends UiComponent
{
    private BufferedImage image;
    private int cost;
    private ButtonType buildType;
    
    public BuildButton(ButtonType buildType)
    {
        this.image = buildType.getImage();
        if (image==null) throw new IllegalArgumentException("No image for "+buildType);
        this.cost = buildType.getCost();
        this.buildType = buildType;
    }
    
    private static final Color fillColor = new Color(0xff808080);
    private static final Color selectColor = new Color(0xff707080);

    public void render(Graphics2D g, float alpha)
    {
        boolean pressed = buildType.isActive(world); 
        fillBlock(g, x, y, 28, 24, pressed?selectColor:fillColor, pressed);
        
        int xo = image.getWidth()-16;
        int yo = image.getHeight()-16;
        g.drawImage(image, x +(width-16)/2-xo/2, y + 4-2-yo/2+(pressed?1:0), null);

        String costString = "§3"+cost;
        
        Text.drawString(costString, g, x+14-(costString.length()-2)*3, y+18);
    }
    
    private static final Color upColor = new Color(0.0f, 0.0f, 0.0f, 0.7f);
    private static final Color downColor = new Color(1.0f, 1.0f, 1.0f, 0.7f);

    private void fillBlock(Graphics2D g, int x, int y, int w, int h, Color color, boolean down)
    {
        g.setColor(color);
        g.fillRect(x - 1, y - 1, w + 2, h + 2);
        g.setColor(down?downColor:upColor);
        g.fillRect(x, y, w + 1, h + 1);
        g.setColor(down?upColor:downColor);
        g.fillRect(x - 1, y - 1, w + 1, h + 1);
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }

    public void mousePressed(int button)
    {
        if (button==1)
        {
            buildType.activate(world);
        }
    }
    
    public String getToolTip()
    {
        return buildType.getToolTip();
    }
}