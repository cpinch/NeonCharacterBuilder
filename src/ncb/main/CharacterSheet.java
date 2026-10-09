package ncb.main;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import ncb.data.AbilityIncrease;
import ncb.data.AbilityScores;
import ncb.data.Feature;
import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.Skill;
import ncb.data.hardcoded.Proficiency;
import ncb.data.interfaces.AlertsChanges;
import ncb.data.interfaces.GetAll;
import ncb.data.interfaces.HasState;
import ncb.data.loadables.Background;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Feat;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Language;
import ncb.data.loadables.Species;
import ncb.data.loadables.Spell;

public class CharacterSheet implements AlertsChanges, GetAll, HasState
{
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public PropertyChangeSupport getPCS()
	{
		return pcs;
	}

	public CharacterSheet()
	{
		super();
		addPropertyChangeListener(PropertyListener.getListener());
	}

	@Override
	public List<GetAll> getChildren()
	{
		List<GetAll> children = new ArrayList<>();

		children.add(charClass);
		children.add(species);
		children.add(background);

		return children;
	}

	// Sub-fields
	private String name = "";
	private CharacterClass charClass;
	private Species species;
	private Background background;
	private final AbilityScores abilityScores = new AbilityScores();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateWithAlert(this.name, name, (v) ->
		{
			this.name = v;
		}, PropertyListener.NAME);
	}

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
		if (background == null || (b != null && !background.getName().equals(b.getName())))
		{
			Background old = background;
			this.background = b;
			pcs.firePropertyChange(PropertyListener.BACKGROUND, old, b);
		}
	}

	public AbilityScores getAbilityScores()
	{
		return abilityScores;
	}

	@Override
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

	@Override
	public void loadState(JSONObject data)
	{
		this.name = data.getString("name");

		setCharClass(CharacterClass.getByName(data.getString("charClassName")));
		setSpecies(Species.getByName(data.getString("speciesName")));
		// TODO Background Loading
		// setBackground(Background.getByName(data.getString("backgroundName")));
		setBackground(Background.getCustom());

		// Safety
		if (charClass == null)
		{
			charClass = CharacterClass.getAllClasses().get(0);
		}
		if (species == null)
		{
			species = Species.getAllSpecies().get(0);
		}
		if (background == null)
		{
			background = Background.getAllBackgrounds().get(0);
		}

		charClass.loadState(data.getJSONObject("charClass"));
		species.loadState(data.getJSONObject("species"));
		background.loadState(data.getJSONObject("background"));
		abilityScores.loadState(data.getJSONObject("abilityScores"));
	}

	// Calculated fields

	public int getLevel()
	{
		// TODO Multiclass
		return charClass.getLevel();
	}

	public int getProficiency()
	{
		return Proficiency.getProfForLevel(getLevel());
	}

	public boolean saveProficient(Ability a)
	{
		List<Ability> profSaves = getAll(Feature::getSaveProfs);
		return profSaves.contains(a);
	}

	public int getTotalAbilitySave(Ability a)
	{
		return getTotalAbilityMod(a) + (saveProficient(a) ? getProficiency() : 0);
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
		else if (getAny(Feature::givesHalfProfAll))
		{
			total += Math.floorDiv(getProficiency(), 2);
		}
		Map<Skill, Ability> abilityAdds = getAllMap(Feature::getAbilitiesAddToSkills);
		abilityAdds.putAll(getAllMap(Feature::getSkillsWAblMap));
		if (abilityAdds.containsKey(s))
		{
			total += getTotalAbilityMod(abilityAdds.get(s)) > 0 ? getTotalAbilityMod(abilityAdds.get(s)) : 1;
		}
		return total;
	}

	public List<Feat> getAllFeats()
	{
		return getAllOf(Feature::getFeat);
	}

	public List<Skill> getAllSkillProfs()
	{
		List<Skill> profSkills = getAll(Feature::getSkillsGranted);
		profSkills.addAll(getAll(Feature::getSkillsSelected));
		profSkills.addAll(getAll(Feature::getSkillsWAblSkills));

		List<Skill> profOrExpSkills = getAll(Feature::getSkillProfOrExpertise);
		for (Skill s : profOrExpSkills)
		{
			if (!profSkills.contains(s))
			{
				profSkills.add(s);
			}
		}

		if (profSkills.contains(Skill.All))
		{
			return Arrays.asList(Skill.realValues());
		}

		return profSkills;
	}

	public boolean skillProficient(Skill s)
	{
		return getAllSkillProfs().contains(s);
	}

	public List<Skill> getAllSkillExperts()
	{
		List<Skill> expSkills = getAll(Feature::getSkillExpertsSelected);

		List<Skill> profSkills = getAll(Feature::getSkillsGranted);
		profSkills.addAll(getAll(Feature::getSkillsSelected));
		profSkills.addAll(getAll(Feature::getSkillsWAblSkills));
		List<Skill> profOrExpSkills = getAll(Feature::getSkillProfOrExpertise);
		for (Skill s : profOrExpSkills)
		{
			if (profSkills.contains(s))
			{
				expSkills.add(s);
			}
		}

		List<Skill> expGSkills = getAll(Feature::getSkillExpsGranted);
		for (Skill prof : getAllSkillProfs())
		{
			if (!expSkills.contains(prof) && (expGSkills.contains(prof) || expGSkills.contains(Skill.All)))
			{
				expSkills.add(prof);
			}
		}

		return expSkills;
	}

	public boolean skillExpert(Skill s)
	{
		return getAllSkillExperts().contains(s);
	}

	public List<String> getAllWeaponProfs()
	{
		return getAll(Feature::getWeaponProfs);
	}

	public List<String> getAllToolProfs()
	{
		return getAll(Feature::getToolProfs);
	}

	public List<String> getAllVehicleProfs()
	{
		return getAll(Feature::getVehicleProfs);
	}

	public List<ArmorTraining> getAllArmorProfs()
	{
		return getAll(Feature::getArmorProfs);
	}

	public List<Language> getAllLanguages()
	{
		List<Language> langs = getAll(Feature::getLanguagesGranted);
		langs.addAll(getAll(Feature::getLanguagesSelected));
		return langs;
	}

	public int getAllNotes()
	{
		// Species never gives notes
		return charClass.getEquipmentNotes() + background.getNotes();
	}

	public List<String> getAllResistances()
	{
		List<String> res = getAll(Feature::getResistancesGranted);
		res.addAll(getAll(Feature::getResistancesSelected));
		// Homeworld trait resistances are unique and have to be handled accordingly
		Homeworld h = background.getHomeworld();
		getAllOf(Feature::getResistancesByHomeworld).forEach(hwRes -> res.add(getResistanceForHomeworld(h, hwRes)));

		return res;
	}

	public static String getResistanceForHomeworld(Homeworld h, Map<String, String> traitToResistMap)
	{
		if (h != null)
		{
			if (h.hasTrait("Any"))
			{
				// Extreme edge case, just let the user handle it on the sheet
				return "Choose 1 (" + String.join(",", traitToResistMap.values()) + ")";
			}
			else
			{
				for (String trait : h.getTraits())
				{
					if (traitToResistMap.containsKey(trait))
					{
						return traitToResistMap.get(trait);
					}
				}
			}
		}
		return "";
	}

	public int getTotalAbilityScore(Ability a)
	{
		// Handle "Primary"
		if (a.equals(Ability.Primary))
		{
			return getTotalAbilityScore(charClass.getSelectedPrimary());
		}

		int score = abilityScores.getTotalScoreFor(a);

		// Handle ability score increases
		List<AbilityIncrease> ablIncreases = getAll(Feature::getIncreasedAbilities);
		ablIncreases.addAll(getAll(Feature::getAbilityIncreasesSelected));

		for (AbilityIncrease ai : ablIncreases)
		{
			if (ai.getAbility() == a && score < ai.getMax())
			{
				score += ai.getAmount();
				if (score > ai.getMax())
				{
					score = ai.getMax();
				}
			}
		}

		return score;
	}

	public int getTotalAbilityMod(Ability a)
	{
		return AbilityScores.getModFor(getTotalAbilityScore(a));
	}

	public Ability getSpellcastingAbility()
	{
		if (charClass.getSpellcastingAbility() != null)
		{
			if (charClass.getSpellcastingAbility() == Ability.Primary)
			{
				return charClass.getSelectedPrimary();
			}
			else
			{
				return charClass.getSpellcastingAbility();
			}
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

	public int getSpellSlots(int spellLevel)
	{
		// TODO Multiclass
		return charClass.getSpellSlots().getCount(spellLevel);
	}

	public int getAC()
	{
		// This just handles basic unarmored ac
		int ac = 10 + getTotalAbilityMod(Ability.Dex);

		List<List<Ability>> acAbls = getEach(Feature::getAbilitiesToAC);
		if (!acAbls.isEmpty())
		{
			// Compare each option, returning the max
			for (List<Ability> abilitiesToAC : acAbls)
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
		return charClass.getSpellcastingAbility() != null || !species.getAll(Feature::getSpellsGranted).isEmpty()
				|| background.getAll(Feature::getSpellsGranted).isEmpty();
	}

	public List<Spell> getAllGrantedSpells()
	{
		return getAll(Feature::getSpellsGranted);
	}

	public List<Spell> getAllSpells()
	{
		List<Spell> gs = getAllGrantedSpells();
		gs.addAll(getAll(Feature::getSpellsSelected));
		return gs;
	}

	public int getSpeed()
	{
		return getTotal(Feature::getSpeedMod);
	}

	public int getMaxHP()
	{
		int conMod = getTotalAbilityMod(Ability.Con);
		int extraHPPerLevel = getTotal(Feature::getExtraHPPerLevel);
		int extraHPLevel1 = getTotal(Feature::getExtraHPLvl1);
		int hd = getCharClass().getHd();

		int maxHP = hd + conMod + extraHPPerLevel + extraHPLevel1;

		// TODO Multiclass
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

	public String getHitDice()
	{
		int level = getLevel();
		int hd = getCharClass().getHd();

		// TODO Multiclass
		return level + "D" + hd;
	}

	public int getInitiative()
	{
		int init = getTotalAbilityMod(Ability.Dex);

		// If we have the other of having one or more other abilities to init, get the
		// largest combination
		List<List<Ability>> initAbls = getEach(Feature::getInitAbilities);
		if (!initAbls.isEmpty())
		{
			for (List<Ability> abls : initAbls)
			{
				int val = 0;
				for (Ability a : abls)
				{
					val += a == Ability.Primary ? getTotalAbilityMod(charClass.getSelectedPrimary())
							: getTotalAbilityMod(a);
				}
				if (val > init)
				{
					init = val;
				}
			}
		}

		if (getAny(Feature::givesProfToInit))
		{
			init += getProficiency();
		}

		return init;
	}

	public List<String> getSheetNotes()
	{
		return getAllOf(Feature::getSheetNotes);
	}

	public List<String> getFeatureStrings()
	{
		List<String> fs = new ArrayList<>();

		// Iterate through every child feature, stopping when we hit a feat
		getChildren().forEach(c -> fs.addAll(getFeatureStrings(c)));

		return fs;
	}

	private List<String> getFeatureStrings(GetAll feature)
	{
		if (feature instanceof Feature && !(feature instanceof Feat))
		{
			List<String> fs = new ArrayList<>();
			Feature f = (Feature) feature;
			if (!f.getText().isBlank())
			{
				fs.add(f.getName() + " - " + f.getText());
			}
			f.getChildren().forEach(c -> fs.addAll(getFeatureStrings(c)));
			return fs;
		}
		return Collections.emptyList();

	}

	public List<String> getFeatStrings()
	{
		List<String> fs = new ArrayList<>();

		List<Feat> feats = getAllOf(Feature::getFeat);
		for (Feat f : feats)
		{
			List<String> allText = f.getAllOf(Feature::getText);
			fs.add(f.getName() + " - " + String.join(", ", allText));
		}

		return fs;
	}

	public String getEquipmentItems()
	{
		// TODO Background Loading - If backgrounds can give equipment, need to update
		// this
		return charClass.getEquipmentItems();
	}
}
