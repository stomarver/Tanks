package com.mojang.takns.units.buildings;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.World;
import com.mojang.takns.units.Unit;
import com.mojang.takns.units.Units;

public abstract class Building extends Unit
{
    public int width = 1;
    public int height = 1;

    Sprite baseSprite = new Sprite();
    Sprite baseShadow = new Sprite();

    Image[] baseImage;
    Image image;
    Image shadowImage;

    public Building(Image[] baseImage, Image shadowImage)
    {
        this.baseImage = baseImage;
        this.shadowImage = shadowImage;
        revealRadius = 8;
    }

    public Building(Image image, Image shadowImage)
    {
        this.image = image;
        this.shadowImage = shadowImage;
        revealRadius = 8;
    }

    public void buildAt(int xTile, int yTile)
    {
        xo = x = xTile * 16 + 8;
        yo = y = yTile * 16 + 8;
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        if (baseImage != null) image = baseImage[side.id];

        xTile = (int) (x / 16);
        yTile = (int) (y / 16);
        
        baseSprite.xo = -16;
        baseSprite.yo = -16-height*8;
        baseSprite.x = (int) x;
        baseSprite.zo = 8+(height-1)*16;
        baseSprite.y = (int) y-16+height*8;
        baseSprite.image = image;
        baseSprite.w = width * 16;
        baseSprite.h = height * 16;

        baseShadow.x = (int) x;
        baseShadow.y = (int) y;
        baseShadow.xo = -16;
        baseShadow.yo = -16;
        baseShadow.layer = Sprite.LAYER_SHADOW;
        baseShadow.image = shadowImage;
        baseShadow.w = width * 16;
        baseShadow.h = height * 16;

        if (width == 1)
        {
            baseSprite.xo += 4;
            baseShadow.xo += 4;
        }
        if (height == 1)
        {
            baseSprite.yo += 4;
            baseShadow.yo += 4;
        }

        sprite.sprites.add(baseSprite);
        sprite.sprites.add(baseShadow);
        sprite.x = (int) x;
        sprite.y = (int) y;
        sprite.w = width * 16;
        sprite.h = height * 16;
    }

    public void init(World world, Side side)
    {
        super.init(world, side);

        for (int x = xTile; x < xTile + width; x++)
            for (int y = yTile; y < yTile + height; y++)
            {
                side.fogOfWar.revealStatic(x, y, revealRadius);
                world.map.block(x, y, this);
            }
    }

    public void updateWalls(int x, int y)
    {
        Unit u = world.map.getUnitAt(x, y);
        if (u == null) return;
        if (u instanceof Wall)
        {
            ((Wall) u).updateImages();
        }
    }

    public void setImages(BufferedImage image, BufferedImage shadowImage)
    {
        this.image = image;
        this.shadowImage = shadowImage;

        baseSprite.image = image;
        baseShadow.image = shadowImage;
    }

    public void remove(Units units)
    {
        super.remove(units);

        world.mapRenderer.sprites.remove(sprite);

        for (int x = xTile; x < xTile + width; x++)
            for (int y = yTile; y < yTile + height; y++)
            {
                side.fogOfWar.unRevealStatic(x, y, revealRadius);
                world.map.unblock(x, y);
            }
    }

    public boolean isInside(int x0, int y0, int x1, int y1)
    {
        x0 -= 8 + (width - 1) * 16;
        y0 -= 8 + (height - 1) * 16;
        x1 += 8;
        y1 += 8;
        return x >= x0 && y >= y0 && x <= x1 && y <= y1;
    }

    public void tick()
    {
        if (selected) selectTime--;

        xo = x;
        yo = y;
        zo = z;
    }

    public void addToMinimap(int[] minimapPixels)
    {
        int x = (int) (xo / 16);
        int y = (int) (yo / 16);
        for (int xx = x; xx < x + width; xx++)
            for (int yy = y; yy < y + height; yy++)
            {
                minimapPixels[xx + yy * 64] = side.minimapColor;
            }
    }

    public void postRender(Graphics2D g, float alpha)
    {
        if (selected)
        {
            float t = (selectTime - alpha);
            float a = 1 - t * 0.2f;
            if (a < 0) a = 0;
            if (a > 2) a = 2;
            if (a > 1) a = 1 - (a - 1) * 0.5f;

            if (t < 0) t = 0;
            g.setColor(new Color(1, 1, 1, a));
            int s = (int) (t * 4);

            int r = 12 + s;
            g.drawRect((int) xo - r - world.xCam, (int) yo - r - world.yCam, r * 2 - 1 + (width - 1) * 16, r * 2 - 1 + (height - 1) * 16);
        }
    }

    public boolean isBuilding()
    {
        return true;
    }

    public void renderImageTo(Graphics2D g, int x, int y)
    {
        sprite.renderImageTo(g, x - (width - 1) * 8, y - (height - 1) * 8);
    }

    public boolean isSilo()
    {
        return false;
    }

    public Building newInstance()
    {
        try
        {
            // TODO: Make this waaay more elegant!
            return this.getClass().newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IllegalStateException("Argh, Class.newInstance is not supported!");
        }
    }
}