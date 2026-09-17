package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.SpellList;

public class DataSlSelector extends SelectorPanel
{
	private static final long serialVersionUID = -1528315131040659293L;

	SpellListEditPanel slp = new SpellListEditPanel();

	public DataSlSelector()
	{
		super("Spell List");

		updateSpellLists();

		display.add(slp, BorderLayout.CENTER);
	}

	private void updateSpellLists()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> spellListNames = new ArrayList<>(SpellList.getAllSpellLists().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		spellListNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(spellListNames);
	}

	@Override
	protected void updateSelection()
	{
		slp.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		SpellList.addNewSpellList(name);
		updateSpellLists();
	}
}
