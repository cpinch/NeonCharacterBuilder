package ncb.data.loadables;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class Spell implements HasConfig
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
		if (allSpells.stream().anyMatch(sp -> sp.getName().equals(name)))
		{
			// Dupe, ignore
			return;
		}
		Spell newSp = new Spell();
		newSp.setCustom(custom);
		newSp.loadConfig(data);
		allSpells.add(newSp);
	}

	public Spell()
	{
		this.id = nId++;
	}

	@Override
	public int getId()
	{
		return id;
	}

	public boolean isConcentration()
	{
		return duration.contains("(C)");
	}

	// Loading
	private static final List<Spell> allSpells = new ArrayList<>();

	public static List<Spell> getAllSpells()
	{
		return new ArrayList<>(allSpells);
	}

	public static Spell getById(int id)
	{
		for (Spell s : allSpells)
		{
			if (s.getId() == id)
			{
				return s;
			}
		}
		System.err.println("No spell with id " + id + " was loaded.");
		return null;
	}

	public static Spell getByName(String name)
	{
		for (Spell s : allSpells)
		{
			if (s.getName().equals(name))
			{
				return s;
			}
		}
		System.err.println("No spell with name " + name + " was loaded.");
		return null;
	}

	public static Spell getByAltName(String altName, String baseName)
	{

		for (Spell s : allSpells)
		{
			if (s.getName().equals(altName))
			{
				return s;
			}
		}

		// Add a copy of the spell under the alternative name
		Spell baseSpell = getByName(baseName);
		if (baseSpell == null)
		{
			System.err.println(
					"Could not create spell " + altName + " because no spell with name " + baseName + " was found.");
			return null;
		}
		else
		{
			Spell copy = new Spell(baseSpell);
			copy.name = altName;
			copy.baseName = baseName;
			allSpells.add(copy);
			return copy;
		}
	}

	public static Spell getCopyByName(String name, String notes)
	{
		Spell original = Spell.getByName(name);
		Spell copy = new Spell(original);
		copy.notes = notes;
		return copy;
	}

	public static Spell getCopyByAltName(String altName, String baseName, String notes)
	{
		Spell original = Spell.getByAltName(altName, baseName);
		Spell copy = new Spell(original);
		copy.name = altName;
		copy.baseName = baseName;
		copy.notes = notes;
		return copy;
	}

	public static Spell addNewSpell(String newName)
	{
		if (!newName.isBlank())
		{
			Spell sp = new Spell();
			sp.setName(newName);
			allSpells.add(sp);
			return sp;
		}
		return null;
	}

	public static void sortAll()
	{
		allSpells.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	// Configuration
	private String name = "", school = "", time = "", trigger = "", components = "", range = "", duration = "",
			text = "", materials = "";
	private int level = 0;
	private boolean ritual = false;

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

	public String getSchool()
	{
		return school;
	}

	public void setSchool(String school)
	{
		updateConfig(this.school, school, (v) ->
		{
			this.school = v;
		});
	}

	public String getCastTime()
	{
		return time;
	}

	public void setCastTime(String castTime)
	{
		updateConfig(this.time, castTime, (v) ->
		{
			this.time = v;
		});
	}

	public String getTrigger()
	{
		return trigger;
	}

	public void setTrigger(String trigger)
	{
		updateConfig(this.trigger, trigger, (v) ->
		{
			this.trigger = v;
		});
	}

	public String getComponents()
	{
		return components;
	}

	public void setComponents(String components)
	{
		updateConfig(this.components, components, (v) ->
		{
			this.components = v;
		});
	}

	public String getRange()
	{
		return range;
	}

	public void setRange(String range)
	{
		updateConfig(this.range, range, (v) ->
		{
			this.range = v;
		});
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		updateConfig(this.duration, duration, (v) ->
		{
			this.duration = v;
		});
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		updateConfig(this.text, text, (v) ->
		{
			this.text = v;
		});
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		updateConfig(this.level, level, (v) ->
		{
			this.level = v;
		});
	}

	public String getMaterials()
	{
		return materials;
	}

	public void setMaterials(String materials)
	{
		updateConfig(this.materials, materials, (v) ->
		{
			this.materials = v;
		});
	}

	public boolean isRitual()
	{
		return ritual;
	}

	public void setRitual(boolean ritual)
	{
		updateConfig(this.ritual, ritual, (v) ->
		{
			this.ritual = v;
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "name", name);
		json = putInt(json, "level", level);
		json = putStr(json, "school", school);
		json = putStr(json, "time", time);
		json = putStr(json, "components", components);
		json = putStr(json, "range", range);
		json = putStr(json, "duration", duration);
		json = putStr(json, "trigger", trigger);
		json = putStr(json, "materials", materials);
		json = putBool(json, "ritual", ritual);
		json = putStr(json, "text", text);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		name = data.getString("name");
		level = data.optInt("level");
		school = data.optString("school", "");
		time = data.optString("time", "");
		components = data.optString("components", "");
		range = data.optString("range", "");
		duration = data.optString("duration", "");
		trigger = data.optString("trigger", "");
		materials = data.optString("materials", "");
		ritual = data.optBoolean("ritual", false);
		text = data.optString("text", "");
	}

	// These are kind of half-state half-config variables so are specially handled
	// through the saveToJSONObject and getFromJSONObject methods
	private String notes = "", baseName = "";

	public String getNotes()
	{
		return notes;
	}

	public String getBaseName()
	{
		return baseName;
	}

	public static JSONObject saveToJSONObject(Spell s)
	{
		JSONObject obj = new JSONObject();

		obj.put("spell", s.getName());
		if (!s.getBaseName().isBlank())
		{
			obj.put("baseName", s.getBaseName());
		}
		if (!s.getNotes().isBlank())
		{
			obj.put("note", s.getNotes());
		}

		return obj;
	}

	public static Spell getFromJSONObject(JSONObject sp)
	{
		if (sp.has("baseName") && sp.has("note"))
		{
			return Spell.getCopyByAltName(sp.getString("spell"), sp.getString("baseName"), sp.getString("note"));
		}
		else if (sp.has("baseName"))
		{
			return Spell.getByAltName(sp.getString("spell"), sp.getString("baseName"));
		}
		else if (sp.has("note"))
		{
			return Spell.getCopyByName(sp.getString("spell"), sp.getString("note"));
		}
		else
		{
			return Spell.getByName(sp.getString("spell"));
		}
	}

	@Override
	public String toString()
	{
		return name + (ritual ? " (R)" : "");
	}

	private Spell(Spell original)
	{
		this();

		this.name = original.name;
		this.school = original.school;
		this.time = original.time;
		this.trigger = original.trigger;
		this.components = original.components;
		this.range = original.range;
		this.duration = original.duration;
		this.text = original.text;
		this.materials = original.materials;
		this.level = original.level;
		this.ritual = original.ritual;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getId() == ((Spell) o).getId();
	}

}
