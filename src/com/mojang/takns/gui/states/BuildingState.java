package com.mojang.takns.gui.states;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mojang.takns.sprites.BuildIcons;
import com.mojang.takns.terrain.*;
import com.mojang.takns.units.buildings.Building;

public class BuildingState extends State
{
    private boolean repeat;
    private Building buildingTemplate;
    private int cost = 50;

    public BuildingState(Building buildingTemplate)
    {
        this(buildingTemplate, false);
    }

    public BuildingState(Building buildingTemplate, boolean repeat)
    {
        this.buildingTemplate = buildingTemplate;
        this.repeat = repeat;
        cost = buildingTemplate.getCost();
    }

    public Building build(int x, int y)
    {
        Building building = buildingTemplate.newInstance();
        building.buildAt(x, y);
        return building;
    }

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
                Building unit = build(xTile, yTile);
                world.playerSide.units.addUnit(unit);
            }
            if (!repeat)
            {
                gameView.setState(null);
            }
        }

        if (button == 1)
        {
            gameView.setState(null);
        }
    }

    private boolean canBuild(int x, int y)
    {
        if (world.playerSide.money < cost) return false;
        if (x < 0 || y < 0 || x > 63 || y > 63) return false;

        for (int xx = 0; xx < buildingTemplate.width; xx++)
            for (int yy = 0; yy < buildingTemplate.height; yy++)
            {
                if (Tiles.tiles[world.map.getTile(x + xx, y + yy)].terrain != Terrain.TERRAIN_SLAB) return false;
                if (!world.playerSide.fogOfWar.isLit(x + xx, y + yy)) return false;
                if (world.map.getUnitAt(x + xx, y + yy) != null) return false;
            }

        return true;
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
            g.drawRect(xTile * 16 - world.xCam, yTile * 16 - world.yCam, buildingTemplate.width * 16 - 1, buildingTemplate.height * 16 - 1);
        }
        else
        {
            g.setColor(new Color(1, 0, 0, 0.5f));
            g.drawRect(xTile * 16 - world.xCam, yTile * 16 - world.yCam, buildingTemplate.width * 16 - 1, buildingTemplate.height * 16 - 1);
        }
    }

    public int getCost()
    {
        return cost;
    }

    private BufferedImage image;
    public BufferedImage getImage()
    {
        if (image==null)
        {
            image = BuildIcons.getBufferedImage(buildingTemplate);
        }
        return image;
    }
}