package com.mojang.takns.units.buildings;

import com.mojang.takns.Side;
import com.mojang.takns.World;
import com.mojang.takns.sprites.Voxels;
import com.mojang.takns.units.Unit;

public class Wall extends Building
{
    public Wall()
    {
        super(Voxels.walls[0], Voxels.wallShadows[0]);
        width = 1;
        height = 1;
        revealRadius = 0;
        cost = 50;
    }

    public void init(World world, Side side)
    {
        super.init(world, side);

        updateImages();

        updateWalls(xTile - 1, yTile);
        updateWalls(xTile + 1, yTile);
        updateWalls(xTile, yTile - 1);
        updateWalls(xTile, yTile + 1);
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        setImages(Voxels.walls[10], Voxels.wallShadows[10]);
    }

    public void updateImages()
    {
        boolean left = isWall(xTile - 1, yTile);
        boolean right = isWall(xTile + 1, yTile);
        boolean up = isWall(xTile, yTile - 1);
        boolean down = isWall(xTile, yTile + 1);

        int image = (up ? 1 : 0) + (left ? 2 : 0) + (down ? 4 : 0) + (right ? 8 : 0);
        setImages(Voxels.walls[image], Voxels.wallShadows[image]);
    }

    private boolean isWall(int x, int y)
    {
        Unit u = world.map.getUnitAt(x, y);
        if (u == null) return false;
        return u.shouldConnectToWall();
    }

    public boolean shouldConnectToWall()
    {
        return true;
    }

    public void addToMinimap(int[] minimapPixels)
    {
        int x = (int) (xo / 16);
        int y = (int) (yo / 16);
        if (x >= 0 && y >= 0 && x < 64 && y < 64)
        {
            minimapPixels[x + y * 64] = 0xff606060;
        }
    }
    
    public String getName()
    {
        return "Wall";
    }
}