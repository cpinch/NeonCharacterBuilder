package ncb.main;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ncb.data.AbilityIncrease;
import ncb.data.AbilityScores;
import ncb.data.Feature;
import ncb.data.LeveledText;
import ncb.data.SpellSlots;
import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.DamageType;
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
import ncb.io.JsonDataLoader;

public class CharacterSheet implements AlertsChanges, GetAll, HasState
{
	public static final int baseClassIndex = -1;

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

		// Our first level up is always in our base class
		lvlUpIndexes.add(baseClassIndex);
	}

	@Override
	public List<GetAll> getChildren()
	{
		List<GetAll> children = new ArrayList<>();

		children.add(charClass);
		children.addAll(multiclasses);
		children.add(species);
		children.add(background);

		return children;
	}

	// Completely temporary state
	private int loadedLevel = 0;

	public int getLoadedLevel()
	{
		return loadedLevel;
	}

	// Sub-fields
	private String name = "";
	private CharacterClass charClass;
	private final List<CharacterClass> multiclasses = new ArrayList<>();
	private final List<Integer> lvlUpIndexes = new ArrayList<>();
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

	public List<CharacterClass> getMulticlasses()
	{
		return multiclasses;
	}

	public void addMulticlass(CharacterClass cls)
	{
		List<CharacterClass> old = new ArrayList<>(multiclasses);
		multiclasses.add(cls);
		lvlUpIndexes.add(multiclasses.size() - 1);
		pcs.firePropertyChange(PropertyListener.CLASS, old, multiclasses);
	}

	public void replaceMulticlass(int index, CharacterClass newCls)
	{
		List<CharacterClass> old = new ArrayList<>(multiclasses);
		multiclasses.set(index, newCls);
		pcs.firePropertyChange(PropertyListener.CLASS, old, multiclasses);
	}

	public List<CharacterClass> getAllCharClasses()
	{
		List<CharacterClass> classes = new ArrayList<>();
		classes.add(charClass);
		classes.addAll(multiclasses);
		return classes;
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
		JSONArray multiArr = new JSONArray();
		multiclasses.forEach((c) ->
		{
			JSONObject m = new JSONObject();
			m.put("name", c.getName());
			m.put("state", c.saveState());
		});
		json.put("multiclasses", multiArr);
		JSONArray lvlUpArr = new JSONArray();
		for (int i = 0; i < lvlUpIndexes.size(); i++)
		{
			lvlUpArr.put(lvlUpIndexes.get(i));
		}
		json.put("lvlUpIndexes", lvlUpArr);

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
		multiclasses.clear();
		JsonDataLoader.jsonArrayToObjectArray(data.getJSONArray("multiclasses")).forEach(co ->
		{
			String clsName = co.getString("name");
			CharacterClass cls = CharacterClass.getByName(clsName);
			cls.loadState(co.getJSONObject("state"));
			multiclasses.add(cls);
		});
		lvlUpIndexes.clear();
		JSONArray lvlUpArr = data.getJSONArray("lvlUpIndexes");
		for (int i = 0; i < lvlUpArr.length(); i++)
		{
			lvlUpIndexes.add(lvlUpArr.getInt(i));
		}

		// For use in pdf editing
		this.loadedLevel = getLevel();
	}

	// Calculated fields

	public int getLevel()
	{
		return charClass.getLevel() + multiclasses.stream().mapToInt(cls -> cls.getLevel()).sum();
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

	public List<LeveledText> getAllWeaponProfTexts()
	{
		return getAll(Feature::getWeaponProfTexts);
	}

	public List<String> getAllToolProfs()
	{
		return getAll(Feature::getToolProfs);
	}

	public List<LeveledText> getAllToolProfTexts()
	{
		return getAll(Feature::getToolProfTexts);
	}

	public List<String> getAllVehicleProfs()
	{
		return getAll(Feature::getVehicleProfs);
	}

	public List<LeveledText> getAllVehicleProfTexts()
	{
		return getAll(Feature::getVehicleProfTexts);
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
		List<DamageType> res = getAll(Feature::getResistancesGranted);
		res.addAll(getAll(Feature::getResistancesSelected));

		List<String> resStr = new ArrayList<>();
		resStr.addAll(res.stream().map(r -> r.toString()).toList());

		// Homeworld trait resistances are unique and have to be handled accordingly
		Homeworld h = background.getHomeworld();
		List<String> hwRes = new ArrayList<>();
		for (Map<String, DamageType> hwR : getAllOf(Feature::getResistancesByHomeworld))
		{
			String r = getResistanceForHomeworld(h, hwR);
			if (r != null && !r.isBlank())
			{
				hwRes.add(r);
			}
		}
		resStr.addAll(hwRes);

		return resStr;
	}

	public static String getResistanceForHomeworld(Homeworld h, Map<String, DamageType> traitToResistMap)
	{
		if (h != null)
		{
			if (h.hasTrait("Any"))
			{
				// Extreme edge case, just let the user handle it on the sheet
				return "Choose 1 ("
						+ String.join(",", traitToResistMap.values().stream().map(r -> r.toString()).toList()) + ")";
			}
			else
			{
				for (String trait : h.getTraits())
				{
					if (traitToResistMap.containsKey(trait))
					{
						return traitToResistMap.get(trait).toString();
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

	public Map<Ability, Integer> getTotalAbilityScores()
	{
		Map<Ability, Integer> abls = new HashMap<>();
		for (Ability abl : Ability.realValues())
		{
			abls.put(abl, getTotalAbilityScore(abl));
		}
		return abls;
	}

	public int getTotalAbilityMod(Ability a)
	{
		return AbilityScores.getModFor(getTotalAbilityScore(a));
	}

	public Ability getSpellcastingAbility()
	{
		if (charClass.isSpellcaster())
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
		else if (multiclasses.stream().anyMatch(cls -> cls.isSpellcaster()))
		{
			for (CharacterClass cls : multiclasses)
			{
				if (cls.getSpellcastingAbility() == Ability.Primary)
				{
					return cls.getSelectedPrimary();
				}
				else
				{
					return cls.getSpellcastingAbility();
				}
			}
		}

		// If the class doesn't set a spellcasting ability then either this will return
		// null (in which case the character would be marked as not a spellcaster), or
		// this will return the selected spellcasting ability from the ability scores
		// tab.
		return abilityScores.getSpellcastingAbility();
	}

	public int getSpellSlots(int spellLevel)
	{
		int slots = 0;

		// To decide how to get this, we need a list of all spellcasting classes the
		// character has, except those with weird single slots (like Warlock)
		List<CharacterClass> spellcastingClasses = new ArrayList<>();
		if (charClass.isSpellcaster() && charClass.isFullSlots())
		{
			spellcastingClasses.add(charClass);
		}
		multiclasses.forEach(cls ->
		{
			if (cls.isSpellcaster() && charClass.isFullSlots())
			{
				spellcastingClasses.add(cls);
			}
		});

		if (spellcastingClasses.size() == 1)
		{
			// 1 spellcasting class, easy, use its slots
			slots = spellcastingClasses.get(0).getSpellSlots().getCount(spellLevel);
		}
		else
		{
			// 2+ spellcasting classes, use the sum of their levels, half for half-casters,
			// classes that only go to level 5 spells, and the multiclass spell slots table
			int scLvl = spellcastingClasses.stream().mapToInt(cls ->
			{
				return cls.isFullCaster() ? cls.getLevel() : (int) Math.ceil(cls.getLevel() / 2);
			}).sum();
			return SpellSlots.getMulticlassSlots(scLvl).getCount(spellLevel);
		}

		// Regardless, we /then/ add special single slots to this total, if any
		if (charClass.isSpellcaster() && !charClass.isFullSlots())
		{
			slots += charClass.getSpellSlots().getCount(spellLevel);
		}
		for (CharacterClass cls : multiclasses)
		{
			if (cls.isSpellcaster() && !cls.isFullSlots())
			{
				slots += cls.getSpellSlots().getCount(spellLevel);
			}
		}

		return slots;
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
		return charClass.isSpellcaster() || multiclasses.stream().anyMatch(cls -> cls.isSpellcaster())
				|| species.isSpellcaster() || (background != null && background.isSpellcaster());
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

		if (charClass.getLevel() > 1)
		{
			int normalHPPerLevel = (int) Math.ceil(charClass.getHd() / 2 + 0.5);
			for (int i = 2; i <= charClass.getLevel(); i++)
			{
				maxHP += conMod + normalHPPerLevel + extraHPPerLevel;
			}
		}
		for (CharacterClass cls : multiclasses)
		{
			int normalHPPerLevel = (int) Math.ceil(cls.getHd() / 2 + 0.5);
			for (int i = 1; i <= cls.getLevel(); i++)
			{
				maxHP += conMod + normalHPPerLevel + extraHPPerLevel;
			}
		}

		return maxHP;
	}

	public String getHitDice()
	{
		Map<Integer, Integer> hdLevels = new HashMap<>();

		hdLevels.put(charClass.getHd(), charClass.getLevel());

		for (CharacterClass cls : multiclasses)
		{
			if (hdLevels.containsKey(cls.getHd()))
			{
				hdLevels.put(cls.getHd(), hdLevels.get(cls.getHd()) + cls.getLevel());
			}
			else
			{
				hdLevels.put(cls.getHd(), cls.getLevel());
			}
		}

		return String.join("+", hdLevels.entrySet().stream().map(e -> e.getValue() + "D" + e.getKey()).toList());
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

	public List<LeveledText> getSheetNotes()
	{
		List<LeveledText> notes = new ArrayList<>();
		notes.addAll(getAllOf(Feature::getSheetNotes));

		if (!multiclasses.isEmpty())
		{
			notes.add(new LeveledText(1, "Multiclass Features",
					"5.5E Multiclassing generally gives all features, but a few (Channel, Extra Attack, and Unarmored Defenses) do not stack. See https://www.dndbeyond.com/sources/dnd/basic-rules-2014/customization-options#ClassFeatures for more details on how multiclassing with these features interacts."));
		}

		return notes;
	}

	public List<LeveledText> getFeatureStrings()
	{
		List<LeveledText> fs = new ArrayList<>();

		// Iterate through every child feature, stopping when we hit a feat
		getChildren().forEach(c -> fs.addAll(getFeatureStrings(c)));

		return fs;
	}

	private List<LeveledText> getFeatureStrings(GetAll feature)
	{
		if (feature instanceof Feature && !(feature instanceof Feat))
		{
			List<LeveledText> fs = new ArrayList<>();
			Feature f = (Feature) feature;
			if (!f.getText().isBlank())
			{
				fs.add(new LeveledText(f.getLevel(), f.getName(), f.getName() + " - " + f.getText()));
			}
			f.getChildren().forEach(c -> fs.addAll(getFeatureStrings(c)));
			return fs;
		}
		return Collections.emptyList();

	}

	public List<LeveledText> getFeatStrings()
	{
		List<LeveledText> fs = new ArrayList<>();

		List<Feat> feats = getAllOf(Feature::getFeat);
		for (Feat f : feats)
		{
			List<String> allText = f.getAllOf(Feature::getText);
			fs.add(new LeveledText(f.getLevel(), f.getName(), f.getName() + " - " + String.join(", ", allText)));
		}

		return fs;
	}

	public String getEquipmentItems()
	{
		// TODO Background Loading - If backgrounds can give equipment, need to update
		// this
		return charClass.getEquipmentItems();
	}

	public boolean canMulticlassOutOfCurrentClasses()
	{
		Map<Ability, Integer> abilityScores = getTotalAbilityScores();
		if (!charClass.multiclassPrereqsMet(abilityScores))
		{
			return false;
		}
		for (CharacterClass cls : multiclasses)
		{
			if (!cls.multiclassPrereqsMet(abilityScores))
			{
				return false;
			}
		}
		return true;
	}

	public void levelUp(int classIndex)
	{
		lvlUpIndexes.add(classIndex);
		if (classIndex == baseClassIndex)
		{
			charClass.setLevel(charClass.getLevel() + 1);
		}
		else
		{
			multiclasses.get(classIndex).setLevel(multiclasses.get(classIndex).getLevel() + 1);
		}
	}

	public List<Integer> getLevelUpOrder()
	{
		return lvlUpIndexes;
	}
}
