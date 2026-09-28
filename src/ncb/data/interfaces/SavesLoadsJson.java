package ncb.data.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public interface SavesLoadsJson
{
	default JSONObject putStr(JSONObject data, String label, String obj)
	{
		if (obj != null && !obj.isBlank())
		{
			data.put(label, obj);
		}
		return data;
	}

	default JSONObject putInt(JSONObject data, String label, int obj)
	{
		if (obj > 0)
		{
			data.put(label, obj);
		}
		return data;
	}

	default JSONObject putBool(JSONObject data, String label, boolean obj)
	{
		if (obj)
		{
			data.put(label, obj);
		}
		return data;
	}

	default JSONObject putObj(JSONObject data, String label, JSONObject obj)
	{
		if (obj != null && !obj.isEmpty())
		{
			data.put(label, obj);
		}
		return data;
	}

	default JSONObject putList(JSONObject data, String label, List<?> obj)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			obj.forEach(s -> arr.put(s.toString()));
			data.put(label, arr);
		}
		return data;
	}

	default List<String> getList(JSONObject data, String label)
	{
		List<String> val = new ArrayList<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				val.add(arr.getString(i));
			}
		}
		return val;
	}

	default JSONObject putIntList(JSONObject data, String label, List<Integer> obj)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			obj.forEach(s -> arr.put(s));
			data.put(label, arr);
		}
		return data;
	}

	default List<Integer> getIntList(JSONObject data, String label)
	{
		List<Integer> val = new ArrayList<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				val.add(arr.getInt(i));
			}
		}
		return val;
	}

	default JSONObject putObjList(JSONObject data, String label, List<JSONObject> obj)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			obj.forEach(s -> arr.put(s));
			data.put(label, arr);
		}
		return data;
	}

	default List<JSONObject> getObjList(JSONObject data, String label)
	{
		List<JSONObject> val = new ArrayList<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				val.add(arr.getJSONObject(i));
			}
		}
		return val;
	}

	default JSONObject putMap(JSONObject data, String label, Map<String, String> obj, String keyLabel, String valLabel)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			for (Map.Entry<String, String> entry : obj.entrySet())
			{
				JSONObject o = new JSONObject();
				o.put(keyLabel, entry.getKey());
				o.put(valLabel, entry.getValue());
				arr.put(o);
			}
			data.put(label, arr);
		}
		return data;
	}

	default Map<String, String> getMap(JSONObject data, String label, String keyLabel, String valLabel)
	{
		Map<String, String> val = new HashMap<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject o = arr.getJSONObject(i);
				val.put(o.getString(keyLabel), o.getString(valLabel));
			}
		}
		return val;
	}

	default JSONObject putIntMap(JSONObject data, String label, Map<Integer, Integer> obj, String keyLabel,
			String valLabel)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			for (Map.Entry<Integer, Integer> entry : obj.entrySet())
			{
				JSONObject o = new JSONObject();
				o.put(keyLabel, entry.getKey());
				o.put(valLabel, entry.getValue());
				arr.put(o);
			}
			data.put(label, arr);
		}
		return data;
	}

	default Map<Integer, Integer> getIntMap(JSONObject data, String label, String keyLabel, String valLabel)
	{
		Map<Integer, Integer> val = new HashMap<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject o = arr.getJSONObject(i);
				val.put(o.getInt(keyLabel), o.getInt(valLabel));
			}
		}
		return val;
	}

	default JSONObject putStrIntMap(JSONObject data, String label, Map<String, Integer> obj, String keyLabel,
			String valLabel)
	{
		if (obj != null && !obj.isEmpty())
		{
			JSONArray arr = new JSONArray();
			for (Map.Entry<String, Integer> entry : obj.entrySet())
			{
				JSONObject o = new JSONObject();
				o.put(keyLabel, entry.getKey());
				o.put(valLabel, entry.getValue());
				arr.put(o);
			}
			data.put(label, arr);
		}
		return data;
	}

	default Map<String, Integer> getStrIntMap(JSONObject data, String label, String keyLabel, String valLabel)
	{
		Map<String, Integer> val = new HashMap<>();
		JSONArray arr = data.optJSONArray(label);
		if (arr != null)
		{
			for (int i = 0; i < arr.length(); i++)
			{
				JSONObject o = arr.getJSONObject(i);
				val.put(o.getString(keyLabel), o.getInt(valLabel));
			}
		}
		return val;
	}
}
