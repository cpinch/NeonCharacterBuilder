package ncb.data;

import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class SpellSlots implements HasConfig
{
	private Customizable parent;

	@Override
	public Customizable getParent()
	{
		return parent;
	}

	@Override
	public void setParent(Customizable p)
	{
		this.parent = p;
	}

	@Override
	public int getId()
	{
		return 0;
	}

	private final Integer[] slots = new Integer[9];

	public SpellSlots()
	{
		for (int i = 0; i < slots.length; i++)
		{
			slots[i] = 0;
		}
	}

	public SpellSlots(JSONObject data)
	{
		super();
		loadConfig(data);
	}

	public Integer[] getSlots()
	{
		return slots;
	}

	public int getFirstNonZeroSlotLevel()
	{
		for (int spLvl = 1; spLvl <= 9; spLvl++)
		{
			if (slots[spLvl - 1] > 0)
			{
				return spLvl;
			}
		}
		return 1;
	}

	public int getHighestSlotLevel()
	{
		for (int spLvl = 9; spLvl >= 1; spLvl--)
		{
			if (slots[spLvl - 1] > 0)
			{
				return spLvl;
			}
		}
		return 1;
	}

	public int getCount(int spLvl)
	{
		return slots[spLvl - 1];
	}

	public void setSlot(int spLvl, int val)
	{
		updateConfig(slots[spLvl - 1], val, (v) ->
		{
			slots[spLvl - 1] = val;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		// Only save slots if we have any
		if (Arrays.asList(slots).stream().anyMatch(n -> n > 0))
		{
			json = putIntList(json, "slots", Arrays.asList(slots));
		}

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		List<Integer> list = getIntList(data, "slots");
		for (int i = 0; i < list.size() && i < this.slots.length; i++)
		{
			this.slots[i] = list.get(i);
		}
	}

	@Override
	public String toString()
	{
		return "Slots: "
				+ String.join(", ", Arrays.asList(slots).stream().map(i -> i == null ? "" : i.toString()).toList());
	}
}
