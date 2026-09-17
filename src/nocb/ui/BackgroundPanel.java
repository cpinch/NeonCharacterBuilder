package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.Background;
import nocb.main.CharacterSheet;

public class BackgroundPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Background> backgroundName;

	private final BackgroundSkillsPanel skills;
	private final BackgroundToolsPanel tools;
	private final BackgroundVehiclesPanel vehicles;
	private final BackgroundLangsPanel langs;
	private final BackgroundAbilitiesPanel abilities;
	private final BackgroundHomeworldPanel homeworld;
	private final BackgroundFeatPanel feat;

	private final JLabel notes = UILib.getLabel("0");

	public BackgroundPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		c.gridwidth = 2;
		backgroundName = new JComboBox<>(Background.getAllBackgrounds().toArray(new Background[0]));
		backgroundName.addItem(Background.getCustom());
		backgroundName.addActionListener(this);
		backgroundName.setBackground(VaporwaveColors.DEEP_VIOLET);
		backgroundName.setForeground(VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Select Background: ", backgroundName, c);
		c.gridy++;

		skills = new BackgroundSkillsPanel(sheet);
		add(skills, c);
		c.gridy++;
		c.gridwidth = 1;

		tools = new BackgroundToolsPanel(sheet);
		add(tools, c);
		c.gridx++;

		vehicles = new BackgroundVehiclesPanel(sheet);
		add(vehicles, c);
		c.gridx = 0;
		c.gridy++;

		c.gridwidth = 2;
		langs = new BackgroundLangsPanel(sheet);
		add(langs, c);
		c.gridy++;

		abilities = new BackgroundAbilitiesPanel(sheet);
		add(abilities, c);
		c.gridy++;

		UILib.addLabeledComponent(this, "Notes: ", notes, c);
		c.gridy++;

		c.gridwidth = 1;
		c.weighty = 1;

		c.weightx = 0.5;
		homeworld = new BackgroundHomeworldPanel(sheet);
		add(homeworld, c);
		c.gridx++;

		c.weightx = 1;
		feat = new BackgroundFeatPanel(sheet);
		add(feat, c);

		homeworld.setUpdateCallback(() -> feat.updateDetails());
		backgroundName.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(backgroundName))
		{
			Background selectedBackground = (Background) backgroundName.getSelectedItem();
			sheet.setBackground(selectedBackground);

			notes.setText("" + selectedBackground.getNotes());
			skills.updateDetails();
			tools.updateDetails();
			vehicles.updateDetails();
			langs.updateDetails();
			abilities.updateDetails();
			homeworld.updateDetails();
			feat.updateDetails();
		}
	}

	public void updateDetails()
	{
		backgroundName.setSelectedItem(sheet.getBackground().getName());
		skills.updateDetails();
		tools.updateDetails();
		vehicles.updateDetails();
		langs.updateDetails();
		abilities.updateDetails();
		homeworld.updateDetails();
		feat.updateDetails();
	}
}
