package ncb.ui.tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import ncb.data.enums.Ability;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.subpanels.AbilityScorePanel;

public class AbilityPanel extends UIPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final Map<Ability, AbilityScorePanel> abilityPanels = new HashMap<>();

	public AbilityPanel(CharacterSheet sheet)
	{
		super();

		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this); // Primary and/or spellcasting ability update
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this); // Obvious
		PropertyListener.listenForChanges(PropertyListener.BGABILITYOPTIONS, this); // Obvious
		PropertyListener.listenForChanges(PropertyListener.ABILITYSCORES, this); // Obvious
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this); // Possible spellcasting ability update
		PropertyListener.listenForChanges(PropertyListener.PRIMARYABILITY, this); // Possible spellcasting ability
																					// update
		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(5, 5, 5, 5);
		c.weighty = 0.1;

		UILib.addTextDisplay(this,
				"<html>Ability Generation should be done however your GM specifies. Once you have generated your ability scores:<br><b>Fill in the generated values in the boxes below and selected your background ability increases (+2/+1 or +1/+1/+1)</b>.<br><i>Bold indicates the ability is one of your class's primary abilities.</i></html>",
				c).setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		c.gridy++;

		add(getAbilitiesPanel(), c);
		c.gridy++;

		c.weighty = 1;
		UILib.addTextDisplay(this,
				"<html>Typically you will use one of these methods for generation:<br><br><b>Standard Array</b>: Use the following 6 scores: 15, 14, 13, 12, 10, 8.<br><b>Random Generation</b>: Roll 4d6 and drop 1 six times then assign each number to an ability.<br><b>Point Buy</b>: Start with each ability at 8. Spend 27 points across all abilities with the cost per value from the table below:<br><br>"
						+ getPBTable() + "</html>",
				c).setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		updatePrimaryAbilities();
		updateSpellcastingAbilities();
		updateBGAbilities();
	}

	private JPanel getAbilitiesPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		panel.setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();

		ButtonGroup spellBtnGroup = new ButtonGroup();
		for (Ability a : Ability.values())
		{
			if (a.equals(Ability.Primary))
			{
				continue;
			}
			AbilityScorePanel asp = new AbilityScorePanel(a.getFullName(), (score) -> updateScore(a, score),
					(btnNum, checked) -> checkBG(a, btnNum, checked), (selected) -> selectSpell(a, selected));
			spellBtnGroup.add(asp.getSpellcastingButton());
			abilityPanels.put(a, asp);
		}

		panel.add(abilityPanels.get(Ability.Str), c);
		c.gridy++;
		panel.add(abilityPanels.get(Ability.Dex), c);
		c.gridy++;
		panel.add(abilityPanels.get(Ability.Con), c);
		c.gridx++;
		c.gridy = 0;

		panel.add(abilityPanels.get(Ability.Int), c);
		c.gridy++;
		panel.add(abilityPanels.get(Ability.Wis), c);
		c.gridy++;
		panel.add(abilityPanels.get(Ability.Cha), c);

		return panel;
	}

	private void updateScore(Ability a, int newScore)
	{
		sheet.getAbilityScores().setBaseScoreFor(a, newScore);
	}

	private void checkBG(Ability a, int index, boolean checked)
	{
		sheet.getAbilityScores().setBGIncreaseFor(a, (checked ? index : 0));

		if (checked)
		{
			enforceBackgroundAbilityLimits(a, index);
		}
	}

	private void selectSpell(Ability a, boolean selected)
	{
		if (selected)
		{
			sheet.getAbilityScores().setSpellcastingAbility(a);
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		switch (prop)
		{
			case PropertyListener.CLASS:
				updatePrimaryAbilities();
			case PropertyListener.PRIMARYABILITY: // This is a deliberate "fall-through"
			case PropertyListener.SPECIES:
				updateSpellcastingAbilities();
			break;
			case PropertyListener.ABILITYSCORES:
				updateAbilityScores();
			case PropertyListener.BACKGROUND: // Also a deliberate "fall-through"
			case PropertyListener.BGABILITYOPTIONS:
				updateBGAbilities();
			break;
		}
	}

	private void updatePrimaryAbilities()
	{
		if (sheet.getCharClass() != null)
		{
			List<Ability> primaryAbilities = sheet.getCharClass().getPrimaryAbilities();
			for (Ability a : Ability.realValues())
			{
				AbilityScorePanel ap = abilityPanels.get(a);
				ap.setLabelBoldState(primaryAbilities.contains(a));
			}
		}
	}

	private void updateSpellcastingAbilities()
	{
		List<Ability> spellAbilities = getSpellcastingAbilityOptions(sheet);
		for (Ability a : Ability.realValues())
		{
			AbilityScorePanel ap = abilityPanels.get(a);
			ap.showSpellcastingButton((spellAbilities.contains(a)) || (spellAbilities.contains(Ability.Primary)
					&& a.equals(sheet.getCharClass().getSelectedPrimary())));
			if (sheet.getSpellcastingAbility().equals(a))
			{
				ap.selectSpellcasting();
			}
		}
	}

	private void updateBGAbilities()
	{
		if (sheet.getBackground() != null)
		{
			List<Ability> bgAbilities = sheet.getBackground().getAbilityOptions();
			for (Ability a : Ability.realValues())
			{
				AbilityScorePanel ap = abilityPanels.get(a);
				if (bgAbilities.contains(a))
				{
					ap.showCheckboxes();
					int sheetIncrease = sheet.getAbilityScores().getBGIncreaseFor(a);
					if (sheetIncrease == 1)
					{
						ap.selectCheckbox1();
					}
					else if (sheetIncrease == 2)
					{
						ap.selectCheckbox2();
					}
					else
					{
						ap.unSelectCheckboxes();
					}
				}
				else
				{
					ap.clearCheckboxes();
					sheet.getAbilityScores().setBGIncreaseFor(a, 0);
				}
			}
		}
	}

	private void updateAbilityScores()
	{
		if (sheet.getCharClass() != null)
		{
			for (Ability a : Ability.realValues())
			{
				AbilityScorePanel ap = abilityPanels.get(a);
				ap.setScore(sheet.getAbilityScores().getBaseScoreFor(a));
			}
		}
	}

	private static String getPBTable()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<table style='border-collapse: collapse;' width='50%'>");

		sb.append(wrapRow("<th>Score</th>" + wrapCol(9) + wrapCol(10) + wrapCol(11) + wrapCol(12) + wrapCol(13)
				+ wrapCol(14) + wrapCol(15)));
		sb.append(wrapRow("<th>Cost</th>" + wrapCol(1) + wrapCol(2) + wrapCol(3) + wrapCol(4) + wrapCol(5) + wrapCol(7)
				+ wrapCol(9)));

		sb.append("</table>");

		return sb.toString();
	}

	private static String wrapRow(String text)
	{
		return "<tr>" + text + "</tr>";
	}

	private static String wrapCol(int text)
	{
		return "<td style='border: 1px solid black;text-align: center;'>" + text + "</td>";
	}

	private static final List<Ability> allSpellcastingAbilities = List.of(Ability.Int, Ability.Wis, Ability.Cha);

	private static List<Ability> getSpellcastingAbilityOptions(CharacterSheet sheet)
	{
		if (sheet.getCharClass().isSpellcaster())
		{
			return List.of(sheet.getCharClass().getSpellcastingAbility());
		}
		else if (sheet.isSpellcaster())
		{
			return allSpellcastingAbilities;
		}
		else
		{
			return Collections.emptyList();
		}
	}

	// This list is used to track the order the user checked boxes in for unchecking
	// the oldest +1 in the rare +1, +1, +2 scenario
	// It could be made smarter and do proper garbage collection instead of
	// infinitely growing, but it's a list of an enum that will probably have <10
	// entries 99.999% of the time, so not worth it
	private List<Ability> checkedHistory = new ArrayList<>();

	private void enforceBackgroundAbilityLimits(Ability a, int index)
	{
		checkedHistory.add(a);

		// The components are enforcing "no ability can get +1 and +2" already, so only
		// need to worry about either having 3 +1s or 1 +1 and 1 +2.
		Map<Ability, Integer> bgIncreases = sheet.getAbilityScores().getAllBGIncreases();
		if (bgIncreases.values().stream().mapToInt(Integer::intValue).sum() > 3)
		{
			// Too many increases
			if (index == 1)
			{
				// We just added a +1. There are only 3 "slots" for +1. So obviously, we have 2
				// +1s and a +2 selected. And since we just selected a +1, we probably want both
				// +1s (that's what we'll assume at least). So uncheck the +2.
				for (Map.Entry<Ability, Integer> inc : bgIncreases.entrySet())
				{
					if (inc.getKey() == a)
					{
						continue;
					}
					if (inc.getValue() == 2)
					{
						sheet.getAbilityScores().setBGIncreaseFor(inc.getKey(), 0);
						abilityPanels.get(inc.getKey()).unSelectCheckboxes();
						return;
					}
				}
			}
			else
			{
				// We just added a +2. There are now either 2 +2s (and possibly a +1, but that
				// doesn't matter) or 1 +2 and 2 +1s.

				// The second +2s the easiest to handle, we check that first
				for (Map.Entry<Ability, Integer> inc : bgIncreases.entrySet())
				{
					if (inc.getKey() == a)
					{
						continue;
					}
					if (inc.getValue() == 2)
					{
						sheet.getAbilityScores().setBGIncreaseFor(inc.getKey(), 0);
						abilityPanels.get(inc.getKey()).unSelectCheckboxes();
						return;
					}
				}

				// If we get here then we definitely have 2 +1s selected and need to unselect 1,
				// whichever was oldest selected.
				int age = -1;
				Ability otherOne = null;
				for (Map.Entry<Ability, Integer> inc : bgIncreases.entrySet())
				{
					if (inc.getKey() == a)
					{
						continue;
					}
					if (inc.getValue() == 1)
					{
						if (otherOne == null)
						{
							otherOne = inc.getKey();
							age = checkedHistory.lastIndexOf(inc.getKey());
						}
						else
						{
							int age2 = checkedHistory.lastIndexOf(inc.getKey());

							// Because we are constantly appending new entries, the lower "age" is the one
							// that was checked earlier
							if (age < age2)
							{
								sheet.getAbilityScores().setBGIncreaseFor(otherOne, 0);
								abilityPanels.get(otherOne).unSelectCheckboxes();
								return;
							}
							else
							{
								sheet.getAbilityScores().setBGIncreaseFor(inc.getKey(), 0);
								abilityPanels.get(inc.getKey()).unSelectCheckboxes();
								return;
							}
						}
					}
				}
			}
		}
	}
}
