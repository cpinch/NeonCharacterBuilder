package nocb.main;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import nocb.data.Ability;
import nocb.data.AbilityScores;
import nocb.data.AlertsChanges;
import nocb.data.ArmorProf;
import nocb.data.Background;
import nocb.data.CharacterClass;
import nocb.data.Feat;
import nocb.data.Language;
import nocb.data.Proficiency;
import nocb.data.Skill;
import nocb.data.Species;
import nocb.data.Spell;

public class CharacterSheet extends AlertsChanges
{
	private String name = "";

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
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
		if (charClass == null || (c != null && !charClass.getName().equals(c.getName())))
		{
			if (charClass != null)
			{
				// Transfer the old class level over
				c.setLevel(charClass.getLevel());
			}
			CharacterClass old = charClass;
			this.charClass = c;
			pcs.firePropertyChange(PropertyListener.CLASS, old, c);
		}
	}

	public Species getSpecies()
	{
		return species;
	}

	public void setSpecies(Species s)
	{
		if (species == null || (s != null && !species.getName().equals(s.getName())))
		{
			Species old = species;
			this.species = s;
			species.setupGetCharLevel(() -> getLevel());
			pcs.firePropertyChange(PropertyListener.SPECIES, old, s);
		}
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

	public int getLevel()
	{
		// TODO multiclass - sum levels of all classes
		return charClass.getLevel();
	}

	public int getProficiency()
	{
		return Proficiency.getProfForLevel(getLevel());
	}

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
		else if (charClass.givesHalfProfAll() || charClass.getSubclass().givesHalfProfAll())
		{
			total += Math.floorDiv(getProficiency(), 2);
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

		allFeats.addAll(background.getAllFeats());
		allFeats.addAll(species.getAllFeats());
		allFeats.addAll(charClass.getAllFeats());

		return allFeats;
	}

	public List<Skill> getAllSkillProfs()
	{
		List<Skill> allSkills = new ArrayList<>();

		allSkills.addAll(background.getAllSkills());
		allSkills.addAll(species.getAllSkills());
		allSkills.addAll(charClass.getAllSkills());

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
		List<String> allWeapons = new ArrayList<>();

		// Neither species nor background ever give weapon proficiencies
		allWeapons.addAll(charClass.getWeaponProfs());

		return allWeapons;
	}

	public List<String> getAllToolProfs()
	{
		List<String> allToolProfs = new ArrayList<>();

		allToolProfs.addAll(background.getAllToolProfs());
		allToolProfs.addAll(species.getAllToolProfs());
		allToolProfs.addAll(charClass.getAllToolProfs());

		return allToolProfs;
	}

	public String getAllVehicleProfs()
	{
		// Neither class nor species ever give vehicle proficiencies
		return background.getVehicleProfs();
	}

	public List<ArmorProf> getAllArmorProfs()
	{
		List<ArmorProf> armorProfs = new ArrayList<>();

		// Neither species nor background ever give armor proficiencies
		armorProfs.addAll(charClass.getArmorProfs());

		return armorProfs;
	}

	public List<Language> getAllLanguages()
	{
		List<Language> langs = new ArrayList<>();

		langs.addAll(background.getAllLanguages());
		// Species never gives languages
		langs.addAll(charClass.getAllLanguages());

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

		allRes.addAll(background.getAllResistances(background.getHomeworld()));
		allRes.addAll(species.getAllResistances(background.getHomeworld()));
		allRes.addAll(charClass.getAllResistances(background.getHomeworld()));

		return allRes;
	}

	public int getTotalAbilityScore(Ability a)
	{
		// Handle "Primary"
		if (a.equals(Ability.Primary))
		{
			return getTotalAbilityScore(charClass.getSelectedPrimary());
		}

		// TODO lvl 4 - Need to query class features for ability score increases
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
		int ac = 10 + getTotalAbilityMod(Ability.Dex);

		if (!charClass.getAllAbilitiesToAC().isEmpty())
		{
			// Compare each option, returning the max
			for (List<Ability> abilitiesToAC : charClass.getAllAbilitiesToAC())
			{
				int newAC = 10;
				for (Ability a : abilitiesToAC)
				{
					newAC += getTotalAbilityMod(a);
				}
				if (newAC > ac)
				{
					ac = newAC;
				}
			}
		}

		return ac;
	}

	public boolean isSpellcaster()
	{
		return charClass.isSpellcaster() || species.isSpellcaster() || background.makesSpellcaster();
	}

	public List<Spell> getAllGrantedSpells()
	{
		List<Spell> gs = new ArrayList<>();

		gs.addAll(background.getAllSpellsGranted());
		gs.addAll(species.getAllSpellsGranted());
		gs.addAll(charClass.getAllSpellsGranted());

		return gs;
	}

	public List<Spell> getAllSpells()
	{
		List<Spell> gs = new ArrayList<>();

		gs.addAll(background.getAllSpellsGranted());
		gs.addAll(species.getAllSpellsGranted());
		gs.addAll(charClass.getAllSpells());

		return gs;
	}

	public int getSpeed()
	{
		return charClass.getTotalSpeedMod() + species.getTotalSpeedMod() + background.getTotalSpeedMod();
	}

	public int getMaxHP()
	{
		// Neither species nor background affects hit points

		int conMod = getTotalAbilityMod(Ability.Con);
		int extraHPPerLevel = charClass.getTotalExtraHPPerLevel();
		int hd = getCharClass().getHd();

		int maxHP = hd + conMod + extraHPPerLevel + charClass.getTotalExtraHPLvl1();

		// TODO Multiclass changes needed
		if (getLevel() > 1)
		{
			int normalHPPerLevel = (int) Math.ceil(charClass.getHd() / 2 + 0.5);
			for (int i = 2; i <= getLevel(); i++)
			{
				maxHP += conMod + normalHPPerLevel + extraHPPerLevel;
			}
		}

		return maxHP;
	}

	public int getInitiative()
	{
		int init = getTotalAbilityMod(Ability.Dex);

		if (charClass.getAnyGivesProfToInit() || species.getAnyGivesProfToInit())
		{
			init += getProficiency();
		}

		return init;
	}

	public List<String> getSheetNotes()
	{
		List<String> sn = new ArrayList<>();

		sn.addAll(background.getAllSheetNotes());
		sn.addAll(species.getAllSheetNotes());
		sn.addAll(charClass.getAllSheetNotes());

		return sn;
	}

	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("name", name);

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

			setCharClass(CharacterClass.getByName(data.getString("charClassName")));
			setSpecies(Species.getByName(data.getString("speciesName")));
			// TODO backgrounds - currently backgrounds are just custom, this will be needed
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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		super.addPropertyChangeListener(l);
		charClass.addPropertyChangeListener(l);
		species.addPropertyChangeListener(l);
		background.addPropertyChangeListener(l);
		// abilityScores.addPropertyChangeListener(l);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		super.removePropertyChangeListener(l);
		charClass.removePropertyChangeListener(l);
		species.removePropertyChangeListener(l);
		background.removePropertyChangeListener(l);
		// abilityScores.removePropertyChangeListener(l);
	}
}
