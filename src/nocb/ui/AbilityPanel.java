package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import nocb.data.Ability;
import nocb.main.CharacterSheet;

public class AbilityPanel extends JPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final Map<Ability, AbilityScorePanel> abilityPanels = new HashMap<>();

	public AbilityPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		c.weighty = 0.1;
		c.gridwidth = 2;
		JTextPane p = UILib.addTextDisplay(this,
				"<html>Ability Generation should be done however your GM specifies. Once you have generated your ability scores:<br><b>Fill in the generated values in the boxes below and selected your background ability increases (+2/+1 or +1/+1/+1)</b>.<br><i>Bold indicates the ability is one of your class's primary abilities.</i></html>",
				c);
		UILib.centerTextInPane(p);
		p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.gridwidth = 1;
		c.weighty = 0;
		c.gridy++;

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

		add(abilityPanels.get(Ability.Str), c);
		c.gridx++;

		add(abilityPanels.get(Ability.Int), c);
		c.gridx = 0;
		c.gridy++;

		add(abilityPanels.get(Ability.Dex), c);
		c.gridx++;

		add(abilityPanels.get(Ability.Wis), c);
		c.gridx = 0;
		c.gridy++;

		add(abilityPanels.get(Ability.Con), c);
		c.gridx++;

		add(abilityPanels.get(Ability.Cha), c);
		c.gridx = 0;
		c.gridy++;

		c.weighty = 1;
		c.gridwidth = 2;
		JTextPane p2 = UILib.addTextDisplay(this,
				"<html>Typically you will use one of these methods for generation:<br><br><b>Standard Array</b>: Use the following 6 scores: 15, 14, 13, 12, 10, 8.<br><b>Random Generation</b>: Roll 4d6 and drop 1 six times then assign each number to an ability.<br><b>Point Buy</b>: Start with each ability at 8. Spend 27 points across all abilities.<br>"
						+ getPBTable() + "</html>",
				c);
		UILib.centerTextInPane(p2);
		p2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		c.gridwidth = 1;
		c.gridy++;
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

	public void updateDetails()
	{
		List<Ability> primaryAbilities = sheet.getCharClass().getPrimaryAbilities();
		List<Ability> bgAbilities = sheet.getBackground().getAbilityOptions();
		List<Ability> spellAbilities = getSpellcastingAbilityOptions(sheet);
		for (Ability a : Ability.values())
		{
			if (a.equals(Ability.Primary))
			{
				continue;
			}

			AbilityScorePanel ap = abilityPanels.get(a);

			ap.setScore(sheet.getAbilityScores().getBaseScoreFor(a));

			if (primaryAbilities.contains(a))
			{
				ap.setLabelBoldState(true);
			}
			else
			{
				ap.setLabelBoldState(false);
			}

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

			ap.showSpellcastingButton(spellAbilities.contains(a) || (spellAbilities.contains(Ability.Primary)
					&& a.equals(sheet.getCharClass().getPrimaryAbilities().get(0))));
		}

		if (!spellAbilities.isEmpty())
		{
			if (spellAbilities.get(0).equals(Ability.Primary))
			{
				abilityPanels.get(sheet.getCharClass().getPrimaryAbilities().get(0)).selectSpellcasting();
			}
			else
			{
				abilityPanels.get(spellAbilities.get(0)).selectSpellcasting();
			}
		}
	}

	private static String getPBTable()
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<center><table style='border-collapse: collapse;'>");

		sb.append(wrapRow("<th>Score</th>" + wrapCol(9) + wrapCol(10) + wrapCol(11) + wrapCol(12) + wrapCol(13)
				+ wrapCol(14) + wrapCol(15)));
		sb.append(wrapRow("<th>Cost</th>" + wrapCol(1) + wrapCol(2) + wrapCol(3) + wrapCol(4) + wrapCol(5) + wrapCol(7)
				+ wrapCol(9)));

		sb.append("</table>");
		sb.append("</center>");

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
		else
		{
			return allSpellcastingAbilities;
		}
	}
}
