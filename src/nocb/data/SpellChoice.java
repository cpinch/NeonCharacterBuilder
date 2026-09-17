package nocb.data;

import org.json.JSONObject;

public class SpellChoice
{
	private SpellList spellList;
	private Spell selected;

	public SpellChoice()
	{
	}

	public SpellChoice(SpellList spellList, int lvl)
	{
		this.spellList = spellList;
		// Default to the first spell of the appropriate level in the list
		selected = spellList.getSpellsForLevel(lvl).get(0);
	}

	public SpellList getSpellList()
	{
		return spellList;
	}

	public Spell getSpell()
	{
		return selected;
	}

	public void setSpell(Spell s)
	{
		this.selected = s;
	}

	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("spellList", spellList.getName());
		json.put("selected", selected.getName());

		return json;
	}

	public boolean loadState(JSONObject data)
	{
		try
		{
			this.spellList = SpellList.getForClass(data.getString("spellList"));
			this.selected = Spell.getByName(data.getString("selected"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
