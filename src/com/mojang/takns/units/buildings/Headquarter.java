package com.mojang.takns.units.buildings;

import com.mojang.takns.gui.buttons.*;
import com.mojang.takns.sprites.Voxels;

public class Headquarter extends Building
{
    private static ButtonType[] buildButtons = 
    {
        BuildButtonType.SLAB,
        BuildButtonType.WALL,
        BuildButtonType.WALL_TURRET,
        BuildButtonType.HQ,
    };
    
    public Headquarter()
    {
        super(Voxels.hqBase, Voxels.hqShadow);
        width = 2;
        height = 2;
        
        cost = 500;
    }
    
    public String getName()
    {
        return "HQ";
    }
    
    public boolean isSilo()
    {
        return true;
    }

    public ButtonType[] getButtons()
    {
        return buildButtons;
    }
}