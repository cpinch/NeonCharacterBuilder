package ncb.main;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ncb.ui.ListensForChanges;

public class PropertyListener implements PropertyChangeListener
{
	public static final String HOMEWORLD = "Homeworld", SKILLPROFS = "Skill Profs", SKILLEXPS = " Skill Expertise",
			SELECTED = "Selected", CLASS = "Class", SPECIES = "Species", SUBCLASS = "Subclass",
			CLASSLEVEL = "Class Level", RESISTANCES = "Resistances", LANGUAGES = "Languages", SPELLS = "Spells",
			BGABILITYOPTIONS = "Background Ability Options", PRIMARYABILITY = "Primary Ability",
			EQUIPMENT = "Equipment", SIZE = "Size", ABILITYSCORES = "Ability Scores",
			SPELLCASTINGABILITY = "Spellcasting Ability", BACKGROUND = "Background", NAME = "Name";

	private static final Map<String, List<ListensForChanges>> listeners = new HashMap<>();
	private static PropertyListener listener = new PropertyListener();

	public static PropertyListener getListener()
	{
		return listener;
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
		if (listeners.containsKey(e.getPropertyName()))
		{
			List<ListensForChanges> lcs = new ArrayList<>(listeners.get(e.getPropertyName()));
			lcs.forEach(l -> l.updateProperty(e.getPropertyName()));
		}
	}
}
