package nocb.data;

import org.json.JSONObject;

public class SpeciesTrait extends Feature
{
	private int level = 1;

	public SpeciesTrait(JSONObject data)
	{
		super();
		this.loadFromData(data);

		this.level = data.optInt("level", 1);
	}

	public SpeciesTrait()
	{
		super();
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int lvl)
	{
		if (this.level != lvl)
		{
			this.level = lvl;
			setCustom(true);
		}
	}

	public JSONObject saveTrait()
	{
		JSONObject data = super.saveFeature();

		if (level > 1)
		{
			data.put("level", level);
		}

		return data;
	}
}
