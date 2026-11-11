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

	private final int[] slots = new int[9];

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

	public SpellSlots(int... slots)
	{
		for (int i = 0; i < this.slots.length && i < slots.length; i++)
		{
			this.slots[i] = slots[i];
		}
	}

	public int[] getSlots()
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
		if (Arrays.stream(slots).anyMatch(n -> n > 0))
		{
			json = putIntList(json, "slots", Arrays.stream(slots).boxed().toList());
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

	private static final List<SpellSlots> multiclassSlots = List.of(new SpellSlots(2), new SpellSlots(3),
			new SpellSlots(4, 2), new SpellSlots(4, 3), new SpellSlots(4, 3, 2), new SpellSlots(4, 3, 3),
			new SpellSlots(4, 3, 3, 1), new SpellSlots(4, 3, 3, 2), new SpellSlots(4, 3, 3, 3, 1),
			new SpellSlots(4, 3, 3, 3, 2), new SpellSlots(4, 3, 3, 3, 2, 1), new SpellSlots(4, 3, 3, 3, 2, 1),
			new SpellSlots(4, 3, 3, 3, 2, 1, 1), new SpellSlots(4, 3, 3, 3, 2, 1, 1),
			new SpellSlots(4, 3, 3, 3, 2, 1, 1, 1), new SpellSlots(4, 3, 3, 3, 2, 1, 1, 1),
			new SpellSlots(4, 3, 3, 3, 3, 1, 1, 1, 1), new SpellSlots(4, 3, 3, 3, 3, 1, 1, 1, 1),
			new SpellSlots(4, 3, 3, 3, 3, 2, 1, 1, 1), new SpellSlots(4, 3, 3, 3, 3, 2, 2, 1, 1));

	public static SpellSlots getMulticlassSlots(int spellcasterLevel)
	{
		return multiclassSlots.get(spellcasterLevel);
	}
}
