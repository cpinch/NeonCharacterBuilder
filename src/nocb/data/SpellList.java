package nocb.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class SpellList
{
	private static int nId = 1;

	private final int id;
	private String name = "";
	private final Map<Integer, List<String>> spellNames = new HashMap<>();
	private boolean custom = false;

	public SpellList()
	{
		this.id = nId++;
	}

	public int getId()
	{
		return id;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<String> getSpellNamesForLevel(int spellLevel)
	{
		if (spellNames.containsKey(spellLevel))
		{
			return spellNames.get(spellLevel);
		}
		return Collections.emptyList();
	}

	public List<Spell> getSpellsForLevel(int spellLevel)
	{
		return getSpells(getSpellNamesForLevel(spellLevel));
	}

	public void setSpellNamesForLevel(int spellLevel, List<String> spn)
	{
		spellNames.put(spellLevel, spn);
	}

	public List<Spell> getSpells(List<String> spellNames)
	{
		List<Spell> spells = new ArrayList<>();
		for (String s : spellNames)
		{
			if (s.contains("("))
			{
				// Alternate spell name
				String altName = s.substring(0, s.indexOf('(')).trim();
				String baseName = s.substring(s.indexOf('(') + 1, s.indexOf(')')).trim();
				spells.add(Spell.getByAltName(altName, baseName));
			}
			else
			{
				spells.add(Spell.getByName(s));
			}
		}
		return spells;
	}

	public boolean isCustom()
	{
		return custom;
	}

	public void setCustom(boolean custom)
	{
		this.custom = custom;
	}

	// Loading
	private static final List<SpellList> allSpellLists = new ArrayList<>();

	public static List<SpellList> getAllSpellLists()
	{
		return allSpellLists;
	}

	public static SpellList getById(int id)
	{
		for (SpellList s : allSpellLists)
		{
			if (s.getId() == id)
			{
				return s;
			}
		}
		System.out.println("Unknown spell list id " + id);
		return null;
	}

	public static SpellList getForClass(String className)
	{
		for (SpellList s : allSpellLists)
		{
			if (s.getName().equals(className))
			{
				return s;
			}
		}
		System.out.println("Unknown class name for spell list " + className);
		return null;
	}

	public static void addNewSpellList(String newName)
	{
		if (!newName.isBlank())
		{
			SpellList sl = new SpellList();
			sl.setName(newName);
			allSpellLists.add(sl);
		}
	}

	public static void sortAll()
	{
		allSpellLists.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadSpellList(JSONObject data)
	{
		String name = data.getString("name");
		if (allSpellLists.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate spell list, skip
			return;
		}

		SpellList newList = new SpellList();

		newList.name = name;

		JSONArray spellArr = data.getJSONArray("levels");
		for (int i = 0; i < spellArr.length(); i++)
		{
			JSONObject lvlList = spellArr.getJSONObject(i);

			int level = lvlList.getInt("level");

			List<String> spellNames = new ArrayList<>();
			JsonDataLoader.jsonArrayToStringArray(lvlList.getJSONArray("spells")).forEach(s -> spellNames.add(s));

			newList.spellNames.put(level, spellNames);
		}
		newList.setCustom(data.optBoolean("custom", false));

		if (!newList.getName().isBlank())
		{
			allSpellLists.add(newList);
		}
	}

	public JSONObject saveSpellList()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);

		JSONArray levels = new JSONArray();
		for (Map.Entry<Integer, List<String>> sbl : spellNames.entrySet())
		{
			JSONObject lvlObj = new JSONObject();
			lvlObj.put("level", sbl.getKey());

			JSONArray spells = new JSONArray();
			sbl.getValue().forEach(s -> spells.put(s));
			lvlObj.put("spells", spells);

			levels.put(lvlObj);
		}
		data.put("levels", levels);
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}
}
