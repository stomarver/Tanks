package com.mojang.takns.units.vehicles;

import java.awt.image.BufferedImage;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.units.*;

public abstract class Vehicle extends MoveableUnit
{
    Sprite baseSprite;
    Sprite baseShadow;

    protected Unit targetUnit;

    float za;
    float dira;
    public float dir;
    public float speed = 0;

    public float vehicleSpeed = 0;

    int baseAngle;
    protected BufferedImage[][] baseImages;
    protected BufferedImage[] shadowImages;
    
    protected boolean aimingAtEnemy = false;
    protected int reloadTime = 0;
    
    public Vehicle(BufferedImage[][] baseImages, BufferedImage[] shadowImages)
    {
        this.baseImages = baseImages;
        this.shadowImages = shadowImages;
        revealRadius = 5;
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        int w = baseImages[0][0].getWidth();
        int h = baseImages[0][0].getHeight();

        baseSprite = new Sprite();
        baseSprite.image = baseImages[side.id][0];
        baseSprite.xo = -w / 2;
        baseSprite.yo = -w / 2 - (h - w) + 2;
        baseSprite.zo = 6;

        baseShadow = new Sprite();
        baseShadow.layer = Sprite.LAYER_SHADOW;
        baseShadow.image = shadowImages[0];
        baseShadow.xo = -w / 2 + 1;
        baseShadow.yo = -w / 2 + 1;

        sprite.sprites.add(baseSprite);
        sprite.sprites.add(baseShadow);

        dir = (float) (Math.random() * 32);
        dira = 0;
        speed = 0;

        x = xOld = (int) (x / 16) * 16 + 8;
        y = yOld = (int) (y / 16) * 16 + 8;
    }

    protected void pathFound()
    {
        speed = 0;
    }

    private void move()
    {
        float tileSpeed = world.map.getTerrainTypeAtPixel(x, y).travelCost / 10.0f;

        if (xNextTile != xOld && yNextTile != yOld)
        {
            tileSpeed *= 1.41;
        }

        p += speed / tileSpeed;
        while (p >= 1)
        {
            xOld = xNextTile;
            yOld = yNextTile;

            if (pathFinder.pathP > 0 && !pathFinder.isPathing)
            {
                int p = pathFinder.path[pathFinder.pathP - 1];
                int xx = (p & 63) * 16 + 8;
                int yy = (p >> 6) * 16 + 8;

                if (canTravelTo(xx / 16, yy / 16))
                {
                    xNextTile = xx;
                    yNextTile = yy;
                    world.map.block(xx / 16, yy / 16, this);
                    pathFinder.pathP--;
                    this.p--;
                }
                else
                {
                    this.p = 0;
                    speed = 0;
                    moveTo(xTarget, yTarget);
                }
            }
            else
            {
                p = 0;
                speed = 0;
                x = xNextTile;
                y = yNextTile;
                moving = false;
                return;
            }
        }
    }

    private void turn()
    {
        if (xNextTile != xOld || yNextTile != yOld)
        {
            turnTowards(xNextTile, yNextTile);
        }
    }

    protected void aimAt(int xt, int yt)
    {
        if (moving) return;
        turnTowards(xt, yt);
    }
    
    private void turnTowards(int xt, int yt)
    {
        float tDir = ((int) (Math.atan2(yt - yOld, xt - xOld) * 16 / (Math.PI) + 8f));
        while (tDir < 0)
            tDir += 32;
        while (tDir > 31)
            tDir -= 32;

        float dDir = tDir - dir;
        while (dDir >= 16)
            dDir -= 32;
        while (dDir < -16)
            dDir += 32;

        dDir = dDir * 0.2f;
        if (dDir * dDir < 0.1)
        {
            dir = tDir;
            dDir = 0;
            aimingAtEnemy = true;
        }
        if (dDir != 0)
        {
            speed *= 0.98f;
        }
        if (dDir < -1)
        {
            speed *= 0.25f;
            dDir = -1;
        }
        if (dDir > 1)
        {
            speed *= 0.25f;
            dDir = 1;
        }
        dir += dDir;
        while (dir < 0)
            dir += 32;
        while (dir > 31)
            dir -= 32;
    }

    public void tick()
    {
        super.tick();
        
        if (reloadTime>0) reloadTime--;

        if (targetUnit != null && !side.units.seenEnemies.contains(targetUnit))
        {
            targetUnit = null;
        }

        if ((targetUnit == null && random.nextInt(10) == 0) || random.nextInt(40) == 0)
        {
            targetUnit = side.units.closestSeenEnemy(x, y, revealRadius);
        }

        updatePathfinder();

        if (moving)
        {
            move();
            turn();

            speed *= 0.92f;
            speed += vehicleSpeed / 100.0f;

            x = xOld + (xNextTile - xOld) * p;
            y = yOld + (yNextTile - yOld) * p;
        }

        aimingAtEnemy = false;
        if (targetUnit!=null)
            aimAt((int)(targetUnit.x), (int)(targetUnit.y));

        baseAngle = (int) (dir / 2 + 0.5f) & 15;

        z += za;

        if (z < 0)
        {
            z = 0;
            za = -za * 0.4f;
        }

        za -= 0.8f;
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
        baseSprite.image = baseImages[side.id][baseAngle];

        baseShadow.x = xx + zz / 2;
        baseShadow.y = yy + zz;
        baseShadow.image = shadowImages[baseAngle];
    }
}