package ncb.ui.DataEditor.Editors;

import java.awt.Color;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.SpellList;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class SpellListEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private SpellList list;

	@Override
	protected String getItemName()
	{
		return list.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		list.setName(s);
	}

	public SpellListEditPanel()
	{
		super();

		c.weighty = 1;
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			final int spLvl = lvl;
			linkedProperties
					.add(UILib.addLabeledLinkedTextArea(this, 2, (lvl == 0 ? "Cantrips: " : "Level " + lvl + ": "), c,
							Color.white, Color.black, () -> getSpells(spLvl), (s) -> updateSpells(spLvl, s)));
			c.gridy++;
		}

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		list = (SpellList) sel;

		linkedProperties.forEach(l -> l.updateValue());

		setVisible(true);
	}

	public String getSpells(int lvl)
	{
		return String.join(", ", list.getSpellsForLevel(lvl).stream().map(s -> spellToText(s)).toList());
	}

	private void updateSpells(int lvl, String s)
	{
		list.setSpellsForLevel(lvl, parseSpells(s.split(",")));
	}
}
