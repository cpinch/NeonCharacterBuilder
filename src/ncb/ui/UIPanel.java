package ncb.ui;

import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public abstract class UIPanel extends JPanel
{
	private static final long serialVersionUID = 3017876921635882523L;

	public UIPanel()
	{
		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
	}
}
