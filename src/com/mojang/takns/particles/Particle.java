package com.mojang.takns.particles;

import java.util.*;

public class Particle
{
    protected final Random random = new Random();
    
    public void init(ParticleSystem particleSystem)
    {
    }
    
    public boolean tick()
    {
        return false;
    }
    
    public void render(float alpha)
    {
    }

    public void remove(ParticleSystem particleSystem)
    {
    }
}