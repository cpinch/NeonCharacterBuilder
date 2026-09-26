package ncb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import ncb.data.loadables.Subclass;

public class DataSubClsSelector extends SelectorPanel
{
	private static final long serialVersionUID = 5331577565314781141L;

	SubclassEditPanel scpp = new SubclassEditPanel();

	public DataSubClsSelector()
	{
		super("Subclass");

		updateSubclasses();

		display.add(scpp, BorderLayout.CENTER);
	}

	private void updateSubclasses()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> subclassNames = new ArrayList<>(Subclass.getAllSubclasses().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		subclassNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(subclassNames);
	}

	@Override
	protected void updateSelection()
	{
		if (selector.getSelectedValue() != null)
		{
			scpp.setSelectedId(selector.getSelectedValue().getId());
		}
	}

	@Override
	protected void createNew(String name)
	{
		Subclass.addNewSubclass(name);
		updateSubclasses();
	}

	@Override
	protected void clearSelectionCustom()
	{
		scpp.clearSelectedCustom();
	}
}
