package ncb.ui.tabs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ncb.data.loadables.Background;
import ncb.main.CharacterSheet;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.BackgroundAbilitiesPanel;
import ncb.ui.subpanels.BackgroundFeatPanel;
import ncb.ui.subpanels.BackgroundHomeworldPanel;
import ncb.ui.subpanels.BackgroundLangsPanel;
import ncb.ui.subpanels.BackgroundSkillsPanel;
import ncb.ui.subpanels.BackgroundToolsPanel;
import ncb.ui.subpanels.BackgroundVehiclesPanel;

public class BackgroundPanel extends UIPanel implements ActionListener
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

	private final JLabel notes = UILib.getLabel("0", VaporwaveColors.HOT_PINK);

	public BackgroundPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		GridBagConstraints c = UILib.getStandardGBC();

		c.gridwidth = 2;
		List<Background> allBg = Background.getAllBackgrounds();
		allBg.add(Background.getCustom()); // TODO Background Loading
		backgroundName = UILib.getComboBox(allBg.toArray(new Background[0]), this, VaporwaveColors.DEEP_VIOLET,
				VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Background: ", backgroundName, c, VaporwaveColors.HOT_PINK);
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

		UILib.addLabeledComponent(this, "Notes: ", notes, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.gridwidth = 1;
		c.weighty = 1;
		c.weightx = 0.3;
		homeworld = new BackgroundHomeworldPanel(sheet);
		add(homeworld, c);
		c.gridx++;
		c.weightx = 1;
		feat = new BackgroundFeatPanel(sheet);
		add(feat, c);

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
		}
	}
}
