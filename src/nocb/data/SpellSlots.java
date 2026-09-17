package nocb.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class SpellSlots
{
	private final Map<Integer, Map<Integer, Integer>> spellSlotsByLevel = new HashMap<>();

	public SpellSlots(JSONArray lvlOptions)
	{
		for (int i = 0; i < lvlOptions.length(); i++)
		{
			JSONObject slotsByLevel = lvlOptions.getJSONObject(i);

			int charLevel = slotsByLevel.getInt("charLevel");
			Map<Integer, Integer> spellCountBySpellLevel = new HashMap<>();

			JsonDataLoader.jsonArrayToObjectArray(slotsByLevel.getJSONArray("slots")).forEach(sc ->
			{
				spellCountBySpellLevel.put(sc.getInt("level"), sc.getInt("count"));
			});

			spellSlotsByLevel.put(charLevel, spellCountBySpellLevel);
		}
	}

	public int getSpellSlots(int charLevel, int spellLevel)
	{
		if (!spellSlotsByLevel.containsKey(charLevel) || !spellSlotsByLevel.get(charLevel).containsKey(spellLevel))
		{
			return 0;
		}
		return spellSlotsByLevel.get(charLevel).get(spellLevel);
	}
}
