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
import ncb.data.loadables.Homeworld;
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
			children.add(selected.getHighestFeature(getTopLevel()));
		}
		if (feat != null)
		{
			children.add(feat.getHighestFeature(getTopLevel()));
		}
		return children;
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
		return saveProfs;
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
		return weaponProfs;
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
		return tools;
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
		return vehicles;
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
		return armorTrains;
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
		return skillProfs;
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
		return freeSpells;
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
		return freeLangs;
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
		return freeRes;
	}

	public void setResistancesGranted(List<String> res)
	{
		updateConfig(freeRes, res, (v) ->
		{
			this.freeRes.clear();
			this.freeRes.addAll(v);
		});
	}

	public List<Ability> getAbilitiesToAC()
	{
		return ACAbilities;
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
		return optResistances;
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
		return skillOptions;
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
		return optSkillExps;
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
		return optSpellChoices;
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
		return resistanceByHomeworldTrait;
	}

	public String getResistanceByHomeworld(Homeworld h)
	{
		if (h != null)
		{
			for (String trait : h.getTraits())
			{
				// TODO - I think this doesn't work for Polygania with it's "Any" trait, then we
				// need to select right?
				if (resistanceByHomeworldTrait.containsKey(trait))
				{
					return resistanceByHomeworldTrait.get(trait);
				}
			}
		}
		return "";
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
		return skillsAddExtraAbility;
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
		return spellChoices;
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
		return upgrades;
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
		json = putMap(json, "resHome", resistanceByHomeworldTrait, "res", "trait");
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

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");
		level = data.optInt("level", 1); // Level defaults to 1 instead of the typical 0
		text = data.optString("text", "");
		speed = data.optInt("speedMod", 0);
		// TODO - legacy, decom once species are updated
		if (speed == 0)
		{
			speed = data.optInt("speed", 0);
		}
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
		resistanceByHomeworldTrait.putAll(getMap(data, "resHome", "res", "trait"));
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

	public List<String> getResistancesSelected()
	{
		if (isCopy)
		{
			return original.getResistancesSelected();
		}
		else
		{
			return resistancesSelected;
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
			return skillsSelected;
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
			return skillExpertsSelected;
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
			return languagesSelected;
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
		else
		{
			return selected;
		}
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
			}, PropertyListener.SELECTED);
		}
	}

	public Feat getFeat()
	{
		if (isCopy)
		{
			return original.getFeat();
		}
		else
		{
			return feat;
		}
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
				this.chosenSpell = s;
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

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		resistancesSelected.clear();
		resistancesSelected.addAll(getList(data, "resistanceSelected"));
		skillsSelected.clear();
		skillsSelected.addAll(getList(data, "skillsSelected").stream().map(s -> Skill.skillByName(s)).toList());
		skillExpertsSelected.clear();
		skillExpertsSelected
				.addAll(getList(data, "skillExpertSelected").stream().map(s -> Skill.skillByName(s)).toList());
		languagesSelected.clear();
		languagesSelected.addAll(getList(data, "languagesSelected").stream().map(l -> Language.getByName(l)).toList());
		JSONObject selectedObj = data.optJSONObject("selected");
		if (selectedObj != null)
		{
			selected = Selectable.getSelectableByTypeAndName(selectedObj.getString("type"),
					selectedObj.getString("name"));
			selected.loadState(selectedObj.getJSONObject("state"));
		}
		JSONObject featObj = data.optJSONObject("feat");
		if (featObj != null)
		{
			feat = Feat.getByName(featObj.getString("name"));
			feat.loadState(featObj.getJSONObject("state"));
		}
		JSONObject spellObj = data.optJSONObject("chosenSpell");
		if (spellObj != null)
		{
			chosenSpell = Spell.getFromJSONObject(spellObj);
		}
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
