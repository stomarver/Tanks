package com.mojang.takns.sound;

public class SelectionSound extends Sound
{
    private int step = 0;
    private int delayTicks = 0;
    private int attackTicks, sustainTicks, decayTicks;
    
    public SelectionSound(float delay)
    {
        delayTicks = (int)(delay*SoundEngine.SAMPLE_RATE);
        
        attackTicks = (int)(0.001f*SoundEngine.SAMPLE_RATE);
        sustainTicks = (int)(0.04f*SoundEngine.SAMPLE_RATE);
        decayTicks = (int)(0.02f*SoundEngine.SAMPLE_RATE);
    }
    
    public boolean read(float[] buf, int bufferSize)
    {
        for (int i=0; i<bufferSize; i++)
        {
            if (step<delayTicks)
            {
                buf[i] = 0;
            }
            else
            {
                float volume = 1;
                
                if (step>delayTicks+attackTicks+sustainTicks)
                {
                    volume = 1-(step-(delayTicks+attackTicks+sustainTicks))/(float)decayTicks;
                }
                if (step<delayTicks+attackTicks)
                {
                    volume = (step-delayTicks)/(float)attackTicks;
                }
                
                if (volume<0) buf[i] = 0;
                else buf[i] = (sin(step, 1000)+saw(step, 2000)+square(step, 3000)*0.4f)*volume*0.03f;
            }
            step++;
        }
        return step<(delayTicks+attackTicks+sustainTicks+decayTicks);
    }
}