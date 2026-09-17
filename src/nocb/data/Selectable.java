package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;
import nocb.main.CharacterSheet;

public class Selectable extends Feature
{
	protected String type = "";
	protected List<SelectablePrereq> prereqs = new ArrayList<>();
	protected List<SelectableFeature> features = new ArrayList<>();

	public Selectable()
	{
		super();
	}

	@Override
	protected List<? extends Feature> getChildFeatures()
	{
		return features;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public List<SelectablePrereq> getPrereqs()
	{
		return prereqs;
	}

	public void addNewPrereq()
	{
		prereqs.add(new SelectablePrereq());
	}

	public boolean prereqsMet(CharacterSheet sheet)
	{
		for (SelectablePrereq fp : prereqs)
		{
			if (!fp.met(sheet))
			{
				return false;
			}
		}
		return true;
	}

	public List<SelectableFeature> getFeatures()
	{
		return features;
	}

	public void addNewFeature(String name)
	{
		SelectableFeature sf = new SelectableFeature();
		sf.setName(name);
		features.add(sf);
	}

	// Loading
	private static final List<Selectable> allSelectables = new ArrayList<>();

	public static List<Selectable> getAllSelectables()
	{
		return allSelectables;
	}

	public static List<Selectable> getAllValidSelectables(CharacterSheet sheet)
	{
		List<Selectable> filtered = new ArrayList<>();

		for (Selectable f : allSelectables)
		{
			if (f.prereqsMet(sheet))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	public static List<Selectable> getAllValidSelectablesOfType(CharacterSheet sheet, String type)
	{
		List<Selectable> filtered = new ArrayList<>();

		for (Selectable f : allSelectables)
		{
			if (f.type.equals(type) && f.prereqsMet(sheet))
			{
				filtered.add(f);
			}
		}

		return filtered;
	}

	public static List<Selectable> getSelectablesByType(String type)
	{
		List<Selectable> sels = new ArrayList<>();
		for (Selectable f : allSelectables)
		{
			if (f.getType().equals(type))
			{
				sels.add(f);
			}
		}
		return sels;
	}

	public static Selectable getSelectableById(int id)
	{
		for (Selectable f : allSelectables)
		{
			if (f.getId() == id)
			{
				return f;
			}
		}
		System.out.println("Error! Could not find selectable with id " + id);
		return null;
	}

	public static Selectable getSelectableByTypeAndName(String type, String name)
	{
		for (Selectable f : allSelectables)
		{
			if (f.getType().equals(type) && f.getName().equals(name))
			{
				return f;
			}
		}
		System.out.println("Error! Could not find selectable with type " + type + " and name " + name);
		return null;
	}

	public static void addNewSelectable(String name)
	{
		Selectable newSel = new Selectable();
		newSel.name = name;
		allSelectables.add(newSel);
	}

	public static void sortAll()
	{
		allSelectables.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadSelectable(JSONObject data)
	{
		String type = data.getString("type");
		String name = data.getString("name");

		if (allSelectables.stream().anyMatch(s -> s.getType().equals(type) && s.getName().equals(name)))
		{
			// Duplicate selectable, skip
			return;
		}

		Selectable newSel = new Selectable();

		newSel.type = type;
		newSel.name = name;

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("prereqs"))
				.forEach(p -> newSel.prereqs.add(new SelectablePrereq(p)));

		JsonDataLoader.jsonArrayToObjectArray(data.getJSONArray("features"))
				.forEach(f -> newSel.features.add(new SelectableFeature(f)));

		newSel.setCustom(data.optBoolean("custom", false));

		if (!newSel.getName().isBlank())
		{
			allSelectables.add(newSel);
		}
	}

	public JSONObject saveSelectable()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		data.put("type", type);

		if (!prereqs.isEmpty())
		{
			JSONArray preArr = new JSONArray();
			for (SelectablePrereq p : prereqs)
			{
				preArr.put(p.saveSelectablePrereq());
			}
			data.put("prereqs", preArr);
		}

		JSONArray feaArr = new JSONArray();
		for (SelectableFeature f : features)
		{
			feaArr.put(f.saveSelectableFeature());
		}
		data.put("features", feaArr);

		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("name", name);
		json.put("type", type);

		JSONArray arr = new JSONArray();
		for (SelectableFeature f : features)
		{
			arr.put(f.saveState());
		}
		json.put("features", arr);

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		boolean success = true;
		try
		{
			JSONArray arr = data.getJSONArray("features");

			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject fo = arr.getJSONObject(i);
				String foName = fo.getString("name");
				for (SelectableFeature f : features)
				{
					if (f.getName().equals(foName))
					{
						f.loadState(fo);
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;
		}
		return success;
	}
}
