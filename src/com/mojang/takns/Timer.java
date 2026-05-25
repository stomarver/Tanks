package com.mojang.takns;

public class Timer
{
    public float alpha;

    private int ticksPerSecond;
    private int msPerTick;
    private long lastTime = -1;
    private int passedTime = 0;
    private int frames = 0;
    private int ticks = 0;
    public int fps = 0;
    private float averageFrameTime = 0;
    private boolean useAverageFrameTime = false;

    public Timer(int ticksPerSecond)
    {
        this.ticksPerSecond = ticksPerSecond;
        msPerTick = 1000 / ticksPerSecond;
    }

    public int advanceTime()
    {
        long now = System.nanoTime() / 1000000;
        if (lastTime == -1) lastTime = now;
        int frameTime = (int) (now - lastTime);
        if (useAverageFrameTime)
        {
            averageFrameTime += (frameTime-averageFrameTime)*0.1f;
            frameTime = (int)(averageFrameTime);
        }
        else
        {
            if (frameTime < 0)
            {
                System.out.println("WARNING: Negative frame time detected, switching to average frame times.");
                useAverageFrameTime = true;
                frameTime = 0;
            }
        }
        passedTime += frameTime;
        lastTime = now;
        frames++;

        int ticksToProcess = passedTime / msPerTick;
        passedTime -= ticksToProcess * msPerTick;

        ticks += ticksToProcess;
        while (ticks >= ticksPerSecond)
        {
            fps = frames;
            System.out.println("fps: " + fps);
            frames = 0;
            ticks -= ticksPerSecond;
        }


        alpha = (float) passedTime / (float) msPerTick;
        return ticksToProcess;
    }
}