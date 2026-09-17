package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Selectable;

public class DataSelSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7716882898063441557L;

	SelectableEditPanel stp = new SelectableEditPanel();

	public DataSelSelector()
	{
		super("Selectable");

		updateSelectables();

		display.add(stp, BorderLayout.CENTER);
	}

	private void updateSelectables()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> selectableNames = new ArrayList<>(Selectable.getAllSelectables().stream()
				.map(s -> new SelectorItem(s.getId(), s.getType() + "-" + s.getName() + (s.isCustom() ? "*" : "")))
				.toList());
		selectableNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(selectableNames);
	}

	@Override
	protected void updateSelection()
	{
		stp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Selectable.addNewSelectable(name);
		updateSelectables();
	}
}
