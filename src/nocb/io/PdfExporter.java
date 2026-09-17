package nocb.io;

import static java.util.Map.entry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;

import nocb.data.Ability;
import nocb.data.ArmorProf;
import nocb.data.Skill;
import nocb.data.Spell;
import nocb.main.CharacterSheet;
import nocb.ui.Toast;

public class PdfExporter
{
	private static File TEMPLATE = new File("./resources/Neon-Odyssey-Character-Sheet.pdf");
	private static final String NAME_F = "Character Name", CLASS_F = "Class", SPECIES_F = "Species",
			/* SUBCLASS_F = "Subclass", */FEATURES_F = "Features and Traits", HOMEWORLD_F = "Homeworld",
			LEVEL_F = "Level", AC_F = "Armor", INITIATIVE_F = "Initiative", SPEED_F = "Speed", SIZE_F = "Size",
			HPCUR_F = "Speed 1", HPMAX_F = "HP Max", HDMAX_F = "HD Max", NOTES_F = "Galactic Notes",
			RESISTANCES_F = "Resistances", ENABLE_BTN_VALUE = "Yes";
	private static final Map<Ability, String> ABL_SCORE_FS = Map.of(Ability.Str, "Score STR", Ability.Dex,
			"Score STR 1", Ability.Con, "Score STR 2", Ability.Int, "Score STR 3", Ability.Wis, "Score STR 4",
			Ability.Cha, "Score STR 5");
	private static final Map<Ability, String> ABL_MOD_FS = Map.of(Ability.Str, "Mod STR", Ability.Dex, "Mod STR 1",
			Ability.Con, "Mod STR 2", Ability.Int, "Mod STR 3", Ability.Wis, "Mod STR 4", Ability.Cha, "Mod STR 5");
	private static final Map<Ability, String> ABL_SAVE_FS = Map.of(Ability.Str, "Save STR", Ability.Dex, "Save STR 1",
			Ability.Con, "Save STR 2", Ability.Int, "Save STR 3", Ability.Wis, "Save STR 4", Ability.Cha, "Save STR 5");
	private static final Map<Ability, String> ABL_PROF_FS = Map.of(Ability.Str, "Ability Save", Ability.Dex,
			"Ability Save 1", Ability.Con, "Ability Save 2", Ability.Int, "Ability Save 3", Ability.Wis,
			"Ability Save 4", Ability.Cha, "Ability Save 5");
	private static final String PROF_F = "Score STR 6", PASSPERC_F = "Score STR 7";
	private static final Map<Skill, String> SKILLS_FS = Map.ofEntries(entry(Skill.Acrobatics, "Skill 1"),
			entry(Skill.Animal_Handling, "Skill"), entry(Skill.Arcana, "Skill 3"), entry(Skill.Athletics, "Skill 2"),
			entry(Skill.Computers, "Skill 5"), entry(Skill.Deception, "Skill 4"), entry(Skill.History, "Skill 9"),
			entry(Skill.Insight, "Skill 6"), entry(Skill.Intimidation, "Skill 10"),
			entry(Skill.Investigation, "Skill 7"), entry(Skill.Medicine, "Skill 11"), entry(Skill.Nature, "Skill 8"),
			entry(Skill.Perception, "Skill 15"), entry(Skill.Performance, "Skill 12"),
			entry(Skill.Persuasion, "Skill 16"), entry(Skill.Religion, "Skill 13"),
			entry(Skill.Sleight_of_Hand, "Skill 17"), entry(Skill.Stealth, "Skill 14"),
			entry(Skill.Survival, "Skill 19"), entry(Skill.Technology, "Skill 18"));
	private static final Map<Skill, String> SKILL_PROFS_FS = Map.ofEntries(entry(Skill.Acrobatics, "Skills"),
			entry(Skill.Animal_Handling, "Skills 1"), entry(Skill.Arcana, "Skills 2"),
			entry(Skill.Athletics, "Skills 3"), entry(Skill.Computers, "Skills 4"), entry(Skill.Deception, "Skills 5"),
			entry(Skill.History, "Skills 6"), entry(Skill.Insight, "Skills 7"), entry(Skill.Intimidation, "Skills 8"),
			entry(Skill.Investigation, "Skills 9"), entry(Skill.Medicine, "Skills 10"),
			entry(Skill.Nature, "Skills 11"), entry(Skill.Perception, "Skills 12"),
			entry(Skill.Performance, "Skills 13"), entry(Skill.Persuasion, "Skills 14"),
			entry(Skill.Religion, "Skills 15"), entry(Skill.Sleight_of_Hand, "Skills 16"),
			entry(Skill.Stealth, "Skills 17"), entry(Skill.Survival, "Skills 18"),
			entry(Skill.Technology, "Skills 19"));
	private static final String LANGUAGES_F = "Languages", FEATS_F = "Feats and Professions", EQUIPMENT_F = "Equipment",
			WPROFS_F = "Weapon Proficiencies", TPROFS_F = "Tool Proficiencies", VPROFS_F = "Vehicle Proficiencies",
			APLIGHT_F = "Armor Proficiency 1", APMEDIUM_F = "Armor Proficiency 2", APHEAVY_F = "Armor Proficiency 3",
			APSHIELD_F = "Armor Proficiency 4", SHEETNOTES_F = "Notes";
	private static final String SPELLATK_F = "Spell Attack Bonus", SPELLSAVE_F = "Spell Save DC",
			SPELLMOD_F = "Spellcasting Modifier", SPELLABILITY_INT_F = "Spellcasting Ability 1",
			SPELLABILITY_WIS_F = "Spellcasting Ability 2", SPELLABILITY_CHA_F = "Spellcasting Ability 3",
			SLOTS_PRE_F = "Total ";
	private static final String SPELLNAME_PRE_F = "Spell Name ", SPELLLEVEL_PRE_F = "Spell Level ",
			SPELLCAST_PRE_F = "Spell Casting Time ", SPELLRANGE_PRE_F = "Spell Range ",
			SPELLFEATURES_PRE_F = "Spell Features ", SPELLSCHOOL_PRE_F = "Spell School ",
			SPELLNOTES_PRE_F = "Spell Notes ";
	private static final int spellListStart = 1, spellListEnd = 32;

	private static final List<String> notesToWrite = new ArrayList<>();

	public static boolean exportCharacterSheet(CharacterSheet c)
	{
		if (c.getName().isBlank())
		{
			System.out.println("Cannot export character with a blank name.");
			return false;
		}
		try (PDDocument pdf = Loader.loadPDF(new RandomAccessReadBufferedFile(TEMPLATE)))
		{
			PDDocumentCatalog docCatalog = pdf.getDocumentCatalog();
			PDAcroForm acroForm = docCatalog.getAcroForm();

			if (acroForm != null)
			{
				fillAbilityScores(acroForm, c);

				fillBasicCharInfo(acroForm, c);

				fillSkills(acroForm, c);

				fillFeatures(acroForm, c);

				fillProfsAndEquipment(acroForm, c);

				fillFeats(acroForm, c);

				fillCurrencyAndLanguages(acroForm, c);

				fillSpellcastingDetails(acroForm, c);

				fillSpells(acroForm, c);

				if (!notesToWrite.isEmpty())
				{
					writeNotes(acroForm);
				}
			}

			pdf.save(c.getName() + ".pdf");
			pdf.close();
			new Toast("Successfully Exported Character Sheet", 0);
		}
		catch (IOException e)
		{
			JOptionPane.showMessageDialog(null, "Failed to export pdf. Most likely it is open in another application.",
					"Export PDF Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private static void fillBasicCharInfo(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		fillField(acroForm, NAME_F, c.getName());
		fillField(acroForm, CLASS_F, c.getCharClass().getName());
		fillField(acroForm, SPECIES_F, c.getSpecies().getName());
		fillField(acroForm, HOMEWORLD_F, c.getBackground().getHomeworld().getName());
		fillField(acroForm, LEVEL_F, c.getLevel());

		fillField(acroForm, AC_F, c.getAC());
		fillField(acroForm, SIZE_F, c.getSpecies().getSize());
		fillField(acroForm, SPEED_F, c.getSpeed());
		fillField(acroForm, INITIATIVE_F, c.getTotalAbilityMod(Ability.Dex));

		fillField(acroForm, HPCUR_F, c.getMaxHP());
		fillField(acroForm, HPMAX_F, c.getMaxHP());
		fillField(acroForm, HDMAX_F, "1D" + c.getCharClass().getHd());

		fillField(acroForm, RESISTANCES_F, String.join(", ", c.getAllResistances()));
	}

	private static void fillAbilityScores(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		for (Ability a : Ability.realValues())
		{
			fillField(acroForm, ABL_SCORE_FS.get(a), c.getTotalAbilityScore(a));
			fillField(acroForm, ABL_MOD_FS.get(a), c.getTotalAbilityMod(a));
			checkField(acroForm, ABL_PROF_FS.get(a), c.saveProficient(a));
			fillField(acroForm, ABL_SAVE_FS.get(a), c.getTotalAbilitySave(a));
		}

		fillField(acroForm, PROF_F, c.getProficiency());
	}

	private static void fillSkills(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		for (Skill s : Skill.realValues())
		{
			checkField(acroForm, SKILL_PROFS_FS.get(s), c.skillProficient(s));
			fillField(acroForm, SKILLS_FS.get(s), c.getSkillTotal(s));
		}
		fillField(acroForm, PASSPERC_F, 10 + c.getSkillTotal(Skill.Perception));
	}

	private static void fillFeatures(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		List<String> featureStrings = c.getFeatureStrings();
		// TODO post1.0 - overflow into notes?
		fillField(acroForm, FEATURES_F, String.join(System.lineSeparator(), featureStrings));
	}

	private static void fillProfsAndEquipment(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		fillField(acroForm, WPROFS_F, String.join(", ", c.getAllWeaponProfs()));
		fillField(acroForm, TPROFS_F, String.join(", ", c.getAllToolProfs()));
		fillField(acroForm, VPROFS_F, c.getAllVehicleProfs());

		List<ArmorProf> armorProfs = c.getAllArmorProfs();
		if (armorProfs.contains(ArmorProf.Light))
		{
			checkField(acroForm, APLIGHT_F, true);
		}
		if (armorProfs.contains(ArmorProf.Medium))
		{
			checkField(acroForm, APMEDIUM_F, true);
		}
		if (armorProfs.contains(ArmorProf.Heavy))
		{
			checkField(acroForm, APHEAVY_F, true);
		}
		if (armorProfs.contains(ArmorProf.Shields))
		{
			checkField(acroForm, APSHIELD_F, true);
		}

		fillField(acroForm, EQUIPMENT_F, c.getCharClass().getEquipmentItems());
	}

	private static void fillFeats(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		fillField(acroForm, FEATS_F,
				String.join(System.lineSeparator(), c.getAllFeats().stream().map(f -> f.getFeatString()).toList()));
	}

	private static void fillCurrencyAndLanguages(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		fillField(acroForm, NOTES_F, c.getAllNotes());

		fillField(acroForm, LANGUAGES_F,
				String.join(System.lineSeparator(), c.getAllLanguages().stream().map(l -> l.toString()).toList()));

		List<String> sheetNotes = c.getSheetNotes();
		if (!sheetNotes.isEmpty())
		{
			notesToWrite.addAll(sheetNotes);
		}
	}

	private static void fillSpellcastingDetails(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		if (c.isSpellcaster())
		{
			Ability spellcastingAbility = c.getSpellcastingAbility();
			int sam = c.getTotalAbilityMod(spellcastingAbility);

			switch (spellcastingAbility)
			{
				case Int:
					checkField(acroForm, SPELLABILITY_INT_F, true);
				break;
				case Wis:
					checkField(acroForm, SPELLABILITY_WIS_F, true);
				break;
				case Cha:
					checkField(acroForm, SPELLABILITY_CHA_F, true);
				break;
				default:
					System.out.println("Got unknown spellcasting ability " + spellcastingAbility);
			}

			fillField(acroForm, SPELLMOD_F, sam);
			fillField(acroForm, SPELLATK_F, sam + c.getProficiency());
			fillField(acroForm, SPELLSAVE_F, 8 + sam + c.getProficiency());

			// No such thing as 0th level spell slots
			for (int i = 1; i <= 9; i++)
			{
				int slotCount = c.getCharClass().getSpellSlots(c.getLevel(), i);

				if (slotCount > 0)
				{
					fillField(acroForm, SLOTS_PRE_F + i, slotCount);
				}
			}
		}
	}

	private static void fillSpells(PDAcroForm acroForm, CharacterSheet c) throws IOException
	{
		List<Spell> spells = c.getAllSpells();
		spells.sort(Comparator.comparingInt(Spell::getLevel));
		for (int i = 0; i < (spellListEnd - spellListStart); i++)
		{
			if (i >= spells.size())
			{
				return;
			}

			Spell s = spells.get(i);
			int listIndex = spellListStart + i; // Offset our 0-indexed value by the start of the field names
			fillField(acroForm, SPELLLEVEL_PRE_F + listIndex, (s.getLevel() == 0 ? "C" : "" + s.getLevel()));
			fillField(acroForm, SPELLNAME_PRE_F + listIndex, s.getName());
			fillField(acroForm, SPELLCAST_PRE_F + listIndex, s.getCastTime());
			fillField(acroForm, SPELLRANGE_PRE_F + listIndex, s.getRange());
			fillField(acroForm, SPELLFEATURES_PRE_F + listIndex, getSpellFeatures(s));
			fillField(acroForm, SPELLSCHOOL_PRE_F + listIndex, s.getSchool());
			fillField(acroForm, SPELLNOTES_PRE_F + listIndex, s.getNotes());
		}
	}

	private static String getSpellFeatures(Spell s)
	{
		List<String> features = new ArrayList<>();

		if (s.isConcentration())
		{
			features.add("(C)");
		}
		if (s.isRitual())
		{
			features.add("(R)");
		}
		features.add(s.getComponents());

		return String.join(" ", features);
	}

	private static void writeNotes(PDAcroForm acroForm) throws IOException
	{
		// For some reason the notes field in the sheet uses a non-standard font so we
		// have to override it
		PDField field = acroForm.getField(SHEETNOTES_F);
		if (field instanceof PDTextField)
		{
			((PDTextField) field).setDefaultAppearance("/Helv 12 Tf 0 g");
			fillField(acroForm, SHEETNOTES_F, String.join(", ", notesToWrite));
		}
	}

	private static void fillField(PDAcroForm form, String fieldName, int value) throws IOException
	{
		fillField(form, fieldName, "" + value);
	}

	private static void fillField(PDAcroForm form, String fieldName, String value) throws IOException
	{
		if (value != null && !value.isBlank())
		{
			form.getField(fieldName).setValue(value);
		}
	}

	private static void checkField(PDAcroForm form, String fieldName, boolean value) throws IOException
	{
		if (value)
		{
			form.getField(fieldName).setValue(ENABLE_BTN_VALUE);
		}
	}
}
