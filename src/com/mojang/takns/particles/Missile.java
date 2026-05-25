package com.mojang.takns.particles;

import com.mojang.takns.*;
import com.mojang.takns.sprites.Sprites;

public class Missile extends Particle
{
    private float x, y, z, xa, ya, za;
    private ParticleSystem particleSystem;
    private Sprite sprite;
    private Sprite shadowSprite;
    private int age = 0;

    public static Missile createMissile(float xStart, float yStart, float zStart, float xTarget, float yTarget)
    {
        float xd = xTarget - xStart;
        float yd = yTarget - yStart;
        float dir = (float) (Math.atan2(xd, yd));
        float dist = (float) Math.sqrt(xd * xd + yd * yd);
        float za = (float) Math.sqrt(dist + 1) / 2 - 2;

        float time = za / 0.2f;
        float topHeight = zStart + za * time + -0.2f * time * time * 0.5f;

        time += (float) Math.sqrt(topHeight / 0.1);

        float pow = dist / time;
        float xa = (float) Math.sin(dir) * pow;
        float ya = (float) Math.cos(dir) * pow;

        return new Missile(xStart, yStart, zStart, xa, ya, za);
    }

    public Missile(float x, float y, float z, float xa, float ya, float za)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.xa = xa;
        this.ya = ya;
        this.za = za;

        age = 0;

        sprite = new Sprite();
        shadowSprite = new Sprite();
        shadowSprite.layer = Sprite.LAYER_SHADOW;
    }

    public void init(ParticleSystem particleSystem)
    {
        this.particleSystem = particleSystem;
        particleSystem.addSprite(sprite);
        particleSystem.addSprite(shadowSprite);
    }

    public void remove(ParticleSystem particleSystem)
    {
        particleSystem.removeSprite(sprite);
        particleSystem.removeSprite(shadowSprite);
    }

    public boolean tick()
    {
        if (Math.random() > age / 20.0)
        {
            particleSystem.addParticle(new SmokeParticle(x, y, z, xa, ya, za, SmokeParticle.TYPE_WHITE));
        }
        if (age == 0)
        {
            for (int i = 0; i < 8; i++)
            {
                float xxa = -xa * i / 7 + (float) (Math.random() * 2 - 1);
                float yya = -ya * i / 7 + (float) (Math.random() * 2 - 1);
                float zza = -za * i / 7 + (float) (Math.random() * 2 - 1);
                particleSystem.addParticle(new SmokeParticle(x, y, z, xxa, yya, zza, SmokeParticle.TYPE_WHITE));
            }
        }
        age++;

        x += xa;
        y += ya;
        z += za;
        if (z <= 0)
        {
            particleSystem.addParticle(new Explosion(x, y, z, xa * 0.2f, ya * 0.2f, 0));
            return false;
        }
        za -= 0.2;

        return true;
    }

    public void render(float alpha)
    {
        sprite.x = (int) (x + xa * alpha);
        sprite.y = (int) (y + ya * alpha);
        sprite.z = (int) (z + za * alpha);
        sprite.image = Sprites.bullet[5];

        shadowSprite.x = (int) (x + xa * alpha);
        shadowSprite.y = (int) (y + ya * alpha);
        shadowSprite.z = (int) (0);
        int i = (int) ((z + za * alpha) / 20) + 4;
        if (i > 7) i = 7;
        shadowSprite.image = Sprites.shadow[i];
    }
}