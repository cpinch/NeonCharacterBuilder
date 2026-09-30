package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Subclass;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.SubclassEditPanel;

public class DataSubClsSelector extends SelectorPanel
{
	private static final long serialVersionUID = 5331577565314781141L;

	SubclassEditPanel scpp = new SubclassEditPanel();

	public DataSubClsSelector()
	{
		super("Subclass");

		display.add(scpp, BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return Subclass.addNewSubclass(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return scpp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return Subclass.getAllSubclasses();
	}
}
