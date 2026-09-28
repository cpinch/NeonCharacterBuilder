package ncb.data.interfaces;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Selectable;
import ncb.data.loadables.Spell;
import ncb.data.loadables.Subclass;

public interface HasState extends AlertsChanges, SavesLoadsJson
{
	public abstract JSONObject saveState();

	public abstract void loadState(JSONObject data);

	default <T> void updateWithAlert(List<T> o, List<T> n, Consumer<List<T>> setter, String eventType)
	{
		if (!o.equals(n))
		{
			List<T> old = new ArrayList<>(o);
			setter.accept(n);
			getPCS().firePropertyChange(eventType, old, n);
		}
	}

	default void updateWithAlert(Selectable o, Selectable n, Consumer<Selectable> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(Spell o, Spell n, Consumer<Spell> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(Homeworld o, Homeworld n, Consumer<Homeworld> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(Subclass o, Subclass n, Consumer<Subclass> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(Ability o, Ability n, Consumer<Ability> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.toString().equals(n.toString())))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(int o, int n, Consumer<Integer> setter, String eventType)
	{
		if (o != n)
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}

	default void updateWithAlert(String o, String n, Consumer<String> setter, String eventType)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.equals(n)))
		{
			setter.accept(n);
			getPCS().firePropertyChange(eventType, o, n);
		}
	}
}
