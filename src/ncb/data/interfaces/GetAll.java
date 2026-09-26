package ncb.data.interfaces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import ncb.data.Feature;

public interface GetAll
{
	public List<? extends GetAll> getChildren();

	default boolean getAny(Function<Feature, Boolean> method)
	{
		if (this instanceof Feature && method.apply((Feature) this))
		{
			return true;
		}
		for (GetAll child : getChildren())
		{
			if (child.getAny(method))
			{
				return true;
			}
		}

		return false;
	}

	default int getTotal(Function<Feature, Integer> method)
	{
		int total = 0;

		if (this instanceof Feature)
		{
			total += method.apply((Feature) this);
		}
		for (GetAll child : getChildren())
		{
			total += child.getTotal(method);
		}

		return total;
	}

	default <T> List<T> getAllOf(Function<Feature, T> method)
	{
		List<T> all = new ArrayList<>();

		if (this instanceof Feature)
		{
			T thisR = method.apply((Feature) this);
			if (thisR != null && !thisR.toString().isBlank())
			{
				all.add(thisR);
			}
		}
		getChildren().forEach(f -> all.addAll(f.getAllOf(method)));

		return all;
	}

	default <T> List<T> getAll(Function<Feature, List<T>> method)
	{
		List<T> all = new ArrayList<>();

		if (this instanceof Feature)
		{
			all.addAll(method.apply((Feature) this));
		}
		getChildren().forEach(f -> all.addAll(f.getAll(method)));

		return all;
	}

	default <T, R> Map<T, R> getAllMap(Function<Feature, Map<T, R>> method)
	{
		Map<T, R> all = new HashMap<>();

		if (this instanceof Feature)
		{
			all.putAll(method.apply((Feature) this));
		}
		getChildren().forEach(f -> all.putAll(f.getAllMap(method)));

		return all;
	}

	default <T> List<List<T>> getEach(Function<Feature, List<T>> method)
	{
		List<List<T>> each = new ArrayList<>();

		if (this instanceof Feature)
		{
			each.add(method.apply((Feature) this));
		}
		getChildren().forEach(f -> each.addAll(f.getEach(method)));

		return each;
	}
}
