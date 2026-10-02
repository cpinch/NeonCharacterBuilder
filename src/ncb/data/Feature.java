package ncb.data;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.Skill;
import ncb.data.interfaces.AlertsChanges;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.GetAll;
import ncb.data.interfaces.HasConfig;
import ncb.data.interfaces.HasState;
import ncb.data.loadables.Feat;
import ncb.data.loadables.Language;
import ncb.data.loadables.Selectable;
import ncb.data.loadables.Spell;
import ncb.data.loadables.SpellList;
import ncb.main.PropertyListener;

// When adding new options make sure you add them to all relevant locations:
//- One of Config or State variables
//- Getter/Setter in same location
//- One of Config or State loader
//- Duplication constructor at bottom of file
public class Feature implements AlertsChanges, HasState, HasConfig, GetAll, Cloneable
{
	// Setup stuff
	private static int nId = 1;
	private final int id;
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Customizable parent;

	@Override
	public PropertyChangeSupport getPCS()
	{
		return pcs;
	}

	@Override
	public Customizable getParent()
	{
		return parent;
	}

	@Override
	public void setParent(Customizable p)
	{
		this.parent = p;
	}

	public int getTopLevel()
	{
		Customizable parent = getParent();
		if (parent == null)
		{
			return getLevel();
		}
		else
		{
			if (parent instanceof Feature)
			{
				return ((Feature) parent).getTopLevel();
			}
		}
		return 1;
	}

	public Feature()
	{
		super();
		this.id = nId++;
		addPropertyChangeListener(PropertyListener.getListener());
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public List<? extends GetAll> getChildren()
	{
		List<Feature> children = new ArrayList<>();
		if (selected != null)
		{
			children.add(getSelected());
		}
		if (feat != null)
		{
			children.add(getFeat());
		}
		return children;
	}

	public List<String> getFeatureNamesAtLevel(int lvl)
	{
		List<String> names = new ArrayList<>();
		if (this.getHighestFeature(lvl).getLevel() == lvl)
		{
			names.add(getName());
		}
		if (selected != null)
		{
			names.addAll(selected.getFeatureNamesAtLevel(lvl));
		}
		if (feat != null)
		{
			names.addAll(feat.getFeatureNamesAtLevel(lvl));
		}

		return names;
	}

	@Override
	public String toString()
	{
		return name;
	}

	// Configuration
	private String name = "";
	private int level = 1;
	private String text = "";
	private String sheetNotes = "";
	private int speed = 0;
	private int lvl1Hp = 0;
	private int allLvlHp = 0;
	private boolean halfProfAll = false;
	private boolean initProf = false;
	private final List<Ability> saveProfs = new ArrayList<>();
	private final List<String> weaponProfs = new ArrayList<>();
	private final List<String> tools = new ArrayList<>();
	private final List<String> vehicles = new ArrayList<>();
	private final List<ArmorTraining> armorTrains = new ArrayList<>();
	private final List<Skill> skillProfs = new ArrayList<>();
	private final List<Spell> freeSpells = new ArrayList<>();
	private final List<Language> freeLangs = new ArrayList<>();
	private final List<String> freeRes = new ArrayList<>();
	private final List<Ability> ACAbilities = new ArrayList<>();
	private final List<String> optResistances = new ArrayList<>();
	private int optResistancesCount = 0;
	private final List<Skill> skillOptions = new ArrayList<>();
	private int skillCount = 0;
	private final List<Skill> optSkillExps = new ArrayList<>();
	private int optSkillExpsCount = 0;
	private final List<String> langOptionNames = new ArrayList<>();
	private int langSelectCount = 0;
	private final List<Spell> optSpellChoices = new ArrayList<>();
	private String optSelectableType = "";
	private String featTrait = "";
	private boolean optFeatIgnoreReqs = false;
	private final Map<String, String> resistanceByHomeworldTrait = new HashMap<>();
	private final Map<Skill, Ability> skillsAddExtraAbility = new HashMap<>();
	private final Map<Integer, List<SpellChoice>> spellChoices = new HashMap<>();
	private final List<Feature> upgrades = new ArrayList<>();
	private final List<Ability> initAbilities = new ArrayList<>();
	private final List<Skill> skillsOptsToAddExtraAbl = new ArrayList<>();
	private Ability extraAblForSkillOpt = null;
	private final List<Skill> profOrExp = new ArrayList<>();
	private final List<AbilityIncrease> increasesAbilities = new ArrayList<>();
	private final List<AbilityIncrease> abilityIncreaseOptions = new ArrayList<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateConfig(this.name, name, (v) ->
		{
			this.name = v;
		});
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		updateConfig(this.level, level, (v) ->
		{
			this.level = v;
		});
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		updateConfig(this.text, text, (v) ->
		{
			this.text = v;
		});
	}

	public String getSheetNotes()
	{
		return sheetNotes;
	}

	public void setSheetNotes(String notes)
	{
		updateConfig(this.sheetNotes, notes, (v) ->
		{
			this.sheetNotes = v;
		});
	}

	public int getSpeedMod()
	{
		return speed;
	}

	public void setSpeedMod(int speed)
	{
		updateConfig(this.speed, speed, (v) ->
		{
			this.speed = v;
		});
	}

	public int getExtraHPLvl1()
	{
		return lvl1Hp;
	}

	public void setExtraHPLvl1(int hp)
	{
		updateConfig(lvl1Hp, hp, (v) ->
		{
			this.lvl1Hp = v;
		});
	}

	public int getExtraHPPerLevel()
	{
		return allLvlHp;
	}

	public void setExtraHPPerLevel(int hp)
	{
		updateConfig(allLvlHp, hp, (v) ->
		{
			this.allLvlHp = v;
		});
	}

	public boolean givesHalfProfAll()
	{
		return halfProfAll;
	}

	public void setHalfProfAll(boolean b)
	{
		updateConfig(halfProfAll, b, (v) ->
		{
			this.halfProfAll = v;
		});
	}

	public boolean givesProfToInit()
	{
		return initProf;
	}

	public void setProfToInit(boolean b)
	{
		updateConfig(initProf, b, (v) ->
		{
			this.initProf = v;
		});
	}

	public List<Ability> getSaveProfs()
	{
		return new ArrayList<>(saveProfs);
	}

	public void setSaveProfs(List<Ability> saves)
	{
		updateConfig(saveProfs, saves, (v) ->
		{
			this.saveProfs.clear();
			this.saveProfs.addAll(v);
		});
	}

	public List<String> getWeaponProfs()
	{
		return new ArrayList<>(weaponProfs);
	}

	public void setWeaponProfs(List<String> weapons)
	{
		updateConfig(weaponProfs, weapons, (v) ->
		{
			this.weaponProfs.clear();
			this.weaponProfs.addAll(v);
		});
	}

	public List<String> getToolProfs()
	{
		return new ArrayList<>(tools);
	}

	public void setToolProfs(List<String> tools)
	{
		updateConfig(this.tools, tools, (v) ->
		{
			this.tools.clear();
			this.tools.addAll(v);
		});
	}

	public List<String> getVehicleProfs()
	{
		return new ArrayList<>(vehicles);
	}

	public void setVehicleProfs(List<String> profs)
	{
		this.updateConfig(vehicles, profs, (v) ->
		{
			this.vehicles.clear();
			this.vehicles.addAll(v);
		});
	}

	public List<ArmorTraining> getArmorProfs()
	{
		return new ArrayList<>(armorTrains);
	}

	public void setArmorProfs(List<ArmorTraining> armor)
	{
		updateConfig(armorTrains, armor, (v) ->
		{
			this.armorTrains.clear();
			this.armorTrains.addAll(v);
		});
	}

	public List<Skill> getSkillsGranted()
	{
		return new ArrayList<>(skillProfs);
	}

	public void setSkillsGranted(List<Skill> skills)
	{
		updateConfig(skillProfs, skills, (v) ->
		{
			this.skillProfs.clear();
			this.skillProfs.addAll(v);
		});
	}

	public List<Spell> getSpellsGranted()
	{
		return new ArrayList<>(freeSpells);
	}

	public void setSpellsGranted(List<Spell> spells)
	{
		updateConfig(freeSpells, spells, (v) ->
		{
			this.freeSpells.clear();
			this.freeSpells.addAll(v);
		});
	}

	public List<Language> getLanguagesGranted()
	{
		return new ArrayList<>(freeLangs);
	}

	public void setLanguagesGranted(List<Language> langs)
	{
		updateConfig(freeLangs, langs, (v) ->
		{
			this.freeLangs.clear();
			this.freeLangs.addAll(v);
		});
	}

	public List<String> getResistancesGranted()
	{
		return new ArrayList<>(freeRes);
	}

	public void setResistancesGranted(List<String> res)
	{
		System.out.println("Setting res " + res + " / " + res.size());
		updateConfig(freeRes, res, (v) ->
		{
			this.freeRes.clear();
			this.freeRes.addAll(v);
		});
	}

	public List<Ability> getAbilitiesToAC()
	{
		return new ArrayList<>(ACAbilities);
	}

	public void setAbilitiesToAC(List<Ability> abilities)
	{
		updateConfig(ACAbilities, abilities, (v) ->
		{
			this.ACAbilities.clear();
			this.ACAbilities.addAll(v);
		});
	}

	public List<String> getResistanceOptions()
	{
		return new ArrayList<>(optResistances);
	}

	public void setResistanceOptions(List<String> res)
	{
		updateConfig(optResistances, res, (v) ->
		{
			this.optResistances.clear();
			this.optResistances.addAll(v);
		});
	}

	public int getResistanceOptionsCount()
	{
		return optResistancesCount;
	}

	public void setResistanceOptionsCount(int count)
	{
		updateConfig(optResistancesCount, count, (v) ->
		{
			this.optResistancesCount = v;
		});
	}

	public List<Skill> getSkillSelectionOptions()
	{
		return new ArrayList<>(skillOptions);
	}

	public void setSkillSelectionOptions(List<Skill> skills)
	{
		updateConfig(skillOptions, skills, (v) ->
		{
			this.skillOptions.clear();
			this.skillOptions.addAll(v);
		});
	}

	public int getSkillSelectionCount()
	{
		return skillCount;
	}

	public void setSkillSelectionCount(int count)
	{
		updateConfig(skillCount, count, (v) ->
		{
			this.skillCount = v;
		});
	}

	public List<Skill> getSkillExpertOptions()
	{
		return new ArrayList<>(optSkillExps);
	}

	public void setSkillExpertOptions(List<Skill> skills)
	{
		updateConfig(optSkillExps, skills, (v) ->
		{
			this.optSkillExps.clear();
			this.optSkillExps.addAll(v);
		});
	}

	public int getSkillExpertCount()
	{
		return optSkillExpsCount;
	}

	public void setSkillExpertCount(int count)
	{
		updateConfig(optSkillExpsCount, count, (v) ->
		{
			this.optSkillExpsCount = v;
		});
	}

	public List<Language> getLanguageSelectionOptions()
	{
		return this.langOptionNames.contains("Any") ? Language.getAllLanguages()
				: langOptionNames.stream().map(n -> Language.getByName(n)).toList();
	}

	public void setLanguageSelectionOptions(List<String> langs)
	{
		updateConfig(langOptionNames, langs, (v) ->
		{
			this.langOptionNames.clear();
			this.langOptionNames.addAll(v);
		});
	}

	public int getLanguageSelectionCount()
	{
		return langSelectCount;
	}

	public void setLanguageSelectionCount(int count)
	{
		updateConfig(langSelectCount, count, (v) ->
		{
			this.langSelectCount = v;
		});
	}

	public List<Spell> getSpecificSpellChoices()
	{
		return new ArrayList<>(optSpellChoices);
	}

	public void setSpecificSpellChoices(List<Spell> spells)
	{
		updateConfig(optSpellChoices, spells, (v) ->
		{
			this.optSpellChoices.clear();
			this.optSpellChoices.addAll(v);
		});
	}

	public String getSelectableName()
	{
		return optSelectableType;
	}

	public void setSelectableName(String name)
	{
		updateConfig(optSelectableType, name, (v) ->
		{
			this.optSelectableType = v;
		});
	}

	public String getFeatTraitName()
	{
		return featTrait;
	}

	public void setFeatTraitName(String name)
	{
		updateConfig(featTrait, name, (v) ->
		{
			this.featTrait = v;
		});
	}

	public boolean featIgnoresPrereqs()
	{
		return optFeatIgnoreReqs;
	}

	public void setFeatIgnoresPrereqs(boolean b)
	{
		updateConfig(optFeatIgnoreReqs, b, (v) ->
		{
			this.optFeatIgnoreReqs = v;
		});
	}

	public Map<String, String> getResistancesByHomeworld()
	{
		return new HashMap<>(resistanceByHomeworldTrait);
	}

	public void setResistancesByHomeworld(Map<String, String> resByHome)
	{
		updateMapConfig(resistanceByHomeworldTrait, resByHome, (v) ->
		{
			resistanceByHomeworldTrait.clear();
			resistanceByHomeworldTrait.putAll(v);
		});
	}

	public Map<Skill, Ability> getAbilitiesAddToSkills()
	{
		return new HashMap<>(skillsAddExtraAbility);
	}

	public void setAbilitiesAddToSkills(Map<Skill, Ability> sToA)
	{
		updateMapConfig(skillsAddExtraAbility, sToA, (v) ->
		{
			skillsAddExtraAbility.clear();
			skillsAddExtraAbility.putAll(v);
		});
	}

	public Map<Integer, List<SpellChoice>> getSpellChoices()
	{
		return new HashMap<>(spellChoices);
	}

	public void setSpellChoices(Map<Integer, List<SpellChoice>> spellChoices)
	{
		updateMapConfig(this.spellChoices, spellChoices, (v) ->
		{
			spellChoices.clear();
			spellChoices.putAll(v);
		});
	}

	public List<Feature> getUpgrades()
	{
		return new ArrayList<>(upgrades);
	}

	public void addUpgrade()
	{
		upgrades.add(new Feature(upgrades.isEmpty() ? this : upgrades.get(upgrades.size() - 1)));
		setCustom(true);
	}

	public void removeUpgrade()
	{
		if (!upgrades.isEmpty())
		{
			upgrades.remove(upgrades.size() - 1);
			setCustom(true);
		}
	}

	public boolean hasUpgradeAt(int lvl)
	{
		return upgrades.stream().anyMatch(f -> f.getLevel() == lvl);
	}

	public Feature getHighestFeature(int lvl)
	{
		Feature max = this;
		for (Feature f : upgrades)
		{
			if (f.getLevel() <= lvl && f.getLevel() > max.getLevel())
			{
				max = f;
			}
		}
		return max;
	}

	public List<Ability> getInitAbilities()
	{
		return new ArrayList<>(initAbilities);
	}

	public void setInitAbilities(List<Ability> abls)
	{
		updateConfig(this.initAbilities, abls, (v) ->
		{
			initAbilities.clear();
			initAbilities.addAll(v);
		});
	}

	public List<Skill> getSkillsWAblSkills()
	{
		return new ArrayList<>(skillsOptsToAddExtraAbl);
	}

	public Ability getSkillsWAblAbility()
	{
		return extraAblForSkillOpt;
	}

	public void setSkillsWAbl(List<Skill> skills, Ability abl)
	{
		if (!skillsOptsToAddExtraAbl.equals(skills) || ((extraAblForSkillOpt == null)
				|| (extraAblForSkillOpt != abl && extraAblForSkillOpt.name().equals(abl.name()))))
		{
			skillsOptsToAddExtraAbl.clear();
			skillsOptsToAddExtraAbl.addAll(skills);
			extraAblForSkillOpt = abl;
			setCustom(true);
		}
	}

	public List<Skill> getSkillProfOrExpertise()
	{
		return new ArrayList<>(profOrExp);
	}

	public void setSkillProfOrExpertise(List<Skill> skills)
	{
		updateConfig(this.profOrExp, skills, (v) ->
		{
			profOrExp.clear();
			profOrExp.addAll(v);
		});
	}

	public List<AbilityIncrease> getIncreasedAbilities()
	{
		return new ArrayList<>(increasesAbilities);
	}

	public void setIncreasedAbilities(List<AbilityIncrease> abl)
	{
		updateConfig(increasesAbilities, abl, (v) ->
		{
			increasesAbilities.clear();
			increasesAbilities.addAll(v);
		});
	}

	public List<AbilityIncrease> getAbilityIncreaseOptions()
	{
		return new ArrayList<>(abilityIncreaseOptions);
	}

	public void setAbilityIncreaseOptions(List<AbilityIncrease> abl)
	{
		updateConfig(abilityIncreaseOptions, abl, (v) ->
		{
			abilityIncreaseOptions.clear();
			abilityIncreaseOptions.addAll(v);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "name", name);
		json = putInt(json, "level", level);
		json = putStr(json, "text", text);
		json = putInt(json, "speedMod", speed);
		json = putStr(json, "sheetNotes", sheetNotes);
		json = putInt(json, "extraHPPerLevel", allLvlHp);
		json = putInt(json, "extraHPLvl1", lvl1Hp);
		json = putBool(json, "halfProfAll", halfProfAll);
		json = putBool(json, "profInit", initProf);
		json = putList(json, "saves", saveProfs);
		json = putList(json, "weapons", weaponProfs);
		json = putList(json, "tools", tools);
		json = putList(json, "vehicles", vehicles);
		json = putList(json, "armor", armorTrains);
		json = putList(json, "skills", skillProfs);
		json = putObjList(json, "grantsSpells", freeSpells.stream().map(s -> Spell.saveToJSONObject(s)).toList());
		json = putList(json, "langs", freeLangs);
		json = putList(json, "res", freeRes);
		json = putList(json, "acAbilities", ACAbilities);
		json = putList(json, "resOptions", optResistances);
		json = putList(json, "skillOptions", skillOptions);
		json = putInt(json, "skillCount", skillCount);
		json = putList(json, "skillExpertOptions", optSkillExps);
		json = putInt(json, "skillExpertCount", optSkillExpsCount);
		json = putList(json, "langOptions", langOptionNames);
		json = putInt(json, "langSelectCount", langSelectCount);
		json = putStr(json, "selectable", optSelectableType);
		json = putStr(json, "featTrait", featTrait);
		json = putBool(json, "featIgnores", optFeatIgnoreReqs);
		json = putObjList(json, "specificSpells",
				optSpellChoices.stream().map(s -> Spell.saveToJSONObject(s)).toList());
		json = putMap(json, "resHome", resistanceByHomeworldTrait, "trait", "res");
		// Custom object shape
		if (!spellChoices.isEmpty())
		{
			JSONArray scArr = new JSONArray();
			for (Map.Entry<Integer, List<SpellChoice>> sce : spellChoices.entrySet())
			{
				JSONObject sc = new JSONObject();
				sc.put("level", sce.getKey());
				sc.put("count", sce.getValue().size());
				sc.put("spellList", sce.getValue().get(0).getSpellList().getName());
				scArr.put(sc);
			}
			json.put("spellChoices", scArr);
		}
		json = putMap(json, "addAbilitiesToSkills",
				skillsAddExtraAbility.entrySet().stream()
						.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString())),
				"skill", "ability");
		json = putObjList(json, "upgrades", upgrades.stream().map(u -> u.saveConfig()).toList());
		json = putList(json, "initAbilities", initAbilities.stream().map(a -> a.toString()).toList());
		json = putList(json, "skillsWExtraAbl", skillsOptsToAddExtraAbl.stream().map(s -> s.toString()).toList());
		json = putStr(json, "extraAblForSkillChoices",
				extraAblForSkillOpt == null ? "" : extraAblForSkillOpt.toString());
		json = putList(json, "profOrExp", profOrExp.stream().map(s -> s.toString()).toList());
		json = putObjList(json, "increasesAbilities", increasesAbilities.stream().map(ia -> ia.saveConfig()).toList());
		json = putObjList(json, "abilityIncreaseOptions",
				abilityIncreaseOptions.stream().map(ia -> ia.saveConfig()).toList());

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");
		level = data.optInt("level", 1); // Level defaults to 1 instead of the typical 0
		text = data.optString("text", "");
		speed = data.optInt("speedMod", 0);
		sheetNotes = data.optString("sheetNotes", "");
		allLvlHp = data.optInt("extraHPPerLevel", 0);
		lvl1Hp = data.optInt("extraHPLvl1", 0);
		halfProfAll = data.optBoolean("halfProfAll", false);
		initProf = data.optBoolean("profInit", false);
		saveProfs.clear();
		saveProfs.addAll(getList(data, "saves").stream().map(s -> Ability.valueOf(s)).toList());
		weaponProfs.clear();
		weaponProfs.addAll(getList(data, "weapons"));
		tools.clear();
		tools.addAll(getList(data, "tools"));
		vehicles.clear();
		vehicles.addAll(getList(data, "vehicles"));
		armorTrains.clear();
		armorTrains.addAll(getList(data, "armor").stream().map(s -> ArmorTraining.valueOf(s)).toList());
		skillProfs.clear();
		skillProfs.addAll(getList(data, "skills").stream().map(s -> Skill.skillByName(s)).toList());
		freeSpells.clear();
		freeSpells.addAll(getObjList(data, "grantsSpells").stream().map(o -> Spell.getFromJSONObject(o)).toList());
		freeLangs.clear();
		freeLangs.addAll(getList(data, "langs").stream().map(l -> Language.getByName(l)).toList());
		freeRes.clear();
		freeRes.addAll(getList(data, "res"));
		ACAbilities.clear();
		ACAbilities.addAll(getList(data, "acAbilities").stream().map(s -> Ability.valueOf(s)).toList());
		optResistances.clear();
		optResistances.addAll(getList(data, "resOptions"));
		skillOptions.clear();
		skillOptions.addAll(getList(data, "skillOptions").stream().map(s -> Skill.skillByName(s)).toList());
		skillCount = data.optInt("skillCount", 0);
		optSkillExps.clear();
		optSkillExps.addAll(getList(data, "skillExpertOptions").stream().map(s -> Skill.skillByName(s)).toList());
		optSkillExpsCount = data.optInt("skillExpertCount", 0);
		langOptionNames.clear();
		langOptionNames.addAll(getList(data, "langOptions"));
		langSelectCount = data.optInt("langSelectCount", 0);
		optSelectableType = data.optString("selectable");
		featTrait = data.optString("featTrait");
		optFeatIgnoreReqs = data.optBoolean("featIgnores", false);
		optSpellChoices.clear();
		optSpellChoices
				.addAll(getObjList(data, "specificSpells").stream().map(o -> Spell.getFromJSONObject(o)).toList());
		resistanceByHomeworldTrait.clear();
		resistanceByHomeworldTrait.putAll(getMap(data, "resHome", "trait", "res"));
		JSONArray scArr = data.optJSONArray("spellChoices");
		if (scArr != null)
		{
			for (int i = 0; i < scArr.length(); i++)
			{
				JSONObject sc = scArr.getJSONObject(i);
				int level = sc.getInt("level");
				int count = sc.getInt("count");
				String spellListName = sc.getString("spellList");
				List<SpellChoice> spCh = new ArrayList<>();
				for (int v = 0; v < count; v++)
				{
					spCh.add(new SpellChoice(SpellList.getForClass(spellListName), level));
				}
				spellChoices.put(level, spCh);
			}
		}
		skillsAddExtraAbility.clear();
		skillsAddExtraAbility.putAll(getMap(data, "addAbilitiesToSkills", "skill", "ability").entrySet().stream()
				.collect(Collectors.toMap(e -> Skill.skillByName(e.getKey()), e -> Ability.valueOf(e.getValue()))));
		upgrades.clear();
		getObjList(data, "upgrades").forEach(o ->
		{
			Feature up = new Feature();
			up.loadConfig(o);
			up.isCopy = true;
			up.original = this;
			upgrades.add(up);
		});
		initAbilities.clear();
		initAbilities.addAll(getList(data, "initAbilities").stream().map(s -> Ability.valueOf(s)).toList());
		skillsOptsToAddExtraAbl.clear();
		skillsOptsToAddExtraAbl
				.addAll(getList(data, "skillsWExtraAbl").stream().map(s -> Skill.skillByName(s)).toList());
		String extraAblForSkillOptName = data.optString("extraAblForSkillChoices", "");
		if (!extraAblForSkillOptName.isBlank())
		{
			extraAblForSkillOpt = Ability.valueOf(extraAblForSkillOptName);
		}
		profOrExp.clear();
		profOrExp.addAll(getList(data, "profOrExp").stream().map(s -> Skill.skillByName(s)).toList());
		increasesAbilities.clear();
		increasesAbilities
				.addAll(getObjList(data, "increasesAbilities").stream().map(ia -> new AbilityIncrease(ia)).toList());
		abilityIncreaseOptions.clear();
		abilityIncreaseOptions.addAll(
				getObjList(data, "abilityIncreaseOptions").stream().map(ia -> new AbilityIncrease(ia)).toList());
	}

	// Used for upgrades, as we want to keep the same state
	private boolean isCopy = false;
	private Feature original = null;

	public boolean isCopy()
	{
		return isCopy;
	}

	// State
	private final List<String> resistancesSelected = new ArrayList<>();
	private final List<Skill> skillsSelected = new ArrayList<>();
	private final List<Skill> skillExpertsSelected = new ArrayList<>();
	private final List<Language> languagesSelected = new ArrayList<>();
	private Selectable selected;
	private Feat feat;
	private Spell chosenSpell;
	private Skill skillWAblSelected = null;
	private final List<AbilityIncrease> abilityIncreasesSelected = new ArrayList<>();

	public List<String> getResistancesSelected()
	{
		if (isCopy)
		{
			return original.getResistancesSelected();
		}
		else
		{
			return new ArrayList<>(resistancesSelected);
		}
	}

	public void setResistancesSelected(List<String> res)
	{
		if (isCopy)
		{
			original.setResistancesSelected(res);
		}
		else
		{
			updateWithAlert(resistancesSelected, res, (v) ->
			{
				resistancesSelected.clear();
				resistancesSelected.addAll(v);
			}, PropertyListener.RESISTANCES);
		}
	}

	public List<Skill> getSkillsSelected()
	{
		if (isCopy)
		{
			return original.getSkillsSelected();
		}
		else
		{
			return new ArrayList<>(skillsSelected);
		}
	}

	public void setSkillsSelected(List<Skill> skills)
	{
		if (isCopy)
		{
			original.setSkillsSelected(skills);
		}
		else
		{
			updateWithAlert(skillsSelected, skills, (v) ->
			{
				skillsSelected.clear();
				skillsSelected.addAll(v);
			}, PropertyListener.SKILLPROFS);
		}
	}

	public List<Skill> getSkillExpertsSelected()
	{
		if (isCopy)
		{
			return original.getSkillExpertsSelected();
		}
		else
		{
			return new ArrayList<>(skillExpertsSelected);
		}
	}

	public void setSkillExpertsSelected(List<Skill> skills)
	{
		if (isCopy)
		{
			original.setSkillExpertsSelected(skills);
		}
		else
		{
			updateWithAlert(skillExpertsSelected, skills, (v) ->
			{
				skillExpertsSelected.clear();
				skillExpertsSelected.addAll(v);
			}, PropertyListener.SKILLEXPS);
		}
	}

	public List<Language> getLanguagesSelected()
	{
		if (isCopy)
		{
			return original.getLanguagesSelected();
		}
		else
		{
			return new ArrayList<>(languagesSelected);
		}
	}

	public void setLanguagesSelected(List<Language> langs)
	{
		if (isCopy)
		{
			original.setLanguagesSelected(langs);
		}
		else
		{
			updateWithAlert(languagesSelected, langs, (v) ->
			{
				languagesSelected.clear();
				languagesSelected.addAll(v);
			}, PropertyListener.LANGUAGES);
		}
	}

	public Selectable getSelected()
	{
		if (isCopy)
		{
			return original.getSelected();
		}
		else if (selected != null)
		{
			// Handle Upgrades
			return (Selectable) selected.getHighestFeature(getTopLevel());
		}
		return null;
	}

	public void setSelected(Selectable sel)
	{
		if (isCopy)
		{
			original.setSelected(sel);
		}
		else
		{
			updateWithAlert(selected, sel, (v) ->
			{
				this.selected = v;
				this.selected.setParent(this);
			}, PropertyListener.SELECTED);
		}
	}

	public Feat getFeat()
	{
		if (isCopy)
		{
			return original.getFeat();
		}
		else if (feat != null)
		{
			// Handle Upgrades
			return (Feat) feat.getHighestFeature(getTopLevel());
		}
		return null;
	}

	public void setFeat(Feat ft)
	{
		if (isCopy)
		{
			original.setFeat(ft);
		}
		else
		{
			updateWithAlert(feat, ft, (v) ->
			{
				this.feat = (Feat) v;
				this.feat.setParent(this);
			}, PropertyListener.SELECTED);
		}
	}

	public Spell getChosenSpell()
	{
		if (isCopy)
		{
			return original.getChosenSpell();
		}
		else
		{
			return chosenSpell;
		}
	}

	public void setChosenSpell(Spell s)
	{
		if (isCopy)
		{
			original.setChosenSpell(s);
		}
		else
		{
			updateWithAlert(chosenSpell, s, (v) ->
			{
				this.chosenSpell = v;
			}, PropertyListener.SPELLS);
		}
	}

	public List<Spell> getSpellsSelected()
	{
		if (isCopy)
		{
			return original.getSpellsSelected();
		}
		else
		{
			List<Spell> s = new ArrayList<>();

			if (chosenSpell != null)
			{
				s.add(chosenSpell);
			}

			return s;
		}
	}

	public Skill getSkillWAbilitySelected()
	{
		return skillWAblSelected;
	}

	public void setSkillWAbilitySelected(Skill s)
	{
		if (isCopy)
		{
			original.setSkillWAbilitySelected(s);
		}
		else
		{
			updateWithAlert(skillWAblSelected, s, (v) ->
			{
				this.skillWAblSelected = v;
			}, PropertyListener.SKILLPROFS);
		}
	}

	public Map<Skill, Ability> getSkillsWAblMap()
	{
		Map<Skill, Ability> map = new HashMap<>();
		if (getSkillWAbilitySelected() != null)
		{
			map.put(getSkillWAbilitySelected(), extraAblForSkillOpt);
		}
		return map;
	}

	public List<AbilityIncrease> getAbilityIncreasesSelected()
	{
		return new ArrayList<>(abilityIncreasesSelected);
	}

	public void setAbilityIncreasesSelected(List<AbilityIncrease> abl)
	{
		if (isCopy)
		{
			original.setAbilityIncreasesSelected(abl);
		}
		else
		{
			updateWithAlert(abilityIncreasesSelected, abl, (v) ->
			{
				abilityIncreasesSelected.clear();
				abilityIncreasesSelected.addAll(v);
			}, PropertyListener.ABILITYSCOREINC);
		}
	}

	@Override
	public JSONObject saveState()
	{
		if (isCopy)
		{
			return original.saveState();
		}
		JSONObject json = new JSONObject();

		json = putList(json, "resistanceSelected", resistancesSelected);
		json = putList(json, "skillsSelected", skillsSelected);
		json = putList(json, "skillExpertSelected", skillExpertsSelected);
		json = putList(json, "languagesSelected", languagesSelected);

		if (selected != null)
		{
			JSONObject selObj = new JSONObject();
			selObj.put("name", selected.getName());
			selObj.put("type", selected.getType());
			selObj.put("state", selected.saveState());
			json = putObj(json, "selected", selObj);
		}
		if (feat != null)
		{
			JSONObject ftObj = new JSONObject();
			ftObj.put("name", feat.getName());
			ftObj.put("state", feat.saveState());
			json = putObj(json, "feat", ftObj);
		}
		if (chosenSpell != null)
		{
			json = putObj(json, "chosenSpell", Spell.saveToJSONObject(chosenSpell));
		}
		if (skillWAblSelected != null)
		{
			json = putStr(json, "skillWAbl", skillWAblSelected.toString());
		}
		if (!abilityIncreasesSelected.isEmpty())
		{
			json = putList(json, "abilityIncreases",
					abilityIncreasesSelected.stream().map(ai -> ai.getAbility().toString()).toList());
		}

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		setResistancesSelected(getList(data, "resistanceSelected"));
		setSkillsSelected(getList(data, "skillsSelected").stream().map(s -> Skill.skillByName(s)).toList());
		setSkillExpertsSelected(getList(data, "skillExpertSelected").stream().map(s -> Skill.skillByName(s)).toList());
		setLanguagesSelected(getList(data, "languagesSelected").stream().map(l -> Language.getByName(l)).toList());
		JSONObject selectedObj = data.optJSONObject("selected");
		if (selectedObj != null)
		{
			Selectable sel = Selectable.getSelectableByTypeAndName(selectedObj.getString("type"),
					selectedObj.getString("name"));
			sel.loadState(selectedObj.getJSONObject("state"));
			setSelected(sel);
		}
		JSONObject featObj = data.optJSONObject("feat");
		if (featObj != null)
		{
			Feat ft = Feat.getByName(featObj.getString("name"));
			ft.loadState(featObj.getJSONObject("state"));
			setFeat(ft);
		}
		JSONObject spellObj = data.optJSONObject("chosenSpell");
		if (spellObj != null)
		{
			setChosenSpell(Spell.getFromJSONObject(spellObj));
		}
		String skillWAblSelectedName = data.optString("skillWAbl");
		if (!skillWAblSelectedName.isBlank())
		{
			setSkillWAbilitySelected(Skill.skillByName(skillWAblSelectedName));
		}
		setAbilityIncreasesSelected(getList(data, "abilityIncreases").stream().map(a ->
		{
			Ability abl = Ability.valueOf(a);
			for (AbilityIncrease ai : abilityIncreaseOptions)
			{
				if (ai.getAbility() == abl)
				{
					return ai;
				}
			}
			return abilityIncreaseOptions.get(0);
		}).toList());
	}

	public Feature(Feature original)
	{
		this();

		isCopy = true;
		this.original = original;

		// Copy all configuration
		this.name = original.name;
		this.level = original.level + 1;
		this.text = original.text;
		this.sheetNotes = original.sheetNotes;
		this.speed = original.speed;
		this.lvl1Hp = original.lvl1Hp;
		this.allLvlHp = original.allLvlHp;
		this.halfProfAll = original.halfProfAll;
		this.initProf = original.initProf;
		this.saveProfs.addAll(original.saveProfs);
		this.weaponProfs.addAll(original.weaponProfs);
		this.tools.addAll(original.tools);
		this.vehicles.addAll(original.vehicles);
		this.armorTrains.addAll(original.armorTrains);
		this.skillProfs.addAll(original.skillProfs);
		this.freeSpells.addAll(original.freeSpells);
		this.freeLangs.addAll(original.freeLangs);
		this.freeRes.addAll(original.freeRes);
		this.ACAbilities.addAll(original.ACAbilities);
		this.optResistances.addAll(original.optResistances);
		this.optResistancesCount = original.optResistancesCount;
		this.skillOptions.addAll(original.skillOptions);
		this.skillCount = original.skillCount;
		this.optSkillExps.addAll(original.optSkillExps);
		this.optSkillExpsCount = original.optSkillExpsCount;
		this.langOptionNames.addAll(original.langOptionNames);
		this.langSelectCount = original.langSelectCount;
		this.optSpellChoices.addAll(original.optSpellChoices);
		this.optSelectableType = original.optSelectableType;
		this.featTrait = original.featTrait;
		this.optFeatIgnoreReqs = original.optFeatIgnoreReqs;
		this.resistanceByHomeworldTrait.putAll(original.resistanceByHomeworldTrait);
		this.skillsAddExtraAbility.putAll(original.skillsAddExtraAbility);
		this.spellChoices.putAll(original.spellChoices);
		// We, obviously, do not copy upgrades
	}
}
