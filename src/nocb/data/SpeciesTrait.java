package nocb.data;

import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class SpeciesTrait extends Feature
{
	public SpeciesTrait(JSONObject data)
	{
		super();
		this.loadFromData(data);

		this.spellsGranted.clear();
		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("grantsSpells")).forEach(spell ->
		{
			String name = spell.getString("spell");
			int level = spell.optInt("level", 1);
			String note = spell.optString("note");

			// TODO post1.0 - handle levels better when level up
			Spell s = Spell.getCopyByName(name, (level > 1 ? "lvl " + level + ". " : "") + note);
			this.spellsGranted.add(s);
		});
	}

	public SpeciesTrait()
	{
		super();
	}
}
