package com.mojang.takns.gui.states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mojang.takns.terrain.*;

public class SlabState extends State
{
    int cost = 20;
    
    public void mousePressed(int button)
    {
        if (button == 3)
        {
            int xMouse = gameView.xMouse + world.xCam;
            int yMouse = gameView.yMouse + world.yCam;

            int xTile = (xMouse) / 16;
            int yTile = (yMouse) / 16;

            if (canBuild(xTile, yTile))
            {
                world.playerSide.chargeMoney(cost);
                world.map.setTile(xTile, yTile, Tiles.TYPE_SLAB);
            }
        }

        if (button == 1)
        {
            gameView.setState(null);
            return;
        }
    }

    private boolean canBuild(int x, int y)
    {
        if (world.playerSide.money<cost) return false;
        if (x < 0 || y < 0 || x >= 64 || y >= 64) return false;
        if (Tiles.tiles[world.map.getTile(x, y)].terrain != Terrain.TERRAIN_GRASS) return false;
        if (!world.playerSide.fogOfWar.isLit(x, y)) return false;

        if (x > 0 && Tiles.tiles[world.map.getTile(x-1, y)].terrain == Terrain.TERRAIN_SLAB) return true;
        if (x < 63 && Tiles.tiles[world.map.getTile(x+1, y)].terrain == Terrain.TERRAIN_SLAB) return true;
        if (y > 0 && Tiles.tiles[world.map.getTile(x, y-1)].terrain == Terrain.TERRAIN_SLAB) return true;
        if (y < 63 && Tiles.tiles[world.map.getTile(x, y+1)].terrain == Terrain.TERRAIN_SLAB) return true;
        
        return false;
    }

    public void render(Graphics2D g, float alpha)
    {
        if (!gameView.hovered) return;
        
        int xMouse = gameView.xMouse + world.xCam;
        int yMouse = gameView.yMouse + world.yCam;

        int xTile = (xMouse) / 16;
        int yTile = (yMouse) / 16;

        if (canBuild(xTile, yTile))
        {
            g.setColor(new Color(0, 1, 0, 0.5f));
            g.drawRect(xTile*16-world.xCam, yTile*16-world.yCam, 15, 15);
        }
        else
        {
            g.setColor(new Color(1, 0, 0, 0.5f));
            g.drawRect(xTile*16-world.xCam, yTile*16-world.yCam, 15, 15);
        }
    }

    public int getCost()
    {
        return cost;
    }

    public BufferedImage getImage()
    {
        return Tiles.tiles[Tiles.TYPE_SLAB].image;
    }
}