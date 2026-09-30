package ncb.ui.tabs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import ncb.data.loadables.Species;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.SpeciesFeaturesPanel;
import ncb.ui.subpanels.SpeciesTraitsPanel;

public class SpeciesPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> speciesName;
	private final SpeciesTraitsPanel traitsPanel;
	private final SpeciesFeaturesPanel featuresPanel;

	public SpeciesPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		GridBagConstraints c = UILib.getStandardGBC();
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);

		speciesName = UILib.getComboBox(
				Species.getAllSpecies().stream().map(s -> s.getName()).toList().toArray(new String[0]), this,
				VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Select Species: ", speciesName, c, VaporwaveColors.HOT_PINK);
		c.gridy++;
		c.weighty = 1;

		traitsPanel = new SpeciesTraitsPanel(sheet);

		featuresPanel = new SpeciesFeaturesPanel(sheet);
		JScrollPane scroll = UILib.getScrollPaneFor(featuresPanel);

		// Allow the splitpane to shrink these however the user wants
		traitsPanel.setMinimumSize(new Dimension(0, 0));
		featuresPanel.setMinimumSize(new Dimension(0, 0));
		scroll.setMinimumSize(new Dimension(0, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, traitsPanel, scroll);
		splitPane.setDividerLocation(0.35);
		splitPane.setResizeWeight(0.35);
		splitPane.setOpaque(false);
		add(splitPane, c);

		speciesName.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(speciesName))
		{
			Species selectedSpecies = Species.getByName((String) speciesName.getSelectedItem());
			sheet.setSpecies(selectedSpecies);
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		speciesName.setSelectedItem(sheet.getSpecies().getName());
	}
}
