package com.mojang.takns;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class CompoundSprite extends Sprite
{
    public ArrayList<Sprite> sprites = new ArrayList<Sprite>();

    public CompoundSprite()
    {
    }
    
    public CompoundSprite(CompoundSprite sprite)
    {
        this.x = sprite.x;
        this.y = sprite.y;
        this.w = sprite.w;
        this.h = sprite.h;
        
        this.xo = sprite.xo;
        this.yo = sprite.yo;
        this.zo = sprite.zo;
    }

    public void addTo(SortedSet<Sprite> visibleSprites)
    {
        for (int i=0; i<sprites.size(); i++)
        {
            sprites.get(i).addTo(visibleSprites);
        }
    }

    public void renderImageTo(Graphics2D g, int x, int y)
    {
        SortedSet<Sprite> sortedSprites = new TreeSet<Sprite>();
        sortedSprites.addAll(sprites);
        
        for (Sprite sprite : sortedSprites)
        {
            sprite.renderImageTo(g, x-this.x, y-this.y);
        }
    }
}