package nocb.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JWindow;

/**
 * This class was started from
 * https://www.geeksforgeeks.org/java/java-swing-creating-toast-message/ then
 * heavily modified and rewritten for this use case. Still want to give credit
 * for the starting point.
 */
public class Toast implements Runnable
{
	private final JWindow win;

	public Toast(String text, int offset)
	{
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();

		win = new JWindow();

		win.setBackground(new Color(0, 0, 0, 0));
		win.add(new ToastPanel(text));
		win.setLocation(screensize.width - 300, screensize.height - 100 - offset);
		win.setSize(300, 100);

		new Thread(this).start();
	}

	@Override
	public void run()
	{
		try
		{
			win.setOpacity(1);
			win.setVisible(true);

			// wait for some time
			Thread.sleep(2000);

			// make the message disappear slowly
			for (double d = 1.0; d > 0.2; d -= 0.1)
			{
				Thread.sleep(100);
				win.setOpacity((float) d);
			}

			// set the visibility to false
			win.setVisible(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private class ToastPanel extends JPanel
	{
		private static final long serialVersionUID = 8946647606850709510L;

		private final String text;

		public ToastPanel(String text)
		{
			this.text = text;
		}

		@Override
		public void paintComponent(Graphics g)
		{
			int wid = g.getFontMetrics().stringWidth(text);
			int hei = g.getFontMetrics().getHeight();

			// draw the boundary of the toast and fill it
			g.setColor(Color.black);
			g.fillRect(10, 10, wid + 30, hei + 10);
			g.setColor(Color.black);
			g.drawRect(10, 10, wid + 30, hei + 10);

			// set the color of text
			g.setColor(new Color(255, 255, 255, 240));
			g.drawString(text, 25, 27);
			int t = 250;

			// draw the shadow of the toast
			for (int i = 0; i < 4; i++)
			{
				t -= 60;
				g.setColor(new Color(0, 0, 0, t));
				g.drawRect(10 - i, 10 - i, wid + 30 + i * 2, hei + 10 + i * 2);
			}
		}
	}
}
