package ncb.ui.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JTextPane;

import ncb.data.enums.Ability;
import ncb.data.enums.Skill;
import ncb.data.loadables.Spell;
import ncb.io.DocExporter;
import ncb.io.JsonDataLoader;
import ncb.io.PdfExporter;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;

public class SummaryPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private JTextPane summaryText;

	private JButton export = new JButton("Save and Export Character Sheet");

	private boolean hasEmptyCharName = false, hasInvalidBackgroundIncreases = false, hasDuplicateSkillProfs = false,
			hasDuplicateSpells = false, hasDuplicateSkillExpertise = false;

	public SummaryPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		// Summary panel needs to update for a lot of things
		PropertyListener.listenForChanges(PropertyListener.ABILITYSCORES, this);
		PropertyListener.listenForChanges(PropertyListener.ABILITYSCOREINC, this);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);
		PropertyListener.listenForChanges(PropertyListener.BGABILITYOPTIONS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);
		PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);
		PropertyListener.listenForChanges(PropertyListener.LANGUAGES, this);
		PropertyListener.listenForChanges(PropertyListener.NAME, this);
		PropertyListener.listenForChanges(PropertyListener.PRIMARYABILITY, this);
		PropertyListener.listenForChanges(PropertyListener.RESISTANCES, this);
		PropertyListener.listenForChanges(PropertyListener.SELECTED, this);
		PropertyListener.listenForChanges(PropertyListener.SIZE, this);
		PropertyListener.listenForChanges(PropertyListener.SKILLEXPS, this);
		PropertyListener.listenForChanges(PropertyListener.SKILLPROFS, this);
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);
		PropertyListener.listenForChanges(PropertyListener.SPELLS, this);
		PropertyListener.listenForChanges(PropertyListener.SUBCLASS, this);

		setLayout(new BorderLayout());

		summaryText = UILib.getTextDisplay("", VaporwaveColors.HOT_PINK);
		add(UILib.getScrollPaneFor(summaryText), BorderLayout.CENTER);

		add(export, BorderLayout.SOUTH);
		export.addActionListener(this);
		export.setBackground(VaporwaveColors.NEON_BLUE);

		updateProperty(PropertyListener.CLASS);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(export))
		{
			PdfExporter.exportCharacterSheet(sheet);
			if (sheet.isSpellcaster())
			{
				DocExporter.exportSpells(sheet);
			}
			JsonDataLoader.saveCharacterStateToFile(sheet, new File(sheet.getName() + ".nchar"));
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		// We could be more efficient about this and only re-check/re-generate what was
		// updated, but since it's one big text field by design that's kinda difficult
		// and it's not that much processing time
		hasEmptyCharName = sheet.getName().isBlank();
		checkForInvalidBackgroundAbilityIncreases(sheet.getAbilityScores().getAllBGIncreases());
		List<Skill> skillProfs = sheet.getAllSkillProfs();
		skillProfs.sort((a, b) -> a.toString().compareTo(b.toString()));
		checkForDuplicateSkills(skillProfs);
		List<Spell> spells = sheet.getAllSpells();
		if (spells.contains(null))
		{
			spells.clear();
		}
		checkForDuplicateSpells(spells);
		List<Skill> skillExperts = sheet.getAllSkillExperts();
		skillExperts.sort((a, b) -> a.toString().compareTo(b.toString()));
		checkForDuplicateSkillExpertise(skillExperts);

		if (hasEmptyCharName)
		{
			export.setEnabled(false);
		}
		else
		{
			export.setEnabled(true);
		}

		StringBuilder text = new StringBuilder();
		text.append("<html>");

		if (hasEmptyCharName)
		{
			text.append("<span style='color: red;'>");
		}
		text.append("Character Name: ");
		text.append(sheet.getName());
		if (hasEmptyCharName)
		{
			text.append(
					"<b>(Character Name must be set in order to export. Please set a Character Name on the Details tab.)</b></span>");
		}
		text.append("<br>");
		text.append("Class: ");
		text.append(sheet.getCharClass().getName());
		text.append("<br>");
		text.append("Species: ");
		text.append(sheet.getSpecies().getName());
		text.append("<br>");
		text.append("Homeworld: ");
		text.append(sheet.getBackground().getHomeworld() == null ? "" : sheet.getBackground().getHomeworld().getName());
		text.append("<br><br>");

		if (hasInvalidBackgroundIncreases)
		{
			text.append("<span style='color: orange;'>");
		}
		text.append("Ability Scores: ");
		text.append("Str: ");
		text.append(sheet.getTotalAbilityScore(Ability.Str));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Str) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Str));
		text.append(") ");
		text.append("Dex: ");
		text.append(sheet.getTotalAbilityScore(Ability.Dex));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Dex) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Dex));
		text.append(") ");
		text.append("Con: ");
		text.append(sheet.getTotalAbilityScore(Ability.Con));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Con) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Con));
		text.append(") ");
		text.append("Int: ");
		text.append(sheet.getTotalAbilityScore(Ability.Int));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Int) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Int));
		text.append(") ");
		text.append("Wis: ");
		text.append(sheet.getTotalAbilityScore(Ability.Wis));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Wis) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Wis));
		text.append(") ");
		text.append("Cha: ");
		text.append(sheet.getTotalAbilityScore(Ability.Cha));
		text.append(" (");
		text.append(sheet.getTotalAbilityMod(Ability.Cha) > 0 ? "+" : "");
		text.append(sheet.getTotalAbilityMod(Ability.Cha));
		text.append(") ");
		if (hasInvalidBackgroundIncreases)
		{
			text.append("<i>(Invalid Background Ability Increases. Should be +2/+1 or +1/+1/+1)</i></span>");
		}
		text.append("<br>");
		if (sheet.isSpellcaster())
		{
			text.append("Spellcasting Ability: ");
			text.append(sheet.getSpellcastingAbility());
			text.append("<br><br>");
		}

		if (hasDuplicateSkillProfs)
		{
			text.append("<span style='color: orange;'>");
		}
		text.append("Skill Proficiencies: ");
		text.append(String.join(", ", skillProfs.stream().map(s -> s.toString()).toList()));
		if (hasDuplicateSkillProfs)
		{
			text.append(" <i>(Duplicate skill proficiencies)</i></span>");
		}
		text.append("<br>");

		if (!skillExperts.isEmpty())
		{
			if (hasDuplicateSkillExpertise)
			{
				text.append("<span style='color: orange;'>");
			}
			text.append("Skill Expertise: ");
			text.append(String.join(", ", skillExperts.stream().map(s -> s.toString()).toList()));

			if (hasDuplicateSkillExpertise)
			{
				text.append(" <i>(Duplicate skill expertises)</i></span>");
			}
			text.append("<br>");
		}
		text.append("Weapon Proficiencies: ");
		text.append(String.join(", ", sheet.getAllWeaponProfs()));
		text.append("<br>");
		text.append("Tool Proficiencies: ");
		text.append(String.join(", ", sheet.getAllToolProfs()));
		text.append("<br>");
		text.append("Vehicle Proficiencies: ");
		text.append(sheet.getAllVehicleProfs());
		text.append("<br>");
		text.append("Armor Proficiencies: ");
		text.append(String.join(", ", sheet.getAllArmorProfs().stream().map(ap -> ap.toString()).toList()));
		text.append("<br><br>");

		text.append("Feats: ");
		text.append(String.join(", ", sheet.getAllFeats().stream().map(f -> f.getName()).toList()));
		text.append("<br>");
		text.append("Equipment: ");
		text.append(sheet.getCharClass().getEquipmentItems());
		text.append("<br>");
		text.append("Notes: ");
		text.append(sheet.getCharClass().getEquipmentNotes());
		text.append("<br>");
		text.append("Languages: ");
		text.append(String.join(", ", sheet.getAllLanguages().stream().map(l -> l.toString()).toList()));
		text.append("<br>");
		if (sheet.isSpellcaster())
		{
			text.append("Spells:");
			text.append("<br>");
			for (int lvl = 0; lvl <= 9; lvl++)
			{
				final int spellLvl = lvl;
				List<Spell> spellsForLevel = spells.stream().filter(s -> s.getLevel() == spellLvl).toList();
				if (!spellsForLevel.isEmpty())
				{
					if (lvl == 0)
					{
						text.append("Cantrips: ");
					}
					else
					{
						text.append("Level ");
						text.append(lvl);
						text.append(": ");
					}
					text.append(String.join(", ", spellsForLevel.stream().map(s -> s.getName()).toList()));
					text.append("<br>");
				}
			}
		}
		if (hasDuplicateSpells)
		{
			text.append("<span style='color: orange;'>");
			text.append(
					"&nbsp;&nbsp;&nbsp;&nbsp;Warning - One or more duplicate spells found. Your character will not receive any bonuses from duplicates, avoid them if possible.");
			text.append("</span><br>");
		}
		text.append("<br>");

		summaryText.setText(text.toString());
	}

	private void checkForDuplicateSkills(List<Skill> skills)
	{
		hasDuplicateSkillProfs = new HashSet<>(skills).size() < skills.size();
	}

	private void checkForDuplicateSkillExpertise(List<Skill> skills)
	{
		hasDuplicateSkillExpertise = new HashSet<>(skills).size() < skills.size();
	}

	private void checkForInvalidBackgroundAbilityIncreases(Map<Ability, Integer> bgIncreases)
	{
		int totalInc = bgIncreases.values().stream().mapToInt(Integer::intValue).sum();
		hasInvalidBackgroundIncreases = totalInc != 3;
	}

	private void checkForDuplicateSpells(List<Spell> spells)
	{
		hasDuplicateSpells = new HashSet<>(spells.stream().map(s -> s.getName()).toList()).size() < spells.size();
	}
}
