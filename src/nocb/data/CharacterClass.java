package nocb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class CharacterClass extends Feature
{
	private String desc = "";
	private final List<Ability> primaryAbilities = new ArrayList<>();
	private final List<Ability> primaryAbilityOptions = new ArrayList<>();
	private Ability selectedPrimary = null;
	private int hd = 6;
	private final List<ClassEquipment> equipment = new ArrayList<>();
	private int selectedEquipmentIndex = 0;
	private final Map<Integer, Map<Integer, Integer>> spellSlotsByLevel = new HashMap<>();
	private final Map<Integer, Map<Integer, Integer>> knownSpellsByLevel = new HashMap<>();

	private final List<ClassFeature> classFeatures = new ArrayList<>();

	public CharacterClass()
	{
		super();
	}

	@Override
	protected List<? extends Feature> getChildFeatures()
	{
		return classFeatures;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		this.desc = desc;
	}

	public int getHd()
	{
		return hd;
	}

	public void setHD(int hd)
	{
		this.hd = hd;
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
		this.primaryAbilityOptions.clear();
		this.primaryAbilityOptions.addAll(options);
	}

	public List<Ability> getPrimaryAbilities()
	{
		return primaryAbilities;
	}

	public void setPrimaryAbilities(List<Ability> abls)
	{
		primaryAbilities.clear();
		primaryAbilities.addAll(abls);
	}

	public Ability getSelectedPrimary()
	{
		return selectedPrimary;
	}

	public void selectPrimary(Ability a)
	{
		selectedPrimary = a;
	}

	public List<ClassFeature> getClassFeatures()
	{
		return classFeatures;
	}

	public void addClassFeature(String name)
	{
		ClassFeature cf = new ClassFeature();
		cf.setName(name);
		classFeatures.add(cf);
	}

	/**
	 * We override this from feature as we use a custom list by char level
	 */
	@Override
	public List<Spell> getAllSpells()
	{
		throw new UnsupportedOperationException("CharacterClass must have the int param version called for this.");
	}

	public List<Spell> getAllSpells(int classLevel)
	{
		List<Spell> spells = new ArrayList<>();

		spells.addAll(getAllSpellsGranted());
		for (int i = 0; i <= 9; i++)
		{
			spells.addAll(getSpellChoicesByLevel(classLevel, i).stream().map(sc -> sc.getSpell()).toList());
		}

		return spells;
	}

	/**
	 * We override this from feature as we use a custom list by char level
	 */
	@Override
	public List<SpellChoice> getSpellChoicesByLevel(int spellLevel)
	{
		throw new UnsupportedOperationException("CharacterClass must have the 2 int version called for this.");
	}

	public List<SpellChoice> getSpellChoicesByLevel(int classLevel, int spellLevel)
	{
		int count = 0;
		if (knownSpellsByLevel.containsKey(classLevel))
		{
			Map<Integer, Integer> knownForLevel = knownSpellsByLevel.get(classLevel);

			if (knownForLevel.containsKey(spellLevel))
			{
				count = knownForLevel.get(spellLevel);
			}
		}

		if (this.spellChoices.get(spellLevel) == null)
		{
			this.spellChoices.put(spellLevel, new ArrayList<>());
		}

		List<SpellChoice> scs = new ArrayList<>();
		SpellList spellList = SpellList.getForClass(name);
		for (int i = 0; i < count; i++)
		{
			if (this.spellChoices.get(spellLevel).size() > i)
			{
				scs.add(this.spellChoices.get(spellLevel).get(i));
			}
			else
			{
				SpellChoice sc = new SpellChoice(spellList, spellLevel);
				scs.add(sc);
				this.spellChoices.get(spellLevel).add(sc);
			}
		}

		for (Feature f : classFeatures)
		{
			scs.addAll(f.getAllSpellChoicesByLevel(spellLevel));
		}

		return scs;
	}

	public int getSpellSlots(int classLevel, int spellLevel)
	{
		if (spellSlotsByLevel.containsKey(classLevel))
		{
			Map<Integer, Integer> slotsForLevel = spellSlotsByLevel.get(classLevel);

			if (slotsForLevel.containsKey(spellLevel))
			{
				return slotsForLevel.get(spellLevel);
			}
		}
		return 0;
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
		equipment.clear();
		equipment.addAll(e);
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
		System.out.println("Unknown class " + name);
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
		System.out.println("Unknown class id " + id);
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

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("spellSlots")).forEach(slotsByLevel ->
		{
			int charLevel = slotsByLevel.getInt("charLevel");
			Map<Integer, Integer> spellCountBySpellLevel = new HashMap<>();

			JsonDataLoader.jsonArrayToObjectArray(slotsByLevel.getJSONArray("slots")).forEach(sc ->
			{
				spellCountBySpellLevel.put(sc.getInt("level"), sc.getInt("count"));
			});

			newClass.spellSlotsByLevel.put(charLevel, spellCountBySpellLevel);
		});

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("knownSpells")).forEach(knownSpellsByLevel ->
		{
			int charLevel = knownSpellsByLevel.getInt("charLevel");
			Map<Integer, Integer> spellCountBySpellLevel = new HashMap<>();

			JsonDataLoader.jsonArrayToObjectArray(knownSpellsByLevel.getJSONArray("spellChoices")).forEach(sc ->
			{
				spellCountBySpellLevel.put(sc.getInt("level"), sc.getInt("count"));
			});

			newClass.knownSpellsByLevel.put(charLevel, spellCountBySpellLevel);
		});

		if (!newClass.getName().isBlank())
		{
			allClasses.add(newClass);
		}
	}

	public JSONObject saveClass()
	{
		JSONObject data = new JSONObject();

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
		if (!spellSlotsByLevel.isEmpty())
		{
			JSONArray spellSlots = new JSONArray();
			for (Map.Entry<Integer, Map<Integer, Integer>> lvlCounts : spellSlotsByLevel.entrySet())
			{
				JSONArray slots = new JSONArray();
				for (Map.Entry<Integer, Integer> spCounts : lvlCounts.getValue().entrySet())
				{
					JSONObject obj = new JSONObject();
					obj.put("level", spCounts.getKey());
					obj.put("count", spCounts.getValue());
					slots.put(obj);
				}
				spellSlots.put(lvlCounts.getKey(), slots);
			}
			data.put("spellSlots", spellSlots);
		}
		if (!knownSpellsByLevel.isEmpty())
		{
			JSONArray knownSpells = new JSONArray();
			for (Map.Entry<Integer, Map<Integer, Integer>> lvlCounts : knownSpellsByLevel.entrySet())
			{
				JSONArray known = new JSONArray();
				for (Map.Entry<Integer, Integer> spCounts : lvlCounts.getValue().entrySet())
				{
					JSONObject obj = new JSONObject();
					obj.put("level", spCounts.getKey());
					obj.put("count", spCounts.getValue());
					known.put(obj);
				}
				knownSpells.put(lvlCounts.getKey(), known);
			}
			data.put("knownSpells", knownSpells);
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
		// All we need to save is choices, so selectedEquipment, spellChoices, and then
		// classFeatures, everything else we pull based on name
		JSONObject json = super.saveState(); // This handles skill and spell choices

		json.put("selectedEquipmentIndex", selectedEquipmentIndex);
		json.put("classFeatures", new JSONArray(classFeatures.stream().map(cf -> cf.saveState()).toList()));

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		super.loadState(data); // This handles spell and skill choices

		boolean successful = true;
		try
		{
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
		}
		catch (Exception e)
		{
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}
}
