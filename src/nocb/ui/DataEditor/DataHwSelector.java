package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Homeworld;

public class DataHwSelector extends SelectorPanel
{
	private static final long serialVersionUID = -7769521505448542004L;

	HomeworldEditPanel hwp = new HomeworldEditPanel();

	public DataHwSelector()
	{
		super("Homeworld");

		updateHomeworlds();

		display.add(hwp, BorderLayout.CENTER);
	}

	private void updateHomeworlds()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> homeworldNames = new ArrayList<>(Homeworld.getAllHomeworlds().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		homeworldNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(homeworldNames);
	}

	@Override
	protected void updateSelection()
	{
		hwp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Homeworld.addNewHomeworld(name);
		updateHomeworlds();
	}
}
