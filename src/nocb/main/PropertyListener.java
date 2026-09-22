package nocb.main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nocb.data.AlertsChanges;
import nocb.ui.ListensForChanges;

public class PropertyListener implements PropertyChangeListener
{
	public static final String HOMEWORLD = "Homeworld", SKILLS = "Skills", SELECTED = "Selected", CLASS = "Class",
			SPECIES = "Species", SUBCLASS = "Subclass", CLASSLEVEL = "ClassLevel";

	private static final Map<String, List<ListensForChanges>> listeners = new HashMap<>();

	private final CharacterSheet sheet;

	public PropertyListener(CharacterSheet sheet)
	{
		this.sheet = sheet;
		sheet.addPropertyChangeListener(this);
	}

	public static void setup(CharacterSheet sheet)
	{
		new PropertyListener(sheet);
	}

	public static void listenForChanges(String type, ListensForChanges comp)
	{
		if (!listeners.containsKey(type))
		{
			listeners.put(type, new ArrayList<>());
		}
		listeners.get(type).add(comp);
	}

	public static void stopListening(ListensForChanges comp)
	{
		listeners.values().forEach(ll -> ll.remove(comp));
	}

	@Override
	public void propertyChange(PropertyChangeEvent e)
	{
		switch (e.getPropertyName())
		{
			case CLASS:
				sheet.getCharClass().addPropertyChangeListener(this);
			break;
			case SPECIES:
				sheet.getSpecies().addPropertyChangeListener(this);
			break;
			case SUBCLASS:
				sheet.getCharClass().getSubclass().addPropertyChangeListener(this);
			break;
		}
		if (listeners.containsKey(e.getPropertyName()))
		{
			listeners.get(e.getPropertyName()).forEach(l -> l.updateProperty(e.getPropertyName()));
		}
		// Cleanup, don't need to listen to objects that were dropped
		if (e.getOldValue() != null && e.getOldValue() instanceof AlertsChanges)
		{
			((AlertsChanges) e.getOldValue()).removePropertyChangeListener(this);
		}
	}
}
