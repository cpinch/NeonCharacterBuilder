package nocb.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.Ability;
import nocb.data.Background;
import nocb.main.CharacterSheet;

public class BackgroundAbilitiesPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Ability> ability1, ability2, ability3;
	private final JLabel label = new JLabel();

	public BackgroundAbilitiesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);

		UILib.addLabel(this, "Ability Options: ");

		add(label);

		ability1 = new JComboBox<>(Ability.realValues());
		ability1.addActionListener(this);
		ability1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		ability1.setBackground(VaporwaveColors.DEEP_VIOLET);
		ability1.setForeground(VaporwaveColors.LASER_YELLOW);
		add(ability1);
		ability2 = new JComboBox<>(Ability.realValues());
		ability2.addActionListener(this);
		ability2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		ability2.setBackground(VaporwaveColors.DEEP_VIOLET);
		ability2.setForeground(VaporwaveColors.LASER_YELLOW);
		add(ability2);
		ability3 = new JComboBox<>(Ability.realValues());
		ability3.addActionListener(this);
		ability3.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		ability3.setBackground(VaporwaveColors.DEEP_VIOLET);
		ability3.setForeground(VaporwaveColors.LASER_YELLOW);
		add(ability3);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (e.getSource().equals(ability1) || e.getSource().equals(ability2) || e.getSource().equals(ability3))
			{
				bg.setAbilityOptions(List.of((Ability) ability1.getSelectedItem(), (Ability) ability2.getSelectedItem(),
						(Ability) ability3.getSelectedItem()));

			}
		}
	}

	public void updateDetails()
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				List<Ability> options = new ArrayList<>(bg.getAbilityOptions());
				ability1.setVisible(true);
				ability1.setSelectedItem(options.get(0));
				ability2.setVisible(true);
				ability2.setSelectedItem(options.get(1));
				ability3.setVisible(true);
				ability3.setSelectedItem(options.get(2));
				label.setVisible(false);
			}
			else
			{
				ability1.setVisible(false);
				ability2.setVisible(false);
				ability3.setVisible(false);
				label.setVisible(true);
				label.setText(String.join(", ", bg.getAbilityOptions().stream().map(s -> s.toString()).toList()));
			}
		}
	}
}
