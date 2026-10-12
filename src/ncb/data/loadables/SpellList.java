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
		newSL.setCustom(custom);
		newSL.loadConfig(data);
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

	// Loading
	private static final List<SpellList> allSpellLists = new ArrayList<>();

	public static List<SpellList> getAllSpellLists()
	{
		return new ArrayList<>(allSpellLists);
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
	private final Map<Integer, List<Spell>> spells = new HashMap<>();

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

	public List<Spell> getSpellsForLevel(int spellLevel)
	{
		if (spells.containsKey(spellLevel))
		{
			return new ArrayList<>(spells.get(spellLevel));
		}
		return Collections.emptyList();
	}

	public void setSpellsForLevel(int spellLevel, List<Spell> spells)
	{
		if (!this.spells.containsKey(spellLevel) || !this.spells.get(spellLevel).equals(spells))
		{
			this.spells.put(spellLevel, spells);
			setCustom(true);
		}
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json.put("name", name);

		// Custon data object
		if (!spells.isEmpty())
		{
			JSONArray levels = new JSONArray();
			for (Map.Entry<Integer, List<Spell>> sbl : spells.entrySet())
			{
				JSONObject lvlObj = new JSONObject();

				lvlObj.put("level", sbl.getKey());
				putObjList(lvlObj, "spells", sbl.getValue().stream().map(s -> Spell.saveToJSONObject(s)).toList());

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

			List<Spell> spells = new ArrayList<>();
			getObjList(lvlList, "spells").forEach(so -> spells.add(Spell.getFromJSONObject(so)));

			this.spells.put(level, spells);
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
