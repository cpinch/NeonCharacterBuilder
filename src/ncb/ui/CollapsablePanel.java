package ncb.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

public class CollapsablePanel extends JPanel implements NoHorizontalScroll
{
	private static final long serialVersionUID = 2652970109248517759L;

	public final JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	public final JPanel bodyPanel = new NoHorizontalScrollPanel();

	private final JButton toggleBtn = new JButton("▼");
	private boolean collapsed = false;

	public CollapsablePanel(boolean startCollapsed)
	{
		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEtchedBorder());
		setOpaque(false);

		JPanel headerArea = new JPanel();
		headerArea.setOpaque(false);
		headerArea.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		headerPanel.setOpaque(false);
		headerArea.add(headerPanel, c);
		c.gridx++;
		c.weightx = 0;
		toggleBtn.setFocusPainted(false);
		headerArea.add(toggleBtn);

		bodyPanel.setOpaque(false);
		bodyPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		c.gridx = 0;
		c.weightx = 1;
		add(headerArea, c);
		c.weighty = 1;
		c.gridy++;
		add(bodyPanel, c);

		setToggleState(startCollapsed);

		toggleBtn.setBackground(VaporwaveColors.NEON_BLUE);
		toggleBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				setToggleState(!collapsed);
			}
		});

		headerArea.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				setToggleState(!collapsed);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
			}
		});
	}

	private void setToggleState(boolean collapsed)
	{
		this.collapsed = collapsed;
		toggleBtn.setText(collapsed ? "▼" : "▲");
		bodyPanel.setVisible(!collapsed);
	}
}
