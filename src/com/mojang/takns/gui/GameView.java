package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mojang.takns.Side;
import com.mojang.takns.gui.states.*;

public class GameView extends UiComponent
{
    private boolean selecting = false;
    private int xSelect0, xSelect1;
    private int ySelect0, ySelect1;
    private Side side;
    private State state = null;
    
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
    }

    public void tick()
    {
        if (state!=null)
            state.tick();
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