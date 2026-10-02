package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class Homeworld implements HasConfig
{
	private static int nId = 1;
	private final int id;
	private boolean custom = false;

	@Override
	public boolean isCustom()
	{
		return custom;
	}

	@Override
	public void setCustom(boolean b)
	{
		this.custom = b;
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

	public Homeworld()
	{
		this.id = nId++;
	}

	@Override
	public int getId()
	{
		return id;
	}

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allHomeworlds.stream().anyMatch(la -> la.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Homeworld newH = new Homeworld();
		newH.setCustom(custom);
		newH.loadConfig(data);
		allHomeworlds.add(newH);
	}

	// Loading
	private static final List<Homeworld> allHomeworlds = new ArrayList<>();

	public static List<Homeworld> getAllHomeworlds()
	{
		return new ArrayList<>(allHomeworlds);
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
		System.err.println("Unknown homeworld id " + id);
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
		System.err.println("Unknown homeworld " + name);
		return null;
	}

	public static Homeworld addNewHomeworld(String newName)
	{
		if (!newName.isBlank())
		{
			Homeworld hw = new Homeworld();
			hw.setName(newName);
			allHomeworlds.add(hw);
			return hw;
		}
		return null;
	}

	public static void sortAll()
	{
		allHomeworlds.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	private String name = "";
	private String desc;
	private final List<String> traits = new ArrayList<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		updateConfig(this.name, name, (v) ->
		{
			this.name = v;
		});
	}

	public List<String> getTraits()
	{
		return new ArrayList<>(traits);
	}

	public void setTraits(List<String> t)
	{
		updateConfig(this.traits, t, (v) ->
		{
			this.traits.clear();
			this.traits.addAll(v);
		});
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
		updateConfig(this.desc, d, (v) ->
		{
			this.desc = v;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "name", name);
		json = putList(json, "traits", traits);
		json = putStr(json, "desc", desc);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");
		traits.clear();
		traits.addAll(getList(data, "traits"));
		desc = data.optString("desc", "");
	}

	@Override
	public String toString()
	{
		return name;
	}
}
