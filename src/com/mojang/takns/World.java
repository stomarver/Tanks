package com.mojang.takns;

import java.awt.Graphics2D;
import java.util.Random;

import com.mojang.takns.gui.Text;
import com.mojang.takns.gui.GameView;
import com.mojang.takns.gui.MapRenderer;
import com.mojang.takns.gui.UiComponent;
import com.mojang.takns.particles.ParticleSystem;
import com.mojang.takns.sound.SoundEngine;
import com.mojang.takns.sprites.BuildIcons;
import com.mojang.takns.sprites.MonsterSprites;
import com.mojang.takns.sprites.Sprites;
import com.mojang.takns.sprites.Voxels;
import com.mojang.takns.terrain.*;
import com.mojang.takns.units.buildings.*;
import com.mojang.takns.units.monsters.Slime;
import com.mojang.takns.units.vehicles.*;

public class World
{
    public Tiles tiles;
    public Map map;
    public Minimap minimap;
    public MapRenderer mapRenderer;
    public ParticleSystem particleSystem;
    public SoundEngine soundEngine;

    public Side playerSide = new Side(this, 0);
    public Side monsterSide = new Side(this, 1);
    
    public Side[] sides;
    public GameView gameView = new GameView(playerSide);

    private int cameraPanTime = 5;
    public int xCam, yCam;
    public int xCamTarget, yCamTarget;
    public int xCamStart, yCamStart;
    public int cameraMotionSteps = -1;

    private Random random;

    public World()
    {
        this(new Random());
    }

    public World(Random random)
    {
        this.random = random;
    }

    public void init()
    {
        init(false);
    }

    public void init(boolean reduced)
    {
        sides = new Side[2];
        sides[0] = playerSide;
        sides[1] = monsterSide;
        Takns.currentStatus = "Building tiles..";
        tiles = new Tiles(new Random(5433184));
        tiles.createTiles();
        if (!reduced)
        {
            Takns.currentStatus = "Building sprites..";
            Sprites.buildSprites(new Random(1000));
            MonsterSprites.buildSprites(new Random(1000));
        }
        Takns.currentStatus = "Building voxels..";
        Voxels.reduced = reduced;
        Voxels.buildVoxels(new Random(1000));
        if (!reduced)
        {
            BuildIcons.setPreviewSide(playerSide);
        }

        Takns.currentStatus = "Building font..";
        Text.init();

        Takns.currentStatus = "Generating map..";
        boolean mapOk = false;
        int attempt = 0;
        do
        {
            if (attempt > 0)
            {
                Takns.currentStatus = "Regenerating map.. (" + attempt + " maps rejected)";
            }
            attempt++;

            map = new Map(random);

            if (!reduced)
            {
                for (int i=0; i<sides.length; i++)
                    sides[i].reset();

                particleSystem = new ParticleSystem(this);
                mapRenderer = new MapRenderer(this, playerSide, Takns.SCREEN_WIDTH - Takns.PANEL_WIDTH, Takns.SCREEN_HEIGHT);

                if (!placePlayer(playerSide)) continue;
                if (!placeMonsters(monsterSide)) continue;
            }

            mapOk = true;
        }
        while (!mapOk);

        minimap = new Minimap(this);
    }

    private boolean placeMonsters(Side side)
    {
        for (int i=0; i<64; i++)
        {
            int x = random.nextInt(64);
            int y = random.nextInt(64);
            if ((map.getUnitAt(x, y)==null) && (map.getTerrainTypeAt(x,y).passableFlags&Terrain.PASSABLE_LAND)>0)
            {
                side.units.addUnit(new Slime(x, y));
            }
        }
        
        return true;
    }

    private boolean placePlayer(Side side)
    {
        boolean ok = getRandomPosition(Terrain.TERRAIN_GRASS, 6, 6);
        if (!ok) return false;

        int gems = countTerrain(Terrain.TERRAIN_GEM, xTile + 3, yTile + 3, 8, 8);
        if (gems < 10) return false;

        for (int xx = xTile + 2; xx < xTile + 4; xx++)
            for (int yy = yTile + 2; yy < yTile + 4; yy++)
                map.tiles[xx + yy * map.width] = Tiles.TYPE_SLAB;

        Headquarter hq = new Headquarter();
        hq.buildAt(xTile + 2, yTile + 2);
        side.units.addUnit(hq);

        side.units.addUnit(new Harvester(xTile + 1, yTile + 3));
        side.units.addUnit(new Harvester(xTile + 4, yTile ));
        side.units.addUnit(new Car(xTile + 2, yTile + 5));
        side.units.addUnit(new Tank(xTile + 5, yTile + 4));
        side.units.addUnit(new Tank(xTile + 3, yTile + 4));

        centerCam((xTile + 3) * 16, (yTile + 3) * 16);

        return ok;
    }

    private int xTile;
    private int yTile;

    public UiComponent hoveredComponent;

    private int countTerrain(Terrain terrainType, int x, int y, int w, int h)
    {
        int count = 0;
        for (int xx = x - w; xx <= x + w; xx++)
            for (int yy = y - h; yy <= y + h; yy++)
            {
                if (xx >= 0 && yy >= 0 && xx < map.width && yy < map.height) if (Tiles.tiles[map.tiles[xx + yy * map.width]].terrain == terrainType) count++;
            }

        return count;
    }

    private boolean getRandomPosition(Terrain terrainType, int w, int h)
    {
        int tries = 0;
        findPos: do
        {
            tries++;
            int x = random.nextInt(map.width - w);
            int y = random.nextInt(map.height - h);
            for (int xx = x; xx < x + w; xx++)
                for (int yy = y; yy < y + h; yy++)
                    if (Tiles.tiles[map.tiles[xx + yy * map.width]].terrain != terrainType) continue findPos;

            xTile = x;
            yTile = y;

            return true;
        }
        while (tries > 100);
        return false;
    }

    public void tick()
    {
        particleSystem.tick();
        for (int i=0; i<sides.length; i++)
            sides[i].tick();
        if (cameraMotionSteps != -1)
        {
            cameraMotionSteps++;
            if (cameraMotionSteps == cameraPanTime)
            {
                cameraMotionSteps = -1;
            }
        }

        for (int i=0; i<sides.length; i++)
        {
            sides[i].units.clearSeenEnemies();
            for (int j=0; j<sides.length; j++)
            {
                if (i==j) continue;
                sides[j].units.setVisible(sides[i]);
            }
        }
    }

    public void render(float alpha)
    {
        playerSide.fogOfWar.prepareRender();
        particleSystem.render(alpha);
        for (int i=0; i<sides.length; i++)
        {
            sides[i].units.render(alpha, playerSide.fogOfWar);
        }
        playerSide.fogOfWar.buildImage();

        if (cameraMotionSteps != -1)
        {
            float step = (cameraMotionSteps + alpha) / cameraPanTime;
            float x = xCamStart + (xCamTarget - xCamStart) * step;
            float y = yCamStart + (yCamTarget - yCamStart) * step;
            setCam((int) x, (int) y);
        }
    }

    public void centerCamSmooth(int x, int y)
    {
        cameraPanTime = 5;
        cameraMotionSteps = 0;
        xCamStart = xCam;
        yCamStart = yCam;
        xCamTarget = x - gameView.width / 2;
        yCamTarget = y - gameView.height / 2;
        if (xCamTarget < 0) xCamTarget = 0;
        if (yCamTarget < 0) yCamTarget = 0;
        if (xCamTarget >= map.width * 16 - gameView.width) xCamTarget = map.width * 16 - gameView.width - 1;
        if (yCamTarget >= map.height * 16 - gameView.height) yCamTarget = map.height * 16 - gameView.height - 1;
    }

    public void centerCam(int x, int y)
    {
        cameraMotionSteps = -1;
        setCam(x - gameView.width / 2, y - gameView.height / 2);
    }

    public void setCam(int x, int y)
    {
        this.xCam = x;
        this.yCam = y;

        if (xCam < 0) xCam = 0;
        if (yCam < 0) yCam = 0;
        if (xCam >= map.width * 16 - gameView.width) xCam = map.width * 16 - gameView.width - 1;
        if (yCam >= map.height * 16 - gameView.height) yCam = map.height * 16 - gameView.height - 1;
    }
    
    public void smoothCam(int x, int y)
    {
        cameraPanTime = 1;
        cameraMotionSteps = 0;
        xCamStart = xCam;
        yCamStart = yCam;
        xCamTarget = x;
        yCamTarget = y;
        if (xCamTarget < 0) xCamTarget = 0;
        if (yCamTarget < 0) yCamTarget = 0;
        if (xCamTarget >= map.width * 16 - gameView.width) xCamTarget = map.width * 16 - gameView.width - 1;
        if (yCamTarget >= map.height * 16 - gameView.height) yCamTarget = map.height * 16 - gameView.height - 1;
    }

    public void moveCam(int x, int y)
    {
        cameraMotionSteps = -1;
        setCam(xCam + x, yCam + y);
    }

    public void postRender(Graphics2D g, float alpha)
    {
        for (int i=0; i<sides.length; i++)
        {
            sides[i].units.postRender(g, alpha);
        }
    }

    public void moveCamera(boolean u, boolean d, boolean l, boolean r)
    {
        if (!u && !d && !l && !r) return;
        int panSpeed = 16;
        int xd = 0;
        int yd = 0;
        if (u) yd-=panSpeed;
        if (d) yd+=panSpeed;
        if (l) xd-=panSpeed;
        if (r) xd+=panSpeed;
        smoothCam(xCam+xd, yCam+yd);
    }
}