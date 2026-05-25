package com.mojang.takns.gui;

import java.awt.Color;
import java.awt.Graphics2D;

import com.mojang.takns.Takns;

public class HudComponent extends UiComponent
{
    public void render(Graphics2D g, float alpha)
    {
        Text.drawString("Money §3"+world.playerSide.money, g, 2, 2);
        
        if (world.hoveredComponent!=null)
        {
            String toolTip = world.hoveredComponent.getToolTip();
            
            if (toolTip!=null && toolTip!="")
            {
                g.setColor(new Color(0.1f, 0.1f, 0.2f, 0.8f));
                g.fillRect(0, Takns.SCREEN_HEIGHT - 10, world.gameView.width, 10);
                g.setColor(new Color(1, 1, 1, 1.0f));
                
                Text.drawString(toolTip, g, (world.gameView.width-toolTip.length()*6)/2, Takns.SCREEN_HEIGHT - 7);
            }
        }
    }
}