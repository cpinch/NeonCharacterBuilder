package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;

import ncb.data.ClassEquipment;
import ncb.data.ClassSpells;
import ncb.data.Feature;
import ncb.data.SpellChoice;
import ncb.data.enums.Ability;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.GetAll;
import ncb.main.PropertyListener;

public class CharacterClass extends Feature
{
	private boolean custom = false;

	@Override
	public boolean isCustom()
	{
		return custom;
	}

	@Override
	public void setCustom(boolean b)
	{
		this.custom = b;
	}

	@Override
	public Customizable getParent()
	{
		return null;
	}

	@Override
	public void setParent(Customizable p)
	{
	}

	public CharacterClass()
	{
		super();
		classSpells.setParent(this);
	}

	@Override
	public List<? extends GetAll> getChildren()
	{
		return getClassFeatures();
	}

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allClasses.stream().anyMatch(cls -> cls.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		CharacterClass newCls = new CharacterClass();
		newCls.loadConfig(data);
		newCls.setCustom(custom);
		allClasses.add(newCls);
	}

	public void addClassFeature(String name)
	{
		Feature f = new Feature();
		f.setName(name);
		f.setParent(this);
		classFeatures.add(f);
		setCustom(true);
	}

	public void removeClassFeature(int id)
	{
		Optional<Feature> fOpt = classFeatures.stream().filter(f -> f.getId() == id).findAny();
		if (fOpt.isPresent())
		{
			classFeatures.remove(fOpt.get());
			setCustom(true);
		}
	}

	public void removeLastClassFeature()
	{
		classFeatures.remove(classFeatures.size() - 1);
		setCustom(true);
	}

	@Override
	public List<Spell> getSpellsSelected()
	{
		List<Spell> spells = super.getSpellsSelected();
		spells.addAll(classSpells.getAllSelectedSpells(level));
		return spells;
	}

	public ClassSpells getClassSpells()
	{
		return classSpells;
	}

	/**
	 * We need to grab the spell choices from its ClassSkills. This is used by the
	 * getAll that is called by SpellsPanel, and we want that to keep using the
	 * getAll so it grabs from class features too
	 */
	public List<SpellChoice> getSpellChoicesByLevel(int spellLevel)
	{
		return classSpells.getSpellChoicesByLevel(level, spellLevel);
	}

	/**
	 * This is called by the FeaturePanel and FeatureEditPanel, neither of which
	 * classes uses
	 */
	@Override
	public Map<Integer, List<SpellChoice>> getSpellChoices()
	{
		throw new UnsupportedOperationException("CharacterClass must go through the ClassSpells class.");
	}

	public String getEquipmentItems()
	{
		if (equipment.isEmpty())
		{
			return "";
		}
		return equipment.get(selectedEquipmentIndex).getItems();
	}

	public int getEquipmentNotes()
	{
		if (equipment.isEmpty())
		{
			return 0;
		}
		return equipment.get(selectedEquipmentIndex).getNotes();
	}

	// Loading
	private static final List<CharacterClass> allClasses = new ArrayList<>();

	public static List<CharacterClass> getAllClasses()
	{
		return allClasses;
	}

	public static CharacterClass getByName(String name)
	{
		for (CharacterClass cc : allClasses)
		{
			if (cc.getName().equals(name))
			{
				return cc;
			}
		}
		System.err.println("Unknown class " + name);
		return null;
	}

	public static CharacterClass getById(int id)
	{
		for (CharacterClass cc : allClasses)
		{
			if (cc.getId() == id)
			{
				return cc;
			}
		}
		System.err.println("Unknown class id " + id);
		return null;
	}

	public static void sortAll()
	{
		allClasses.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static CharacterClass addNewClass(String newName)
	{
		if (!newName.isBlank())
		{
			CharacterClass cc = new CharacterClass();
			cc.setName(newName);
			allClasses.add(cc);
			return cc;
		}
		return null;
	}

	// Configuration
	private String desc = "";
	private int hd = 6;
	private Ability spellcastingAbility = null;
	private final List<Ability> primaryAbilities = new ArrayList<>();
	private final List<Ability> primaryAbilityOptions = new ArrayList<>();
	private final List<ClassEquipment> equipment = new ArrayList<>();
	private final List<Feature> classFeatures = new ArrayList<>();

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		updateConfig(this.desc, desc, (v) ->
		{
			this.desc = v;
		});
	}

	public int getHd()
	{
		return hd;
	}

	public void setHd(int hd)
	{
		updateConfig(this.hd, hd, (v) ->
		{
			this.hd = v;
		});
	}

	public Ability getSpellcastingAbility()
	{
		return spellcastingAbility;
	}

	public void setSpellcastingAbility(Ability abl)
	{
		updateConfig(this.spellcastingAbility, abl, (v) ->
		{
			this.spellcastingAbility = v;
		});
	}

	public List<Ability> getPrimaryAbilities()
	{
		return primaryAbilities;
	}

	public void setPrimaryAbilities(List<Ability> abilities)
	{
		updateConfig(this.primaryAbilities, abilities, (v) ->
		{
			this.primaryAbilities.clear();
			this.primaryAbilities.addAll(v);
		});
	}

	public List<Ability> getPrimaryAbilityOptions()
	{
		return primaryAbilityOptions;
	}

	public void setPrimaryAbilityOptions(List<Ability> abilities)
	{
		updateConfig(this.primaryAbilityOptions, abilities, (v) ->
		{
			this.primaryAbilityOptions.clear();
			this.primaryAbilityOptions.addAll(v);
		});
	}

	public List<ClassEquipment> getEquipmentOptions()
	{
		return equipment;
	}

	public void setEquipmentOptions(List<ClassEquipment> equipment)
	{
		updateConfig(this.equipment, equipment, (v) ->
		{
			this.equipment.clear();
			this.equipment.addAll(v);
		});
	}

	@Override
	public List<String> getFeatureNamesAtLevel(int lvl)
	{
		List<String> names = new ArrayList<>();
		classFeatures.forEach(f -> names.addAll(f.getFeatureNamesAtLevel(lvl)));

		// Hardcoding when classes get subclasses
		if (lvl == 3)
		{
			names.add("Gain a Subclass");
		}

		return names;
	}

	public List<Feature> getAllClassFeatures()
	{
		classFeatures.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
		return classFeatures;
	}

	public List<Feature> getClassFeatures()
	{
		List<Feature> features = getClassOnlyFeatures();
		if (subclass != null)
		{
			features.addAll(subclass.getSubclassFeatures());
		}
		return features;
	}

	public List<Feature> getClassOnlyFeatures()
	{
		List<Feature> features = new ArrayList<>();
		for (Feature f : classFeatures)
		{
			if (f.getLevel() > level)
			{
				continue;
			}
			// Handle upgrades
			features.add(f.getHighestFeature(level));
		}
		return features;
	}

	public void setClassFeatures(List<Feature> features)
	{
		updateConfig(this.classFeatures, features, (v) ->
		{
			this.classFeatures.clear();
			this.classFeatures.addAll(v);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putStr(json, "desc", desc);
		json = putInt(json, "hd", hd);
		json = putStr(json, "spellcastingAbility", (spellcastingAbility == null ? "" : spellcastingAbility.toString()));
		JSONObject primary = new JSONObject();
		primary = putList(primary, "primaryAbilities", primaryAbilities.stream().map(a -> a.toString()).toList());
		primary = putList(primary, "primaryAbilityOptions",
				primaryAbilityOptions.stream().map(a -> a.toString()).toList());
		json = putObj(json, "primaryAbility", primary);
		json = putObjList(json, "equipment", equipment.stream().map(e -> e.saveConfig()).toList());
		json = putObjList(json, "features", classFeatures.stream().map(f -> f.saveConfig()).toList());
		json = putObj(json, "classSpells", classSpells.saveConfig());

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		desc = data.optString("desc", "");
		hd = data.optInt("hd", 0);
		String spellcastingAbilityName = data.optString("spellcastingAbility", "");
		if (!spellcastingAbilityName.isBlank())
		{
			spellcastingAbility = Ability.valueOf(spellcastingAbilityName);
		}
		JSONObject primary = data.optJSONObject("primaryAbility");
		if (primary != null)
		{
			primaryAbilities.clear();
			primaryAbilities
					.addAll(getList(primary, "primaryAbilities").stream().map(a -> Ability.valueOf(a)).toList());
			primaryAbilityOptions.clear();
			primaryAbilityOptions
					.addAll(getList(primary, "primaryAbilityOptions").stream().map(a -> Ability.valueOf(a)).toList());
		}
		getObjList(data, "equipment").forEach(e ->
		{
			ClassEquipment eq = new ClassEquipment();
			eq.loadConfig(e);
			equipment.add(eq);
		});
		getObjList(data, "features").forEach(f ->
		{
			Feature fe = new Feature();
			fe.loadConfig(f);
			fe.setParent(this);
			classFeatures.add(fe);
		});
		classSpells.loadConfig(data.optJSONObject("classSpells"));
	}

	// Class Spells has both config and state
	private final ClassSpells classSpells = new ClassSpells();

	// State
	private Ability selectedPrimary = null;
	private int selectedEquipmentIndex = 0;
	private int level = 1;
	private Subclass subclass;

	public Ability getSelectedPrimary()
	{
		return selectedPrimary;
	}

	public void setSelectedPrimary(Ability a)
	{
		updateWithAlert(selectedPrimary, a, (v) ->
		{
			this.selectedPrimary = a;
		}, PropertyListener.PRIMARYABILITY);
	}

	public int getSelectedEquipmentIndex()
	{
		return selectedEquipmentIndex;
	}

	public void setSelectedEquipmentIndex(int i)
	{
		updateWithAlert(selectedEquipmentIndex, i, (v) ->
		{
			this.selectedEquipmentIndex = i;
		}, PropertyListener.EQUIPMENT);
	}

	@Override
	public int getLevel()
	{
		return level;
	}

	@Override
	public void setLevel(int lvl)
	{
		updateWithAlert(level, lvl, (v) ->
		{
			this.level = lvl;
		}, PropertyListener.CLASSLEVEL);
	}

	public Subclass getSubclass()
	{
		return subclass;
	}

	public void setSubclass(Subclass sc)
	{
		if (level >= 3)
		{
			updateWithAlert(subclass, sc, (v) ->
			{
				this.subclass = v;
				this.subclass.setCls(this);
			}, PropertyListener.SUBCLASS);
		}
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		if (selectedPrimary != null)
		{
			json = putStr(json, "selectedPrimary", selectedPrimary.toString());
		}
		json = putInt(json, "selectedEquipmentIndex", selectedEquipmentIndex);
		json = putInt(json, "level", level);

		json = putObjList(json, "features", classFeatures.stream().map(f -> f.saveState()).toList());

		if (subclass != null)
		{
			JSONObject subclassObj = new JSONObject();
			subclassObj.put("name", subclass.getName());
			subclassObj.put("state", subclass.saveState());
			json = putObj(json, "subclass", subclassObj);
		}
		json = putObj(json, "classSpells", classSpells.saveState());

		json = putObjList(json, "features", classFeatures.stream().map(f ->
		{
			JSONObject d = new JSONObject();
			d.put("fname", f.getName());
			d.put("flvl", f.getLevel());
			d.put("fstate", f.saveState());
			return d;
		}).toList());

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		super.loadState(data);

		String selectedPrimaryName = data.optString("selectedPrimary", "");
		if (!selectedPrimaryName.isBlank())
		{
			setSelectedPrimary(Ability.valueOf(selectedPrimaryName));
		}
		setSelectedEquipmentIndex(data.optInt("selectedEquipmentIndex", 0));
		setLevel(data.optInt("level", 1));
		JSONObject subclassO = data.optJSONObject("subclass");
		if (subclassO != null)
		{
			Subclass sub = Subclass.getByName(subclassO.getString("name"));
			sub.loadState(subclassO.getJSONObject("state"));
			setSubclass(sub);
		}
		classSpells.loadState(data.optJSONObject("classSpells"));

		getObjList(data, "features").forEach(f ->
		{
			String fname = f.getString("fname");
			int flvl = f.getInt("flvl");
			for (Feature f2 : classFeatures)
			{
				if (f2.getName().equals(fname) && f2.getLevel() == flvl)
				{
					f2.loadState(f.getJSONObject("fstate"));
					break;
				}
			}
		});
	}
}
