package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Spell implements Cloneable
{
	private static int nId = 1;

	private final int id;
	private String name = "", school = "", castTime = "", trigger = "", components = "", range = "", duration = "",
			text = "", notes = "", materials = "";
	private int level = 0;
	private boolean ritual = false, custom = false;

	public Spell()
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

	public String getSchool()
	{
		return school;
	}

	public void setSchool(String school)
	{
		this.school = school;
	}

	public String getCastTime()
	{
		return castTime;
	}

	public void setCastTime(String castTime)
	{
		this.castTime = castTime;
	}

	public String getTrigger()
	{
		return trigger;
	}

	public void setTrigger(String trigger)
	{
		this.trigger = trigger;
	}

	public String getComponents()
	{
		return components;
	}

	public void setComponents(String components)
	{
		this.components = components;
	}

	public String getRange()
	{
		return range;
	}

	public void setRange(String range)
	{
		this.range = range;
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		this.duration = duration;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}

	public String getNotes()
	{
		return notes;
	}

	public String getMaterials()
	{
		return materials;
	}

	public void setMaterials(String materials)
	{
		this.materials = materials;
	}

	public boolean isRitual()
	{
		return ritual;
	}

	public void setRitual(boolean ritual)
	{
		this.ritual = ritual;
	}

	public boolean isConcentration()
	{
		return duration.contains("(C)");
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
	private static final List<Spell> allSpells = new ArrayList<>();

	public static List<Spell> getAllSpells()
	{
		return allSpells;
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
		System.out.println("No spell with id " + id + " was loaded.");
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
		System.out.println("No spell with name " + name + " was loaded.");
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
		Spell copy = (Spell) baseSpell.clone();
		copy.setName(altName);
		allSpells.add(copy);
		return copy;
	}

	public static Spell getCopyByName(String name, String notes)
	{
		Spell spell = Spell.getByName(name);
		Spell copy = (Spell) spell.clone();
		copy.notes = notes;
		return copy;
	}

	public static void addNewSpell(String newName)
	{
		if (!newName.isBlank())
		{
			Spell sp = new Spell();
			sp.setName(newName);
			allSpells.add(sp);
		}
	}

	public static void sortAll()
	{
		allSpells.sort((a, b) -> a.getName().compareTo(b.getName()));
	}

	public static void loadSpell(JSONObject data)
	{
		String name = data.getString("name");
		if (allSpells.stream().anyMatch(s -> s.getName().equals(name)))
		{
			// Duplicate spell, skip
			return;
		}

		Spell newSpell = new Spell();

		newSpell.setName(name);
		newSpell.setLevel(data.getInt("level"));
		newSpell.setSchool(data.getString("school"));
		newSpell.setCastTime(data.getString("time"));
		newSpell.setComponents(data.getString("components"));
		newSpell.setRange(data.getString("range"));
		newSpell.setDuration(data.getString("duration"));
		newSpell.setText(data.getString("text"));
		newSpell.setTrigger(data.optString("trigger", ""));
		newSpell.setMaterials(data.optString("materials", ""));
		newSpell.setRitual(data.optBoolean("ritual", false));
		newSpell.setCustom(data.optBoolean("custom", false));

		if (!newSpell.name.isBlank())
		{
			allSpells.add(newSpell);
		}
	}

	public JSONObject saveSpell()
	{
		JSONObject data = new JSONObject();

		data.put("name", name);
		data.put("level", level);
		data.put("school", school);
		data.put("time", castTime);
		data.put("components", components);
		data.put("range", range);
		data.put("duration", duration);
		data.put("text", text);

		if (!trigger.isBlank())
		{
			data.put("trigger", trigger);
		}
		if (!materials.isBlank())
		{
			data.put("materials", materials);
		}
		if (ritual)
		{
			data.put("ritual", true);
		}
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	@Override
	public String toString()
	{
		return name + (ritual ? " (R)" : "");
	}

	@Override
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
}
