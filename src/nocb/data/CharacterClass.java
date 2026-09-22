package nocb.data;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;
import nocb.main.PropertyListener;

public class CharacterClass extends Feature
{
	private String desc = "";
	private final List<Ability> primaryAbilities = new ArrayList<>();
	private final List<Ability> primaryAbilityOptions = new ArrayList<>();
	private Ability selectedPrimary = null;
	private int hd = 6;
	private final List<ClassEquipment> equipment = new ArrayList<>();
	private int selectedEquipmentIndex = 0;
	private final ClassSpells classSpells = new ClassSpells();

	private final List<ClassFeature> classFeatures = new ArrayList<>();

	private int level = 1;
	private Subclass subclass;

	public CharacterClass()
	{
		super();
	}

	@Override
	protected List<? extends Feature> getAllChildFeatures()
	{
		return getAllClassFeatures();
	}

	@Override
	protected List<? extends Feature> getChildFeatures()
	{
		return getClassFeatures();
	}

	public List<ClassFeature> getFeaturesAtLevel(int lvl)
	{
		List<ClassFeature> features = new ArrayList<>();
		for (ClassFeature f : classFeatures)
		{
			if (f.getLevel() == lvl)
			{
				features.add(f);
			}
		}
		if (lvl == 3)
		{
			ClassFeature subclassF = new ClassFeature();
			subclassF.setName("Gain a Subclass");
			features.add(subclassF);
		}
		return features;
	}

	@Override
	public void setName(String name)
	{
		super.setName(name);
		classSpells.setClassName(name);
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int lvl)
	{
		if (lvl != level)
		{
			int old = level;
			this.level = lvl;
			pcs.firePropertyChange(PropertyListener.CLASSLEVEL, old, lvl);
			if (level >= 3 && subclass == null)
			{
				setSubclass(Subclass.getForClassName(name).get(0));
			}
		}
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		if (!this.desc.equals(desc))
		{
			this.desc = desc;
			setCustom(true);
		}
	}

	public int getHd()
	{
		return hd;
	}

	public void setHD(int hd)
	{
		if (this.hd != hd)
		{
			this.hd = hd;
			setCustom(true);
		}
	}

	public boolean hasSelectablePrimary()
	{
		return !primaryAbilityOptions.isEmpty();
	}

	public List<Ability> getPrimaryAbilityOptions()
	{
		return primaryAbilityOptions;
	}

	public void setPrimaryAbilityOptions(List<Ability> options)
	{
		if (!primaryAbilityOptions.equals(options))
		{
			this.primaryAbilityOptions.clear();
			this.primaryAbilityOptions.addAll(options);
			setCustom(true);
		}
	}

	public List<Ability> getPrimaryAbilities()
	{
		return primaryAbilities;
	}

	public void setPrimaryAbilities(List<Ability> abls)
	{
		if (!this.primaryAbilities.equals(abls))
		{
			primaryAbilities.clear();
			primaryAbilities.addAll(abls);
			setCustom(true);
		}
	}

	public Ability getSelectedPrimary()
	{
		return selectedPrimary;
	}

	public void selectPrimary(Ability a)
	{
		selectedPrimary = a;
	}

	public List<ClassFeature> getAllClassFeatures()
	{
		classFeatures.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
		return classFeatures;
	}

	public List<ClassFeature> getClassOnlyFeatures()
	{
		// Only return features of our class level or lower and drop any features of a
		// lower level that have the same name as a higher level feature
		List<ClassFeature> features = new ArrayList<>();
		for (ClassFeature f : classFeatures)
		{
			if (f.getLevel() > level)
			{
				continue;
			}
			List<ClassFeature> existingFeatures = features.stream().filter(ef -> ef.getName().equals(f.getName()))
					.toList();
			if (!existingFeatures.isEmpty())
			{
				if (f.getLevel() > existingFeatures.get(0).getLevel())
				{
					features.remove(existingFeatures.get(0));
				}
				else
				{
					continue;
				}
			}
			features.add(f);
		}
		return features;
	}

	public List<ClassFeature> getClassFeatures()
	{
		List<ClassFeature> features = getClassOnlyFeatures();
		if (subclass != null)
		{
			features.addAll(subclass.getSubclassFeatures());
		}
		return features;
	}

	public void addClassFeature(String name)
	{
		ClassFeature cf = new ClassFeature();
		cf.setName(name);
		classFeatures.add(cf);
		setCustom(true);
	}

	public void removeLastClassFeature()
	{
		classFeatures.remove(classFeatures.size() - 1);
		setCustom(true);
	}

	@Override
	public List<Spell> getAllSpells()
	{
		List<Spell> spells = new ArrayList<>();

		spells.addAll(super.getAllSpells());
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
	@Override
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

	/**
	 * This is called by the FeatureEditPanel which classes doesn't uses
	 */
	@Override
	public void setSpellChoices(Map<Integer, List<SpellChoice>> spellChoices)
	{
		throw new UnsupportedOperationException("CharacterClass must go through the ClassSpells class.");
	}

	public int getEquipmentOptionsCount()
	{
		return equipment.size();
	}

	public List<ClassEquipment> getEquipmentOptions()
	{
		return equipment;
	}

	public void setEquipmentOptions(List<ClassEquipment> e)
	{
		if (!equipment.equals(e))
		{
			equipment.clear();
			equipment.addAll(e);
			setCustom(true);
		}
	}

	public int getSelectedEquipmentIndex()
	{
		return selectedEquipmentIndex;
	}

	public void setSelectedEquipment(int index)
	{
		if (index >= 0 && index < equipment.size())
		{
			this.selectedEquipmentIndex = index;
		}
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

	public Ability getSpellcastingAbility()
	{
		for (ClassFeature feature : classFeatures)
		{
			if (feature.getSpellcastingAbility() != null)
			{
				Ability s = feature.getSpellcastingAbility();
				if (s.equals(Ability.Primary))
				{
					return selectedPrimary;
				}
				else
				{
					return s;
				}
			}
		}
		return null;
	}

	public boolean isSpellcaster()
	{
		for (ClassFeature feature : classFeatures)
		{
			if (feature.getSpellcastingAbility() != null)
			{
				return true;
			}
		}
		return false;
	}

	public Subclass getSubclass()
	{
		return subclass;
	}

	public void setSubclass(Subclass sc)
	{
		if (this.subclass == null || (sc != null && !this.subclass.getName().equals(sc.getName())))
		{
			Subclass old = subclass;
			this.subclass = sc;
			this.subclass.setCharClass(this);
			pcs.firePropertyChange(PropertyListener.SUBCLASS, old, sc);
		}
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

	public static void addNewClass(String newName)
	{
		if (!newName.isBlank())
		{
			CharacterClass cc = new CharacterClass();
			cc.setName(newName);
			allClasses.add(cc);
		}
	}

	public static void loadClass(JSONObject data)
	{
		String name = data.getString("name");
		if (allClasses.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate class, skip
			return;
		}

		CharacterClass newClass = new CharacterClass();

		newClass.loadFromData(data);

		newClass.desc = data.getString("desc");
		JSONObject primaryAbility = data.getJSONObject("primaryAbility");
		JsonDataLoader.jsonArrayToStringArray(primaryAbility.optJSONArray("primaryAbilities"))
				.forEach(a -> newClass.primaryAbilities.add(Ability.valueOf(a)));
		JsonDataLoader.jsonArrayToStringArray(primaryAbility.optJSONArray("primaryAbilityOptions"))
				.forEach(a -> newClass.primaryAbilityOptions.add(Ability.valueOf(a)));
		newClass.hd = data.optInt("hd", 0);
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("equipment"))
				.forEach(feature -> newClass.equipment.add(new ClassEquipment(feature)));

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("features"))
				.forEach(feature -> newClass.classFeatures.add(new ClassFeature(feature)));

		newClass.classSpells.setClassName(name);
		newClass.classSpells.loadFromData(data.optJSONObject("classSpells"));

		if (!newClass.getName().isBlank())
		{
			allClasses.add(newClass);
		}
	}

	public JSONObject saveClass()
	{
		JSONObject data = super.saveFeature();

		data.put("name", name);
		data.put("desc", desc);
		JSONObject primaryAbility = new JSONObject();
		if (!primaryAbilities.isEmpty())
		{
			JSONArray abls = new JSONArray();
			for (Ability a : primaryAbilities)
			{
				abls.put(a.toString());
			}
			primaryAbility.put("primaryAbilities", abls);
		}
		if (!primaryAbilityOptions.isEmpty())
		{
			JSONArray ablO = new JSONArray();
			for (Ability a : primaryAbilityOptions)
			{
				ablO.put(a.toString());
			}
			primaryAbility.put("primaryAbilityOptions", ablO);
		}
		data.put("primaryAbility", primaryAbility);
		data.put("hd", hd);
		if (!equipment.isEmpty())
		{
			JSONArray equip = new JSONArray();
			for (ClassEquipment ce : equipment)
			{
				equip.put(ce.saveEquipment());
			}
			data.put("equipment", equip);
		}
		if (!classFeatures.isEmpty())
		{
			JSONArray cf = new JSONArray();
			for (ClassFeature c : classFeatures)
			{
				cf.put(c.saveClassFeature());
			}
			data.put("features", cf);
		}
		if (isSpellcaster())
		{
			data.put("classSpells", classSpells.saveClassSpells());
		}
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState(); // This handles skill choices

		json.put("level", level);
		json.put("selectedEquipmentIndex", selectedEquipmentIndex);
		json.put("classFeatures", new JSONArray(classFeatures.stream().map(cf -> cf.saveState()).toList()));
		json.put("spellChoices", classSpells.saveState());
		if (subclass != null)
		{
			json.put("subclassName", subclass.getName());
			json.put("subclassState", subclass.saveState());
		}

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		super.loadState(data); // This handles spell and skill choices

		boolean successful = true;
		try
		{
			this.level = data.optInt("level", 1);
			this.selectedEquipmentIndex = data.getInt("selectedEquipmentIndex");
			JSONArray featureStates = data.getJSONArray("classFeatures");
			for (int i = 0; i < featureStates.length(); i++)
			{
				JSONObject featureState = featureStates.getJSONObject(i);
				String featureName = featureState.getString("name");
				for (ClassFeature cf : classFeatures)
				{
					if (cf.getName().equals(featureName))
					{
						successful = successful && cf.loadState(featureState);
						break;
					}
				}
			}
			this.classSpells.loadState(data.optJSONObject("spellChoices"));
			String scName = data.optString("subclassName", "");
			if (!scName.isBlank())
			{
				this.subclass = Subclass.getByName(scName);
				this.subclass.loadFromData(data.getJSONObject("subclassState"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		super.addPropertyChangeListener(l);
		classFeatures.forEach(cf -> cf.addPropertyChangeListener(l));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		pcs.removePropertyChangeListener(l);
		classFeatures.forEach(cf -> cf.removePropertyChangeListener(l));
	}
}
