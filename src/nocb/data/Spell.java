package nocb.data;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Spell implements Cloneable
{
	private static int nId = 1;

	private final int id;
	private String name = "", school = "", castTime = "", trigger = "", components = "", range = "", duration = "",
			text = "", notes = "", materials = "", baseName = "";
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
		if (!this.name.equals(name))
		{
			this.name = name;
			setCustom(true);
		}
	}

	public String getSchool()
	{
		return school;
	}

	public void setSchool(String school)
	{
		if (!this.school.equals(school))
		{
			this.school = school;
			setCustom(true);
		}
	}

	public String getCastTime()
	{
		return castTime;
	}

	public void setCastTime(String castTime)
	{
		if (!this.castTime.equals(castTime))
		{
			this.castTime = castTime;
			setCustom(true);
		}
	}

	public String getTrigger()
	{
		return trigger;
	}

	public void setTrigger(String trigger)
	{
		if (!this.trigger.equals(trigger))
		{
			this.trigger = trigger;
			setCustom(true);
		}
	}

	public String getComponents()
	{
		return components;
	}

	public void setComponents(String components)
	{
		if (!this.components.equals(components))
		{
			this.components = components;
			setCustom(true);
		}
	}

	public String getRange()
	{
		return range;
	}

	public void setRange(String range)
	{
		if (!this.range.equals(range))
		{
			this.range = range;
			setCustom(true);
		}
	}

	public String getDuration()
	{
		return duration;
	}

	public void setDuration(String duration)
	{
		if (!this.duration.equals(duration))
		{
			this.duration = duration;
			setCustom(true);
		}
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		if (!this.text.equals(text))
		{
			this.text = text;
			setCustom(true);
		}
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		if (this.level != level)
		{
			this.level = level;
			setCustom(true);
		}
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
		if (!this.materials.equals(materials))
		{
			this.materials = materials;
			setCustom(true);
		}
	}

	public boolean isRitual()
	{
		return ritual;
	}

	public void setRitual(boolean ritual)
	{
		if (this.ritual != ritual)
		{
			this.ritual = ritual;
			setCustom(true);
		}
	}

	public boolean isConcentration()
	{
		return duration.contains("(C)");
	}

	public boolean isCustom()
	{
		return custom;
	}

	private void setCustom(boolean custom)
	{
		this.custom = custom;
	}

	public void clearCustom()
	{
		setCustom(false);
	}

	public String getBaseName()
	{
		return baseName;
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
			Spell copy = (Spell) baseSpell.clone();
			copy.name = altName;
			copy.baseName = baseName;
			allSpells.add(copy);
			return copy;
		}
	}

	public static Spell getCopyByName(String name, String notes)
	{
		Spell spell = Spell.getByName(name);
		Spell copy = (Spell) spell.clone();
		copy.notes = notes;
		return copy;
	}

	public static Spell getCopyByAltName(String altName, String baseName, String notes)
	{
		Spell spell = Spell.getByAltName(altName, baseName);
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

		newSpell.name = name;
		newSpell.level = data.getInt("level");
		newSpell.school = data.getString("school");
		newSpell.castTime = data.getString("time");
		newSpell.components = data.getString("components");
		newSpell.range = data.getString("range");
		newSpell.duration = data.getString("duration");
		newSpell.text = data.getString("text");
		newSpell.trigger = data.optString("trigger", "");
		newSpell.materials = data.optString("materials", "");
		newSpell.ritual = data.optBoolean("ritual", false);
		newSpell.custom = data.optBoolean("custom", false);

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

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getId() == ((Spell) o).getId();
	}

	public JSONObject toJSONObject()
	{
		JSONObject sp = new JSONObject();
		sp.put("spell", getName());
		if (!getNotes().isBlank())
		{
			sp.put("note", getNotes());
		}
		if (!getBaseName().isBlank())
		{
			sp.put("baseName", getBaseName());
		}
		return sp;
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
}
