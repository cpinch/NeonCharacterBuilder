package nocb.data;

import org.json.JSONObject;

public class SpellChoices
{
	private final int spellLevel;
	private final int count;
	private final String spellList;

	public SpellChoices(JSONObject data)
	{
		this.spellLevel = data.getInt("level");
		this.count = data.getInt("count");
		this.spellList = data.optString("spellList");
	}

	public int getSpellLevel()
	{
		return spellLevel;
	}

	public int getCount()
	{
		return count;
	}

	public String getSpellList()
	{
		return spellList;
	}
}
