package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class Homeworld
{
	private static int nId = 1;

	private final int id;
	private String name = "";
	private final List<String> traits = new ArrayList<>();
	private String desc;
	private boolean custom = false;

	public Homeworld()
	{
		this.id = nId++;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public int getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<String> getTraits()
	{
		return traits;
	}

	public void setTraits(List<String> t)
	{
		traits.clear();
		traits.addAll(t);
	}

	public boolean hasTrait(String trait)
	{
		return traits.contains(trait) || traits.contains("Any");
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String d)
	{
		desc = d;
	}

	public boolean isCustom()
	{
		return custom;
	}

	public void setCustom(boolean cust)
	{
		this.custom = cust;
	}

	// Loading
	private static final List<Homeworld> allHomeworlds = new ArrayList<>();

	public static List<Homeworld> getAllHomeworlds()
	{
		return allHomeworlds;
	}

	public static Homeworld getById(int id)
	{
		for (Homeworld h : allHomeworlds)
		{
			if (h.getId() == id)
			{
				return h;
			}
		}
		System.out.println("Unknown homeworld id " + id);
		return null;
	}

	public static Homeworld getByName(String name)
	{
		for (Homeworld h : allHomeworlds)
		{
			if (h.getName().equals(name))
			{
				return h;
			}
		}
		System.out.println("Unknown homeworld " + name);
		return null;
	}

	public static void addNewHomeworld(String newName)
	{
		if (!newName.isBlank())
		{
			Homeworld hw = new Homeworld();
			hw.setName(newName);
			allHomeworlds.add(hw);
		}
	}

	public static void sortAll()
	{
		allHomeworlds.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadHomeworld(JSONObject data)
	{
		String name = data.getString("name");
		if (allHomeworlds.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate homeworld, skip
			return;
		}

		Homeworld newWorld = new Homeworld();
		newWorld.name = name;
		JsonDataLoader.jsonArrayToStringArray(data.getJSONArray("traits")).forEach(trait -> newWorld.traits.add(trait));
		newWorld.desc = data.getString("desc");
		newWorld.setCustom(data.optBoolean("custom", false));

		if (!newWorld.getName().isBlank())
		{
			allHomeworlds.add(newWorld);
		}
	}

	public JSONObject saveHomeworld()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		JSONArray traitArr = new JSONArray();
		for (String trait : traits)
		{
			traitArr.put(trait);
		}
		data.put("traits", traitArr);
		data.put("desc", desc);
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}
}
