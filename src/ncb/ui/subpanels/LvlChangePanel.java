package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import ncb.main.CharacterSheet;
import ncb.ui.CollapsablePanel;
import ncb.ui.HasAssociatedClass;
import ncb.ui.UILib;

public class LvlChangePanel extends CollapsablePanel implements HasAssociatedClass
{
	private static final long serialVersionUID = -4522031612337864604L;

	private final CharacterSheet sheet;
	private final int classIndex;

	public LvlChangePanel(CharacterSheet sheet, int multiclassIndex, int clsLvl, int charLvl, boolean startCollapsed)
	{
		super(startCollapsed);

		this.sheet = sheet;
		this.classIndex = multiclassIndex;

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		UILib.addLabel(headerPanel, "Level " + charLvl + " - " + getAssociatedClass().getName(), c);
		c.gridy++;

		List<String> changes = new ArrayList<>();

		List<String> updatedFeatures = getAssociatedClass().getFeatureNamesAtLevel(clsLvl);
		changes.addAll(updatedFeatures.stream().map(fn -> "Class Feature: " + fn).toList());
		if (getAssociatedClass().getSubclass() != null)
		{
			changes.addAll(getAssociatedClass().getSubclass().getFeatureNamesAtLevel(clsLvl).stream()
					.map(fb -> "Sublass Feature: " + fb).toList());
		}
		changes.addAll(
				sheet.getSpecies().getTraitNamesAtLevel(charLvl).stream().map(fn -> "Species Trait: " + fn).toList());

		// For simplicity we just assume that, if the character is a spellcasting class,
		// that it got new known spells or can update its old selections.
		// TODO future - we can probably do this better
		if (getAssociatedClass().isSpellcaster())
		{
			changes.add("New Spell Options");
		}

		UILib.addTextDisplay(bodyPanel, "<html>" + String.join("<br>", changes) + "</html>", c);
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public CharacterSheet getSheet()
	{
		return sheet;
	}
}
