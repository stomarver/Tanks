package com.mojang.takns.units.vehicles;

import com.mojang.takns.sprites.*;

public class Car extends Vehicle
{
    public Car(int xTile, int yTile)
    {
        super(Voxels.carBase, Voxels.carShadow);
        
        this.x = xTile*16;
        this.y = yTile*16;
        
        vehicleSpeed = 1.0f;
        revealRadius = 6;
    }

    public String getName()
    {
        return "Car";
    }
}