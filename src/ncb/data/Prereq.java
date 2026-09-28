package ncb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Homeworld;
import ncb.main.CharacterSheet;

public class Prereq implements HasConfig
{
	private static final String clsLvl = "Class Level", pSel = "Prior Selection", hTrait = "Homeworld Trait";
	public static final List<String> prereqTypes = List.of(clsLvl, pSel, hTrait);
	private static int nId = 1;
	private final int id;
	private Customizable parent;

	public Prereq()
	{
		this.id = nId++;
	}

	public int getId()
	{
		return id;
	}

	@Override
	public Customizable getParent()
	{
		return parent;
	}

	public void setParent(Customizable p)
	{
		this.parent = p;
	}

	public boolean met(CharacterSheet sheet)
	{
		switch (type)
		{
			case hTrait:
				Homeworld h = sheet.getBackground().getHomeworld();
				if (h != null)
				{
					for (String req : required)
					{
						if (h.hasTrait(req))
						{
							return true;
						}
					}
				}
				return false;
			case clsLvl:
				final Map<String, Integer> requiredClassLevels = new HashMap<>();
				required.forEach(
						r -> requiredClassLevels.put(r.split("-")[0].trim(), Integer.parseInt(r.split("-")[1].trim())));
				List<CharacterClass> classes = List.of(sheet.getCharClass()); // TODO multiclass - revise this

				for (Map.Entry<String, Integer> rcl : requiredClassLevels.entrySet())
				{
					if (classes.stream()
							.anyMatch(c -> c.getName().equals(rcl.getKey()) && c.getLevel() >= rcl.getValue()))
					{
						return true;
					}
				}
				return false;
			case pSel:
				for (Feature cf : sheet.getCharClass().getClassFeatures())
				{
					if (cf.getName().equals(required.get(0))
							|| (cf.getSelected() != null && cf.getSelected().getName().equals(required.get(0)))
							|| cf.getFeat() != null && cf.getFeat().getName().equals(required.get(0)))
					{
						return true;
					}
				}
				return false;
			default:
				System.err.println("Unknown Prereq Type " + type);
		}
		return false;
	}

	// Configuration
	private String type = "";
	private final List<String> required = new ArrayList<>();

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

	public List<String> getRequired()
	{
		return required;
	}

	public void setRequired(List<String> req)
	{
		updateConfig(this.required, req, (v) ->
		{
			this.required.clear();
			this.required.addAll(v);
		});
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "type", type);
		json = putList(json, "required", required);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		type = data.optString("type", "");
		required.clear();
		getList(data, "required").forEach(r -> required.add(r));
	}

	@Override
	public String toString()
	{
		switch (type)
		{
			case hTrait:
				return "Requires Homeworld has trait in (" + String.join(", ", required) + ")";
			case clsLvl:
				return "Requires Class Level " + String.join(", ", required);
			case pSel:
				return "Requires Prior Class Feature " + required.get(0);
			default:
				return "Unknown Prereq Type " + type;
		}
	}
}
