package com.mojang.takns.particles;

import java.awt.Image;

import com.mojang.takns.*;

public class Splat extends Particle
{
    private int age;
    private int life;
    private Sprite sprite;
    
    public Splat(int x, int y, Image image)
    {
        sprite = new Sprite();
        sprite.layer = Sprite.LAYER_TERRAIN_SPLAT;
        sprite.x = x;
        sprite.y = y;
        sprite.image = image;
        age = 0;
        life = 400;
    }
    
    public void init(ParticleSystem particleSystem)
    {
        particleSystem.addSprite(sprite);
    }
    
    public boolean tick()
    {
        sprite.alpha = 1-age/(float)life;
        age++;
        return age<life;
    }

    public void remove(ParticleSystem particleSystem)
    {
        particleSystem.removeSprite(sprite);
    }
}