package com.mojang.takns;

import java.awt.*;
import java.util.SortedSet;

public class Sprite implements Comparable<Sprite>
{
    public static final int LAYER_TERRAIN_SPLAT = -1;
    public static final int LAYER_SHADOW = -1;
    public static final int LAYER_NORMAL = 0;
    public static final int LAYER_AIR = 1;
    public static final int LAYER_HUD = 2;

    private static int ID_COUNTER = 0;
    private final int id = ID_COUNTER++;

    public int x, y, z;
    public int xo = -8, yo = -8, zo = 0;
    public int w = 16, h = 16;
    public int layer = Sprite.LAYER_NORMAL;
    public Image image;
    public float alpha = 1;

    public int compareTo(Sprite s)
    {
        if (s == this) return 0;

        if (layer < s.layer) return -1;
        if (layer > s.layer) return 1;

        if (y + zo+z < s.y + s.zo+s.z) return -1;
        if (y + zo+z > s.y + s.zo+s.z) return 1;
        return (id < s.id) ? -1 : 1;
    }

    public void addTo(SortedSet<Sprite> visibleSprites)
    {
        visibleSprites.add(this);
    }

    public void renderImageTo(Graphics2D g, int xOffs, int yOffs)
    {
        g.drawImage(image, x + xo+xOffs, y - z + yo+yOffs, null);
    }
}