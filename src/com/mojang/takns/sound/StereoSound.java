package com.mojang.takns.sound;

public class StereoSound
{
    private static final float SOUND_FALLOFF = 6.0f;
    
    private Sound sound;
    private SoundSource soundSource;
    private float lPan = -999f;
    private float rPan = -999f;
    
    public StereoSound(Sound sound, float pan)
    {
        this(sound, new PanSource(pan));
    }

    public StereoSound(Sound sound, SoundSource soundSource)
    {
        this.sound = sound;
        this.soundSource = soundSource;
    }

    private static float[] buf = new float[1];
    public boolean read(float[] lBuf, float[] rBuf, int bufferSize)
    {
        if (buf.length<bufferSize)
        {
            buf = new float[bufferSize];
        }
        
        boolean alive = sound.read(buf, bufferSize);
        
        float panTarget = soundSource.getXSoundPos();
        
        float volume = 1;
        if (panTarget<-1) volume+=(panTarget+1)*SOUND_FALLOFF;
        if (panTarget>1) volume-=(panTarget-1)*SOUND_FALLOFF;
        float ySound = soundSource.getYSoundPos();
        if (ySound<-1) volume+=(ySound+1)*SOUND_FALLOFF;
        if (ySound>1) volume-=(ySound-1)*SOUND_FALLOFF;
        
        if (panTarget<-1) panTarget = -1;
        if (panTarget>1) panTarget = 1;
        
        float lPanT = (1-panTarget)*volume;
        float rPanT = (1+panTarget)*volume;
        if (lPanT<0) lPanT = 0;
        if (rPanT<0) rPanT = 0;
        if (lPanT>1) lPanT = 1;
        if (rPanT>1) rPanT = 1;
        
        if (lPan==-999f) lPan = lPanT;
        if (rPan==-999f) rPan = rPanT;

        if (lPan<=0 && lPanT<=0 && rPan<=0 && rPanT<=0) return alive;
        
        for (int i=0; i<bufferSize; i++)
        {
            lPan += (lPanT-lPan)*0.001f;
            rPan += (rPanT-rPan)*0.001f;
            lBuf[i]+=buf[i]*lPan;
            rBuf[i]+=buf[i]*rPan;
        }
        
        return alive;
    }
}