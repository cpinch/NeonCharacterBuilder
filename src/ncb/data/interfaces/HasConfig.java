package ncb.data.interfaces;

import java.util.List;
import java.util.function.Consumer;

import org.json.JSONObject;

import ncb.data.enums.Ability;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Selectable;
import ncb.data.loadables.Spell;
import ncb.data.loadables.Subclass;

public interface HasConfig extends Customizable, SavesLoadsJson
{
	public abstract JSONObject saveConfig();

	public abstract void loadConfig(JSONObject data);

	default <T> void updateConfig(List<T> o, List<T> n, Consumer<List<T>> setter)
	{
		if (!o.equals(n))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	// TODO - These could be combined if a shared "hasGetName" interface existed or
	// they implemented comparable or something
	default void updateConfig(Selectable o, Selectable n, Consumer<Selectable> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(Spell o, Spell n, Consumer<Spell> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(Homeworld o, Homeworld n, Consumer<Homeworld> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(Subclass o, Subclass n, Consumer<Subclass> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.getName().equals(n.getName())))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(Ability o, Ability n, Consumer<Ability> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.toString().equals(n.toString())))
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(boolean o, boolean n, Consumer<Boolean> setter)
	{
		if (o != n)
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(int o, int n, Consumer<Integer> setter)
	{
		if (o != n)
		{
			setter.accept(n);
			setCustom(true);
		}
	}

	default void updateConfig(String o, String n, Consumer<String> setter)
	{
		if ((o == null && n != null) || (o != null && n == null) || (o != n && !o.equals(n)))
		{
			setter.accept(n);
			setCustom(true);
		}
	}
}
