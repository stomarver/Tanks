package com.mojang.takns.sound;

import java.util.Random;

public class Sound
{
    private Random random = new Random();
    
    protected final float saw(float phase, float rate)
    {
        phase/=SoundEngine.SAMPLE_RATE;
        phase*=rate;
        phase-=(int)phase;
        if (phase<0.5f) return phase*4-1;
        return 1-(phase-0.5f)*4;
    }

    protected final float square(float phase, float rate)
    {
        phase/=SoundEngine.SAMPLE_RATE;
        phase*=rate*2;
        return (((int)phase)&1)*2-1;
    }
    
    protected final float sin(float phase, float rate)
    {
        return (float)(Math.sin(phase*Math.PI*2*rate/SoundEngine.SAMPLE_RATE)); 
    }

    protected final float noise()
    {
        return random.nextFloat()*2-1; 
    }

    private float noise;
    protected final float noise(float pitch)
    {
        float noiseTarget=random.nextFloat()*2-1;
        noise += (noiseTarget-noise)*pitch;
        return noise; 
    }
    
    public boolean read(float[] lBuf, int bufferSize)
    {
        return false;
    }
}