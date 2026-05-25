package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import com.mojang.takns.Side;
import com.mojang.takns.gui.states.*;
import com.mojang.takns.units.MoveableUnit;
import com.mojang.takns.units.Unit;

public class GameView extends UiComponent
{
    private boolean selecting = false;
    private int xSelect0, xSelect1;
    private int ySelect0, ySelect1;
    private Side side;
    private State state = null;

    private static class MoveClickEffect
    {
        int x;
        int y;
        int age;
        int maxAge = 20;
    }

    private List<MoveClickEffect> moveClickEffects = new ArrayList<MoveClickEffect>();
    
    public GameView(Side side)
    {
        this.side = side;
    }

    public void render(Graphics2D g, float alpha)
    {
        world.mapRenderer.render(g);
        world.postRender(g, alpha);

        if (state!=null)
        {
            state.render(g, alpha);
        }

        if (selecting)
        {
            g.setColor(new Color(1, 1, 1, 1.0f));
            int x = xSelect0 < xSelect1 ? xSelect0 : xSelect1;
            int y = ySelect0 < ySelect1 ? ySelect0 : ySelect1;
            int w = xSelect0 < xSelect1 ? xSelect1 - xSelect0 : xSelect0 - xSelect1;
            int h = ySelect0 < ySelect1 ? ySelect1 - ySelect0 : ySelect0 - ySelect1;
            g.drawRect(x, y, w, h);
        }

        renderMovePathDebug(g);
        renderMoveClickEffects(g);
    }

    public void tick()
    {
        if (state!=null)
            state.tick();

        for (int i = 0; i < moveClickEffects.size(); i++)
        {
            MoveClickEffect fx = moveClickEffects.get(i);
            fx.age++;
            if (fx.age >= fx.maxAge)
            {
                moveClickEffects.remove(i);
                i--;
            }
        }
    }

    private void addMoveClickEffect(int xWorld, int yWorld)
    {
        MoveClickEffect fx = new MoveClickEffect();
        fx.x = xWorld;
        fx.y = yWorld;
        moveClickEffects.add(fx);
    }

    private void renderMoveClickEffects(Graphics2D g)
    {
        for (int i = 0; i < moveClickEffects.size(); i++)
        {
            MoveClickEffect fx = moveClickEffects.get(i);
            float progress = fx.age / (float) fx.maxAge;
            int radius = 17 - (int) (progress * 14);
            if (radius < 3) radius = 3;
            float alpha = progress;
            if (alpha < 0) alpha = 0;
            if (alpha > 1) alpha = 1;
            g.setColor(new Color(0.65f, 0.95f, 1.0f, alpha));
            int xScreen = fx.x - world.xCam;
            int yScreen = fx.y - world.yCam;
            g.drawOval(xScreen - radius, yScreen - radius, radius * 2, radius * 2);
        }
    }

    private void renderMovePathDebug(Graphics2D g)
    {
        g.setColor(new Color(1.0f, 0.85f, 0.2f, 0.85f));

        List<Unit> selected = side.units.selectedUnits;
        for (int i = 0; i < selected.size(); i++)
        {
            Unit unit = selected.get(i);
            if (!(unit instanceof MoveableUnit)) continue;
            MoveableUnit moveable = (MoveableUnit) unit;

            int xPrev = (int) moveable.x;
            int yPrev = (int) moveable.y;
            int pathLength = moveable.getPathLength();

            for (int p = pathLength - 1; p >= 0; p--)
            {
                int tile = moveable.getPathTileAt(p);
                if (tile < 0) continue;
                int xTile = tile & 63;
                int yTile = tile >> 6;
                int xNext = xTile * 16 + 8;
                int yNext = yTile * 16 + 8;

                int x0 = xPrev - world.xCam;
                int y0 = yPrev - world.yCam;
                int x1 = xNext - world.xCam;
                int y1 = yNext - world.yCam;
                drawDashedLine(g, x0, y0, x1, y1, 4, 3);
                xPrev = xNext;
                yPrev = yNext;
            }
        }
    }

    private void drawDashedLine(Graphics2D g, int x0, int y0, int x1, int y1, int dash, int gap)
    {
        int dx = x1 - x0;
        int dy = y1 - y0;
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len <= 0.001f) return;

        float ux = dx / len;
        float uy = dy / len;
        float pos = 0;
        while (pos < len)
        {
            float end = pos + dash;
            if (end > len) end = len;

            int sx = (int) (x0 + ux * pos);
            int sy = (int) (y0 + uy * pos);
            int ex = (int) (x0 + ux * end);
            int ey = (int) (y0 + uy * end);
            g.drawLine(sx, sy, ex, ey);

            pos += dash + gap;
        }
    }

    public void drag(int button, int xStart, int yStart)
    {
        if (button == 2)
        {
            world.moveCam(xLastDrag - xMouse, yLastDrag - yMouse);
        }
        
        if (state!=null)
        {
            state.drag(button, xStart, yStart);
            return;
        }
        
        if (button == 1)
        {
            xSelect0 = xStart;
            ySelect0 = yStart;
            xSelect1 = xMouse;
            ySelect1 = yMouse;
            selecting = true;
        }
    }

    public void mousePressed(int button)
    {
        selecting = false;
        if (button == 1)
        {
            side.units.unSelectAll();
            setState(null);
        }
        
        if (state!=null)
        {
            state.mousePressed(button);
            return;
        }
        
        int xMouse = this.xMouse + world.xCam;
        int yMouse = this.yMouse + world.yCam;

        if (button == 3)
        {
            side.units.moveAllSelected(xMouse / 16, yMouse / 16);
            addMoveClickEffect((xMouse / 16) * 16 + 8, (yMouse / 16) * 16 + 8);
        }
    }

    public void mouseReleased(int button)
    {
        if (button == 1)
        {
            side.units.unSelectAll();

            if (selecting)
            {
                int x0 = xSelect0 < xSelect1 ? xSelect0 : xSelect1;
                int y0 = ySelect0 < ySelect1 ? ySelect0 : ySelect1;
                int x1 = xSelect0 < xSelect1 ? xSelect1 : xSelect0;
                int y1 = ySelect0 < ySelect1 ? ySelect1 : ySelect0;
                boolean selected = side.units.selectAll(x0 + world.xCam, y0 + world.yCam, x1 + world.xCam, y1 + world.yCam);
                if (!selected)
                {
                    side.units.selectClosest(xMouse + world.xCam, yMouse + world.yCam);
                }
                selecting = false;
            }
            else
            {
                side.units.selectClosest(xMouse + world.xCam, yMouse + world.yCam);
            }
        }
    }

    public void setState(State state)
    {
        this.state = state;
        if (state!=null) state.init(world, this);
    }

    public State getState()
    {
        return state;
    }
}