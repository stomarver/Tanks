package com.mojang.takns.sound;

public class PanSource implements SoundSource
{
    private float pan;

    public PanSource(float pan)
    {
        this.pan = pan;
    }

    public float getXSoundPos()
    {
        return pan;
    }

    public float getYSoundPos()
    {
        return 0;
    }
}