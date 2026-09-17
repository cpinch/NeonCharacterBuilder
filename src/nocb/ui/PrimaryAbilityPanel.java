package nocb.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.Ability;
import nocb.main.CharacterSheet;

public class PrimaryAbilityPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -1513890001811947077L;

	private final CharacterSheet sheet;

	private final JComboBox<Ability> selector = new JComboBox<>();
	private final JLabel label = UILib.getLabel("");

	public PrimaryAbilityPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
		setBackground(VaporwaveColors.DARK_PURPLE);

		JLabel l = UILib.addLabel(this, "Primary Ability:");
		l.setFont(UILib.boldFont);
		l.setForeground(VaporwaveColors.HOT_PINK);

		selector.addActionListener(this);
		selector.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		selector.setAlignmentX(Component.LEFT_ALIGNMENT);
		selector.setBackground(VaporwaveColors.DEEP_VIOLET);
		selector.setForeground(VaporwaveColors.LASER_YELLOW);
		add(selector);

		label.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 10));
		label.setFont(UILib.boldFont);
		add(label);
	}

	public void updateDetails()
	{
		if (sheet.getCharClass() != null)
		{
			if (sheet.getCharClass().hasSelectablePrimary())
			{
				selector.setVisible(true);
				label.setVisible(false);
				selector.removeAllItems();
				sheet.getCharClass().getPrimaryAbilityOptions().forEach(a -> selector.addItem(a));
			}
			else
			{
				selector.setVisible(false);
				label.setVisible(true);
				label.setText(String.join(" and ",
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
				sheet.getCharClass().selectPrimary(selected);
			}
		}
	}
}
