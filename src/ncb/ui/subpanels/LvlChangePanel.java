package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import ncb.main.CharacterSheet;
import ncb.ui.CollapsablePanel;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class LvlChangePanel extends CollapsablePanel
{
	private static final long serialVersionUID = -4522031612337864604L;

	public LvlChangePanel(int lvl, CharacterSheet sheet, boolean startCollapsed)
	{
		super(startCollapsed);

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		UILib.addLabel(headerPanel, "Level " + lvl, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		List<String> changes = new ArrayList<>();

		List<String> updatedFeatures = sheet.getCharClass().getFeatureNamesAtLevel(lvl);
		changes.addAll(updatedFeatures.stream().map(fn -> "Class Feature: " + fn).toList());
		if (sheet.getCharClass().getSubclass() != null)
		{
			changes.addAll(sheet.getCharClass().getSubclass().getFeatureNamesAtLevel(lvl).stream()
					.map(fb -> "Sublass Feature: " + fb).toList());
		}
		changes.addAll(
				sheet.getSpecies().getTraitNamesAtLevel(lvl).stream().map(fn -> "Species Trait: " + fn).toList());

		// For simplicity we just assume that, if the character's class is a
		// spellcasting class, that it got new known spells or can update its old
		// selections.
		if (sheet.getCharClass().getSpellcastingAbility() != null)
		{
			changes.add("New Spell Options");
		}

		UILib.addTextDisplay(bodyPanel, "<html>" + String.join("<br>", changes) + "</html>", c,
				VaporwaveColors.HOT_PINK);
	}
}
