package nocb.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class AbilityScores
{
	private final Map<Ability, Integer> scores = new HashMap<>();
	private final Map<Ability, Integer> bgIncreases = new HashMap<>();
	private Ability spellcastingAbility; // Only used if the character's class doesn't set it

	public AbilityScores()
	{
		scores.put(Ability.Str, 8);
		scores.put(Ability.Dex, 8);
		scores.put(Ability.Con, 8);
		scores.put(Ability.Int, 8);
		scores.put(Ability.Wis, 8);
		scores.put(Ability.Cha, 8);

		bgIncreases.put(Ability.Str, 0);
		bgIncreases.put(Ability.Dex, 0);
		bgIncreases.put(Ability.Con, 0);
		bgIncreases.put(Ability.Int, 0);
		bgIncreases.put(Ability.Wis, 0);
		bgIncreases.put(Ability.Cha, 0);
	}

	public int getBaseScoreFor(Ability a)
	{
		return scores.get(a);
	}

	public void setBaseScoreFor(Ability a, int score)
	{
		scores.put(a, score);
	}

	public int getBGIncreaseFor(Ability a)
	{
		return bgIncreases.get(a);
	}

	public void setBGIncreaseFor(Ability a, int increase)
	{
		bgIncreases.put(a, increase);
	}

	public Map<Ability, Integer> getAllBGIncreases()
	{
		return bgIncreases;
	}

	public int getTotalScoreFor(Ability a)
	{
		return getBaseScoreFor(a) + getBGIncreaseFor(a);
	}

	public static int getModFor(int score)
	{
		return Math.floorDiv(score, 2) - 5;
	}

	public void setSpellcastingAbility(Ability a)
	{
		this.spellcastingAbility = a;
	}

	public Ability getSpellcastingAbility()
	{
		return spellcastingAbility;
	}

	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("scores", new JSONObject(scores));
		json.put("bgIncreases", new JSONObject(bgIncreases));
		if (spellcastingAbility != null)
		{
			json.put("spellcastingAbility", spellcastingAbility.name());
		}

		return json;
	}

	public boolean loadState(JSONObject data)
	{
		try
		{
			JSONObject sc = data.getJSONObject("scores");
			for (String key : sc.keySet())
			{
				scores.put(Ability.valueOf(key), sc.getInt(key));
			}
			JSONObject bg = data.getJSONObject("bgIncreases");
			for (String key : bg.keySet())
			{
				bgIncreases.put(Ability.valueOf(key), bg.getInt(key));
			}
			if (data.has("spellcastingAbility"))
			{
				spellcastingAbility = Ability.valueOf(data.getString("spellcastingAbility"));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
