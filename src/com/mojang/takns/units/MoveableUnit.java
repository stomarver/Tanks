package com.mojang.takns.units;


import com.mojang.takns.ai.PathFinder;
import com.mojang.takns.terrain.Terrain;
import com.mojang.takns.terrain.Tiles;
import com.mojang.takns.units.Unit;

public abstract class MoveableUnit extends Unit
{
    protected PathFinder pathFinder = new PathFinder();
    private static int[] travelCosts = new int[64 * 64];

    public MoveableUnit()
    {
        super();
    }

    protected void updatePathfinder()
    {
        if (pathFinder.isPathing && !moving)
        {
            p = 0;
            pathFinder.continueFindingPath(150);
    
            if (!pathFinder.isPathing && pathFinder.pathP > 0)
            {
                int p = pathFinder.path[--pathFinder.pathP];
                xOld = (int) (x / 16) * 16 + 8;
                yOld = (int) (y / 16) * 16 + 8;
                xNextTile = (p & 63) * 16 + 8;
                yNextTile = (p >> 6) * 16 + 8;
                
                pathFound();
                moving = true;
    
                if (xOld == xNextTile && yOld == yNextTile)
                    this.p = 1;
            }
        }
    }
    
    public void moveTo(int xDestination, int yDestination)
    {
        super.moveTo(xDestination, yDestination);

        int[] map = world.map.tiles;
        Unit[] blockMap = world.map.blockMap;
        for (int i = 0; i < 64 * 64; i++)
        {
            if (!side.fogOfWar.isRevealed(i & 63, i / 64))
            {
                travelCosts[i] = 50;
            }
            else
            {
                travelCosts[i] = Tiles.tiles[map[i]].terrain.travelCost;
                if ((Tiles.tiles[map[i]].terrain.passableFlags & Terrain.PASSABLE_LAND)==0)
                    travelCosts[i] = 0;

                Unit blockUnit = blockMap[i];
                if (blockUnit != null && blockUnit != this)
                {
                    if (blockUnit.moving)
                    {
                        travelCosts[i] += 15;
                    }
                    else
                    {
                        travelCosts[i] = 0;
                    }
                }
            }
        }

        if (moving)
        {
            pathFinder.startFindingPath(travelCosts, (int) (xNextTile / 16), (int) (yNextTile / 16), xDestination, yDestination);
        }
        else
        {
            pathFinder.startFindingPath(travelCosts, (int) (x / 16), (int) (y / 16), xDestination, yDestination);
        }
    }
    
    protected void pathFound()
    {
    }

    protected boolean canTravelTo(int x, int y)
    {
    	if (world.map.tiles[x + y * 64] < 15)
    		return false;
    	Unit blockUnit = world.map.blockMap[x + y * 64];
    	if (blockUnit != null && blockUnit != this)
    		return false;
    	return true;
    }

}