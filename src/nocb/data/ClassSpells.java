package nocb.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class ClassSpells
{
	public static final int FLAT = 5, FULL = 6, GAIN = 7;

	// Configuration
	private int knownStyle = FLAT, slotsStyle = FLAT;
	private final Map<Integer, Map<Integer, Integer>> slots = new HashMap<>();
	private final Map<Integer, Integer> knownCantrips = new HashMap<>();
	private final Map<Integer, Integer> knownSpells = new HashMap<>();
	private String clsName;

	// State
	private final List<SpellChoice> cantripBucket = new ArrayList<>();
	private final List<SpellChoice> spellBucket = new ArrayList<>();
	private final Map<Integer, List<SpellChoice>> spellChoicesByLvl = new HashMap<>();

	public void setClassName(String clsName)
	{
		this.clsName = clsName;
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
				if (knownCantrips.containsKey(clvl))
				{
					return knownCantrips.get(clvl);
				}
			}
			else
			{
				if (highestSlot(charLvl) == spLvl && knownSpells.containsKey(clvl))
				{
					return knownSpells.get(clvl);
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
				if (knownCantrips.containsKey(clvl))
				{
					count += knownCantrips.get(clvl);
				}
			}
			else
			{
				if (highestSlot(clvl) == spLvl && knownSpells.containsKey(clvl))
				{
					count += knownSpells.get(clvl);
				}
			}
		}
		return count;
	}

	public List<SpellChoice> getSpellChoicesByLevel(int charLvl, int spLvl)
	{
		List<SpellChoice> scs = new ArrayList<>();
		if (knownStyle == FLAT)
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
						cantripBucket.add(new SpellChoice(getSpellList(), 0));
					}
					scs.add(cantripBucket.get(i));
				}
				else
				{
					if (spellBucket.size() <= i)
					{
						spellBucket.add(new SpellChoice(getSpellList(), spLvl));
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
					spellChoicesByLvl.get(spLvl).add(new SpellChoice(getSpellList(), spLvl));
				}
				scs.add(spellChoicesByLvl.get(spLvl).get(i));
			}
		}
		return scs;
	}

	public int getSpellSlots(int charLvl, int spLvl)
	{
		if (slotsStyle == FLAT)
		{
			// Flat style
			// We want to go backwards through levels, to handle missing "duplicate" levels
			// properly
			// The first time we find a set entry, if it's greater than or less than spLvl
			// we know we have no slots of this level
			// If it's exactly spLvl we return it
			for (int clvl = charLvl; clvl > 0; clvl--)
			{
				if (slots.containsKey(clvl) && !slots.get(clvl).isEmpty())
				{
					// We have a level with slot information
					Integer count = slots.get(clvl).get(spLvl);
					if (count == null)
					{
						return 0;
					}
					else
					{
						return count;
					}
				}
			}
		}
		else
		{
			// Full style records
			// We want the highest level entry in slots with this spLvl <= charLvl
			for (int clvl = charLvl; clvl > 0; clvl--)
			{
				if (slots.containsKey(clvl) && slots.get(clvl).containsKey(spLvl))
				{
					return slots.get(clvl).get(spLvl);
				}
			}
		}
		return 0;
	}

	private int highestSlot(int charLvl)
	{
		if (slotsStyle == FLAT)
		{
			for (int clvl = charLvl; clvl > 0; clvl--)
			{
				if (slots.containsKey(clvl))
				{
					// For flat style slots, there is 1 entry in the char level value map and its
					// key is the spell level of the slots
					return Collections.max(slots.get(clvl).keySet());
				}
			}

		}
		else
		{
			int max = 1;
			for (int clvl = charLvl; clvl > 0; clvl--)
			{
				if (slots.containsKey(clvl))
				{
					int m = Collections.max(slots.get(clvl).keySet());
					if (m > max)
					{
						max = m;
					}
				}
			}
			return max;
		}
		return 0;
	}

	public int getKnownStyle()
	{
		return knownStyle;
	}

	public Map<Integer, Integer> getAllFlatCantrips()
	{
		return knownCantrips;
	}

	public void setAllFlatCantrips(Map<Integer, Integer> known)
	{
		this.knownCantrips.clear();
		this.knownCantrips.putAll(known);
		knownStyle = FLAT;
	}

	public Map<Integer, Integer> getAllFlatKnown()
	{
		return knownSpells;
	}

	public void setAllFlatKnown(Map<Integer, Integer> known)
	{
		this.knownSpells.clear();
		this.knownSpells.putAll(known);
		knownStyle = FLAT;
	}

	public Map<Integer, Integer> getAllGainCantrips()
	{
		return knownCantrips;
	}

	public void setAllGainCantrips(Map<Integer, Integer> known)
	{
		this.knownCantrips.clear();
		this.knownCantrips.putAll(known);
		knownStyle = GAIN;
	}

	public Map<Integer, Integer> getAllGainKnown()
	{
		return knownSpells;
	}

	public void setAllGainKnown(Map<Integer, Integer> known)
	{
		this.knownSpells.clear();
		this.knownSpells.putAll(known);
		knownStyle = GAIN;
	}

	public int getSlotsStyle()
	{
		return slotsStyle;
	}

	public Map<Integer, Map<Integer, Integer>> getAllFlatSlots()
	{
		return slots;
	}

	public void setAllFlatSlots(Map<Integer, Map<Integer, Integer>> slots)
	{
		this.slots.clear();
		this.slots.putAll(slots);
		slotsStyle = FLAT;
	}

	public Map<Integer, Map<Integer, Integer>> getAllFullSlots()
	{
		return slots;
	}

	public void setAllFullSlots(Map<Integer, Map<Integer, Integer>> slots)
	{
		this.slots.clear();
		this.slots.putAll(slots);
		slotsStyle = FULL;
	}

	public void loadFromData(JSONObject data)
	{
		if (data != null)
		{
			knownStyle = data.optInt("knownStyle", FLAT);
			JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("knownCantrips")).forEach(k ->
			{
				knownCantrips.put(k.getInt("charLvl"), k.getInt("count"));
			});
			JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("knownSpells")).forEach(k ->
			{
				knownSpells.put(k.getInt("charLvl"), k.getInt("count"));
			});

			slotsStyle = data.optInt("slotsStyle", FLAT);
			JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("spellSlots")).forEach(s ->
			{
				Map<Integer, Integer> slotsForLvl = new HashMap<>();
				JsonDataLoader.jsonArrayToObjectArray(s.optJSONArray("slotCounts")).forEach(s2 ->
				{
					slotsForLvl.put(s2.getInt("spellLvl"), s2.getInt("count"));
				});
				slots.put(s.getInt("charLvl"), slotsForLvl);
			});
			// We don't need to save or load spellList as the class we are associated to
			// will set that for us
		}
	}

	public JSONObject saveClassSpells()
	{
		JSONObject data = new JSONObject();

		data.put("knownStyle", knownStyle);
		JSONArray cantripsByCharLvl = new JSONArray();
		for (Map.Entry<Integer, Integer> knownEntries : knownCantrips.entrySet())
		{
			JSONObject k = new JSONObject();
			k.put("charLvl", knownEntries.getKey());
			k.put("count", knownEntries.getValue());
			cantripsByCharLvl.put(k);
		}
		data.put("knownCantrips", cantripsByCharLvl);
		JSONArray knownByCharLvl = new JSONArray();
		for (Map.Entry<Integer, Integer> knownEntries : knownSpells.entrySet())
		{
			JSONObject k = new JSONObject();
			k.put("charLvl", knownEntries.getKey());
			k.put("count", knownEntries.getValue());
			knownByCharLvl.put(k);
		}
		data.put("knownSpells", knownByCharLvl);

		data.put("slotsStyle", slotsStyle);
		JSONArray slotsByCharLvl = new JSONArray();
		for (Map.Entry<Integer, Map<Integer, Integer>> slotsEntries : slots.entrySet())
		{
			JSONObject s = new JSONObject();
			s.put("charLvl", slotsEntries.getKey());
			JSONArray slvls = new JSONArray();
			for (Map.Entry<Integer, Integer> lvlSlotEntries : slotsEntries.getValue().entrySet())
			{
				JSONObject s2 = new JSONObject();
				s2.put("spellLvl", lvlSlotEntries.getKey());
				s2.put("count", lvlSlotEntries.getValue());
				slvls.put(s2);
			}
			s.put("slotCounts", slvls);
			slotsByCharLvl.put(s);
		}
		data.put("spellSlots", slotsByCharLvl);

		return data;
	}

	public JSONObject saveState()
	{
		JSONObject data = new JSONObject();

		if (!cantripBucket.isEmpty())
		{
			JSONArray cbucket = new JSONArray();
			for (SpellChoice sc : cantripBucket)
			{
				cbucket.put(sc.getSpell().toJSONObject());
			}
			data.put("cantripBucket", cbucket);
		}
		if (!spellBucket.isEmpty())
		{
			JSONArray sbucket = new JSONArray();
			for (SpellChoice sc : spellBucket)
			{
				sbucket.put(sc.getSpell().toJSONObject());
			}
			data.put("spellBucket", sbucket);
		}
		if (!spellChoicesByLvl.isEmpty())
		{
			JSONArray scbl = new JSONArray();
			for (Map.Entry<Integer, List<SpellChoice>> scl : spellChoicesByLvl.entrySet())
			{
				JSONObject l = new JSONObject();
				l.put("spellLevel", scl.getKey());
				JSONArray sels = new JSONArray();
				for (SpellChoice sc : scl.getValue())
				{
					sels.put(sc.getSpell().toJSONObject());
				}
				l.put("selections", sels);
				scbl.put(l);
			}
			data.put("spellChoicesByLvl", scbl);
		}

		return data;
	}

	public void loadState(JSONObject data)
	{
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("cantripBucket"))
				.forEach(sp -> cantripBucket.add(new SpellChoice(getSpellList(), Spell.getFromJSONObject(sp))));

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("spellBucket"))
				.forEach(sp -> spellBucket.add(new SpellChoice(getSpellList(), Spell.getFromJSONObject(sp))));

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("spellChoicesByLvl")).forEach(l ->
		{
			int spLvl = l.getInt("spellLevel");
			List<SpellChoice> sels = new ArrayList<>();
			JsonDataLoader.jsonArrayToObjectArray(data.getJSONArray("selections"))
					.forEach(sc -> sels.add(new SpellChoice(getSpellList(), Spell.getFromJSONObject(sc))));
			spellChoicesByLvl.put(spLvl, sels);
		});
	}

	private SpellList getSpellList()
	{
		return SpellList.getForClass(clsName);
	}
}
