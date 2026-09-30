package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.json.JSONObject;

import ncb.data.Feature;
import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.GetAll;
import ncb.main.PropertyListener;

public class Species extends Feature
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

	@Override
	public int getTopLevel()
	{
		try
		{
			return getCharLevel.call();
		}
		catch (Exception e)
		{
			return 1;
		}
	}

	@Override
	public List<? extends GetAll> getChildren()
	{
		return getTraits();
	}

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allSpecies.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Species newS = new Species();
		newS.loadConfig(data);
		newS.setCustom(custom);
		allSpecies.add(newS);
	}

	private Callable<Integer> getCharLevel;

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

	public List<String> getTraitNamesAtLevel(int lvl)
	{
		List<String> names = new ArrayList<>();
		traits.forEach(f -> names.addAll(f.getFeatureNamesAtLevel(lvl)));
		return names;
	}

	public List<Feature> getAllTraits()
	{
		traits.sort((a, b) -> a.getName().compareTo(b.getName()));
		return traits;
	}

	public List<Feature> getTraits()
	{
		final int lvl = getCharLevel();
		List<Feature> features = new ArrayList<>();
		for (Feature f : traits)
		{
			if (f.getLevel() > lvl)
			{
				continue;
			}
			// Handle upgrades
			features.add(f.getHighestFeature(lvl));
		}
		return features;
	}

	public void addNewTrait(String name)
	{
		Feature st = new Feature();
		st.setName(name);
		st.setParent(this);
		traits.add(st);
		setCustom(true);
	}

	public void removeSpeciesTrait(int id)
	{
		Optional<Feature> fOpt = traits.stream().filter(f -> f.getId() == id).findAny();
		if (fOpt.isPresent())
		{
			traits.remove(fOpt.get());
			setCustom(true);
		}
	}

	public void removeLastTrait()
	{
		traits.remove(traits.size() - 1);
		setCustom(true);
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

	public static Species addNewSpecies(String newName)
	{
		if (!newName.isBlank())
		{
			Species sp = new Species();
			sp.setName(newName);
			allSpecies.add(sp);
			return sp;
		}
		return null;
	}

	// Configuration
	private String type = "", desc = "", size = "";
	private Homeworld speciesHomeworld;
	private final List<Feature> traits = new ArrayList<>();

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

	public String getSpeciesSize()
	{
		return size;
	}

	public void setSpeciesSize(String size)
	{
		updateConfig(this.size, size, (v) ->
		{
			this.size = v;
		});
	}

	public String getSize()
	{
		return selectedSize.isBlank() ? size : selectedSize;
	}

	public Homeworld getHomeworld()
	{
		return speciesHomeworld;
	}

	public void setHomeworld(Homeworld h)
	{
		updateConfig(this.speciesHomeworld, h, (v) ->
		{
			this.speciesHomeworld = v;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putStr(json, "desc", desc);
		json = putStr(json, "type", type);
		json = putList(json, "size", size.chars().mapToObj(c -> String.valueOf((char) c)).collect(Collectors.toList()));
		if (speciesHomeworld != null)
		{
			json = putStr(json, "homeworld", speciesHomeworld.getName());
		}
		json = putObjList(json, "traits", traits.stream().map(t -> t.saveConfig()).toList());

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		desc = data.optString("desc", "");
		type = data.optString("type", "Humanoid");
		size = String.join("", getList(data, "size"));
		if (size.isBlank())
		{
			size = "M";
		}
		String homeworldName = data.optString("homeworld");
		if (!homeworldName.isBlank())
		{
			speciesHomeworld = Homeworld.getByName(homeworldName);
		}
		getObjList(data, "traits").forEach(t ->
		{
			Feature f = new Feature();
			f.loadConfig(t);
			f.setParent(this);
			traits.add(f);
		});
	}

	// State
	protected String selectedSize = "";

	public String getSelectedSize()
	{
		return selectedSize;
	}

	public void setSelectedSize(String s)
	{
		updateWithAlert(selectedSize, s, (v) ->
		{
			this.selectedSize = s;
		}, PropertyListener.SIZE);
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		json = putStr(json, "selectedSize", selectedSize);

		json = putObjList(json, "features", traits.stream().map(f ->
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

		setSelectedSize(data.optString("selectedSize", ""));

		getObjList(data, "features").forEach(f ->
		{
			String fname = f.getString("fname");
			int flvl = f.getInt("flvl");
			for (Feature f2 : traits)
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