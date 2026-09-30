package ncb.ui.DataEditor.Selectors;

import java.awt.BorderLayout;
import java.util.List;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Spell;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.SelectorPanel;
import ncb.ui.DataEditor.Editors.SpellEditPanel;

public class DataSpSelector extends SelectorPanel
{
	private static final long serialVersionUID = 7915891727989905182L;

	SpellEditPanel sep = new SpellEditPanel();

	public DataSpSelector()
	{
		super("Spell");

		display.add(sep, BorderLayout.CENTER);
	}

	@Override
	protected Customizable createNew(String name)
	{
		return Spell.addNewSpell(name);
	}

	@Override
	protected EditPanel getEditPanel()
	{
		return sep;
	}

	@Override
	protected List<? extends Customizable> getItemsList()
	{
		// Filter out spells that are just copies of other spells
		return Spell.getAllSpells().stream().filter(sp -> sp.getBaseName().isBlank()).toList();
	}
}
