package nocb.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import nocb.data.Ability;
import nocb.data.AbilityScores;
import nocb.data.ArmorProf;
import nocb.data.Background;
import nocb.data.CharacterClass;
import nocb.data.Feat;
import nocb.data.Language;
import nocb.data.Skill;
import nocb.data.Species;
import nocb.data.Spell;

public class CharacterSheet
{
	// Sheet-specific fields
	private String name = "";
	private int lvl = 1, prof = 2;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getLevel()
	{
		return lvl;
	}

	public int getProficiency()
	{
		return prof;
	}

	// Sub-fields
	private CharacterClass charClass;
	private Species species;
	private Background background;
	private final AbilityScores abilityScores = new AbilityScores();

	public CharacterClass getCharClass()
	{
		return charClass;
	}

	public void setCharClass(CharacterClass c)
	{
		this.charClass = c;
	}

	public Species getSpecies()
	{
		return species;
	}

	public void setSpecies(Species s)
	{
		this.species = s;
	}

	public Background getBackground()
	{
		return background;
	}

	public void setBackground(Background b)
	{
		this.background = b;
	}

	public AbilityScores getAbilityScores()
	{
		return abilityScores;
	}

	// Calculated fields

	public boolean saveProficient(Ability a)
	{
		// Neither species nor background ever give save proficiencies
		return charClass.getSaveProfs().contains(a);
	}

	public int getTotalAbilitySave(Ability a)
	{
		return getTotalAbilityMod(a) + (saveProficient(a) ? getProficiency() : 0);
	}

	public boolean skillProficient(Skill s)
	{
		return getAllSkillProfs().contains(s);
	}

	public boolean skillExpert(Skill s)
	{
		// Neither species nor background ever give skill expertise
		return charClass.getAllSkillsExpert().contains(s);
	}

	public int getSkillTotal(Skill s)
	{
		int total = getTotalAbilityMod(s.getAbility());
		// if (skillHalfProficient()) {} // TODO post1.0 - Icon Level up
		if (skillProficient(s))
		{
			if (skillExpert(s))
			{
				total += getProficiency() * 2;
			}
			else
			{
				total += getProficiency();
			}
		}
		Map<Skill, Ability> abilityAdds = charClass.getAllAbilitiesAddToSkills();
		if (abilityAdds.containsKey(s))
		{
			total += getTotalAbilityMod(abilityAdds.get(s)) > 0 ? getTotalAbilityMod(abilityAdds.get(s)) : 1;
		}
		return total;
	}

	public List<String> getFeatureStrings()
	{
		List<String> fs = new ArrayList<>();

		fs.addAll(species.getAllFeatureStrings());
		fs.addAll(charClass.getAllFeatureStrings());

		return fs;
	}

	public List<Feat> getAllFeats()
	{
		List<Feat> allFeats = new ArrayList<>();

		allFeats.addAll(charClass.getAllFeats());
		allFeats.addAll(species.getAllFeats());
		allFeats.addAll(background.getAllFeats());

		return allFeats;
	}

	public List<Skill> getAllSkillProfs()
	{
		List<Skill> allSkills = new ArrayList<>();

		allSkills.addAll(charClass.getAllSkills());
		allSkills.addAll(species.getAllSkills());
		allSkills.addAll(background.getAllSkills());

		return allSkills;
	}

	public List<Skill> getAllSkillExperts()
	{
		List<Skill> allSkills = new ArrayList<>();

		allSkills.addAll(charClass.getAllSkillsExpert());
		// Species and background never add expertise

		return allSkills;
	}

	public List<String> getAllWeaponProfs()
	{
		// Neither species nor background ever give weapon proficiencies
		return charClass.getWeaponProfs();
	}

	public List<String> getAllToolProfs()
	{
		List<String> allToolProfs = new ArrayList<>();

		allToolProfs.addAll(charClass.getAllToolProfs());
		allToolProfs.addAll(species.getAllToolProfs());
		allToolProfs.addAll(background.getAllToolProfs());

		return allToolProfs;
	}

	public String getAllVehicleProfs()
	{
		// Neither class nor species ever give vehicle proficiencies
		return background.getVehicleProfs();
	}

	public List<ArmorProf> getAllArmorProfs()
	{
		// Neither species nor background ever give armor proficiencies
		return charClass.getArmorProfs();
	}

	public List<Language> getAllLanguages()
	{
		List<Language> langs = new ArrayList<>();

		langs.addAll(charClass.getAllLanguages());
		// Species never gives languages
		langs.addAll(background.getAllLanguages());

		return langs;
	}

	public int getAllNotes()
	{
		// Species never gives notes
		return charClass.getEquipmentNotes() + background.getNotes();
	}

	public List<String> getAllResistances()
	{
		List<String> allRes = new ArrayList<>();

		allRes.addAll(charClass.getAllResistances(background.getHomeworld()));
		allRes.addAll(species.getAllResistances(background.getHomeworld()));
		allRes.addAll(background.getAllResistances(background.getHomeworld()));

		return allRes;
	}

	public int getTotalAbilityScore(Ability a)
	{
		// Handle "Primary"
		if (a.equals(Ability.Primary))
		{
			return getTotalAbilityScore(charClass.getSelectedPrimary());
		}

		// TODO post1.0 - When handling level up, also need to query class features for
		// increases
		return abilityScores.getTotalScoreFor(a);
	}

	public int getTotalAbilityMod(Ability a)
	{
		return AbilityScores.getModFor(getTotalAbilityScore(a));
	}

	public Ability getSpellcastingAbility()
	{
		if (charClass.isSpellcaster())
		{
			return charClass.getSpellcastingAbility();
		}
		else
		{
			// If the class doesn't set a spellcasting ability then either this will return
			// null (in which case the character would be marked as not a spellcaster), or
			// this will return the selected spellcasting ability from the ability scores
			// tab.
			return abilityScores.getSpellcastingAbility();
		}
	}

	public int getAC()
	{
		// This just handles basic unarmored ac
		int base = 10 + getTotalAbilityMod(Ability.Dex);

		if (charClass.getAbilityAddToAC() != null)
		{
			return base + getTotalAbilityMod(charClass.getAbilityAddToAC());
		}

		return base;
	}

	public boolean isSpellcaster()
	{
		return charClass.isSpellcaster() || species.isSpellcaster() || background.makesSpellcaster();
	}

	public List<Spell> getAllGrantedSpells()
	{
		List<Spell> gs = new ArrayList<>();

		gs.addAll(charClass.getAllSpellsGranted());
		gs.addAll(species.getAllSpellsGranted());
		gs.addAll(background.getAllSpellsGranted());

		return gs;
	}

	public List<Spell> getAllSpells()
	{
		List<Spell> gs = new ArrayList<>();

		gs.addAll(charClass.getAllSpells(lvl)); // Class has both granted and selected spells and spells by class level
		gs.addAll(species.getAllSpellsGranted());
		gs.addAll(background.getAllSpellsGranted());

		return gs;
	}

	public int getSpeed()
	{
		return charClass.getTotalSpeedMod() + species.getTotalSpeedMod() + background.getTotalSpeedMod();
	}

	public int getMaxHP()
	{
		int maxHP = getCharClass().getHd() + getTotalAbilityMod(Ability.Con);

		maxHP += charClass.getTotalExtraHPPerLevel();
		maxHP += species.getTotalExtraHPPerLevel();
		maxHP += background.getTotalExtraHPPerLevel();

		return maxHP;
	}

	public List<String> getSheetNotes()
	{
		List<String> sn = new ArrayList<>();

		sn.addAll(charClass.getAllSheetNotes());
		sn.addAll(species.getAllSheetNotes());
		sn.addAll(background.getAllSheetNotes());

		return sn;
	}

	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("name", name);
		json.put("lvl", lvl);
		json.put("prof", prof);

		json.put("charClassName", charClass.getName());
		json.put("charClass", charClass.saveState());
		json.put("speciesName", species.getName());
		json.put("species", species.saveState());
		json.put("backgroundName", background.getName());
		json.put("background", background.saveState());
		json.put("abilityScores", abilityScores.saveState());

		return json;
	}

	public boolean loadState(JSONObject data)
	{
		boolean success = true;
		try
		{
			this.name = data.getString("name");
			this.lvl = data.getInt("lvl");
			this.prof = data.getInt("prof");

			setCharClass(CharacterClass.getByName(data.getString("charClassName")));
			setSpecies(Species.getByName(data.getString("speciesName")));
			// TODO post1.0 - currently backgrounds are just custom, this will be needed
			// once they exist
			// setBackground(Background.getByName(data.getString("backgroundName")));
			setBackground(Background.getCustom());

			if (charClass == null)
			{
				charClass = CharacterClass.getAllClasses().get(0);
				success = false;
			}
			if (species == null)
			{
				species = Species.getAllSpecies().get(0);
				success = false;
			}
			if (background == null)
			{
				background = Background.getAllBackgrounds().get(0);
				success = false;
			}

			success = success && charClass.loadState(data.getJSONObject("charClass"));
			success = success && species.loadState(data.getJSONObject("species"));
			success = success && background.loadState(data.getJSONObject("background"));
			success = success && abilityScores.loadState(data.getJSONObject("abilityScores"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;
		}
		return success;
	}
}
