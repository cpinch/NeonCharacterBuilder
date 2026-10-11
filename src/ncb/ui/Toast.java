package ncb.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class Toast extends JWindow implements Runnable
{
	private static final long serialVersionUID = -797652304202788608L;
	public static final Point SLOT_1 = new Point(20, 160), SLOT_2 = new Point(20, 90), SLOT_3 = new Point(20, 20);
	private static final int DISPLAYTIME = 3000, FADEOUTTIME = 100;

	private float opacity = 0.0f;

	public Toast(String text, Point slot)
	{
		setAlwaysOnTop(true);
		setOpacity(opacity);
		setBackground(new Color(0, 0, 0, 0)); // Transparent background

		JPanel toastPanel = new JPanel()
		{
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(new Color(50, 50, 50, 230)); // Dark grey with slight transparency
				g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
				g2.dispose();
				super.paintComponent(g);

			}
		};
		toastPanel.setOpaque(false);
		toastPanel.setLayout(new GridBagLayout());

		JLabel textLabel = new JLabel(text);
		textLabel.setForeground(Color.white);
		textLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		toastPanel.add(textLabel);

		add(toastPanel);
		pack();

		Rectangle screenBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		int x = screenBounds.x + screenBounds.width - getWidth() - slot.x;
		int y = screenBounds.y + screenBounds.height - getHeight() - slot.y;
		setLocation(x, y);

		new Thread(this).start();
	}

	@Override
	public void run()
	{
		try
		{
			setOpacity(1);
			setVisible(true);

			Thread.sleep(DISPLAYTIME);

			// make the message disappear slowly
			for (double d = 1.0; d > 0.2; d -= 0.1)
			{
				Thread.sleep(FADEOUTTIME);
				setOpacity((float) d);
			}

			setVisible(false);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
