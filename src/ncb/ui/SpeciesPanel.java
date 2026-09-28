package ncb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncb.data.loadables.Species;
import ncb.main.CharacterSheet;

public class SpeciesPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Species> speciesName;
	private final SpeciesTraitsPanel traitsPanel;
	private final SpeciesFeaturesPanel featuresPanel;

	public SpeciesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		speciesName = new JComboBox<>(Species.getAllSpecies().toArray(new Species[0]));
		speciesName.addActionListener(this);
		speciesName.setBackground(VaporwaveColors.DEEP_VIOLET);
		speciesName.setForeground(VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Select Species: ", speciesName, c);
		c.gridy++;
		c.weighty = 1;

		JPanel splitPane = new JPanel(new GridLayout(1, 2));
		traitsPanel = new SpeciesTraitsPanel(sheet);
		splitPane.add(traitsPanel);

		featuresPanel = new SpeciesFeaturesPanel(sheet);
		JScrollPane scroll = new JScrollPane(featuresPanel);
		scroll.getViewport().setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.add(scroll);

		add(splitPane, c);

		speciesName.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(speciesName))
		{
			Species selectedSpecies = (Species) speciesName.getSelectedItem();
			sheet.setSpecies(selectedSpecies);

			traitsPanel.updateDetails();
			featuresPanel.updateDetails();
		}
	}

	public void updateDetails()
	{
		speciesName.setSelectedItem(sheet.getSpecies());
	}
}
