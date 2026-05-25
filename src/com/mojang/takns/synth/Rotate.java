package com.mojang.takns.synth;

public class Rotate extends Synth
{
    private Synth synth;
    private double sin;
    private double cos;

    public Rotate(Synth synth, double angle)
    {
        this.synth = synth;

        sin = Math.sin(angle);
        cos = Math.cos(angle);
    }

    public double getValue(double x, double y)
    {
        return synth.getValue(x * cos + y * sin, y * cos - x * sin);
    }
}