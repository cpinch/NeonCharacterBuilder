package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.CharacterClass;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.ClassEditPanel;

public class DataClsSelector extends SelectorPanel
{
	private static final long serialVersionUID = 5331577565314781141L;

	ClassEditPanel cpp = new ClassEditPanel();

	public DataClsSelector()
	{
		super("Class");

		updateItems();

		display.add(cpp, BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return CharacterClass.addNewClass(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return cpp;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		return CharacterClass.getAllClasses();
	}
}
