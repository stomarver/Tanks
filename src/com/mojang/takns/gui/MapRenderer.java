package com.mojang.takns.gui;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.util.*;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.World;
import com.mojang.takns.terrain.Tiles;

public class MapRenderer
{
    private int width;
    private int height;
    private World world;
    private Side side;
    private int xCam, yCam;

    public List<Sprite> sprites = new ArrayList<Sprite>();
    private SortedSet<Sprite> visibleSprites = new TreeSet<Sprite>();

    public MapRenderer(World world, Side side, int width, int height)
    {
        this.width = width;
        this.height = height;

        this.world = world;
        this.side = side;
    }

    public void render(Graphics2D g)
    {
        xCam = world.xCam;
        yCam = world.yCam;
        
        renderMap(g);
        renderSprites(g);
        renderReveal(g);
        renderHide(g);
    }
    
    private void renderSprites(Graphics2D g)
    {
        visibleSprites.clear();

        for (int i = 0; i < sprites.size(); i++)
        {
            Sprite sprite = sprites.get(i);
            if (sprite.x + sprite.xo < xCam + width+16 && sprite.y + sprite.yo - sprite.z < yCam + height+16)
            {
                if (sprite.x + sprite.xo > xCam - sprite.w-16 && sprite.y + sprite.yo - sprite.z > yCam - sprite.h-16)
                {
                    if (side.fogOfWar.isVisible(sprite))
                    {
                        sprite.addTo(visibleSprites);
                    }
                }
            }
        }

        float lastAlpha = 1;
        for (Sprite sprite : visibleSprites)
        {
            if (sprite.alpha!=lastAlpha)
            {
                if (sprite.alpha<1)
                    g.setComposite(AlphaComposite.SrcOver.derive(sprite.alpha));
                else
                    g.setComposite(AlphaComposite.SrcOver);
                lastAlpha = sprite.alpha;
            }
            g.drawImage(sprite.image, sprite.x - xCam + sprite.xo, sprite.y - sprite.z - yCam + sprite.yo, null);
        }
        g.setComposite(AlphaComposite.SrcOver);
    }

    private void renderMap(Graphics2D g)
    {
        for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++)
        {
            for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++)
            {
                if (side.fogOfWar.revealMap[x+y*64]!=0)
                {
                    g.drawImage(Tiles.tiles[world.map.tiles[x + y * 64]].image, x * 16 - xCam, y * 16 - yCam, null);
                }
            }
        }
    }

    private void renderReveal(Graphics2D g)
    {
        for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++)
        {
            for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++)
            {
                if (side.fogOfWar.lightMap[x+y*64]!=15 && side.fogOfWar.revealMap[x+y*64]!=0)
                {
                    g.drawImage(Tiles.tiles[256-32+side.fogOfWar.lightMap[x+y*64]].image, x * 16 - xCam, y * 16 - yCam, null);
                }
            }
        }
    }
    
    private void renderHide(Graphics2D g)
    {
        for (int y = (yCam) / 16; y <= (yCam + height) / 16; y++)
        {
            for (int x = (xCam) / 16; x <= (xCam + width) / 16; x++)
            {
                if (side.fogOfWar.revealMap[x+y*64]!=15)
                {
                    g.drawImage(Tiles.tiles[256-16+side.fogOfWar.revealMap[x+y*64]].image, x * 16 - xCam, y * 16 - yCam, null);
                }
            }
        }
    }    
}