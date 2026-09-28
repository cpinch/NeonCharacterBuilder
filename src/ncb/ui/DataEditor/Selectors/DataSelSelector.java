package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Selectable;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.SelectableEditPanel;

public class DataSelSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7716882898063441557L;

	SelectableEditPanel stp = new SelectableEditPanel();

	public DataSelSelector()
	{
		super("Selectable");

		display.add(stp, BorderLayout.CENTER);
	}

	@Override
	protected void createNew(String name)
	{
		Selectable.addNewSelectable(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return stp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Selectable.getAllSelectables();
	}
}
