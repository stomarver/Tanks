package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;

import com.mojang.takns.Side;
import com.mojang.takns.World;
import com.mojang.takns.gui.buttons.ButtonType;
import com.mojang.takns.synth.*;
import com.mojang.takns.units.Unit;

public class PanelComponent extends UiComponent
{
    private BufferedImage image;
    private Side side;
    private UiComponent buttonHolder;

    public void init(World world, int x, int y, int width, int height)
    {
        super.init(world, x, y, width, height);
        side = world.playerSide;

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Synth distortion = new PerlinNoise(5);
        Synth source = new PerlinNoise(5);

        Synth synth = new Emboss(new Distort(distortion, source));

        double[] noise = synth.create(width, height);

        int[] pixels = new int[width * height];

        for (int yy = 0; yy < height; yy++)
        {
            for (int xx = 0; xx < width; xx++)
            {
                double n0 = noise[xx + yy * width];
                int r = (int) (98 + n0 * 3);
                int g = (int) (98 + n0 * 3);
                int b = (int) (98 + n0 * 3);
                
                if (xx == 0)
                {
                    r/=3;
                    g/=3;
                    b/=3;
                }
                pixels[xx + yy * width] = (255 << 24) | (r << 16) | (g << 8) | b;
            }
        }
        
        image.setRGB(0, 0, width, height, pixels, 0, width);

        MinimapComponent minimap = new MinimapComponent(side);
        minimap.init(world, x + 4, y + 4, 64, 64);
        addComponent(minimap);

        buttonHolder = new UiComponent();
        buttonHolder.init(world, x, y + 64, width, height - 64);
        addComponent(buttonHolder);
    }

    Unit lastSelectedUnit = null;

    public void render(Graphics2D g, float alpha)
    {
        g.drawImage(image, x, y, null);

        List<Unit> selectedUnits = side.units.selectedUnits;

        if (selectedUnits.size() == 1)
        {
            Unit unit = selectedUnits.get(0);
            if (unit != lastSelectedUnit)
            {
                selectUnit(unit);
            }
            fillBlock(g, x + width / 2 - 32 + 8, y + 64 + 8 + 32 - 32 + 2, 48, 48);
            renderSingleSelectedUnit(g, alpha, unit);
        }
        else
        {
            if (lastSelectedUnit != null)
            {
                selectUnit(null);
            }

            if (selectedUnits.size() > 1)
            {
                renderSelectedUnits(g, alpha, selectedUnits);
            }
        }
    }

    private void selectUnit(Unit unit)
    {
        lastSelectedUnit = unit;
        world.gameView.setState(null);
        buttonHolder.removeAllComponents();
        if (unit == null) return;

        ButtonType[] buttons = unit.getButtons();
        if (buttons != null)
        {
            for (int i = 0; i < buttons.length; i++)
            {
                int xx = i % 2;
                int yy = i / 2;

                BuildButton buildButton = new BuildButton(buttons[i]);
                buildButton.init(world, x + 4 + 8 - 6 + xx * 32, y + 130 + yy * 26, 28, 24);
                buttonHolder.addComponent(buildButton);
            }
        }
    }

    private void fillBlock(Graphics2D g, int x, int y, int w, int h)
    {
        g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.7f));
        g.fillRect(x, y, w + 1, h + 1);
        g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.7f));
        g.fillRect(x - 1, y - 1, w + 1, h + 1);
    }

    private void renderSingleSelectedUnit(Graphics2D g, float alpha, Unit unit)
    {
        unit.renderImageTo(g, x + width / 2, y + 64 + 8 + 24);
        Text.drawString(unit.getName(), g, x + (width - unit.getName().length() * 6) / 2, y + 64 + 50);
    }

    private void renderSelectedUnits(Graphics2D g, float alpha, List<Unit> selectedUnits)
    {
        int p = 0;
        int y = 0;

        int columns = 2;
        if (selectedUnits.size() > 2 * 2) columns = 3;
        if (selectedUnits.size() > 3 * 4) columns = 4;
        int columnSize = 64 / columns;
        int rows = (selectedUnits.size() - 1) / columns;
        int rowSize = (48 + 16) * 4 / (rows + 8);

        fillBlock(g, x + width / 2 - 32, y + 64 + 8 + 32 - 32 + 2, 64, rows * rowSize + rowSize + 16);

        while (p < selectedUnits.size())
        {
            int rowLength = selectedUnits.size() - p;
            if (rowLength > columns) rowLength = columns;
            for (int x = 0; x < rowLength; x++)
            {
                Unit unit = selectedUnits.get(p++);
                unit.renderImageTo(g, this.x + width / 2 - (rowLength - 1) * columnSize / 2 + x * columnSize, this.y + 64 + 8 + rowSize / 2 + 8 + y * rowSize + 2);
            }
            y++;
        }
    }
}