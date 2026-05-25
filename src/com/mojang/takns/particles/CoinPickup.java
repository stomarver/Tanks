package com.mojang.takns.particles;

import com.mojang.takns.Side;
import com.mojang.takns.Sprite;
import com.mojang.takns.units.Unit;
import com.mojang.takns.units.buildings.Headquarter;

public class CoinPickup extends Particle
{
    private float x;
    private float y;
    private float z;
    private final int value;
    private final Side collectorSide;
    private ParticleSystem particleSystem;
    private Sprite sprite;
    private int age = 0;
    private Unit targetHq;

    public CoinPickup(float x, float y, float z, Side collectorSide, int value)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.collectorSide = collectorSide;
        this.value = value;
    }

    public void init(ParticleSystem particleSystem)
    {
        this.particleSystem = particleSystem;
        sprite = new Sprite();
        sprite.image = com.mojang.takns.sprites.Sprites.bullet[2];
        sprite.zo = 3;
        particleSystem.addSprite(sprite);
    }

    public void remove(ParticleSystem particleSystem)
    {
        particleSystem.removeSprite(sprite);
    }

    public boolean tick()
    {
        age++;
        if (targetHq == null || !targetHq.alive)
        {
            targetHq = findHeadquarter();
            if (targetHq == null) return false;
        }

        float xt = targetHq.x;
        float yt = targetHq.y;
        float xd = xt - x;
        float yd = yt - y;
        float dist = (float) Math.sqrt(xd * xd + yd * yd);

        float speed = 0.6f + 9.0f * (1.0f - (float) Math.exp(-age * 0.18f));
        if (dist <= speed || dist < 1.5f)
        {
            collectorSide.addMoney(value);
            return false;
        }

        x += xd / dist * speed;
        y += yd / dist * speed;
        z = 3 + (float) Math.sin(age * 0.5f) * 1.2f;
        return true;
    }

    public void render(float alpha)
    {
        sprite.x = (int) x;
        sprite.y = (int) y;
        sprite.z = (int) z;
    }

    private Unit findHeadquarter()
    {
        for (int i = 0; i < collectorSide.units.units.size(); i++)
        {
            Unit unit = collectorSide.units.units.get(i);
            if (unit instanceof Headquarter) return unit;
        }
        return null;
    }
}
