package com.mojang.takns.units.monsters;

import java.awt.image.BufferedImage;

import com.mojang.takns.ImageConverter;

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
    private BufferedImage[][] healthImages;
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
        this.healthImages = createHealthImages(this.baseImages);
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
                baseSprite.image = healthImages[getHealthBand()][0];
            }
            else
            {
                float progress = jumpTime / (float) jumpDuration;
                x = xJumpSource + (xJumpTarget * 16 + 8 - xJumpSource) * progress;
                y = yJumpSource + (yJumpTarget * 16 + 8 - yJumpSource) * progress;
                z = (float) (Math.sin(progress * Math.PI) * jumpDuration);

                baseSprite.image = healthImages[getHealthBand()][1];
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

    

    private int getHealthBand()
    {
        float health = 1.0f - (damage / (float) maxDamage);
        if (health <= 0.0f) return 4;
        if (health <= 0.50f) return 3;
        if (health <= 0.66f) return 2;
        if (health <= 0.75f) return 1;
        return 0;
    }

    private BufferedImage[][] createHealthImages(BufferedImage[] source)
    {
        BufferedImage[][] images = new BufferedImage[5][source.length];
        float[][] palette = {
                {1.00f, 1.00f, 1.00f}, // 100% green as original
                {0.85f, 0.85f, 0.25f}, // 75% yellow + darker
                {0.85f, 0.50f, 0.18f}, // 66% orange + darker
                {0.65f, 0.18f, 0.18f}, // 50% red + darker
                {0.14f, 0.04f, 0.04f}  // 0% almost black with red tint
        };

        for (int band = 0; band < images.length; band++)
        {
            for (int i = 0; i < source.length; i++)
            {
                images[band][i] = tint(source[i], palette[band][0], palette[band][1], palette[band][2]);
            }
        }

        return images;
    }

    private BufferedImage tint(BufferedImage src, float rMul, float gMul, float bMul)
    {
        int w = src.getWidth();
        int h = src.getHeight();
        int[] pixels = new int[w * h];
        src.getRGB(0, 0, w, h, pixels, 0, w);

        for (int i = 0; i < pixels.length; i++)
        {
            int c = pixels[i];
            int a = (c >>> 24) & 0xff;
            if (a == 0) continue;

            int r = (c >>> 16) & 0xff;
            int g = (c >>> 8) & 0xff;
            int b = c & 0xff;

            r = (int) (r * rMul);
            g = (int) (g * gMul);
            b = (int) (b * bMul);

            if (r > 255) r = 255;
            if (g > 255) g = 255;
            if (b > 255) b = 255;

            pixels[i] = (a << 24) | (r << 16) | (g << 8) | b;
        }

        return ImageConverter.convert(w, h, pixels, 2);
    }

    public String getName()
    {
        return "Slime";
    }
}