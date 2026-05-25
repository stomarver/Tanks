package com.mojang.takns.particles;

import java.util.Random;

import com.mojang.takns.sprites.Sprites;

public class Explosion extends Particle
{
    private float x, y, z, xa, ya, za;
    private ParticleSystem particleSystem;
    private Random random = new Random();

    public Explosion(float x, float y, float z, float xa, float ya, float za)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xa = xa;
        this.ya = ya;
        this.za = za;
    }

    public void init(ParticleSystem particleSystem)
    {
        this.particleSystem = particleSystem;
    }

    public void remove(ParticleSystem particleSystem)
    {
    }

    public boolean tick()
    {
        particleSystem.addParticle(new Splat((int) x, (int) y, Sprites.crater[random.nextInt(8)]));

        int type = particleSystem.getMap().getTerrainTypeAtPixel(x, y).id;
        for (int i = 0; i < 64; i++)
        {
            float pow = random.nextFloat() * random.nextFloat() * 2 + 0.5f;

            float xa = (float) Math.sin(i * Math.PI * 2 / 16) * pow * 4 + this.xa;
            float ya = (float) Math.cos(i * Math.PI * 2 / 16) * pow * 0.5f * 4 + this.ya;
            float za = random.nextFloat() * random.nextFloat() * 8 + this.za;

            float x = this.x + xa * 2 / pow;
            float y = this.y + ya * 2 / pow;
            float z = this.z;

            particleSystem.addParticle(new DebrisParticle(type, x, y, z, xa, ya, za));
        }
        for (int i = 0; i < 16; i++)
        {
            float pow = random.nextFloat();

            float xa = (float) Math.sin(i * Math.PI * 2 / 16) * pow * 4 + this.xa;
            float ya = (float) Math.cos(i * Math.PI * 2 / 16) * pow * 0.5f * 4 + this.ya;
            float za = random.nextFloat() * random.nextFloat() * random.nextFloat() * 16 + this.za;

            float x = this.x + xa * 2;
            float y = this.y + ya * 2;
            float z = this.z;

            particleSystem.addParticle(new SmokeParticle(x, y, z, xa, ya, za, SmokeParticle.TYPE_WHITE));
        }

        return false;
    }
}