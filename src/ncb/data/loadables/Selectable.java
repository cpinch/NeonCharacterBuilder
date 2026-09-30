package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.Feature;
import ncb.data.Prereq;
import ncb.data.interfaces.GetAll;
import ncb.main.CharacterSheet;

public class Selectable extends Feature
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

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		String type = data.optString("type", "");
		// Selectables are only dupes on both type and name match
		if (allSelectables.stream().anyMatch(sel -> sel.getName().equals(name) && sel.getType().equals(type)))
		{
			// Dupe, ignore
			return;
		}
		Selectable newSel = new Selectable();
		newSel.loadConfig(data);
		newSel.setCustom(custom);
		allSelectables.add(newSel);
	}

	@Override
	public List<? extends GetAll> getChildren()
	{
		// Handle upgrades
		return getFeatures();
	}

	public boolean prereqsMet(CharacterSheet sheet)
	{
		for (Prereq fp : prereqs)
		{
			if (!fp.met(sheet))
			{
				return false;
			}
		}
		return true;
	}

	public void addNewPrereq()
	{
		Prereq pr = new Prereq();
		prereqs.add(pr);
		pr.setParent(this);
		setCustom(true);
	}

	public void removeLastPrereq()
	{
		prereqs.remove(prereqs.size() - 1);
		setCustom(true);
	}

	public void addNewFeature(String name)
	{
		Feature sf = new Feature();
		sf.setName(name);
		sf.setParent(this);
		features.add(sf);
		setCustom(true);
	}

	public void removeLastFeature()
	{
		features.remove(features.size() - 1);
		setCustom(true);
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
		System.err.println("Error! Could not find selectable with id " + id);
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
		System.err.println("Error! Could not find selectable with type " + type + " and name " + name);
		return null;
	}

	public static Selectable addNewSelectable(String name)
	{
		if (!name.isBlank())
		{
			Selectable newSel = new Selectable();
			newSel.setName(name);
			newSel.type = "New Selectable";
			allSelectables.add(newSel);
			return newSel;
		}
		return null;
	}

	public static void sortAll()
	{
		allSelectables.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	protected String type = "";
	protected List<Prereq> prereqs = new ArrayList<>();
	protected List<Feature> features = new ArrayList<>();

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		updateConfig(this.type, type, (v) ->
		{
			this.type = v;
		});
	}

	public List<Prereq> getPrereqs()
	{
		return prereqs;
	}

	public void setPrereqs(List<Prereq> prereqs)
	{
		updateConfig(this.prereqs, prereqs, (v) ->
		{
			this.prereqs.clear();
			this.prereqs.addAll(v);
		});
	}

	public List<Feature> getFeatures()
	{
		// Handle upgrades
		return features.stream().map(f -> f.getHighestFeature(getTopLevel())).toList();
	}

	public List<Feature> getOriginalFeatures()
	{
		return features;
	}

	@Override
	public List<String> getFeatureNamesAtLevel(int lvl)
	{
		List<String> names = new ArrayList<>();
		if (this.getHighestFeature(lvl).getLevel() == lvl)
		{
			names.add(getName());
		}
		features.forEach(f -> names.addAll(f.getFeatureNamesAtLevel(lvl)));

		return names;
	}

	public void setFeatures(List<Feature> features)
	{
		updateConfig(this.features, features, (v) ->
		{
			this.features.clear();
			this.features.addAll(v);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putStr(json, "type", type);
		json = putObjList(json, "prereqs", prereqs.stream().map(p -> p.saveConfig()).toList());
		json = putObjList(json, "features", features.stream().map(f -> f.saveConfig()).toList());

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		type = data.optString("type", "");
		prereqs.clear();
		getObjList(data, "prereqs").forEach(p ->
		{
			Prereq pr = new Prereq();
			pr.loadConfig(p);
			prereqs.add(pr);
		});
		getObjList(data, "features").forEach(f ->
		{
			Feature fe = new Feature();
			fe.loadConfig(f);
			features.add(fe);
		});
	}
}
