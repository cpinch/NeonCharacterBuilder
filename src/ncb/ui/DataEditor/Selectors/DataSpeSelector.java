package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Species;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.SpeciesEditPanel;

public class DataSpeSelector extends SelectorPanel
{
	private static final long serialVersionUID = -2703988813733746384L;

	SpeciesEditPanel spp = new SpeciesEditPanel();

	public DataSpeSelector()
	{
		super("Species");

		display.add(spp, BorderLayout.CENTER);
	}

	@Override
	protected void createNew(String name)
	{
		Species.addNewSpecies(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return spp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Species.getAllSpecies();
	}
}
