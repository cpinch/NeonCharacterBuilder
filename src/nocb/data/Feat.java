package nocb.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import nocb.io.JsonDataLoader;
import nocb.main.CharacterSheet;

public class Feat extends Selectable
{
	private String featType = "";

	public Feat()
	{
		super();
	}

	public String getFeatType()
	{
		return featType;
	}

	public void setFeatType(String type)
	{
		this.featType = type;
	}

	public String getFeatString()
	{
		List<String> allText = getAllText();
		if (allText.isEmpty())
		{
			return this.name;
		}
		else
		{
			return this.name + " - " + String.join(", ", allText);
		}
	}

	// We don't want feats to print into features, they have their own area
	@Override
	public List<String> getAllFeatureStrings()
	{
		return Collections.emptyList();
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
		System.out.println("Unknown feat id " + id);
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
		System.out.println("Unknown feat " + name);
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

	public static void loadFeat(JSONObject data)
	{
		String name = data.getString("name");
		if (allFeats.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate feat, skip
			return;
		}

		Feat newFeat = new Feat();
		newFeat.name = name;

		newFeat.type = "Feat";
		newFeat.featType = data.optString("type", "");

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("prereqs"))
				.forEach(p -> newFeat.prereqs.add(new SelectablePrereq(p)));

		JsonDataLoader.jsonArrayToObjectArray(data.getJSONArray("features"))
				.forEach(f -> newFeat.features.add(new SelectableFeature(f)));

		newFeat.setCustom(data.optBoolean("custom", false));

		if (!newFeat.getName().isBlank())
		{
			allFeats.add(newFeat);
		}
	}

	public JSONObject saveFeat()
	{
		JSONObject data = super.saveSelectable();

		data.put("type", featType.isBlank() ? "" : featType);

		return data;
	}
}
