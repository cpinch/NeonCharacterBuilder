package nocb.data;

import org.json.JSONObject;

public class SelectableFeature extends Feature
{
	public SelectableFeature(JSONObject data)
	{
		super();

		this.loadFromData(data);
	}

	public SelectableFeature()
	{
		super();
	}

	public JSONObject saveSelectableFeature()
	{
		return super.saveFeature();
	}
}
