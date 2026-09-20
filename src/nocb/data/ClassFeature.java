package nocb.data;

import org.json.JSONObject;

public class ClassFeature extends Feature
{
	private int level = 1;
	private Ability spellcastingAbility;

	public ClassFeature(JSONObject data)
	{
		super();

		this.loadFromData(data);

		this.level = data.optInt("level", 1);
		String spellcastingAbilityName = data.optString("spellcastingAbility", "");
		this.spellcastingAbility = spellcastingAbilityName.isBlank() ? null : Ability.valueOf(spellcastingAbilityName);
	}

	public ClassFeature()
	{
		super();
	}

	public JSONObject saveClassFeature()
	{
		JSONObject data = super.saveFeature();

		if (level > 1)
		{
			data.put("level", level);
		}
		if (spellcastingAbility != null)
		{
			data.put("spellcastingAbility", spellcastingAbility.toString());
		}

		return data;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int lvl)
	{
		if (lvl != level)
		{
			this.level = lvl;
			setCustom(true);
		}
	}

	public Ability getSpellcastingAbility()
	{
		return spellcastingAbility;
	}

	public void setSpellcastingAbility(Ability a)
	{
		if (!this.spellcastingAbility.equals(a))
		{
			this.spellcastingAbility = a;
			setCustom(true);
		}
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		if (spellcastingAbility != null)
		{
			json.put("spellcastingAbility", spellcastingAbility.name());
		}

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		boolean success = super.loadState(data);

		try
		{
			if (data.has("spellcastingAbility"))
			{
				this.spellcastingAbility = Ability.valueOf(data.getString("spellcastingAbility"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;
		}

		return success;
	}
}
