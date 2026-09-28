package ncb.data;

import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.interfaces.HasState;
import ncb.main.PropertyListener;

public class AbilityScores implements HasState
{
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public PropertyChangeSupport getPCS()
	{
		return pcs;
	}

	public AbilityScores()
	{
		super();
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

		addPropertyChangeListener(PropertyListener.getListener());
	}

	// State
	private final Map<Ability, Integer> scores = new HashMap<>();
	private final Map<Ability, Integer> bgIncreases = new HashMap<>();
	private Ability spellcastingAbility = Ability.Int; // Only used if the character's class doesn't set it

	public int getBaseScoreFor(Ability a)
	{
		return scores.get(a);
	}

	public void setBaseScoreFor(Ability a, int score)
	{
		if (scores.get(a) != score)
		{
			int old = scores.get(a);
			scores.put(a, score);
			pcs.firePropertyChange(PropertyListener.ABILITYSCORES, old, score);
		}
	}

	public int getBGIncreaseFor(Ability a)
	{
		return bgIncreases.get(a);
	}

	public void setBGIncreaseFor(Ability a, int increase)
	{
		if (bgIncreases.get(a) != increase)
		{
			int old = bgIncreases.get(a);
			bgIncreases.put(a, increase);
			pcs.firePropertyChange(PropertyListener.ABILITYSCORES, old, increase);
		}
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

	public Ability getSpellcastingAbility()
	{
		return spellcastingAbility;
	}

	public void setSpellcastingAbility(Ability a)
	{
		updateWithAlert(this.spellcastingAbility, a, (v) ->
		{
			this.spellcastingAbility = v;
		}, PropertyListener.SPELLCASTINGABILITY);
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json = putStrIntMap(json, "scores",
				scores.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue())),
				"ability", "score");
		json = putStrIntMap(json, "bgIncreases", bgIncreases.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue())), "ability", "increase");
		if (spellcastingAbility != null)
		{
			json = putStr(json, "spellcastingAbility", spellcastingAbility.toString());
		}

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		scores.clear();
		scores.putAll(getStrIntMap(data, "scores", "ability", "score").entrySet().stream()
				.collect(Collectors.toMap(e -> Ability.valueOf(e.getKey()), e -> e.getValue())));
		bgIncreases.clear();
		bgIncreases.putAll(getStrIntMap(data, "bgIncreases", "ability", "increase").entrySet().stream()
				.collect(Collectors.toMap(e -> Ability.valueOf(e.getKey()), e -> e.getValue())));
		String spAblName = data.optString("spellcastingAbility", "");
		if (!spAblName.isBlank())
		{
			spellcastingAbility = Ability.valueOf(spAblName);
		}
	}
}
