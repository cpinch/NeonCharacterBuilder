package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class Background extends Feature
{
	private final List<Ability> abilityOptions = new ArrayList<>();
	private Homeworld homeworld;
	private int notes = 0;
	private String vehicleProfs = "";

	public Background()
	{
		super();
	}

	// Basic Getters
	public int getNotes()
	{
		return notes;
	}

	public List<Ability> getAbilityOptions()
	{
		return abilityOptions;
	}

	public Homeworld getHomeworld()
	{
		return homeworld;
	}

	public String getVehicleProfs()
	{
		return vehicleProfs;
	}

	// Setters

	public void setNotes(int notes)
	{
		this.notes = notes;
	}

	public void setAbilityOptions(List<Ability> opts)
	{
		this.abilityOptions.clear();
		this.abilityOptions.addAll(opts);
	}

	public void setHomeworld(Homeworld h)
	{
		this.homeworld = h;
	}

	// Calculated

	public boolean makesSpellcaster()
	{
		if (getFeat() != null && !getFeat().getAllSpellsGranted().isEmpty())
		{
			return true;
		}
		return false;
	}

	// Loading
	private static final List<Background> allBackgrounds = new ArrayList<>();

	public static List<Background> getAllBackgrounds()
	{
		return allBackgrounds;
	}

	public static void sortAll()
	{
		allBackgrounds.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadBackground(JSONObject data)
	{
		String name = data.getString("name");
		if (allBackgrounds.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate background, skip
			return;
		}

		Background newBackground = new Background();

		newBackground.loadFromData(data);

		// TODO post1.0 - json structure and loading of backgrounds-specific stuff

		if (!newBackground.getName().isBlank())
		{
			allBackgrounds.add(newBackground);
		}
	}

	public JSONObject saveBackground()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		// TODO post1.0 - json structure and loading of backgrounds-specific stuff
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	public static Background getCustom()
	{
		// TODO post1.0 - move this to a loadable custom background file
		Background bg = new Background();
		bg.name = "Custom";
		bg.notes = 2500;
		bg.skillOptions.add(Skill.Any);
		bg.skillsSelected.add(Skill.Acrobatics);
		bg.skillsSelected.add(Skill.Animal_Handling);
		bg.skillsSelected.add(Skill.Computers);
		bg.skillSelectCount = 3;
		bg.toolProfs.add("1 Tool Proficiency");
		bg.vehicleProfs = "2 Vehicle Proficiencies";
		bg.languageOptionNames.add("Any");
		bg.langSelectCount = 3;
		List<Language> allLangs = Language.getAllLanguages();
		bg.languagesSelected.add(allLangs.get(0));
		bg.languagesSelected.add(allLangs.size() > 1 ? allLangs.get(1) : allLangs.get(0));
		bg.languagesSelected.add(allLangs.size() > 2 ? allLangs.get(2) : allLangs.get(0));
		bg.abilityOptions.addAll(List.of(Ability.Str, Ability.Dex, Ability.Con));
		bg.homeworld = Homeworld.getByName("Adonia");
		bg.featTraitName = "Origin";
		return bg;
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		json.put("abilityOptions", new JSONArray(abilityOptions.stream().map(a -> a.toString()).toList()));
		json.put("homeworld", homeworld.getName());
		// Notes isn't editable, don't need to save or load it
		json.put("vehicleProfs", vehicleProfs);

		return json;
	}

	@Override
	public boolean loadState(JSONObject data)
	{
		boolean success = super.loadState(data);

		try
		{
			JSONArray ao = data.getJSONArray("abilityOptions");
			abilityOptions.clear();
			for (int i = 0; i < ao.length(); i++)
			{
				abilityOptions.add(Ability.valueOf(ao.getString(i)));
			}
			this.homeworld = Homeworld.getByName(data.getString("homeworld"));
			// Notes isn't editable, don't need to save or load it
			this.vehicleProfs = data.getString("vehicleProfs");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			success = false;
		}

		return success;
	}
}
