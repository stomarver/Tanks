package com.mojang.takns.gui.buttons;

import java.awt.image.BufferedImage;

import com.mojang.takns.World;

public abstract class ButtonType
{
    public abstract int getCost();

    public abstract void activate(World world);

    public abstract String getToolTip();
    
    public boolean isActive(World world)
    {
        return false;
    }
    
    public abstract BufferedImage getImage();
}