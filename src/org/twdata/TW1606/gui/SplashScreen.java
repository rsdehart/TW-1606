/*
 * SplashScreen.java - Splash screen
 * Copyright (C) 1998, 2004 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.twdata.TW1606.gui;

import javax.swing.*;
import java.awt.*;

/**
 * The splash screen displayed on startup.
 */
public class SplashScreen extends JComponent
{
    private String message = "";
    private static final Color BG_COLOR = new Color(128, 128, 255);

	public SplashScreen()
	{
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

		Font font = new Font("Dialog",Font.PLAIN,14);
		setFont(font);
		fm = getFontMetrics(font);

        Class cl = this.getClass();
		ImageIcon icon = new ImageIcon(cl.getResource("/icons/1606-splash.png"));
        image = icon.getImage();
        
        setBackground(BG_COLOR);
        /*image = getToolkit().getImage(
			getClass().getResource("/icons/tw1606-splash.png"));
        System.out.println("image:"+image+" resource:"+getClass().getResource("/icons/tw1606-splash.png"));
		MediaTracker tracker = new MediaTracker(this);
		tracker.addImage(image,0);

		try
		{
			tracker.waitForAll();
		}
		catch(Exception e)
		{
			System.err.println("Unable to wait for image");
            e.printStackTrace();
		}
        */
		win = new JWindow();

		Dimension screen = getToolkit().getScreenSize();
		Dimension size = new Dimension(image.getWidth(this) + 2,
			image.getHeight(this) + 2 + PROGRESS_HEIGHT);
		win.setSize(size);

		win.getContentPane().add(BorderLayout.CENTER,this);

		win.setLocation((screen.width - size.width) / 2,
			(screen.height - size.height) / 2);
		win.validate();
		win.setVisible(true);

		/*synchronized(this)
		{
			try
			{
				wait();
			}
			catch(InterruptedException ie)
			{
				Log.log(Log.ERROR,this,ie);
			}
		}*/
	}

	public void dispose()
	{
		win.dispose();
        fm = null;
        image = null;
        win = null;
	}

	public synchronized void advance(String msg)
	{
        message = msg;
		progress++;
		repaint();

		// wait for it to be painted to ensure progress is updated
		// continuously
		try
		{
			wait();
		}
		catch(InterruptedException ie)
		{
			//Log.log(Log.ERROR,this,ie);
            ie.printStackTrace();
		}
	}

	public synchronized void paintComponent(Graphics g)
	{
		Dimension size = getSize();

		g.setColor(Color.darkGray);
		g.drawRect(0,0,size.width - 1,size.height - 1);

		g.drawImage(image,1,1,this);

		// XXX: This should not be hardcoded
        int div = (win.getWidth() - 2) * progress / 5;
        
		g.setColor(Color.lightGray);
		g.fillRect(1,image.getHeight(this)+1,
			div,PROGRESS_HEIGHT);
        g.setColor(Color.white);    
        g.fillRect(div,image.getHeight(this)+1,
			(win.getWidth()-2)-div, PROGRESS_HEIGHT);    

		g.setColor(Color.black);

		g.drawString(message,
			(getWidth() - fm.stringWidth(message)) / 2,
			image.getHeight(this) + (PROGRESS_HEIGHT
			+ fm.getAscent() + fm.getDescent()) / 2-2);

		notifyAll();
	}

	// private members
	private FontMetrics fm;
	private JWindow win;
	private Image image;
	private int progress;
	private static final int PROGRESS_HEIGHT = 20;
}
