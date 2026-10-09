package ncb.data;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;
import ncb.data.interfaces.HasState;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Spell;
import ncb.io.JsonDataLoader;
import ncb.main.PropertyListener;

public class ClassSpells implements HasConfig, HasState
{
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private CharacterClass cls;

	@Override
	public PropertyChangeSupport getPCS()
	{
		return pcs;
	}

	public ClassSpells(CharacterClass cls)
	{
		super();
		this.cls = cls;
		addPropertyChangeListener(PropertyListener.getListener());
	}

	@Override
	public Customizable getParent()
	{
		return cls;
	}

	@Override
	public void setParent(Customizable p)
	{
	}

	private int highestSlot(int charLvl)
	{
		return cls.getSpellSlotsForLevel(charLvl).getHighestSlotLevel();
	}

	@Override
	public int getId()
	{
		return 0;
	}

	public List<Spell> getAllSelectedSpells(int charLvl)
	{
		List<Spell> spells = new ArrayList<>();

		for (int spLvl = 0; spLvl <= 9; spLvl++)
		{
			spells.addAll(getSpellChoicesByLevel(charLvl, spLvl).stream().map(sc -> sc.getSpell()).toList());
		}

		return spells;
	}

	private int getCountFlatKnown(int charLvl, int spLvl)
	{
		// This handles "missing" duplicate levels by just grabbing the highest level
		// entry
		for (int clvl = charLvl; clvl > 0; clvl--)
		{
			if (spLvl == 0)
			{
				if (cantripsByLevel.containsKey(clvl))
				{
					return cantripsByLevel.get(clvl);
				}
			}
			else
			{
				if (highestSlot(charLvl) == spLvl && spellsByLevel.containsKey(clvl))
				{
					return spellsByLevel.get(clvl);
				}
			}
		}
		return 0;
	}

	private int getCountFullKnown(int charLvl, int spLvl)
	{
		// This sums up the known values for each level up to charLvl where spLvl is the
		// maximum slot (unless spLvl is 0, of course)
		int count = 0;
		for (int clvl = 1; clvl <= charLvl; clvl++)
		{
			if (spLvl == 0)
			{
				if (cantripsByLevel.containsKey(clvl))
				{
					count += cantripsByLevel.get(clvl);
				}
			}
			else
			{
				if (highestSlot(clvl) == spLvl && spellsByLevel.containsKey(clvl))
				{
					count += spellsByLevel.get(clvl);
				}
			}
		}
		return count;
	}

	public List<SpellChoice> getSpellChoicesByLevel(int charLvl, int spLvl)
	{
		List<SpellChoice> scs = new ArrayList<>();
		if (!gainPerLvl)
		{
			int count = getCountFlatKnown(charLvl, spLvl);

			// Flat style. Pull from the "bucket" of spell choices, cantrip or non-cantrip
			// according to spLvl, adding new ones at the current spell level as needed.
			// This way our prior selections are maintained as the level increases because
			// we're still using the same "bucket" of chosen spells.
			for (int i = 0; i < count; i++)
			{
				if (spLvl == 0)
				{
					if (cantripBucket.size() <= i)
					{
						cantripBucket.add(new SpellChoice(cls.getSpellLists().get(0), 0));
					}
					scs.add(cantripBucket.get(i));
				}
				else
				{
					if (spellBucket.size() <= i)
					{
						spellBucket.add(new SpellChoice(cls.getSpellLists().get(0), spLvl));
					}
					scs.add(spellBucket.get(i));
				}
			}
		}
		else
		{
			int count = getCountFullKnown(charLvl, spLvl);

			// Full style. Pull from the list of spell choices at the spell level, adding
			// new ones as needed.
			// This way our prior selections per level are maintained.
			if (!spellChoicesByLvl.containsKey(spLvl))
			{
				spellChoicesByLvl.put(spLvl, new ArrayList<>());
			}
			for (int i = 0; i < count; i++)
			{
				if (spellChoicesByLvl.get(spLvl).size() <= i)
				{
					spellChoicesByLvl.get(spLvl).add(new SpellChoice(cls.getSpellLists().get(0), spLvl));
				}
				scs.add(spellChoicesByLvl.get(spLvl).get(i));
			}
		}
		return scs;
	}

	// Configuration
	private boolean gainPerLvl = false;
	private final Map<Integer, Integer> cantripsByLevel = new HashMap<>();
	private final Map<Integer, Integer> spellsByLevel = new HashMap<>();

	public boolean isGainPerLvl()
	{
		return gainPerLvl;
	}

	public void setGainPerLvl(boolean b)
	{
		updateConfig(gainPerLvl, b, (v) ->
		{
			gainPerLvl = v;
		});
	}

	public Map<Integer, Integer> getCantripsByLevel()
	{
		return cantripsByLevel;
	}

	public int getCantripsForLevel(int charLvl)
	{
		if (!cantripsByLevel.containsKey(charLvl))
		{
			cantripsByLevel.put(charLvl, 0);
		}
		return cantripsByLevel.get(charLvl);
	}

	public void setCantripsForLevel(int charLvl, int count)
	{
		updateConfig(getCantripsForLevel(charLvl), count, (v) ->
		{
			cantripsByLevel.put(charLvl, v);
		});
	}

	public Map<Integer, Integer> getSpellsByLevel()
	{
		return spellsByLevel;
	}

	public int getSpellsForLevel(int charLvl)
	{
		if (!spellsByLevel.containsKey(charLvl))
		{
			spellsByLevel.put(charLvl, 0);
		}
		return spellsByLevel.get(charLvl);
	}

	public void setSpellsForLevel(int charLvl, int count)
	{
		updateConfig(getSpellsForLevel(charLvl), count, (v) ->
		{
			spellsByLevel.put(charLvl, v);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putBool(json, "gainPerLvl", gainPerLvl);
		json = putIntMap(json, "knownCantrips", cantripsByLevel, "charLvl", "count");
		json = putIntMap(json, "knownSpells", spellsByLevel, "charLvl", "count");

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		if (data != null)
		{
			gainPerLvl = data.optBoolean("gainPerLvl", false);
			cantripsByLevel.clear();
			getIntMap(data, "knownCantrips", "charLvl", "count").entrySet()
					.forEach(e -> cantripsByLevel.put(e.getKey(), e.getValue()));
			spellsByLevel.clear();
			getIntMap(data, "knownSpells", "charLvl", "count").entrySet()
					.forEach(e -> spellsByLevel.put(e.getKey(), e.getValue()));
		}
	}

	// State
	private List<SpellChoice> cantripBucket = new ArrayList<>();
	private List<SpellChoice> spellBucket = new ArrayList<>();
	private Map<Integer, List<SpellChoice>> spellChoicesByLvl = new HashMap<>();

	@Override
	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json = putObjList(json, "cantripBucket", cantripBucket.stream().map(sc -> sc.saveState()).toList());
		json = putObjList(json, "spellBucket", spellBucket.stream().map(sc -> sc.saveState()).toList());

		// Custom object
		if (!spellChoicesByLvl.isEmpty())
		{
			JSONArray scbl = new JSONArray();
			for (Map.Entry<Integer, List<SpellChoice>> scl : spellChoicesByLvl.entrySet())
			{
				JSONObject spl = new JSONObject();
				spl.put("spellLevel", scl.getKey());
				JSONArray sels = new JSONArray();
				for (SpellChoice sc : scl.getValue())
				{
					sels.put(sc.saveState());
				}
				spl.put("selections", sels);
				scbl.put(spl);
			}
			json.put("spellChoicesByLvl", scbl);
		}

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		if (data != null)
		{
			cantripBucket = new ArrayList<>(getObjList(data, "cantripBucket").stream().map(s ->
			{
				SpellChoice sc = new SpellChoice();
				sc.loadState(s);
				return sc;
			}).toList());
			spellBucket = new ArrayList<>(getObjList(data, "spellBucket").stream().map(s ->
			{
				SpellChoice sc = new SpellChoice();
				sc.loadState(s);
				return sc;
			}).toList());

			JSONArray scbl = data.optJSONArray("spellChoicesByLvl");
			if (scbl != null)
			{
				for (int i = 0; i < scbl.length(); i++)
				{
					JSONObject spl = scbl.getJSONObject(i);
					int spLvl = spl.getInt("spellLevel");
					List<SpellChoice> sels = new ArrayList<>();
					JsonDataLoader.jsonArrayToObjectArray(spl.getJSONArray("selections")).forEach(s ->
					{
						SpellChoice sc = new SpellChoice();
						sc.loadState(s);
						sels.add(sc);
					});
					spellChoicesByLvl.put(spLvl, sels);
				}
			}
			// Too many changes to do 1 at a time, just assume something in spells changed
			pcs.firePropertyChange(PropertyListener.SPELLS, "", "Spells loaded");
		}
	}
}
