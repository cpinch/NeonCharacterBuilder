package ncb.data.loadables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONObject;

import ncb.data.Feature;
import ncb.data.enums.Ability;
import ncb.data.enums.Skill;
import ncb.data.interfaces.Customizable;
import ncb.main.PropertyListener;

public class Background extends Feature
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

	public static void loadFromFile(JSONObject data, boolean custom)
	{
		if (data == null)
		{
			return;
		}
		String name = data.getString("name");
		if (allBackgrounds.stream().anyMatch(bg -> bg.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Background newBg = new Background();
		newBg.loadConfig(data);
		newBg.setCustom(custom);
		allBackgrounds.add(newBg);
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

	public static Background getCustom()
	{
		// TODO Background loading
		Background bg = new Background();
		bg.setName("Custom");
		bg.notes = 2500;
		bg.setSkillSelectionOptions(Arrays.asList(Skill.realValues()));
		bg.setSkillsSelected(List.of(Skill.Acrobatics, Skill.Animal_Handling, Skill.Computers));
		bg.setSkillSelectionCount(3);
		bg.setToolProfs(List.of("1 Tool Proficiency"));
		bg.setVehicleProfs(List.of("2 Vehicle Proficiencies"));
		List<Language> allLangs = Language.getAllLanguages();
		bg.setLanguageSelectionOptions(List.of("Any"));
		bg.setLanguageSelectionCount(3);
		bg.setLanguagesSelected(List.of(allLangs.get(0), allLangs.size() > 1 ? allLangs.get(1) : allLangs.get(0),
				allLangs.size() > 2 ? allLangs.get(2) : allLangs.get(0)));
		bg.setAbilityOptions(List.of(Ability.Str, Ability.Dex, Ability.Con));
		bg.homeworld = Homeworld.getByName("Adonia");
		bg.setFeatTraitName("Origin");
		bg.setCustom(false);
		return bg;
	}

	// Configuration
	private int notes = 0;

	public int getNotes()
	{
		return notes;
	}

	public void setNotes(int notes)
	{
		this.updateConfig(this.notes, notes, (v) ->
		{
			this.notes = v;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = super.saveConfig();

		json = putInt(json, "notes", notes);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		super.loadConfig(data);

		notes = data.optInt("notes", 0);
	}

	// State
	private final List<Ability> abilityOptions = new ArrayList<>();
	private Homeworld homeworld;

	public List<Ability> getAbilityOptions()
	{
		return abilityOptions;
	}

	public void setAbilityOptions(List<Ability> ablOpts)
	{
		updateWithAlert(abilityOptions, ablOpts, (v) ->
		{
			this.abilityOptions.clear();
			this.abilityOptions.addAll(ablOpts);
		}, PropertyListener.BGABILITYOPTIONS);
	}

	public Homeworld getHomeworld()
	{
		return homeworld;
	}

	public void setHomeworld(Homeworld h)
	{
		updateWithAlert(homeworld, h, (v) ->
		{
			this.homeworld = h;
		}, PropertyListener.HOMEWORLD);
	}

	@Override
	public JSONObject saveState()
	{
		JSONObject json = super.saveState();

		json = putList(json, "abilityOptions", abilityOptions.stream().map(a -> a.toString()).toList());
		json = putStr(json, "homeworld", homeworld.getName());

		return json;
	}

	@Override
	public void loadState(JSONObject data)
	{
		super.loadState(data);

		setAbilityOptions(getList(data, "abilityOptions").stream().map(a -> Ability.valueOf(a)).toList());
		setHomeworld(Homeworld.getByName(data.getString("homeworld")));
	}
}
