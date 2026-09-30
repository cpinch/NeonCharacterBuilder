package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.DefaultListModel;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Selectable;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectableItem;
import ncb.ui.DataEditor.SelectorItem;
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
	protected Customizable createNew(String name)
	{
		return Selectable.addNewSelectable(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return stp;
	}

	@Override
	protected List<Selectable> getItemsList()
	{
		return Selectable.getAllSelectables();
	}

	// Selectables need to be identify by type and name, so need a unique setup
	@Override
	public void updateItems()
	{
		selector.removeListSelectionListener(this);

		SelectorItem selected = selector.getSelectedValue();
		DefaultListModel<SelectorItem> model = ((DefaultListModel<SelectorItem>) selector.getModel());
		model.removeAllElements();
		List<SelectableItem> selectableItems = new ArrayList<>(
				getItemsList().stream().map(e -> new SelectableItem(e)).toList());
		selectableItems.sort(Comparator.comparing(SelectorItem::toString, String.CASE_INSENSITIVE_ORDER));
		model.addAll(selectableItems);
		selector.setSelectedValue(selected, true);

		selector.addListSelectionListener(this);
		revalidate();
	}
}
