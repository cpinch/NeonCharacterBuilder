package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.SpellList;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.SpellListEditPanel;

public class DataSlSelector extends SelectorPanel
{
	private static final long serialVersionUID = -1528315131040659293L;

	SpellListEditPanel slp = new SpellListEditPanel();

	public DataSlSelector()
	{
		super("Spell List");

		display.add(slp, BorderLayout.CENTER);
	}

	@Override
	protected void createNew(String name)
	{
		SpellList.addNewSpellList(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return slp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return SpellList.getAllSpellLists();
	}
}
