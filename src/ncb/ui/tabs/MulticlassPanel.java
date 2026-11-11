package ncb.ui.tabs;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.enums.Ability;
import ncb.data.loadables.CharacterClass;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.subpanels.ClassFeaturesPanel;
import ncb.ui.subpanels.MulticlassTraitsPanel;

public class MulticlassPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;
	private final int classIndex;

	private final JComboBox<String> classNameSelector;
	private final JLabel classLevelLabel;
	private final MulticlassTraitsPanel traitsPanel;
	private final ClassFeaturesPanel featuresPanel;

	public MulticlassPanel(CharacterSheet sheet, int multiclassIndex)
	{
		this.sheet = sheet;
		this.classIndex = multiclassIndex;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);

		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(5, 15, 5, 5);

		classNameSelector = UILib.getComboBox(new String[0], this);
		UILib.addLabeledComponent(this, "Multiclass: ", classNameSelector, c);
		c.gridx++;
		c.weightx = 0;
		classLevelLabel = UILib.addLabel(this, "Level 1", c);
		classLevelLabel.putClientProperty(FlatClientProperties.STYLE, "foreground: $suggestion");
		classLevelLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		c.gridx = 0;
		c.gridy++;
		c.weightx = 1;

		c.weighty = 1;
		c.gridwidth = 2;
		traitsPanel = new MulticlassTraitsPanel(sheet, multiclassIndex);
		featuresPanel = new ClassFeaturesPanel(sheet, multiclassIndex);
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

		updateProperty(PropertyListener.CLASS);
		traitsPanel.updateProperty(PropertyListener.CLASS);
		featuresPanel.updateProperty(PropertyListener.CLASS);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(classNameSelector))
		{
			CharacterClass selectedClass = CharacterClass.getByName((String) classNameSelector.getSelectedItem());
			sheet.replaceMulticlass(classIndex, selectedClass);
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		if (prop.equals(PropertyListener.CLASS))
		{
			classNameSelector.removeActionListener(this);
			classNameSelector.removeAllItems();
			// Set the dropdown to have all valid multiclass options that aren't selected in
			// a different multiclass OR the base class
			List<String> otherSelectedClasses = new ArrayList<>();
			otherSelectedClasses.add(sheet.getCharClass().getName());
			for (int i = 0; i < sheet.getMulticlasses().size(); i++)
			{
				if (i != classIndex)
				{
					otherSelectedClasses.add(sheet.getMulticlasses().get(i).getName());
				}
			}
			Map<Ability, Integer> abilityScores = sheet.getTotalAbilityScores();
			for (CharacterClass cls : CharacterClass.getAllClasses())
			{
				if (!otherSelectedClasses.contains(cls.getName()) && cls.multiclassPrereqsMet(abilityScores))
				{
					classNameSelector.addItem(cls.getName());
				}
			}
			classNameSelector.setSelectedItem(getAssociatedClass().getName());
			classNameSelector.addActionListener(this);
		}
		else if (prop.equals(PropertyListener.CLASSLEVEL))
		{
			classLevelLabel.setText("Level " + getAssociatedClass().getLevel());
		}
	}

	private CharacterClass getAssociatedClass()
	{
		return sheet.getMulticlasses().get(classIndex);
	}
}
