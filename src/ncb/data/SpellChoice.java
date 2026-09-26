package ncb.data;

import java.beans.PropertyChangeSupport;

import org.json.JSONObject;

import ncb.data.interfaces.HasState;
import ncb.data.loadables.Spell;
import ncb.data.loadables.SpellList;
import ncb.main.PropertyListener;

public class SpellChoice implements HasState
{
	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);

	@Override
	public PropertyChangeSupport getPCS()
	{
		return pcs;
	}

	public SpellChoice()
	{
		addPropertyChangeListener(PropertyListener.getListener());
	}

	public SpellChoice(SpellList spellList, int lvl)
	{
		this.spellList = spellList;
		// Default to the first spell of the appropriate level in the list
		selected = spellList.getSpellsForLevel(lvl).get(0);
		addPropertyChangeListener(PropertyListener.getListener());
	}

	// State
	private SpellList spellList;
	private Spell selected;

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
		updateWithAlert(this.selected, s, (v) ->
		{
			this.selected = v;
		}, PropertyListener.SPELLS);
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "spellList", spellList.getName());
		json = putObj(json, "selected", Spell.saveToJSONObject(selected));

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		spellList = SpellList.getForClass(data.getString("spellList"));
		selected = Spell.getFromJSONObject(data.getJSONObject("selected"));
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getSpellList() == ((SpellChoice) o).getSpellList();
	}

	@Override
	public String toString()
	{
		return "SpellList: " + spellList + " / Selected: " + selected;
	}
}
