package com.mojang.takns.units.monsters;

import java.awt.image.BufferedImage;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.World;
import com.mojang.takns.sprites.*;
import com.mojang.takns.terrain.Terrain;
import com.mojang.takns.units.*;

public class Slime extends MoveableUnit
{
    protected BufferedImage baseImages[];
    protected BufferedImage shadowImage;
    private Sprite baseSprite;
    private Sprite baseShadow;

    private float xJumpSource, yJumpSource;
    private int xJumpTarget, yJumpTarget;

    private boolean jumping = false;
    private int jumpTime = 0;
    private int jumpDuration = 0;

    public Slime(int xTile, int yTile)
    {
        this.x = xTile * 16;
        this.y = yTile * 16;
        
        this.xTile = xTile;
        this.yTile = yTile;
        
        xJumpTarget = xTile;
        yJumpTarget = yTile;

        revealRadius = 6;
        manualBlock = true;

        this.baseImages = MonsterSprites.blob;
        this.shadowImage = MonsterSprites.blobShadow;
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        int w = baseImages[0].getWidth();
        int h = baseImages[0].getHeight();

        baseSprite = new Sprite();
        baseSprite.image = baseImages[0];
        baseSprite.xo = -w / 2;
        baseSprite.yo = -w / 2 - (h - w) + 2;
        baseSprite.zo = 6;

        baseShadow = new Sprite();
        baseShadow.layer = Sprite.LAYER_SHADOW;
        baseShadow.image = shadowImage;
        baseShadow.xo = -w / 2 + 1;
        baseShadow.yo = -w / 2 - (h - w) + 2 + 1;

        sprite.sprites.add(baseSprite);
        sprite.sprites.add(baseShadow);

        x = xOld = (int) (x / 16) * 16 + 8;
        y = yOld = (int) (y / 16) * 16 + 8;
    }

    public void init(World world, Side side)
    {
        super.init(world, side);
        if (world.map.getUnitAt(xTile, yTile) != null)
        {
            System.out.println("Jesus christ, batman!!");
        }
        world.map.block(xTile, yTile, this);
    }
    
    public void remove(Units units)
    {
        super.remove(units);
        world.map.unblock(xJumpTarget, yJumpTarget);
    }
    

    public void tick()
    {
        super.tick();
        if (world.map.getUnitAt(xJumpTarget, yJumpTarget) != this)
        {
            System.out.println("HOOLY CRAP!");
        }

        if (!jumping)
        {
            if (random.nextInt(10) == 0) findRandomTarget();
        }
        else
        {
            jumpTime++;
            if (jumpTime == jumpDuration)
            {
                x = xJumpTarget * 16 + 8;
                y = yJumpTarget * 16 + 8;
                z = 0;
                jumpTime = 0;
                jumping = false;
                baseSprite.image = baseImages[0];
            }
            else
            {
                float progress = jumpTime / (float) jumpDuration;
                x = xJumpSource + (xJumpTarget * 16 + 8 - xJumpSource) * progress;
                y = yJumpSource + (yJumpTarget * 16 + 8 - yJumpSource) * progress;
                z = (float) (Math.sin(progress * Math.PI) * jumpDuration);

                baseSprite.image = baseImages[1];
            }
        }
    }

    private void findRandomTarget()
    {
        int x = xTile + random.nextInt(5) - 2;
        int y = yTile + random.nextInt(5) - 2;
        if (x >= 0 && y >= 0 && x < 64 && y < 64)
        {
            if (world.map.getUnitAt(x, y) != null) return;

            if ((world.map.getTerrainTypeAt(x, y).passableFlags & Terrain.PASSABLE_LAND) == 0) return;


            world.map.unblock(xTile, yTile);
            world.map.block(x, y, this);

            xJumpSource = this.x;
            yJumpSource = this.y;
            xJumpTarget = x;
            yJumpTarget = y;
            jumping = true;
            jumpTime = 0;

            int xd = xJumpTarget - xTile;
            int yd = yJumpTarget - yTile;

            jumpDuration = (int) (Math.sqrt(xd * xd + yd * yd) * 6);
        }
    }

    public void render(float alpha)
    {
        super.render(alpha);

        int xx = (int) (xo + (x - xo) * alpha);
        int yy = (int) (yo + (y - yo) * alpha);
        int zz = (int) (zo + (z - zo) * alpha);

        sprite.x = xx;
        sprite.y = yy;
        sprite.z = zz;

        baseSprite.x = xx;
        baseSprite.y = yy;
        baseSprite.z = zz;

        baseShadow.x = xx + zz / 2;
        baseShadow.y = yy + zz;
        baseShadow.image = shadowImage;
    }

    public String getName()
    {
        return "Slime";
    }
}