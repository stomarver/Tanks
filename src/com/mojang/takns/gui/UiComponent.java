package com.mojang.takns.gui;

import java.awt.Graphics2D;
import java.util.*;

import com.mojang.takns.*;

public class UiComponent
{
    public int x, y, width, height;
    public int xMouse;
    public int yMouse;
    protected boolean mouseOver = false;
    protected World world;
    protected List<UiComponent> components = new ArrayList<UiComponent>();

    protected int xLastDrag = 0;
    protected int yLastDrag = 0;
    protected boolean[] mouseDown = new boolean[16];
    public boolean hovered = false;

    public void init(World world, int x, int y, int width, int height)
    {
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean isOver(int xMouse, int yMouse)
    {
        if (xMouse < x || yMouse < y || xMouse > x + width || yMouse > y + height) return false;
        return true;
    }

    public void addComponent(UiComponent component)
    {
        components.add(component);
    }

    public void removeAllComponents()
    {
        components.clear();
    }

    public final void tickAll()
    {
        tick();
        for (int i = 0; i < components.size(); i++)
        {
            UiComponent component = components.get(i);
            component.tickAll();
        }
    }

    public void tick()
    {
    }

    public final void renderAll(Graphics2D g, float alpha)
    {
        render(g, alpha);
        for (int i = 0; i < components.size(); i++)
        {
            UiComponent component = components.get(i);
            component.renderAll(g, alpha);
        }
    }

    public void render(Graphics2D g, float alpha)
    {
    }

    public UiComponent getComponentAt(int xMouse, int yMouse)
    {
        if (!isOver(xMouse, yMouse)) return null;

        for (int i = 0; i < components.size(); i++)
        {
            UiComponent component = components.get(i);
            if (component.isOver(xMouse, yMouse)) return component.getComponentAt(xMouse, yMouse);
        }

        return this;
    }

    public void setMousePos(int xMouse, int yMouse)
    {
        this.xMouse = xMouse - x;
        this.yMouse = yMouse - y;
    }

    public void startDrag(int xMouse, int yMouse, int button)
    {
        xLastDrag = xMouse;
        yLastDrag = yMouse;
        setMousePos(xMouse, yMouse);
        startDrag(button);
    }

    public void stopDrag(int xMouse, int yMouse, int button)
    {
        setMousePos(xMouse, yMouse);
        stopDrag(button);
    }

    public void drag(int xMouse, int yMouse, int button, int xStart, int yStart)
    {
        setMousePos(xMouse, yMouse);
        drag(button, xStart - x, yStart - y);
        xLastDrag = xMouse;
        yLastDrag = yMouse;
    }

    public void mouseClicked(int xMouse, int yMouse, int button, int count)
    {
        setMousePos(xMouse, yMouse);
        mouseClicked(button, count);
    }

    public void mousePressed(int xMouse, int yMouse, int button)
    {
        if (button < 16) mouseDown[button] = true;
        setMousePos(xMouse, yMouse);
        mousePressed(button);
    }

    public void mouseReleased(int xMouse, int yMouse, int button)
    {
        if (button < 16) mouseDown[button] = false;
        setMousePos(xMouse, yMouse);
        mouseReleased(button);
    }

    public void mouseOver(int xMouse, int yMouse)
    {
        hovered = true;
        setMousePos(xMouse, yMouse);
        mouseOver = true;
        mouseOver();
    }

    public void mouseOut(int xMouse, int yMouse)
    {
        hovered = false;
        setMousePos(xMouse, yMouse);
        mouseOver = false;
        mouseOut();
    }

    public void mouseMoved(int xMouse, int yMouse)
    {
        setMousePos(xMouse, yMouse);
        mouseMoved();
    }

    public void startDrag(int button)
    {
    }

    public void stopDrag(int button)
    {
    }

    public void drag(int button, int xStart, int yStart)
    {
    }

    public void mouseClicked(int button, int count)
    {
    }

    public void mousePressed(int button)
    {
    }

    public void mouseReleased(int button)
    {
    }

    public void mouseOver()
    {
    }

    public void mouseOut()
    {
    }

    public void mouseMoved()
    {
    }

    public String getToolTip()
    {
        return null;
    }
}