package com.mojang.takns;

import java.awt.BorderLayout;
import javax.swing.JApplet;

public class TaknsApplet extends JApplet
{
    private static final long serialVersionUID = 7434887834385440604L;
    
	private Takns takns = new Takns();

    public void init()
    {
        super.init();
        this.setLayout(new BorderLayout());
        this.add(takns, BorderLayout.CENTER);
        takns.init();
        takns.start();
    }

    public void start()
    {
        super.start();
    }

    public void destroy()
    {
        takns.stop();
        super.destroy();
    }
}