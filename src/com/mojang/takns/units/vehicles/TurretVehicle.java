package com.mojang.takns.units.vehicles;

import java.awt.image.BufferedImage;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.particles.Missile;
import com.mojang.takns.units.Unit;

public abstract class TurretVehicle extends Vehicle
{
    Sprite turret;
    Sprite turretShadow;

    float tdira;
    float tdir;
    int turretAngle;

    protected BufferedImage[] turretImages;
    protected BufferedImage[] turretShadowImages;

    public TurretVehicle(BufferedImage[][] baseImages, BufferedImage[] shadowImages, BufferedImage[] turretImages, BufferedImage[] turretShadowImages)
    {
        super(baseImages, shadowImages);
        this.turretImages = turretImages;
        this.turretShadowImages = turretShadowImages;
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        int w = baseImages[0][0].getWidth();
        int h = baseImages[0][0].getHeight();

        turret = new Sprite();
        turret.image = turretImages[0];
        turret.xo = -w / 2;
        turret.yo = -w / 2 - (h - w) + 2;
        turret.zo = 10;

        turretShadow = new Sprite();
        turretShadow.layer = Sprite.LAYER_SHADOW;
        turretShadow.image = turretShadowImages[0];
        turretShadow.xo = -w / 2;
        turretShadow.yo = -w / 2;

        sprite.sprites.add(turret);
        sprite.sprites.add(turretShadow);

        tdir = (float) (Math.random() * 32);
        tdira = 0;
    }

    protected void aimAt(int xt, int yt)
    {
        aimingAtEnemy = false;
        turnTurretTowards(xt, yt);
    }
    
    private void turnTurretTowards(int xt, int yt)
    {
        float tDir = ((int) (Math.atan2(yt - y, xt - x) * 16 / (Math.PI) + 8f));
        while (tDir < 0)
            tDir += 32;
        while (tDir > 31)
            tDir -= 32;

        float dDir = tDir - tdir;
        while (dDir >= 16)
            dDir -= 32;
        while (dDir < -16)
            dDir += 32;

        dDir = dDir * 0.2f;
        if (dDir * dDir < 0.1)
        {
            tdir = tDir;
            dDir = 0;
            aimingAtEnemy = true;
        }

        if (dDir < -1)
        {
            dDir = -1;
        }
        if (dDir > 1)
        {
            dDir = 1;
        }
        tdir += dDir;
        while (tdir < 0)
            tdir += 32;
        while (tdir > 31)
            tdir -= 32;
    }    

    public void tick()
    {
        super.tick();

        tdir += tdira;
        if (Math.random() < 0.01)
        {
            tdira = (float) (Math.random() * 2 - 1) * 0.2f;
        }
        while (tdir < 0)
            tdir += 32;
        while (tdir > 31)
            tdir -= 32;

        turretAngle = (int) (tdir / 2 + 0.5f) & 15;
        
        if (aimingAtEnemy && reloadTime==0 && targetUnit!=null)
        {
            reloadTime=40;
            shootAt(targetUnit);
        }
    }

    public void shootAt(Unit target)
    {
        int xta = (int) (SIN[baseAngle] * 2.9f) + (int) (SIN[turretAngle] * -17.9f);
        int yta = (int) (-COS[baseAngle] * 2.9f) - (int) (COS[turretAngle] * -17.9f);
        world.particleSystem.addParticle(Missile.createMissile(x - xta, y - yta, 6, target.x, target.y));
    }

    public void render(float alpha)
    {
        super.render(alpha);

        int xta = (int) (SIN[baseAngle] * 2.9f) + (int) (SIN[turretAngle] * -4.9f);
        int yta = (int) (-COS[baseAngle] * 2.9f) - (int) (COS[turretAngle] * -4.9f);

        int xx = (int) (xo + (x - xo) * alpha);
        int yy = (int) (yo + (y - yo) * alpha);
        int zz = (int) (zo + (z - zo) * alpha);

        turret.x = xx - xta;
        turret.y = yy - yta;
        turret.z = zz + 4;
        turret.image = turretImages[turretAngle];

        turretShadow.x = xx + (zz + 4) / 2 - xta;
        turretShadow.y = yy + (zz + 4) - yta;
        turretShadow.image = turretShadowImages[turretAngle];
    }
}