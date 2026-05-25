package com.mojang.takns;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import javax.swing.*;

import com.mojang.takns.gui.*;
import com.mojang.takns.intro.MojangLogo;
import com.mojang.takns.intro.TitleBuilder;
import com.mojang.takns.sound.SoundEngine;

public class Takns extends Canvas implements Runnable
{
    private static final long serialVersionUID = -5904259650979188881L;

    public static String currentStatus = "Initializing..";

    private static final int MOJANG_SPLASH_TIME = 800;
    private static final int GAME_SPLASH_TIME = 800;

    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 240;
    public static final int SCALE = 2;
    
    public static final int TICKS_PER_SECOND = 25;

    public static final int PANEL_WIDTH = 64 + 8;
    private static final int MAX_TICKS_PER_FRAME = 10;

    private Cursor cursor;
    private SoundEngine soundEngine;

    private class GameStarterThread extends Thread
    {
        private boolean isDone = false;

        public void run()
        {
            soundEngine = new SoundEngine();
            soundEngine.start();
            
            world.soundEngine = soundEngine;

            currentStatus = "Creating components..";
            gameComponent.init(world, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            world.gameView.init(world, 0, 0, SCREEN_WIDTH - PANEL_WIDTH, SCREEN_HEIGHT);
            gameComponent.addComponent(world.gameView);

            PanelComponent panel = new PanelComponent();
            panel.init(world, SCREEN_WIDTH - PANEL_WIDTH, 0, PANEL_WIDTH, SCREEN_HEIGHT);
            gameComponent.addComponent(panel);

            HudComponent hud = new HudComponent();
            hud.init(world, 0, 0, 0, 0);
            gameComponent.addComponent(hud);
            cursor = new Cursor();

            world.init();

            currentStatus = "Starting up..";
            try
            {
                Thread.sleep(GAME_SPLASH_TIME + MOJANG_SPLASH_TIME);
            }
            catch (InterruptedException e)
            {
            }

            isDone = true;
        }

        public boolean isDone()
        {
            return isDone;
        }
    }

    private Timer timer = new Timer(TICKS_PER_SECOND);

    private boolean keepGoing = true;
    private World world = new World();

    private BufferStrategy bufferStrategy;
    private Image titleImage;
    private VolatileImage image;
    private UiComponent gameComponent = new UiComponent();
    private InputHandler inputHandler;

    public Takns()
    {
        setIgnoreRepaint(true);

        setMinimumSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        setMaximumSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        setPreferredSize(new Dimension(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE));
        this.setBounds(0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);

        setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB_PRE), new Point(0, 0), ""));
    }

    public void init()
    {
        ImageConverter.init(getGraphicsConfiguration());
        image = createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    public void start()
    {
        new Thread(this).start();
    }

    public void stop()
    {
        keepGoing = false;
    }

    MojangLogo mojangLogo = new MojangLogo();
    boolean noLogo = false;

    public void update(Graphics gr)
    {
        paint(gr);
    }

    public void paint(Graphics gr)
    {
        if (noLogo) return;

        if (image == null || image.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
        {
            image = createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
        }

        if (image != null)
        {
            Graphics2D g = image.createGraphics();
            mojangLogo.render(g);
            g.dispose();
        }

        gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
    }

    private void setup()
    {
        long before = System.currentTimeMillis();
        Text.init();
        repaint();

        GameStarterThread gst = new GameStarterThread();
        gst.start();

        titleImage = new TitleBuilder().buildTitleImage();
        long after = System.currentTimeMillis();

        try
        {
            int toSleep = (int) (MOJANG_SPLASH_TIME - (after - before));
            if (toSleep > 0) Thread.sleep(toSleep);
        }
        catch (InterruptedException e2)
        {
        }

        noLogo = true;
        while (!gst.isDone())
        {
            if (image == null || image.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                image = createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
            }

            if (image != null)
            {
                Graphics2D g = image.createGraphics();
                g.drawImage(titleImage, 0, 0, null);
                g.setColor(new Color(0.1f, 0.1f, 0.2f, 0.8f));
                g.fillRect(0, SCREEN_HEIGHT - 10, SCREEN_WIDTH, 10);
                Text.drawString(currentStatus, g, 2, SCREEN_HEIGHT - 7);
                g.setColor(new Color(1, 1, 1, 1.0f));
                g.dispose();

                Graphics gr = bufferStrategy.getDrawGraphics();
                gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

                gr.dispose();
            }

            bufferStrategy.show();

            try
            {
                Thread.sleep(20);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            gst.join();
        }
        catch (InterruptedException e1)
        {
        }
    }

    public void run()
    {
        setup();

        inputHandler = new InputHandler(gameComponent, SCALE);
        addMouseMotionListener(inputHandler);
        addMouseListener(inputHandler);
        addKeyListener(inputHandler);
        Graphics2D g = null;
        if (image != null)
        {
            g = image.createGraphics();
        }

        while (keepGoing)
        {
            if (image == null || image.validate(getGraphicsConfiguration()) == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                if (g != null)
                {
                    g.dispose();
                    g = null;
                }

                image = createVolatileImage(SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE);
                g = image.createGraphics();
            }

            synchronized (inputHandler.lock)
            {
                world.hoveredComponent = gameComponent.getComponentAt(inputHandler.xMouse, inputHandler.yMouse);
                float alpha = updateTime(); // This will call tick();
                world.render(alpha);
                gameComponent.renderAll(g, alpha);

                g.drawImage(cursor.image, inputHandler.xMouse, inputHandler.yMouse, null);
            }

            Graphics gr = bufferStrategy.getDrawGraphics();
            gr.drawImage(image, 0, 0, SCREEN_WIDTH * SCALE, SCREEN_HEIGHT * SCALE, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
            gr.dispose();
            bufferStrategy.show();

            try
            {
                Thread.sleep(2);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private float updateTime()
    {
        boolean upKey = inputHandler.keys[KeyEvent.VK_UP] || inputHandler.keys[KeyEvent.VK_NUMPAD8];
        boolean downKey = inputHandler.keys[KeyEvent.VK_DOWN] || inputHandler.keys[KeyEvent.VK_NUMPAD2];
        boolean leftKey = inputHandler.keys[KeyEvent.VK_LEFT] || inputHandler.keys[KeyEvent.VK_NUMPAD4];
        boolean rightKey = inputHandler.keys[KeyEvent.VK_RIGHT] || inputHandler.keys[KeyEvent.VK_NUMPAD6];

        int ticks = timer.advanceTime();
        if (ticks > MAX_TICKS_PER_FRAME) ticks = MAX_TICKS_PER_FRAME;
        for (int i = 0; i < ticks; i++)
        {
            world.tick();
            world.moveCamera(upKey, downKey, leftKey, rightKey);
            gameComponent.tickAll();
        }

        return timer.alpha;
    }

    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Takns");
        Takns takns = new Takns();
        frame.add(takns);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        takns.init();
        takns.start();
    }
}