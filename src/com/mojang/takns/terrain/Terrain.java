package com.mojang.takns.terrain;

public class Terrain
{
    public static final int PASSABLE_AIR = 0x0001;
    public static final int PASSABLE_LAND = 0x0002;
    public static final int PASSABLE_WATER = 0x0004;

    public static int terrainCount = 0;
    public static Terrain[] terrains = new Terrain[256];

    public static final Terrain TERRAIN_WATER = new Terrain("Water", 0xff7785da, PASSABLE_AIR + PASSABLE_WATER, 10);
    public static final Terrain TERRAIN_SHORE = new Terrain("Shore", 0xff5765ba, PASSABLE_AIR + PASSABLE_WATER, 12);
    public static final Terrain TERRAIN_SAND = new Terrain("Sand", 0xffc6b664, PASSABLE_AIR + PASSABLE_LAND, 15);
    public static final Terrain TERRAIN_GRASS = new Terrain("Grass", 0xff6ba863, PASSABLE_AIR + PASSABLE_LAND, 10);
    public static final Terrain TERRAIN_SLAB = new Terrain("Slab", 0xffb0b0b0, PASSABLE_AIR + PASSABLE_LAND, 8);
    public static final Terrain TERRAIN_GEM = new Terrain("Gems", 0xffdd5dec, PASSABLE_AIR + PASSABLE_LAND, 12);
    public static final Terrain TERRAIN_NONE = null;

    public String name;
    public int color;
    public int passableFlags;
    public int id;
    public int travelCost;

    public Terrain(String name, int color, int passableFlags, int travelCost)
    {
        this.id = terrainCount++;
        terrains[id] = this;
        this.name = name;
        this.color = color;
        this.passableFlags = passableFlags;
        this.travelCost = travelCost;
    }
}