package com.mojang.takns.particles;

import java.awt.image.BufferedImage;

import com.mojang.takns.*;
import com.mojang.takns.sprites.Sprites;

public class SmokeParticle extends Particle
{
    public static final int TYPE_WHITE = 0;
    public static final int TYPE_FIRE = 1;
    
    private static float DAMPEN = 0.7f;

    private Sprite sprite;
    private float x, y, z, xa, ya, za;
    private float age, ageSpeed;
    private int type;
    private BufferedImage[] images;
    
    public SmokeParticle(float x, float y, float z, float xa, float ya, float za, int type)
    {
        this.type = 0;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xa = xa;
        this.ya = ya;
        this.za = za;
        this.type = type;
        
        if (type==TYPE_WHITE) images = Sprites.whiteSmoke;
        if (type==TYPE_FIRE) images = Sprites.fireSmoke;
        
        age = 0;
        
        ageSpeed = ((float)Math.random()*0.2f+0.06f)*2;
        if (type==TYPE_FIRE) ageSpeed*=0.8f;
        
        sprite = new Sprite();
        sprite.image = images[0];
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
        za+=0.1;
        if (type==TYPE_FIRE) za+=(7-age)/30.0f;
        
        age+=ageSpeed;
        
        return age<8;
    }
    
    public void render(float alpha)
    {
        sprite.x = (int)(x+xa*alpha);
        sprite.y = (int)(y+ya*alpha);
        sprite.z = (int)(z+za*alpha);
        sprite.image = images[(int)age];
    }
}