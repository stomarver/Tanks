package com.mojang.takns.particles;

import java.util.*;

import com.mojang.takns.*;
import com.mojang.takns.terrain.Map;

public class ParticleSystem
{
    public List<Particle> particles = new ArrayList<Particle>();
    private World world;
    
    public ParticleSystem(World world)
    {
        this.world = world;
    }
    
    public void addParticle(Particle particle)
    {
        particle.init(this);
        particles.add(particle);
        particle.render(0);
    }

    public void addSprite(Sprite sprite)
    {
        world.mapRenderer.sprites.add(sprite);
    }

    public void removeSprite(Sprite sprite)
    {
        world.mapRenderer.sprites.remove(sprite);
    }
    
    public void tick()
    {
        for (int i=0; i<particles.size(); i++)
        {
            Particle particle = particles.get(i);
            boolean alive = particle.tick(); 
            if (!alive)
            {
                particle.remove(this);
                particles.remove(i);
                i--;
            }
        }
    }

    public void render(float alpha)
    {
        for (int i=0; i<particles.size(); i++)
        {
            Particle particle = particles.get(i);
            particle.render(alpha); 
        }
    }

    public Map getMap()
    {
        return world.map;
    }
}