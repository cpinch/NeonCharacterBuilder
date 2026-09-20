package nocb.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import nocb.io.JsonDataLoader;
import nocb.main.CharacterSheet;

public class SelectablePrereq
{
	private static final String clsLvl = "Class Level", pSel = "Prior Selection", hTrait = "Homeworld Trait";
	public static final List<String> prereqTypes = List.of(clsLvl, pSel, hTrait);
	private static int nId = 1;

	private final int id;
	private String type = "";
	private final List<String> required = new ArrayList<>();
	private boolean custom = false;

	public SelectablePrereq(JSONObject data)
	{
		this.id = nId++;
		this.type = data.getString("type");
		JsonDataLoader.jsonArrayToStringArray(data.getJSONArray("required")).forEach(req -> this.required.add(req));
		this.custom = data.optBoolean("custom", false);
	}

	public JSONObject saveSelectablePrereq()
	{
		JSONObject data = new JSONObject();

		data.put("type", type);
		JSONArray reqArr = new JSONArray();
		for (String req : required)
		{
			reqArr.put(req);
		}
		data.put("required", reqArr);
		if (custom)
		{
			data.put("custom", true);
		}

		return data;
	}

	public SelectablePrereq()
	{
		this.id = nId++;
	}

	public int getId()
	{
		return id;
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

	public List<String> getRequired()
	{
		return required;
	}

	public void setRequred(List<String> req)
	{
		if (!this.required.equals(req))
		{
			this.required.clear();
			this.required.addAll(req);
			setCustom(true);
		}
	}

	public boolean isCustom()
	{
		return custom;
	}

	private void setCustom(boolean custom)
	{
		this.custom = custom;
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
				for (ClassFeature cf : sheet.getCharClass().getClassFeatures())
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
