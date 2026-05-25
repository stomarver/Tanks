package com.mojang.takns.synth;

public class Distort extends Synth
{
    private Synth source;
    private Synth distort;

    public Distort(Synth source, Synth distort)
    {
        this.source = source;
        this.distort = distort;
    }

    public double getValue(double x, double y)
    {

        return source.getValue(x + distort.getValue(x, y), y);
    }
}