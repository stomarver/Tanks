package com.mojang.takns.terrain;

import java.util.Random;

import com.mojang.takns.Minimap;
import com.mojang.takns.NoiseMap;
import com.mojang.takns.units.Unit;

public class Map
{
    public int[] tiles;
    public Unit[] blockMap;
    private Random random;
    
    public int width = 64;
    public int height = 64;
    
    private Minimap minimap;

    public Map()
    {
        this(new Random());
    }

    public Map(Random random)
    {
        this.random = random;
        tiles = new int[64 * 64];
        blockMap = new Unit[64 * 64];

        addNoise(3, -10, Tiles.TYPE_WATER_TO_SAND);
              addNoiseOver(Tiles.TYPE_WATER, 3, -5, Tiles.TYPE_WATER_TO_SAND); // RIVERS!
        addNoiseOver(Tiles.TYPE_SAND, 3, 0, Tiles.TYPE_SAND_TO_GRASS);


        splatOver(Tiles.TYPE_SAND, Tiles.TYPE_GEMS, 8, 20, 32, 4);
        splatDecorators(Tiles.TYPE_GRASS, Tiles.TYPE_GRASS_DECORATORS, 8, 3000);
        splatDecorators(Tiles.TYPE_SAND, Tiles.TYPE_SAND_DECORATORS, 8, 300);
    }

    private void splatDecorators(int tile, int type, int typeCount, int count)
    {
        for (int i = 0; i < count; i++)
        {
            int x = random.nextInt(64);
            int y = random.nextInt(64);
            if (tiles[x + y * 64] == tile)
            {
                tiles[x + y * 64] = type + random.nextInt(typeCount);
            }
        }
    }

    private void splatOver(int tile, int type, int types, int count, int strands, int spread)
    {
        for (int i = 0; i < count; i++)
        {
            int xo = random.nextInt(64);
            int yo = random.nextInt(64);
            if (tiles[xo + yo * 64] == tile)
            {
                for (int s = 0; s < strands; s++)
                {
                    int x = xo;
                    int y = yo;
                    for (int k = 0; k < spread; k++)
                    {
                        x += random.nextInt(7) - 3;
                        y += random.nextInt(7) - 3;
                        if (x >= 0 && y >= 0 && x < 64 && y < 64 && (tiles[x + y * 64] == tile || (tiles[x + y * 64] >= type && tiles[x + y * 64] < type + types)))
                        {
                            tiles[x + y * 64] = type + random.nextInt(types);
                        }
                    }
                }
            }
        }
    }

    private void addNoise(int noise, int limit, int offset)
    {
        addNoise(new NoiseMap(random).getNoise(7, noise), limit, offset);
    }

    private void addNoise(int[] layer, int limit, int offset)
    {
        for (int y = 0; y < 64; y++)
        {
            for (int x = 0; x < 64; x++)
            {
                int tile = 0;
                for (int i = 0; i < 4; i++)
                {
                    if (layer[(x + (i & 1) + (y + (i >> 1)) * 128)] > limit)
                    {
                        tile += 1 << i;
                    }
                }

                if (tile > 0) tiles[x + y * 64] = tile + offset;
            }
        }
    }

    public void addNoiseOver(int tile, int noise, int limit, int offset)
    {
        int[] layer = new NoiseMap(random).getNoise(7, 3);
        for (int y = 0; y < 64; y++)
        {
            for (int x = 0; x < 64; x++)
            {
                if (tiles[x + y * 64] != tile)
                {
                    layer[x + y * 128] = -200;
                    layer[x + y * 128 + 1] = -200;
                    layer[x + y * 128 + 128] = -200;
                    layer[x + y * 128 + 129] = -200;
                }
            }
        }

        addNoise(layer, limit, offset);
    }

    public Terrain getTerrainTypeAtPixel(float x, float y)
    {
        return getTerrainTypeAt((int) (x / 16), (int) (y / 16));
    }
    
    public Terrain getTerrainTypeAt(int xTile, int yTile)
    {
        if (xTile < 0) xTile = 0;
        if (yTile < 0) yTile = 0;
        if (xTile >= width) xTile = width - 1;
        if (yTile >= height) yTile = height - 1;
        return Tiles.tiles[tiles[xTile + yTile * width]].terrain;
    }    

    public void block(int x, int y, Unit unit)
    {
        blockMap[x + y * width] = unit;
    }

    public void unblock(int x, int y)
    {
        blockMap[x + y * width] = null;
    }

    public Unit getUnitAt(int x, int y)
    {
        if (x < 0 || y < 0 || x >= width || y >= height)
        {
            return null;
        }
        return blockMap[x + y * width];
    }

    public int getTile(int xTile, int yTile)
    {
        return tiles[xTile + yTile * width];
    }

    public void setTile(int xTile, int yTile, int tile)
    {
        tiles[xTile + yTile * width] = tile;
        if (minimap!=null)
            minimap.updateArea(xTile, yTile, 1, 1);
    }

    public void setMinimap(Minimap minimap)
    {
        this.minimap = minimap;
    }
}