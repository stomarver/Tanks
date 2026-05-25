package com.mojang.takns;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.mojang.takns.gui.UiComponent;

public class InputHandler implements MouseMotionListener, MouseListener, KeyListener
{
    private UiComponent component;
    private int scale;
    private UiComponent dragSource;
    private int dragButton = -1;
    private int xDragStart;
    private int yDragStart;
    public int xMouse = -100, yMouse = -100;
    private UiComponent lastHovered = null;

    public Object lock = new Object();
    public boolean[] keys = new boolean[256];

    public InputHandler(UiComponent component, int scale)
    {
        this.component = component;
        this.scale = scale;
    }

    public void mouseDragged(MouseEvent e)
    {
        synchronized (lock)
        {
            mouseMoved(e);
        }
    }

    public void mouseMoved(MouseEvent e)
    {
        synchronized (lock)
        {
            xMouse = e.getX() / scale;
            yMouse = e.getY() / scale;
            UiComponent hovered = component.getComponentAt(xMouse, yMouse);

            if (dragSource != null)
            {
                hovered = dragSource;
                int xd = xDragStart - xMouse;
                int yd = yDragStart - yMouse;
                if (xd * xd + yd * yd > 4)
                {
                    dragSource.drag(xMouse, yMouse, dragButton, xDragStart, yDragStart);
                }
            }
            else
            {
                if (hovered != lastHovered)
                {
                    if (lastHovered != null) lastHovered.mouseOut(xMouse, yMouse);
                    if (hovered != null) hovered.mouseOver(xMouse, yMouse);

                    lastHovered = hovered;
                }

                if (lastHovered != null)
                {
                    lastHovered.mouseMoved(xMouse, yMouse);
                }
            }
        }
    }

    public void mouseClicked(MouseEvent e)
    {
        synchronized (lock)
        {
            mouseMoved(e);

            xMouse = e.getX() / scale;
            yMouse = e.getY() / scale;

            if (lastHovered != null) lastHovered.mouseClicked(xMouse, yMouse, e.getButton(), e.getClickCount());
        }
    }

    public void mouseEntered(MouseEvent e)
    {
        synchronized (lock)
        {
            mouseMoved(e);
        }
    }

    public void mouseExited(MouseEvent e)
    {
        synchronized (lock)
        {
            mouseMoved(e);
            xMouse = -999;
        }
    }

    public void mousePressed(MouseEvent e)
    {
        synchronized (lock)
        {
            xMouse = e.getX() / scale;
            yMouse = e.getY() / scale;
            int button = e.getButton();

            if (button != dragButton)
            {
                stopDragging(xMouse, yMouse, dragButton);
            }
            mouseMoved(e);
            startDragging(xMouse, yMouse, button);

            if (lastHovered != null) lastHovered.mousePressed(xMouse, yMouse, button);
        }
    }

    private void startDragging(int xMouse, int yMouse, int button)
    {
        synchronized (lock)
        {
            xDragStart = xMouse;
            yDragStart = yMouse;
            dragSource = component.getComponentAt(xMouse, yMouse);
            if (dragSource != null)
            {
                dragSource.startDrag(xMouse, yMouse, button);
                dragButton = button;
            }
        }
    }

    public void stopDragging(int xMouse, int yMouse, int button)
    {
        synchronized (lock)
        {
            if (dragSource != null)
            {
                dragSource.stopDrag(xMouse, yMouse, button);
                dragSource = null;
                dragButton = -1;
            }
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        synchronized (lock)
        {
            mouseMoved(e);

            xMouse = e.getX() / scale;
            yMouse = e.getY() / scale;
            int button = e.getButton();

            stopDragging(xMouse, yMouse, button);

            if (lastHovered != null) lastHovered.mouseReleased(xMouse, yMouse, button);
        }
    }

    public void keyPressed(KeyEvent e)
    {
        synchronized (lock)
        {
            if (e.getKeyCode()<256)
                keys[e.getKeyCode()] = true;
        }
    }

    public void keyReleased(KeyEvent e)
    {
        synchronized (lock)
        {
            if (e.getKeyCode()<256)
                keys[e.getKeyCode()] = false;
        }
    }

    public void keyTyped(KeyEvent e)
    {
        synchronized (lock)
        {
        }
    }
}