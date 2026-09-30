package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;

import ncb.data.Feature;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.GetAll;

public class Subclass extends Feature
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
		return getCls();
	}

	@Override
	public void setParent(Customizable p)
	{
	}

	@Override
	public List<? extends GetAll> getChildren()
	{
		return getSubclassFeatures();
	}

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allSubclasses.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Subclass newS = new Subclass();
		newS.loadConfig(data);
		newS.setCustom(custom);
		allSubclasses.add(newS);
	}

	private CharacterClass cls;

	private CharacterClass getCls()
	{
		return cls;
	}

	public void setCls(CharacterClass cls)
	{
		this.cls = cls;
	}

	private int getClassLvl()
	{
		if (cls != null)
		{
			return cls.getLevel();
		}
		return 1;
	}

	public void addSubclassFeature(String name)
	{
		Feature cf = new Feature();
		cf.setName(name);
		cf.setParent(this);
		subclassFeatures.add(cf);
		setCustom(true);
	}

	public void removeSubclassFeature(int id)
	{
		Optional<Feature> fOpt = subclassFeatures.stream().filter(f -> f.getId() == id).findAny();
		if (fOpt.isPresent())
		{
			subclassFeatures.remove(fOpt.get());
			setCustom(true);
		}
	}

	public void removeLastSubclassFeature()
	{
		subclassFeatures.remove(subclassFeatures.size() - 1);
		setCustom(true);
	}

	// Loading
	private static final List<Subclass> allSubclasses = new ArrayList<>();

	public static List<Subclass> getAllSubclasses()
	{
		return allSubclasses;
	}

	public static List<Subclass> getForClassName(String clsName)
	{
		List<Subclass> scs = new ArrayList<>();
		for (Subclass sc : allSubclasses)
		{
			if (sc.getAssociatedClassName().equals(clsName))
			{
				scs.add(sc);
			}
		}
		return scs;
	}

	public static Subclass getByName(String name)
	{
		for (Subclass sc : allSubclasses)
		{
			if (sc.getName().equals(name))
			{
				return sc;
			}
		}
		System.err.println("Unknown subclass " + name);
		return null;
	}

	public static Subclass getById(int id)
	{
		for (Subclass sc : allSubclasses)
		{
			if (sc.getId() == id)
			{
				return sc;
			}
		}
		System.err.println("Unknown subclass id " + id);
		return null;
	}

	public static void sortAll()
	{
		allSubclasses.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static Customizable addNewSubclass(String newName)
	{
		if (!newName.isBlank())
		{
			Subclass sc = new Subclass();
			sc.setName(newName);
			allSubclasses.add(sc);
			return sc;
		}
		return null;
	}

	// Configuration
	private String desc = "", associatedClass = "";
	private List<Feature> subclassFeatures = new ArrayList<>();

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		updateConfig(this.desc, desc, (v) ->
		{
			this.desc = v;
		});
	}

	public String getAssociatedClassName()
	{
		return associatedClass;
	}

	public void setAssociatedClass(String clsName)
	{
		updateConfig(this.associatedClass, clsName, (v) ->
		{
			this.associatedClass = v;
		});
	}

	@Override
	public List<String> getFeatureNamesAtLevel(int lvl)
	{
		List<String> names = new ArrayList<>();
		subclassFeatures.forEach(f -> names.addAll(f.getFeatureNamesAtLevel(lvl)));
		return names;
	}

	public List<Feature> getAllSubclassFeatures()
	{
		subclassFeatures.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
		return subclassFeatures;
	}

	public List<Feature> getSubclassFeatures()
	{
		// Only return features of our class level or lower and drop any features of a
		// lower level that have the same name as a higher level feature
		final int level = getClassLvl();
		List<Feature> features = new ArrayList<>();
		for (Feature f : subclassFeatures)
		{
			if (f.getLevel() > level)
			{
				continue;
			}
			List<Feature> existingFeatures = features.stream().filter(ef -> ef.getName().equals(f.getName())).toList();
			if (!existingFeatures.isEmpty())
			{
				if (f.getLevel() > existingFeatures.get(0).getLevel())
				{
					features.remove(existingFeatures.get(0));
				}
				else
				{
					continue;
				}
			}
			// Handle upgrades
			features.add(f.getHighestFeature(level));
		}
		return features;
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putStr(json, "desc", desc);
		json = putStr(json, "class", associatedClass);
		json = putObjList(json, "features", subclassFeatures.stream().map(f -> f.saveConfig()).toList());

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		this.desc = data.optString("desc", "");
		this.associatedClass = data.optString("class", "");
		getObjList(data, "features").forEach(fs ->
		{
			Feature f = new Feature();
			f.loadConfig(fs);
			f.setParent(this);
			subclassFeatures.add(f);
		});
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		json = putObjList(json, "features", subclassFeatures.stream().map(f ->
		{
			JSONObject d = new JSONObject();
			d.put("fname", f.getName());
			d.put("flvl", f.getLevel());
			d.put("fstate", f.saveState());
			return d;
		}).toList());

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		super.loadState(data);

		getObjList(data, "features").forEach(f ->
		{
			String fname = f.getString("fname");
			int flvl = f.getInt("flvl");
			for (Feature f2 : subclassFeatures)
			{
				if (f2.getName().equals(fname) && f2.getLevel() == flvl)
				{
					f2.loadState(f.getJSONObject("fstate"));
					break;
				}
			}
		});
	}
}
