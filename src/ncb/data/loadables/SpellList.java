package ncb.data.loadables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;
import ncb.io.JsonDataLoader;

public class SpellList implements HasConfig
{
	private static int nId = 1;
	private final int id;
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

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allSpellLists.stream().anyMatch(la -> la.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		SpellList newSL = new SpellList();
		newSL.loadConfig(data);
		newSL.setCustom(custom);
		allSpellLists.add(newSL);
	}

	public SpellList()
	{
		this.id = nId++;
	}

	@Override
	public int getId()
	{
		return id;
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
		if (!spellNames.containsKey(spellLevel) || !spellNames.get(spellLevel).equals(spn))
		{
			spellNames.put(spellLevel, spn);
			setCustom(true);
		}
	}

	public static List<Spell> getSpells(List<String> spellNames)
	{
		List<Spell> spells = new ArrayList<>();
		for (String s : spellNames)
		{
			if (s.isBlank())
			{
				continue;
			}
			Spell spell;
			if (s.contains("("))
			{
				// Alternate spell name
				String altName = s.substring(0, s.indexOf('(')).trim();
				String baseName = s.substring(s.indexOf('(') + 1, s.indexOf(')')).trim();
				spell = Spell.getByAltName(altName, baseName);
			}
			else
			{
				spell = Spell.getByName(s);
			}

			if (spell != null)
			{
				spells.add(spell);
			}
		}
		return spells;
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
		System.err.println("Unknown spell list id " + id);
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
		System.err.println("Unknown class name for spell list " + className);
		return null;
	}

	public static SpellList addNewSpellList(String newName)
	{
		if (!newName.isBlank())
		{
			SpellList sl = new SpellList();
			sl.setName(newName);
			allSpellLists.add(sl);
			return sl;
		}
		return null;
	}

	public static void sortAll()
	{
		allSpellLists.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	private String name = "";
	private final Map<Integer, List<String>> spellNames = new HashMap<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		if (!this.name.equals(name))
		{
			this.name = name;
			setCustom(true);
		}
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json.put("name", name);

		// Custon data object
		if (!spellNames.isEmpty())
		{
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
			json.put("levels", levels);
		}

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");

		JSONArray spellArr = data.getJSONArray("levels");
		for (int i = 0; i < spellArr.length(); i++)
		{
			JSONObject lvlList = spellArr.getJSONObject(i);

			int level = lvlList.getInt("level");

			List<String> spellNames = new ArrayList<>();
			JsonDataLoader.jsonArrayToStringArray(lvlList.getJSONArray("spells")).forEach(s -> spellNames.add(s));

			this.spellNames.put(level, spellNames);
		}
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getId() == ((SpellList) o).getId();
	}
}
