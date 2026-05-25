package com.mojang.takns.synth;

import java.util.Random;

public class PerlinNoise extends Synth
{
    private ImprovedNoise[] noiseLevels;
    private int levels;

    public PerlinNoise(int levels)
    {
        this(new Random(), levels);
    }

    public PerlinNoise(Random random, int levels)
    {
        this.levels = levels;
        noiseLevels = new ImprovedNoise[levels];
        for (int i = 0; i < levels; i++)
        {
            noiseLevels[i] = new ImprovedNoise(random);
        }
    }

    public double getValue(double x, double y)
    {
        double value = 0;
        double pow = 1;

        for (int i = 0; i < levels; i++)
        {
            value += noiseLevels[i].getValue(x / pow, y / pow) * pow;
            pow *= 2;
        }

        return value;
    }
}