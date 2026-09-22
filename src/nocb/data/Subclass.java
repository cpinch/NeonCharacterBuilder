package nocb.data;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class Subclass extends Feature
{
	private String desc = "", associatedClass = "";

	private List<ClassFeature> subclassFeatures = new ArrayList<>();

	private CharacterClass cls;

	public void setCharClass(CharacterClass cls)
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

	@Override
	protected List<? extends Feature> getAllChildFeatures()
	{
		return getAllSubclassFeatures();
	}

	@Override
	protected List<? extends Feature> getChildFeatures()
	{
		return getSubclassFeatures();
	}

	public List<ClassFeature> getFeaturesAtLevel(int lvl)
	{
		List<ClassFeature> features = new ArrayList<>();
		for (ClassFeature f : subclassFeatures)
		{
			if (f.getLevel() == lvl)
			{
				features.add(f);
			}
		}
		return features;
	}

	public String getDesc()
	{
		return desc;
	}

	public void setDesc(String desc)
	{
		if (!this.desc.equals(desc))
		{
			this.desc = desc;
			setCustom(true);
		}
	}

	public String getAssociatedClassName()
	{
		return associatedClass;
	}

	public void setAssociatedClass(String clsName)
	{
		if (!this.associatedClass.equals(clsName))
		{
			this.associatedClass = clsName;
			setCustom(true);
		}
	}

	public List<ClassFeature> getAllSubclassFeatures()
	{
		subclassFeatures.sort((a, b) -> Integer.compare(a.getLevel(), b.getLevel()));
		return subclassFeatures;
	}

	public List<ClassFeature> getSubclassFeatures()
	{
		// Only return features of our class level or lower and drop any features of a
		// lower level that have the same name as a higher level feature
		List<ClassFeature> features = new ArrayList<>();
		for (ClassFeature f : subclassFeatures)
		{
			if (f.getLevel() > getClassLvl())
			{
				continue;
			}
			List<ClassFeature> existingFeatures = features.stream().filter(ef -> ef.getName().equals(f.getName()))
					.toList();
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
			features.add(f);
		}
		return features;
	}

	public void addSubclassFeature(String name)
	{
		ClassFeature cf = new ClassFeature();
		cf.setName(name);
		subclassFeatures.add(cf);
		setCustom(true);
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

	public static void addNewSubclass(String newName)
	{
		if (!newName.isBlank())
		{
			Subclass sc = new Subclass();
			sc.setName(newName);
			allSubclasses.add(sc);
		}
	}

	public static void loadSubclass(JSONObject data)
	{
		String name = data.getString("name");
		if (allSubclasses.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate subclass, skip
			return;
		}

		Subclass newSCls = new Subclass();

		newSCls.loadFromData(data);

		newSCls.desc = data.getString("desc");
		newSCls.associatedClass = data.getString("class");

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("features"))
				.forEach(feature -> newSCls.subclassFeatures.add(new ClassFeature(feature)));

		if (!newSCls.getName().isBlank())
		{
			allSubclasses.add(newSCls);
		}
	}

	public JSONObject saveSubclass()
	{
		JSONObject data = super.saveFeature();

		data.put("name", name);
		data.put("desc", desc);
		data.put("class", associatedClass);
		if (!subclassFeatures.isEmpty())
		{
			JSONArray cf = new JSONArray();
			for (ClassFeature c : subclassFeatures)
			{
				cf.put(c.saveClassFeature());
			}
			data.put("features", cf);
		}

		return data;
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = new JSONObject();

		json.put("classFeatures", new JSONArray(subclassFeatures.stream().map(cf -> cf.saveState()).toList()));

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		boolean successful = true;
		try
		{
			JSONArray featureStates = data.getJSONArray("classFeatures");
			for (int i = 0; i < featureStates.length(); i++)
			{
				JSONObject featureState = featureStates.getJSONObject(i);
				String featureName = featureState.getString("name");
				for (ClassFeature cf : subclassFeatures)
				{
					if (cf.getName().equals(featureName))
					{
						successful = successful && cf.loadState(featureState);
						break;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			successful = false;
		}
		return successful;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		super.addPropertyChangeListener(l);
		subclassFeatures.forEach(sf -> sf.addPropertyChangeListener(l));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		super.removePropertyChangeListener(l);
		subclassFeatures.forEach(sf -> sf.removePropertyChangeListener(l));
	}
}
