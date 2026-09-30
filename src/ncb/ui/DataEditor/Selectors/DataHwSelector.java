package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Homeworld;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.HomeworldEditPanel;

public class DataHwSelector extends SelectorPanel
{
	private static final long serialVersionUID = -7769521505448542004L;

	HomeworldEditPanel hwp = new HomeworldEditPanel();

	public DataHwSelector()
	{
		super("Homeworld");

		display.add(hwp, BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return Homeworld.addNewHomeworld(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return hwp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Homeworld.getAllHomeworlds();
	}
}
