package com.mojang.takns.sound;

import java.util.*;
import javax.sound.sampled.*;

public class SoundEngine implements Runnable
{
    public static final float SAMPLE_RATE = 44100.0f;
    private static final int BUFFER_SIZE = (int) (SAMPLE_RATE * 50 / 1000);
    private static final int MIN_BUFFER_SIZE = (int) (SAMPLE_RATE * 10 / 1000);

    private boolean running = false;
    private SourceDataLine dataLine;
    private List<StereoSound> sounds = new ArrayList<StereoSound>();

    public void start()
    {
        try
        {
            AudioFormat audioFormat = new AudioFormat(SAMPLE_RATE, 16, 2, true, true);
            dataLine = AudioSystem.getSourceDataLine(audioFormat);
            dataLine.open(audioFormat, (int)(SAMPLE_RATE*0.1f));
            dataLine.start();

            running = true;

            Thread thread = new Thread(this);
            thread.setDaemon(true);
            thread.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            running = false;
        }
    }

    public void addSound(Sound sound, float pan)
    {
        addSound(new StereoSound(sound, pan));
    }

    public void addSound(StereoSound sound)
    {
        if (!running) return;
        synchronized (sounds)
        {
            sounds.add(sound);
        }
    }

    public void addSound(Sound sound, SoundSource source)
    {
        addSound(new StereoSound(sound, source));
    }

    public void run()
    {
        float[] lBuf = new float[BUFFER_SIZE];
        float[] rBuf = new float[BUFFER_SIZE];
        byte[] data = new byte[BUFFER_SIZE * 4];

        while (running)
        {
            while (dataLine.available() < MIN_BUFFER_SIZE * 2 * 2)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            int toRead = dataLine.available();
            if (toRead>BUFFER_SIZE) toRead = BUFFER_SIZE;

            Arrays.fill(lBuf, 0, toRead, 0);
            Arrays.fill(rBuf, 0, toRead, 0);
            mix(sounds, lBuf, rBuf, toRead);
            for (int i = 0; i < toRead; i++)
            {
                if (lBuf[i]>1) lBuf[i] = 1;
                if (rBuf[i]>1) rBuf[i] = 1;
                if (lBuf[i]<-1) lBuf[i] = -1;
                if (rBuf[i]<-1) rBuf[i] = -1;
                int lSamp = (int) ((lBuf[i]) * 32760);
                int rSamp = (int) ((rBuf[i]) * 32760);

                data[i * 4 + 0] = (byte) ((lSamp >> 8) & 0xff);
                data[i * 4 + 1] = (byte) ((lSamp) & 0xff);
                data[i * 4 + 2] = (byte) ((rSamp >> 8) & 0xff);
                data[i * 4 + 3] = (byte) ((rSamp) & 0xff);
            }
            dataLine.write(data, 0, toRead * 2 * 2);
        }
    }

    private void mix(List<StereoSound> sounds, float[] lBuf, float[] rBuf, int bufferSize)
    {
        synchronized (sounds)
        {
            for (int i = 0; i < sounds.size(); i++)
            {
                StereoSound sound = sounds.get(i);
                boolean alive = sound.read(lBuf, rBuf, bufferSize);
                if (!alive)
                {
                    sounds.remove(i--);
                }
            }
        }
    }
}