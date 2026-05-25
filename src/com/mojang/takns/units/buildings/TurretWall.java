package com.mojang.takns.units.buildings;

import java.awt.image.BufferedImage;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.World;
import com.mojang.takns.particles.Missile;
import com.mojang.takns.sprites.Voxels;
import com.mojang.takns.units.Unit;

public class TurretWall extends Building
{
    protected BufferedImage[] turretImages;
    protected BufferedImage[] turretShadowImages;
    
    Sprite turret, turretShadow;

    float tdira;
    float tdir;
    int turretAngle;
    int reloadTime;
    Unit targetUnit;
    boolean aimingAtEnemy;
    
    public TurretWall()
    {
        super(Voxels.wallTurretBase, Voxels.wallTurretShadow);
        
        width = 1;
        height = 1;

        cost = 200;
        revealRadius = 4;
        
        turretImages = Voxels.turret;
        turretShadowImages = Voxels.turretShadow;
    }

    public void setSide(Side side)
    {
        super.setSide(side);

        int w = turretImages[0].getWidth();
        int h = turretImages[0].getHeight();

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
        
        render(0);
    }

    public void init(World world, Side side)
    {
        super.init(world, side);

        updateWalls(xTile - 1, yTile);
        updateWalls(xTile + 1, yTile);
        updateWalls(xTile, yTile - 1);
        updateWalls(xTile, yTile + 1);    
        
        render(0);
    }
    
    protected void aimAt(int xt, int yt)
    {
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

        if (reloadTime>0) reloadTime--;

        if (targetUnit != null && !side.units.seenEnemies.contains(targetUnit))
        {
            targetUnit = null;
        }

        if ((targetUnit == null && random.nextInt(10) == 0) || random.nextInt(40) == 0)
        {
            targetUnit = side.units.closestSeenEnemy(x, y, revealRadius);
        }
        aimingAtEnemy = false;
        if (targetUnit!=null)
            aimAt((int)(targetUnit.x), (int)(targetUnit.y));

        tdir += tdira;
        if (!aimingAtEnemy && Math.random() < 0.01)
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
        int xta = (int) (0) + (int) (SIN[turretAngle] * -17.9f);
        int yta = (int) (0) - (int) (COS[turretAngle] * -17.9f);
        world.particleSystem.addParticle(Missile.createMissile(x - xta, y - yta, 6, target.x, target.y));
    }    

    public void render(float alpha)
    {
        super.render(alpha);

        int xta = (int) (+ SIN[turretAngle] * -4.9f);
        int yta = (int) (- COS[turretAngle] * -4.9f);

        int xx = (int) (xo + (x - xo) * alpha);
        int yy = (int) (yo + (y - yo) * alpha);
        int zz = (int) (zo + (z - zo) * alpha);

        turret.x = xx - xta;
        turret.y = yy - yta;
        turret.z = zz + 10;
        turret.image = turretImages[turretAngle];

        turretShadow.x = xx + (zz + 4) / 2 - xta;
        turretShadow.y = yy + (zz + 4) - yta;
        turretShadow.image = turretShadowImages[turretAngle];
    }

    public String getName()
    {
        return "Turret";
    }
    
    public boolean shouldConnectToWall()
    {
        return true;
    }
}