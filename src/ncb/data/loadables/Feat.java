package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.main.CharacterSheet;

public class Feat extends Selectable
{
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

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allFeats.stream().anyMatch(ft -> ft.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Feat newFt = new Feat();
		newFt.loadConfig(data);
		newFt.setCustom(custom);
		allFeats.add(newFt);
	}

	// Loading
	private static final List<Feat> allFeats = new ArrayList<>();

	public static List<Feat> getAllLoadedFeats()
	{
		return allFeats;
	}

	public static List<Feat> getAllValidFeats(CharacterSheet sheet)
	{
		List<Feat> filteredFeats = new ArrayList<>();

		for (Feat f : allFeats)
		{
			if (f.prereqsMet(sheet))
			{
				filteredFeats.add(f);
			}
		}

		return filteredFeats;
	}

	public static List<Feat> getAllFeatsOfType(CharacterSheet sheet, String featType)
	{
		List<Feat> filteredFeats = new ArrayList<>();

		for (Feat f : allFeats)
		{
			if (f.featType.equals(featType))
			{
				filteredFeats.add(f);
			}
		}

		return filteredFeats;
	}

	public static List<Feat> getAllValidFeatsOfType(CharacterSheet sheet, String featType)
	{
		List<Feat> filteredFeats = new ArrayList<>();

		for (Feat f : allFeats)
		{
			if (f.featType.equals(featType) && f.prereqsMet(sheet))
			{
				filteredFeats.add(f);
			}
		}

		return filteredFeats;
	}

	public static Feat getById(int id)
	{
		for (Feat f : allFeats)
		{
			if (f.getId() == id)
			{
				return f;
			}
		}
		System.err.println("Unknown feat id " + id);
		return null;
	}

	public static Feat getByName(String name)
	{
		for (Feat f : allFeats)
		{
			if (f.getName().equals(name))
			{
				return f;
			}
		}
		System.err.println("Unknown feat " + name);
		return null;
	}

	public static void addNewFeat(String newName)
	{
		if (!newName.isBlank())
		{
			Feat ft = new Feat();
			ft.setName(newName);
			allFeats.add(ft);
		}
	}

	public static void sortAll()
	{
		allFeats.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	private String featType = "";

	public String getFeatType()
	{
		return featType;
	}

	public void setFeatType(String type)
	{
		updateConfig(featType, type, (v) ->
		{
			this.featType = type;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putStr(json, "type", featType);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		type = "Feat";
		featType = data.optString("type", "");
	}
}
