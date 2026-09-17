package nocb.ui.DataEditor;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import nocb.data.Spell;

public class DataSpSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7915891727989905182L;

	SpellEditPanel sep = new SpellEditPanel();

	public DataSpSelector()
	{
		super("Spell");

		updateSpellList();

		display.add(sep, BorderLayout.CENTER);
	}

	private void updateSpellList()
	{
		((DefaultListModel<SelectorItem>) selector.getModel()).removeAllElements();

		List<SelectorItem> spellNames = new ArrayList<>(Spell.getAllSpells().stream()
				.map(s -> new SelectorItem(s.getId(), s.getName() + (s.isCustom() ? "*" : ""))).toList());
		spellNames.sort(SelectorItem::compareTo);

		((DefaultListModel<SelectorItem>) selector.getModel()).addAll(spellNames);
	}

	@Override
	protected void updateSelection()
	{
		sep.setSelectedId(selector.getSelectedValue().getId());
	}

	@Override
	protected void createNew(String name)
	{
		Spell.addNewSpell(name);
		updateSpellList();
	}
}
