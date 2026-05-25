package com.mojang.takns.synth;

public abstract class Synth
{
    public abstract double getValue(double x, double y);

    public double[] create(int width, int height)
    {
        double[] result = new double[width * height];
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                result[x + y * width] = getValue(x, y);
            }
        }
        return result;
    }
}
