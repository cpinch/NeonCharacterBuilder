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
	public static final List<String> prereqTypes = List.of("homeworld trait", "class level", "prior selectable",
			"class feature");
	private static int nId = 1;

	private final int id;
	private String type;
	private final List<String> required = new ArrayList<>();

	public SelectablePrereq(JSONObject data)
	{
		this.id = nId++;
		this.type = data.getString("type");
		JsonDataLoader.jsonArrayToStringArray(data.getJSONArray("required")).forEach(req -> this.required.add(req));
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
		this.type = type;
	}

	public List<String> getRequired()
	{
		return required;
	}

	public void setRequred(List<String> req)
	{
		this.required.clear();
		this.required.addAll(req);
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

		return data;
	}

	public boolean met(CharacterSheet sheet)
	{
		switch (type)
		{
			case "homeworld trait":
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
			break;
			case "class level":
				final Map<String, Integer> requiredClassLevels = new HashMap<>();
				required.forEach(
						r -> requiredClassLevels.put(r.split("-")[0].trim(), Integer.parseInt(r.split("-")[1].trim())));

				// TODO post1.0 - When level ups are supported will need to rework this
				for (Map.Entry<String, Integer> rcl : requiredClassLevels.entrySet())
				{
					if (rcl.getValue() > 1)
					{
						return false;
					}
				}
				return true;
			case "prior selectable":
				final Map<String, String> requiredSelections = new HashMap<>();
				required.forEach(r -> requiredSelections.put(r.split("-")[0].trim(), r.split("-")[1].trim()));

				// TODO post1.0 - When level ups are supported will need to rework this to look
				// through all selections for one of type key with name value and, if found,
				// return true
				// For now it's impossible to meet
				return false;
			case "class feature":
				return sheet.getCharClass().getClassFeatures().stream()
						.anyMatch(f -> f.getName().equals(required.get(0)));
			default:
				System.out.println("Unknown Prereq Type " + type);
		}
		return false;
	}

	@Override
	public String toString()
	{
		switch (type)
		{
			case "homeworld trait":
				return "Requires Homeworld has trait in (" + String.join(", ", required) + ")";
			case "class level":
				// TODO post1.0 - when level ups
				return "todo";
			case "prior selectable":
				// TODO post1.0 - when level ups
				return "todo";
			case "class feature":
				return "Requires Class Feature " + required.get(0);
			default:
				return "Unknown Prereq Type " + type;
		}
	}
}
