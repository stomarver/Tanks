package com.mojang.takns.units;

import java.awt.Graphics2D;
import java.util.*;

import com.mojang.takns.*;

public class Units
{
    public List<Unit> units = new ArrayList<Unit>();
    public List<Unit> selectedUnits = new ArrayList<Unit>();
    public List<Unit> seenEnemies = new ArrayList<Unit>();
    private World world;
    private Side side;

    public Units(World world, Side side)
    {
        this.world = world;
        this.side = side;
    }

    public void addUnit(Unit unit)
    {
        unit.init(world, side);
        units.add(unit);
    }

    public void tick()
    {
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            unit.tick();
            if (!unit.alive)
            {
                unit.remove(this);
                units.remove(i);
                i--;
            }
        }
    }

    public void render(float alpha, FogOfWar fogOfWar)
    {
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            if (fogOfWar.isVisible(unit))
            {
                unit.addToMinimap(fogOfWar.minimapPixels);
                unit.render(alpha);
            }
            else
            {
                unit.hide();
            }
        }
    }

    public void postRender(Graphics2D g, float alpha)
    {
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            unit.postRender(g, alpha);
        }
    }

    public void unSelectAll()
    {
        selectedUnits.clear();
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            unit.unSelect();
        }
    }

    public void selectClosest(int x, int y)
    {
        float closestDistance = 0;
        Unit closestUnit = null;
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            if (side.fogOfWar.isVisible(unit) && unit.isInside(x, y, x, y))
            {
                float distance = unit.getDistanceSqr(x, y);
                if (closestUnit == null || distance < closestDistance)
                {
                    closestUnit = unit;
                    closestDistance = distance;
                }
            }
        }

        if (closestUnit != null)
        {
            selectedUnits.add(closestUnit);
            closestUnit.select();
        }
    }

    public boolean selectAll(int x0, int y0, int x1, int y1)
    {
        float delay = 0;
        boolean hasSelected = false;
        int unselectable = 0;
        Unit unselectableUnit = null;

        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            if (side.fogOfWar.isVisible(unit) && unit.isInside(x0, y0, x1, y1))
            {
                if (unit.isGroupSelectable())
                {
                    unit.select(delay);
                    selectedUnits.add(unit);
                    delay = (float) (Math.random());
                    hasSelected = true;
                }
                else
                {
                    unselectable++;
                    unselectableUnit = unit;
                }
            }
        }

        if (!hasSelected && unselectable == 1)
        {
            unselectableUnit.select();
            selectedUnits.add(unselectableUnit);
            hasSelected = true;
        }

        return hasSelected;
    }

    public void moveAllSelected(int xTarget, int yTarget)
    {
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            if (unit.selected) unit.moveTo(xTarget, yTarget);
        }
    }

    public void clearSeenEnemies()
    {
        seenEnemies.clear();
    }

    public void setVisible(Side side)
    {
        FogOfWar fogOfWar = side.fogOfWar;
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            if (fogOfWar.isVisible(unit))
            {
                side.units.seenEnemies.add(unit);
            }
        }
    }

    public Unit closestSeenEnemy(float x, float y, float radius)
    {
        return closest(seenEnemies, x, y, radius);
   }

    private Unit closest(List<Unit> units, float x, float y, float radius)
    {
        float closestD = -1;
        Unit closest = null;
        for (int i = 0; i < units.size(); i++)
        {
            Unit unit = units.get(i);
            float xd = (unit.x-x)/16.0f;
            float yd = (unit.y-y)/16.0f;
            if (xd<-radius || xd>radius || yd<-radius || yd>radius) continue;

            float d = xd*xd+yd*yd;
            if (d<radius*radius)
            {
                if (closestD==-1 || d<closestD)
                {
                    closestD = d;
                    closest = unit;
                }
            }
        }
        
        return closest;
    }
}