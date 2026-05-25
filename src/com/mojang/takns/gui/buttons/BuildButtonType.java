package com.mojang.takns.gui.buttons;

import java.awt.image.BufferedImage;

import com.mojang.takns.World;
import com.mojang.takns.gui.states.*;
import com.mojang.takns.units.buildings.*;

public class BuildButtonType extends ButtonType
{
    public static final BuildButtonType SLAB = new BuildButtonType(new SlabState(), "Slabs are required for other buildings");
    public static final BuildButtonType WALL = new BuildButtonType(new BuildingState(new Wall(), true), "Walls provide protection");
    public static final BuildButtonType WALL_TURRET = new BuildButtonType(new BuildingState(new TurretWall()), "Turrets shoot at enemies");
    public static final BuildButtonType HQ = new BuildButtonType(new BuildingState(new Headquarter()), "Headquarters produce other buildings");

    private State state;
    private String toolTip;
    private int cost;
    private BufferedImage image;

    public BuildButtonType(State state, String toolTip)
    {
        this.state = state;
        this.image = state.getImage();
        if (image==null) throw new IllegalArgumentException("!!!");
        this.cost = state.getCost();
        this.toolTip = toolTip;
    }

    public void activate(World world)
    {
        world.gameView.setState(state);
    }

    public boolean isActive(World world)
    {
        return world.gameView.getState() == state;
    }

    public String getToolTip()
    {
        return toolTip;
    }

    public int getCost()
    {
        return cost;
    }

    public BufferedImage getImage()
    {
        return image;
    }
}