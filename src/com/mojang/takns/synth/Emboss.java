package com.mojang.takns.synth;

public class Emboss extends Synth
{
    private Synth synth;

    public Emboss(Synth synth)
    {
        this.synth = synth;
    }

    public double getValue(double x, double y)
    {
        return synth.getValue(x, y) - synth.getValue(x + 1, y + 1);
    }
}