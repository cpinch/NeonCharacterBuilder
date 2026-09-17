package nocb.data;

import org.json.JSONObject;

public class ClassEquipment
{
	private final String items;
	private final int notes;

	public ClassEquipment(JSONObject data)
	{
		this.items = data.optString("items", "");
		this.notes = data.getInt("notes");
	}

	public ClassEquipment(String items, int notes)
	{
		this.items = items;
		this.notes = notes;
	}

	public JSONObject saveEquipment()
	{
		JSONObject data = new JSONObject();

		if (!items.isBlank())
		{
			data.put("items", items);
		}
		data.put("notes", notes);

		return data;
	}

	public String getItems()
	{
		return items;
	}

	public int getNotes()
	{
		return notes;
	}
}
