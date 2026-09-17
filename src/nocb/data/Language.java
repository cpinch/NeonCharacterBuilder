package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class Language
{
	private static int nId = 1;

	private final int id;
	private String name = "";
	private final List<String> spokenAt = new ArrayList<>();
	private boolean custom = false;

	public Language()
	{
		this.id = nId++;
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

	public List<String> getSpokenAt()
	{
		return spokenAt;
	}

	public void setSpokeAt(List<String> spoken)
	{
		this.spokenAt.clear();
		this.spokenAt.addAll(spoken);
	}

	public boolean isCustom()
	{
		return custom;
	}

	public void setCustom(boolean custom)
	{
		this.custom = custom;
	}

	// Loading
	private static final List<Language> allLanguages = new ArrayList<>();

	public static List<Language> getAllLanguages()
	{
		return allLanguages;
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
		System.out.println("Unknown language id " + id);
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
		System.out.println("Unknown language " + name);
		return null;
	}

	public static void addNewLanguage(String newName)
	{
		if (!newName.isBlank())
		{
			Language l = new Language();
			l.setName(newName);
			allLanguages.add(l);
		}
	}

	public static void sortAll()
	{
		allLanguages.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadLanguages(JSONObject data)
	{
		String name = data.getString("name");
		if (allLanguages.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate language
			return;
		}

		Language newLang = new Language();
		newLang.name = name;
		JsonDataLoader.jsonArrayToStringArray(data.getJSONArray("locations")).forEach(loc -> newLang.spokenAt.add(loc));
		newLang.custom = data.optBoolean("custom", false);

		if (!newLang.name.isBlank())
		{
			allLanguages.add(newLang);
		}
	}

	public JSONObject saveLanguage()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		data.put("locations", spokenAt);
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	@Override
	public String toString()
	{
		return name;
	}
}
