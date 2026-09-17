package nocb.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import javax.swing.border.BevelBorder;

public class CollapsablePanel extends JPanel
{
	private static final long serialVersionUID = 2652970109248517759L;

	protected final JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	protected final JPanel bodyPanel = new JPanel();

	private final JButton toggleBtn = new JButton();
	private boolean collapsed = false;

	public CollapsablePanel(boolean startCollapsed)
	{
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, VaporwaveColors.NEON_BLUE,
				VaporwaveColors.NEON_BLUE));
		setBackground(VaporwaveColors.DARK_PURPLE);
		setAlignmentX(Component.LEFT_ALIGNMENT);

		JPanel headerArea = new JPanel();
		headerArea.setOpaque(false);
		headerArea.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();
		c.anchor = GridBagConstraints.WEST;

		headerPanel.setOpaque(false);
		headerArea.add(headerPanel, c);
		c.gridx++;
		c.weightx = 0;
		headerArea.add(toggleBtn);

		bodyPanel.setOpaque(false);
		bodyPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		add(headerArea, BorderLayout.PAGE_START);
		add(bodyPanel, BorderLayout.CENTER);

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
		toggleBtn.setText(collapsed ? "+" : "-");
		bodyPanel.setVisible(!collapsed);
		revalidate();
	}

	// To avoid growing while minimized
	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(Integer.MAX_VALUE, (int) getPreferredSize().getHeight());
	}
}
