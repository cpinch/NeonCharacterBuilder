package ncb.data;

import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class AbilityIncrease implements HasConfig
{
	private Ability a;
	private int amount, max;

	public AbilityIncrease(Ability a, int amount, int max)
	{
		this.a = a;
		this.amount = amount;
		this.max = max;
	}

	public AbilityIncrease(JSONObject data)
	{
		loadConfig(data);
	}

	public Ability getAbility()
	{
		return a;
	}

	public int getAmount()
	{
		return amount;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int m)
	{
		this.max = m;
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json.put("ability", a.toString());
		json.put("amount", amount);
		json.put("max", max);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		a = Ability.valueOf(data.getString("ability"));
		amount = data.getInt("amount");
		max = data.getInt("max");
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

	@Override
	public int getId()
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return a.toString() + " (max " + max + ")";
	}
}
