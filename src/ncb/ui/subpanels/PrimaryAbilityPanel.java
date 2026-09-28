package ncb.ui.subpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.enums.Ability;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class PrimaryAbilityPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -1513890001811947077L;

	private final CharacterSheet sheet;

	private final JComboBox<Ability> selector;
	private final JLabel label;

	public PrimaryAbilityPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);

		label = UILib.addLabel(this, "Primary Ability:", VaporwaveColors.HOT_PINK);

		selector = UILib.getComboBox(new Ability[0], this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(selector);
	}

	@Override
	public void updateProperty(String prop)
	{
		if (sheet.getCharClass() != null)
		{
			if (!sheet.getCharClass().getPrimaryAbilityOptions().isEmpty())
			{
				selector.setVisible(true);
				label.setText("Primary Ability: ");
				selector.removeAllItems();
				sheet.getCharClass().getPrimaryAbilityOptions().forEach(a -> selector.addItem(a));
			}
			else
			{
				selector.setVisible(false);
				label.setText("Primary Ability: " + String.join(" and ",
						sheet.getCharClass().getPrimaryAbilities().stream().map(a -> a.name()).toList()));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(selector))
		{
			Ability selected = (Ability) selector.getSelectedItem();
			if (selected != null)
			{
				sheet.getCharClass().setSelectedPrimary(selected);
			}
		}
	}
}
