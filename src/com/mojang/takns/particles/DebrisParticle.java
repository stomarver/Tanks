package com.mojang.takns.particles;

import com.mojang.takns.*;
import com.mojang.takns.sprites.Sprites;

public class DebrisParticle extends Particle
{
    private static float DAMPEN = 0.85f;

    private Sprite sprite;
    private float x, y, z, xa, ya, za;
    private float age, ageSpeed;
    private int type = 0;
    
    public DebrisParticle(int type, float x, float y, float z, float xa, float ya, float za)
    {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xa = xa;
        this.ya = ya;
        this.za = za;
        
        age = 0;
        
        ageSpeed = ((float)Math.random()*0.1f+0.1f)*8;
        
        sprite = new Sprite();
    }

    public void init(ParticleSystem particleSystem)
    {
        particleSystem.addSprite(sprite);
    }

    public void remove(ParticleSystem particleSystem)
    {
        particleSystem.removeSprite(sprite);
    }
    
    public boolean tick()
    {
        x+=xa;
        y+=ya;
        z+=za;
        if (z<0)
        {
            z = 0;
            xa = xa*0.8f;
            ya = ya*0.8f;
            za = -za*0.8f;
        }
        xa*=DAMPEN;
        ya*=DAMPEN;
        za*=DAMPEN;
        za-=0.2;
        
        age+=ageSpeed;
        
        return age<8;
    }
    
    public void render(float alpha)
    {
        sprite.x = (int)(x+xa*alpha);
        sprite.y = (int)(y+ya*alpha);
        sprite.z = (int)(z+za*alpha);
        sprite.image = Sprites.debris[type][(int)age];
    }
}