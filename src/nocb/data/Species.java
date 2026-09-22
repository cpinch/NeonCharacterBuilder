package nocb.data;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;

public class Species extends Feature
{
	private String type = "", desc = "", size = "", selectedSize = "";
	private Homeworld speciesHomeworld;

	private final List<SpeciesTrait> traits = new ArrayList<>();

	private Callable<Integer> getCharLevel;

	public Species()
	{
		super();
	}

	public void setupGetCharLevel(Callable<Integer> getCharLevel)
	{
		this.getCharLevel = getCharLevel;
	}

	public int getCharLevel()
	{
		try
		{
			return getCharLevel.call();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return 1;
		}
	}

	@Override
	protected List<? extends Feature> getAllChildFeatures()
	{
		return getAllTraits();
	}

	@Override
	protected List<? extends Feature> getChildFeatures()
	{
		return getTraits();
	}

	public List<SpeciesTrait> getTraitsAtLevel(int lvl)
	{
		List<SpeciesTrait> features = new ArrayList<>();
		for (SpeciesTrait f : traits)
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

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		if (!this.type.equals(type))
		{
			this.type = type;
			setCustom(true);
		}
	}

	public String getSpeciesSize()
	{
		return size;
	}

	public void setSpeciesSize(String size)
	{
		if (!this.size.equals(size))
		{
			this.size = size;
			setCustom(true);
		}
	}

	public String getSize()
	{
		return selectedSize.isBlank() ? size : selectedSize;
	}

	public void setSize(String s)
	{
		this.selectedSize = s;
	}

	public Homeworld getHomeworld()
	{
		return speciesHomeworld;
	}

	public void setHomeworld(Homeworld h)
	{
		if (!this.speciesHomeworld.equals(h))
		{
			this.speciesHomeworld = h;
			setCustom(true);
		}
	}

	public List<SpeciesTrait> getAllTraits()
	{
		traits.sort((a, b) -> a.getName().compareTo(b.getName()));
		return traits;
	}

	public List<SpeciesTrait> getTraits()
	{
		int lvl = getCharLevel();
		// Only return traits of our character level or lower and drop any traits of a
		// lower level that have the same name as a higher level trait
		List<SpeciesTrait> features = new ArrayList<>();
		for (SpeciesTrait f : traits)
		{
			if (f.getLevel() > lvl)
			{
				continue;
			}
			List<SpeciesTrait> existingFeatures = features.stream().filter(ef -> ef.getName().equals(f.getName()))
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
		return traits;
	}

	public void addNewTrait(String name)
	{
		SpeciesTrait st = new SpeciesTrait();
		st.setName(name);
		traits.add(st);
		setCustom(true);
	}

	public void removeLastTrait()
	{
		traits.remove(traits.size() - 1);
		setCustom(true);
	}

	// Calculated

	public boolean isSpellcaster()
	{
		return !this.getAllSpellsGranted().isEmpty();
	}

	// Loading
	private static final List<Species> allSpecies = new ArrayList<>();

	public static List<Species> getAllSpecies()
	{
		return allSpecies;
	}

	public static Species getByName(String name)
	{
		for (Species s : allSpecies)
		{
			if (s.getName().equals(name))
			{
				return s;
			}
		}
		System.err.println("Unknown species " + name);
		return null;
	}

	public static Species getById(int id)
	{
		for (Species s : allSpecies)
		{
			if (s.getId() == id)
			{
				return s;
			}
		}
		System.err.println("Unknown species id " + id);
		return null;
	}

	public static void sortAll()
	{
		allSpecies.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void addNewSpecies(String newName)
	{
		if (!newName.isBlank())
		{
			Species sp = new Species();
			sp.setName(newName);
			allSpecies.add(sp);
		}
	}

	public static void loadSpecies(JSONObject data)
	{
		String name = data.getString("name");
		if (allSpecies.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate species, skip
			return;
		}

		Species newSpecies = new Species();

		newSpecies.name = name;
		newSpecies.desc = data.getString("desc");
		newSpecies.type = data.optString("type", "Humanoid");
		JSONArray size = data.optJSONArray("size");
		if (size == null)
		{
			newSpecies.size = "M";
		}
		else
		{
			for (int i = 0; i < size.length(); i++)
			{
				newSpecies.size += size.getString(i);
			}
		}
		if (newSpecies.size.length() > 1)
		{
			newSpecies.selectedSize = "" + newSpecies.size.charAt(0);
		}
		newSpecies.speedMod = data.optInt("speed", 30); // We use speedMod for base speed so it sums up well

		String home = data.optString("homeworld", "");
		if (!home.isBlank())
		{
			newSpecies.speciesHomeworld = Homeworld.getByName(home);
		}

		JsonDataLoader.jsonArrayToObjectArray(data.optJSONArray("traits"))
				.forEach(trait -> newSpecies.traits.add(new SpeciesTrait(trait)));

		if (!newSpecies.getName().isBlank())
		{
			allSpecies.add(newSpecies);
		}
	}

	public JSONObject saveSpecies()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		data.put("desc", desc);
		data.put("type", type);
		JSONArray sz = new JSONArray();
		for (int i = 0; i < size.length(); i++)
		{
			sz.put("" + size.charAt(i));
		}
		data.put("size", sz);
		data.put("speed", speedMod);
		if (speciesHomeworld != null)
		{
			data.put("homeworld", speciesHomeworld.getName());
		}
		if (!traits.isEmpty())
		{
			JSONArray ts = new JSONArray();
			for (SpeciesTrait trait : traits)
			{
				ts.put(trait.saveTrait());
			}
			data.put("traits", ts);
		}
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

		json.put("name", "name");
		json.put("size", selectedSize);

		JSONArray arr = new JSONArray();
		for (SpeciesTrait t : traits)
		{
			arr.put(t.saveState());
		}
		json.put("traits", arr);

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		boolean success = true;
		try
		{
			this.selectedSize = data.getString("size");
			JSONArray arr = data.getJSONArray("traits");
			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject t = arr.getJSONObject(i);
				String tName = t.getString("name");
				for (SpeciesTrait st : traits)
				{
					if (st.getName().equals(tName))
					{
						success = success && st.loadState(t);
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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener l)
	{
		super.addPropertyChangeListener(l);
		traits.forEach(t -> t.addPropertyChangeListener(l));
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener l)
	{
		super.removePropertyChangeListener(l);
		traits.forEach(t -> t.removePropertyChangeListener(l));
	}
}
