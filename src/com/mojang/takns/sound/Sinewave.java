package com.mojang.takns.sound;

public class Sinewave extends Sound
{
    private int step = 0;
    public boolean read(float[] buf, int bufferSize)
    {
        for (int i=0; i<bufferSize; i++)
        {
            float samp = (saw(step+i, 50))*(saw(step+i,20)*0.25f+0.35f);

            buf[i] = samp;
        }
        step+=bufferSize;
        return true;
    }
}