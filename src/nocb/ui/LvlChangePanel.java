package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import nocb.main.CharacterSheet;

public class LvlChangePanel extends CollapsablePanel
{
	private static final long serialVersionUID = -4522031612337864604L;

	public LvlChangePanel(int lvl, CharacterSheet sheet, boolean startCollapsed)
	{
		super(startCollapsed);

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		JLabel l = UILib.addLabel(headerPanel, "Level " + lvl, c);
		l.setFont(UILib.boldFont);
		c.gridy++;

		List<String> changes = new ArrayList<>();

		changes.addAll(sheet.getCharClass().getFeaturesAtLevel(lvl).stream().map(cf -> "Class Feature: " + cf.getName())
				.toList());
		changes.addAll(
				sheet.getSpecies().getTraitsAtLevel(lvl).stream().map(st -> "Species Trait: " + st.getName()).toList());

		// For simplicity we just assume that, if the character's class is a
		// spellcasting class, that it got new known spells. I don't think any
		// spellcasting class ever has a dead level for known spells.
		if (sheet.getCharClass().isSpellcaster())
		{
			changes.add("New Known Spells");
		}

		UILib.addTextDisplay(bodyPanel, "<html>" + String.join("<br>", changes) + "</html>", c);
	}
}
