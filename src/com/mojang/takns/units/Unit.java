package com.mojang.takns.units;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

import com.mojang.takns.CompoundSprite;
import com.mojang.takns.Side;
import com.mojang.takns.Takns;
import com.mojang.takns.World;
import com.mojang.takns.gui.buttons.ButtonType;
import com.mojang.takns.particles.SmokeParticle;
import com.mojang.takns.sound.SelectionSound;
import com.mojang.takns.sound.SoundSource;

public abstract class Unit implements SoundSource
{
    public static final float[] SIN = { 0.0f, 0.38f, 0.71f, 0.92f, 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, };
    public static final float[] COS = { 1.0f, 0.92f, 0.71f, 0.38f, 0f, -0.38f, -0.71f, -0.92f, -1.0f, -0.92f, -0.71f, -0.38f, 0.0f, 0.38f, 0.71f, 0.92f, };

    protected Random random = new Random();
    protected int cost;

    public float x;
    public float y;
    public float z;
    public float xo;
    public float yo;
    public float zo;
    public Side side;
    public boolean alive;
    public boolean selected = false;

    public int damage = 0;
    public int maxDamage = 10;
    public boolean infested = false;
    public int infestationDamage = 0;
    public int infestationMaxDamage = 0;

    public CompoundSprite sprite = new CompoundSprite();
    protected World world;

    protected int xTile;
    protected int yTile;
    private int oldRevealRadius = 0;
    protected int revealRadius = 0;
    protected float p = 0;
    protected int xNextTile = 0;
    protected int yNextTile = 0;

    protected int xTarget = 0;
    protected int yTarget = 0;

    protected int xOld = 0;
    protected int yOld = 0;
    public boolean moving = false;
    protected boolean manualBlock = false;

    public float selectTime = 0;

    public Unit()
    {
        super();
        alive = true;
    }

    public void moveTo(int xDestination, int yDestination)
    {
        xTarget = xDestination;
        yTarget = yDestination;
    }

    public void setSide(Side side)
    {
        this.side = side;
    }
    
    public void init(World world, Side side)
    {
        setSide(side);
        this.world = world;

        world.mapRenderer.sprites.add(sprite);
    }

    public void remove(Units units)
    {
        if (revealRadius != 0 && side != null)
        {
            side.fogOfWar.unRevealStatic(xTile, yTile, revealRadius);
            if (!manualBlock) world.map.unblock(xTile, yTile);
        }
    }

    public void tick()
    {
        if (selected) selectTime--;

        if (damage >= maxDamage / 2)
        {
            if (random.nextFloat()*3 < (damage / (float) maxDamage)-0.4f)
            {
                float xa = random.nextFloat() * 2 - 1;
                float ya = random.nextFloat() * 2 - 1;
                float za = random.nextFloat() * 2 - 1;
                world.particleSystem.addParticle(new SmokeParticle(x + random.nextFloat() * 12 - 6, y + random.nextFloat() * 12 - 6, z + 6, xa, ya, za, SmokeParticle.TYPE_FIRE));
            }
        }

        xo = x;
        yo = y;
        zo = z;

        int _xt = (int) (x / 16);
        int _yt = (int) (y / 16);
        if ((_xt != xTile || _yt != yTile || oldRevealRadius!=revealRadius) && side != null)
        {
            if (oldRevealRadius != 0)
            {
                side.fogOfWar.unRevealStatic(xTile, yTile, oldRevealRadius);
                if (!manualBlock) world.map.unblock(xTile, yTile);
            }
            xTile = _xt;
            yTile = _yt;
            oldRevealRadius = revealRadius;
            if (oldRevealRadius != 0)
            {
                side.fogOfWar.revealStatic(xTile, yTile, oldRevealRadius);
                if (!manualBlock) world.map.block(xTile, yTile, this);
            }
        }
    }

    public void hide()
    {
        sprite.x = -1000;
    }

    public void render(float alpha)
    {
        sprite.x = (int) x;
    }

    public void postRender(Graphics2D g, float alpha)
    {
        if (infested)
        {
            float hp = 1.0f - infestationDamage / (float) infestationMaxDamage;
            if (hp < 0) hp = 0;
            Color c = getInfestationColor(hp);
            int ix = (int) xo - 10 - world.xCam;
            int iy = (int) yo - 10 - world.yCam;
            int iw = 20;
            int ih = 20;
            if (isBuilding())
            {
                iw += 16;
                ih += 16;
            }

            g.setColor(new Color(0.30f, 0.30f, 0.30f, 0.55f));
            g.fillRect(ix, iy, iw, ih);
            g.setColor(new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.75f));
            g.fillRect(ix, iy, iw, ih);

            g.setColor(new Color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, 0.85f));
            g.drawRect(ix - 1, iy - 1, iw + 1, ih + 1);
            g.drawRect(ix + 1, iy + 1, iw - 3, ih - 3);
        }

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
            g.drawRect((int) xo - r - world.xCam, (int) yo - r - world.yCam, r * 2 - 1, r * 2 - 1);
        }
    }

    public void addToMinimap(int[] minimapPixels)
    {
        int x = (int) (xo / 16);
        int y = (int) (yo / 16);
        if (x >= 0 && y >= 0 && x < 64 && y < 64)
        {
            minimapPixels[x + y * 64] = side.minimapColor;
        }
    }

    public boolean isInside(int x0, int y0, int x1, int y1)
    {
        x0 -= 12;
        y0 -= 12;
        x1 += 12;
        y1 += 12;
        return x >= x0 && y >= y0 && x <= x1 && y <= y1;
    }

    public void select()
    {
        select(0);
    }

    public void select(float delay)
    {
        world.soundEngine.addSound(new SelectionSound(delay*4f/Takns.TICKS_PER_SECOND), this);
        selectTime = 5 + delay * 4f;
        selected = true;
    }

    public void unSelect()
    {
        selected = false;
    }

    public void hurt(int amount)
    {
        if (amount <= 0 || !alive) return;
        if (infested)
        {
            infestationDamage += amount;
            if (infestationDamage >= infestationMaxDamage)
            {
                infestationDamage = infestationMaxDamage;
                infested = false;
                world.playerSide.addMoney(80);
            }
            return;
        }
        damage += amount;
        if (damage >= maxDamage)
        {
            damage = maxDamage;
            alive = false;
        }
    }

    public float getHealthRatio()
    {
        if (maxDamage <= 0) return 0;
        return (maxDamage - damage) / (float) maxDamage;
    }

    public void infest(int slimeDamage, int slimeMaxDamage)
    {
        infested = true;
        infestationDamage = slimeDamage;
        infestationMaxDamage = slimeMaxDamage;
        if (infestationMaxDamage <= 0) infestationMaxDamage = 1;
    }

    private Color getInfestationColor(float health)
    {
        if (health <= 0.0f) return new Color(36, 10, 10);
        if (health <= 0.50f) return new Color(166, 46, 46);
        if (health <= 0.66f) return new Color(217, 128, 46);
        if (health <= 0.75f) return new Color(217, 217, 64);
        return new Color(32, 220, 32);
    }

    public float getDistanceSqr(int x0, int y0)
    {
        float xd = x0 - x;
        float yd = y0 - y;
        return xd * xd + yd * yd;
    }
    
    public boolean shouldConnectToWall()
    {
        return false;
    }

    public boolean isGroupSelectable()
    {
        return !isBuilding();
    }

    public boolean isBuilding()
    {
        return false;
    }

    public void renderImageTo(Graphics2D g, int x, int y)
    {
        sprite.renderImageTo(g, x, y);
    }

    public abstract String getName();
    
    public ButtonType[] getButtons()
    {
        return null;
    }
    
    public int getCost()
    {
        return cost;
    }
    
    public float getXSoundPos()
    {
        return (x-world.xCam-world.gameView.width/2.0f)/(float)world.gameView.width;
    }

    public float getYSoundPos()
    {
        return (y-world.yCam-world.gameView.height/2.0f)/(float)world.gameView.height;
    }
}
