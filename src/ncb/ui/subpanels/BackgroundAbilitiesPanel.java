package ncb.ui.subpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.enums.Ability;
import ncb.data.loadables.Background;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundAbilitiesPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Ability> ability1, ability2, ability3;
	private final JLabel label;

	public BackgroundAbilitiesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);

		label = UILib.addLabel(this, "Ability Options: ", VaporwaveColors.HOT_PINK);
		label.setFont(UILib.boldFont);

		ability1 = UILib.getComboBox(Ability.realValues(), this, VaporwaveColors.DEEP_VIOLET,
				VaporwaveColors.LASER_YELLOW);
		add(ability1);
		ability2 = UILib.getComboBox(Ability.realValues(), this, VaporwaveColors.DEEP_VIOLET,
				VaporwaveColors.LASER_YELLOW);
		add(ability2);
		ability3 = UILib.getComboBox(Ability.realValues(), this, VaporwaveColors.DEEP_VIOLET,
				VaporwaveColors.LASER_YELLOW);
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

	@Override
	public void updateProperty(String prop)
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
				label.setText("Ability Options: ");
			}
			else
			{
				ability1.setVisible(false);
				ability2.setVisible(false);
				ability3.setVisible(false);
				label.setText("Ability Options: "
						+ String.join(", ", bg.getAbilityOptions().stream().map(s -> s.toString()).toList()));
			}
		}
	}
}
