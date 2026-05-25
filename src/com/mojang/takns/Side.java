package com.mojang.takns;

import com.mojang.takns.units.Units;

public class Side
{
    public static final int MAX_MONEY = 99999;

    public Units units;
    public FogOfWar fogOfWar;

    public int money = 2000;

    protected World world;

    public int id = 0;
    public int minimapColor = 0xffff0000;
                        
    public Side(World world, int id)
    {
        this.world = world;
        this.id = id;
        
        if (id==1) minimapColor = 0xff00ff00;
        reset();
    }

    public void addMoney(int moneyToAdd)
    {
        money += moneyToAdd;
        if (money > MAX_MONEY) money = MAX_MONEY;
    }

    public void tick()
    {
        fogOfWar.tick();
        units.tick();
    }

    public void reset()
    {
        units = new Units(world, this);
        fogOfWar = new FogOfWar();
    }

    public void chargeMoney(int cost)
    {
        money-=cost;
        if (money < 0) throw new IllegalStateException("Negative money!");
    }
}