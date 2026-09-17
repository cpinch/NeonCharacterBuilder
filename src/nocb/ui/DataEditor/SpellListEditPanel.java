package nocb.ui.DataEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import nocb.data.SpellList;
import nocb.ui.UILib;

public class SpellListEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private SpellList list;

	private final List<JTextArea> spellsByLevel = new ArrayList<>();

	public SpellListEditPanel()
	{
		super();

		c.weighty = 1;
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			final int spLvl = lvl;
			JTextArea sbl = new JTextArea(2, 20);
			sbl.setLineWrap(true);
			sbl.setWrapStyleWord(true);
			sbl.setBorder(BorderFactory.createEtchedBorder());
			sbl.addFocusListener(UILib.createFocusListener(() -> updateSpells(spLvl)));
			UILib.addLabeledComponent(this, (lvl == 0 ? "Cantrips: " : "Level " + lvl + ": "), sbl, c);
			spellsByLevel.add(sbl);
			c.gridy++;
		}

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		list = SpellList.getById(id);

		nameField.setText(list.getName());
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			List<String> spells = list.getSpellNamesForLevel(lvl);
			if (!spells.isEmpty())
			{
				spellsByLevel.get(lvl).setText(String.join(", ", spells));
			}
		}

		setVisible(true);
	}

	private void updateSpells(int lvl)
	{
		List<String> spells = Arrays.asList(spellsByLevel.get(lvl).getText().split(",")).stream().map(s -> s.trim())
				.toList();
		list.setSpellNamesForLevel(lvl, spells);
		list.setCustom(true);
	}

	@Override
	protected void updateName()
	{
		list.setName(nameField.getText());
		list.setCustom(true);
	}
}
