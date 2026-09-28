package ncb.ui.tabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import ncb.data.enums.Ability;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
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
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this); // Possible spellcasting ability update
		PropertyListener.listenForChanges(PropertyListener.PRIMARYABILITY, this); // Possible spellcasting ability
																					// update
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 0.1;

		UILib.addTextDisplay(this,
				"<html>Ability Generation should be done however your GM specifies. Once you have generated your ability scores:<br><b>Fill in the generated values in the boxes below and selected your background ability increases (+2/+1 or +1/+1/+1)</b>.<br><i>Bold indicates the ability is one of your class's primary abilities.</i></html>",
				c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		add(getAbilitiesPanel(), c);
		c.gridy++;

		c.weighty = 1;
		UILib.addTextDisplay(this,
				"<html>Typically you will use one of these methods for generation:<br><br><b>Standard Array</b>: Use the following 6 scores: 15, 14, 13, 12, 10, 8.<br><b>Random Generation</b>: Roll 4d6 and drop 1 six times then assign each number to an ability.<br><b>Point Buy</b>: Start with each ability at 8. Spend 27 points across all abilities.<br>"
						+ getPBTable() + "</html>",
				c, VaporwaveColors.HOT_PINK);

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
			case PropertyListener.BACKGROUND:
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

	private static String getPBTable()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<table style='border-collapse: collapse;'>");

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
		if (sheet.getCharClass().getSpellcastingAbility() != null)
		{
			return List.of(sheet.getCharClass().getSpellcastingAbility());
		}
		else
		{
			return allSpellcastingAbilities;
		}
	}
}
