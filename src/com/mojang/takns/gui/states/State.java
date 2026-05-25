package com.mojang.takns.gui.states;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mojang.takns.World;
import com.mojang.takns.gui.*;

public abstract class State
{
    protected World world;
    protected GameView gameView;
    
    public void init(World world, GameView gameView)
    {
        this.world = world;
        this.gameView = gameView;
    }
    
    public void tick()
    {
    }

    public void mousePressed(int button)
    {
    }

    public void mouseReleased(int button)
    {
    }

    public void render(Graphics2D g, float alpha)
    {
    }

    public void drag(int button, int xStart, int yStart)
    {
    }

    public abstract int getCost();

    public abstract BufferedImage getImage();
}