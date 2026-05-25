package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mojang.takns.Side;

public class MinimapComponent extends UiComponent
{
    private Side side;
    
    public MinimapComponent(Side side)
    {
        this.side = side;
    }
    
    public void render(Graphics2D g, float alpha)
    {
        g.setColor(new Color(0, 0, 0, 0.7f));
        g.drawRect(x - 1, y - 1, width, height);
        g.setColor(new Color(1, 1, 1, 0.7f));
        g.drawRect(x, y, width, height);

        g.drawImage(world.minimap.image, x, y, null);
        g.drawImage(side.fogOfWar.minimapImage, x, y, null);

        g.setColor(new Color(1, 1, 1, 0.75f));
        g.drawRect(x - 1 + (world.xCam + 8) / 16, y - 1 + (world.yCam + 8) / 16, world.gameView.width / 16 + 2, world.gameView.height / 16 + 1);
        g.setColor(new Color(1, 1, 1, 1.0f));
    }

    public void drag(int button, int xStart, int yStart)
    {
        if (button==1)
        {
            world.centerCam((xMouse) * 16, (yMouse) * 16);
        }
    }

    public void mousePressed(int button)
    {
        if (button==1)
        {
            world.centerCamSmooth((xMouse) * 16, (yMouse) * 16);
        }
        if (button==3)
        {
            side.units.moveAllSelected(xMouse, yMouse);
        }
    }
}