package ncb.ui.tabs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import ncb.data.loadables.CharacterClass;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.ClassFeaturesPanel;
import ncb.ui.subpanels.ClassTraitsPanel;

public class ClassPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> classNameSelector;
	private final JLabel classLevelLabel;
	private final ClassTraitsPanel traitsPanel;
	private final ClassFeaturesPanel featuresPanel;

	public ClassPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);

		GridBagConstraints c = UILib.getStandardGBC();

		classNameSelector = UILib.getComboBox(
				CharacterClass.getAllClasses().stream().map(cls -> cls.getName()).toList().toArray(new String[0]), this,
				VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Class: ", classNameSelector, c, VaporwaveColors.HOT_PINK);
		c.gridx++;
		c.weightx = 0;
		classLevelLabel = UILib.addLabel(this, "Level 1", c, VaporwaveColors.ELECTRIC_TEAL);
		classLevelLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
		classLevelLabel.setFont(UILib.boldFont);
		c.gridx = 0;
		c.gridy++;
		c.weightx = 1;

		c.weighty = 1;
		c.gridwidth = 2;
		traitsPanel = new ClassTraitsPanel(sheet);
		featuresPanel = new ClassFeaturesPanel(sheet);
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

		classNameSelector.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(classNameSelector))
		{
			CharacterClass selectedClass = CharacterClass.getByName((String) classNameSelector.getSelectedItem());
			sheet.setCharClass(selectedClass);
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		if (prop.equals(PropertyListener.CLASS))
		{
			classNameSelector.setSelectedItem(sheet.getCharClass().getName());
		}
		else if (prop.equals(PropertyListener.CLASSLEVEL))
		{
			classLevelLabel.setText("Level " + sheet.getCharClass().getLevel());
		}
	}
}
