package com.mojang.takns.units.vehicles;

import com.mojang.takns.particles.DebrisParticle;
import com.mojang.takns.sprites.*;
import com.mojang.takns.terrain.Terrain;
import com.mojang.takns.terrain.Tiles;
import com.mojang.takns.units.Unit;
import com.mojang.takns.units.buildings.Building;

public class Harvester extends Vehicle
{
    public static final int MAX_CARGO = 500;
    public static final int DUMP_SPEED = 5;
    public static final int GEM_LIFE = 100;

    public int cargo = 0;
    public int xLastSiloPos = 0;
    public int yLastSiloPos = 0;
    public int xLastHarvestPos = 0;
    public int yLastHarvestPos = 0;

    private boolean shouldAutofindTarget = false;

    public Harvester(int xTile, int yTile)
    {
        super(Voxels.harvesterBase[0], Voxels.harvesterShadow);

        xLastSiloPos = xTile;
        yLastSiloPos = yTile;

        this.x = xTile * 16;
        this.y = yTile * 16;

        vehicleSpeed = 0.8f;
        revealRadius = 4;
    }

    public void tick()
    {
        super.tick();

        if (nextToSilo())
        {
            xLastSiloPos = xTile;
            yLastSiloPos = yTile;

            if (cargo > 0)
            {
                dumpMoney();
                return;
            }
        }

        if (moving || pathFinder.isPathing) return;

        if (cargo < MAX_CARGO)
        {
            harvest();
        }
    }
    
    private void dumpMoney()
    {
        float xp = this.x + random.nextFloat() * 8 - 4;
        float yp = this.y + random.nextFloat() * 8 - 4;

        float xa = -SIN[baseAngle] * 0;
        float ya = COS[baseAngle] * 0;
        world.particleSystem.addParticle(new DebrisParticle(Terrain.TERRAIN_GEM.id, xp, yp, 6.0f, xa, ya, 3.0f));

        int toDump = cargo;
        if (toDump>DUMP_SPEED) toDump = DUMP_SPEED;
        cargo-=toDump;
        world.playerSide.addMoney(toDump);
        baseImages = Voxels.harvesterBase[1];
        if (cargo == 0)
        {
            baseImages = Voxels.harvesterBase[0];
            moveTo(xLastHarvestPos, yLastHarvestPos);
            shouldAutofindTarget = true;
        }
    }

    private boolean nextToSilo()
    {
        if (isSilo(world.map.getUnitAt(xTile, yTile - 1))) return true;
        if (isSilo(world.map.getUnitAt(xTile, yTile + 1))) return true;
        if (isSilo(world.map.getUnitAt(xTile - 1, yTile))) return true;
        if (isSilo(world.map.getUnitAt(xTile + 1, yTile))) return true;
        return false;
    }

    private boolean isSilo(Unit unit)
    {
        if (unit == null) return false;
        if (!unit.isBuilding()) return false;
        if (unit.side != side) return false;
        if (!((Building)unit).isSilo()) return false;

        return true;
    }

    private void harvest()
    {
        int x = xTile;
        int y = yTile;

        if (x >= 0 && y >= 0 && x < 64 && y < 64)
        {
            int tile = world.map.getTile(x, y);

            if (Tiles.tiles[tile].terrain == Terrain.TERRAIN_GEM)
            {
                if (random.nextInt(2) == 0)
                {
                    float xp = this.x + random.nextFloat() * 8 - 4;
                    float yp = this.y + random.nextFloat() * 8 - 4;

                    float xa = -SIN[baseAngle] * 3;
                    float ya = COS[baseAngle] * 3;

                    world.particleSystem.addParticle(new DebrisParticle(Terrain.TERRAIN_GEM.id, xp, yp, 6.0f, xa, ya, 3.0f));
                }

                xLastHarvestPos = xTile;
                yLastHarvestPos = yTile;
                cargo++;
                shouldAutofindTarget = true;
                baseImages = Voxels.harvesterBase[1];
                if (cargo == MAX_CARGO)
                {
                    baseImages = Voxels.harvesterBase[2];
                    moveTo(xLastSiloPos, yLastSiloPos);
                }

                if (random.nextInt(GEM_LIFE) == 0)
                {
                    tile--;
                    if (Tiles.tiles[tile].terrain != Terrain.TERRAIN_GEM)
                    {
                        tile = Tiles.TYPE_SAND;
                        if (!moving && !pathFinder.isPathing)
                        {
                        }
                    }
                    world.map.setTile(x, y, tile);
                }
            }
            else
            {
                if (shouldAutofindTarget) findNewTarget();
            }
        }
    }

    private void findNewTarget()
    {
        int r = 4;
        int closest = 0;
        int xTarget = -1;
        int yTarget = -1;

        for (int x = xTile - r; x <= xTile + r; x++)
        {
            for (int y = yTile - r; y <= yTile + r; y++)
            {
                if (x >= 0 && y >= 0 && x < 64 && y < 64)
                {
                    int tile = world.map.getTile(x, y);
                    if (Tiles.tiles[tile].terrain == Terrain.TERRAIN_GEM)
                    {
                        int xd = (x - xLastSiloPos);
                        int yd = (y - yLastSiloPos);
                        int distance = xd * xd + yd * yd;
                        if (closest == 0 || distance < closest)
                        {
                            closest = distance;
                            xTarget = x;
                            yTarget = y;
                        }
                    }
                }
            }
        }

        if (xTarget != -1)
        {
            moveTo(xTarget, yTarget);
            shouldAutofindTarget = true;
        }
    }

    public void moveTo(int xDestination, int yDestination)
    {
        super.moveTo(xDestination, yDestination);
        shouldAutofindTarget = false;
    }
    
    public String getName()
    {
        return "Gem van";
    }
}