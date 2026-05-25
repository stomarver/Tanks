package com.mojang.takns.units.vehicles;

import com.mojang.takns.sprites.*;

public class Tank extends TurretVehicle
{
    public Tank(int xTile, int yTile)
    {
        super(Voxels.tankBase, Voxels.baseShadow, Voxels.turret, Voxels.turretShadow);
        
        x = xTile*16;
        y = yTile*16;
        
        vehicleSpeed = 0.5f;
    }
    
    public String getName()
    {
        return "Tank";
    }
}