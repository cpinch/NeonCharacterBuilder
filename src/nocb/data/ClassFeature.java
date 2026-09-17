package nocb.data;

import org.json.JSONObject;

public class ClassFeature extends Feature
{
	private Ability spellcastingAbility;

	public ClassFeature(JSONObject data)
	{
		super();

		this.loadFromData(data);

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

		if (spellcastingAbility != null)
		{
			data.put("spellcastingAbility", spellcastingAbility.toString());
		}

		return data;
	}

	public Ability getSpellcastingAbility()
	{
		return spellcastingAbility;
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
