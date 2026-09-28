package ncb.data;

import org.json.JSONObject;

import ncb.data.interfaces.Customizable;
import ncb.data.interfaces.HasConfig;

public class ClassEquipment implements HasConfig
{
	private Customizable parent;

	@Override
	public Customizable getParent()
	{
		return parent;
	}

	@Override
	public void setParent(Customizable p)
	{
		this.parent = p;
	}

	// Configuration
	private String items = "";
	private int notes = 0;

	public String getItems()
	{
		return items;
	}

	public void setItems(String items)
	{
		this.items = items;
	}

	public int getNotes()
	{
		return notes;
	}

	public void setNotes(int notes)
	{
		this.notes = notes;
	}

	@Override
	public JSONObject saveConfig()
	{
		JSONObject json = new JSONObject();

		json = putStr(json, "items", items);
		json = putInt(json, "notes", notes);

		return json;
	}

	@Override
	public void loadConfig(JSONObject data)
	{
		items = data.optString("items", "");
		notes = data.optInt("notes", 0);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return getItems().equals(((ClassEquipment) o).getItems()) && getNotes() == ((ClassEquipment) o).getNotes();
	}
}
