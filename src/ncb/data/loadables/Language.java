package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class Language implements HasConfig
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

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allLanguages.stream().anyMatch(la -> la.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Language newL = new Language();
		newL.setCustom(custom);
		newL.loadConfig(data);
		allLanguages.add(newL);
	}

	public Language()
	{
		this.id = nId++;
	}

	@Override
	public int getId()
	{
		return id;
	}

	// Loading
	private static final List<Language> allLanguages = new ArrayList<>();

	public static List<Language> getAllLanguages()
	{
		return new ArrayList<>(allLanguages);
	}

	public static Language getById(int id)
	{
		for (Language l : allLanguages)
		{
			if (l.getId() == id)
			{
				return l;
			}
		}
		System.err.println("Unknown language id " + id);
		return null;
	}

	public static Language getByName(String name)
	{
		for (Language l : allLanguages)
		{
			if (l.getName().equals(name))
			{
				return l;
			}
		}
		System.err.println("Unknown language " + name);
		Thread.dumpStack();
		return null;
	}

	public static Language addNewLanguage(String newName)
	{
		if (!newName.isBlank())
		{
			Language l = new Language();
			l.setName(newName);
			allLanguages.add(l);
			return l;
		}
		return null;
	}

	public static void sortAll()
	{
		allLanguages.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	private String name = "";
	private final List<String> spokenAt = new ArrayList<>();

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

	public List<String> getSpokenAt()
	{
		return new ArrayList<>(spokenAt);
	}

	public void setSpokeAt(List<String> spoken)
	{
		updateConfig(this.spokenAt, spoken, (v) ->
		{
			this.spokenAt.clear();
			this.spokenAt.addAll(spoken);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "name", name);
		json = putList(json, "locations", spokenAt);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");
		spokenAt.clear();
		spokenAt.addAll(getList(data, "locations"));
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getId() == ((Language) o).getId();
	}
}
