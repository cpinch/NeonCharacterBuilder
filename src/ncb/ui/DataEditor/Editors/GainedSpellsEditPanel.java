package ncb.ui.DataEditor.Editors;

import java.awt.Color;

import javax.swing.BoxLayout;
import javax.swing.SpinnerNumberModel;

import ncb.data.loadables.CharacterClass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.PropertyLinkedSpinner;

public class GainedSpellsEditPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -1490061632608447226L;

	private final PropertyLinkedSpinner lvl1, otherLevels;

	private CharacterClass cls;

	public GainedSpellsEditPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		lvl1 = UILib.addLabeledLinkedSpinner(this, "Spells Gained at Level 1: ", new SpinnerNumberModel(0, 0, 10, 1),
				Color.white, Color.black, () -> getLevel1Spells(), (i) -> updateLevel1Spells(i));

		otherLevels = UILib.addLabeledLinkedSpinner(this, "Spells Gained each level after 1: ",
				new SpinnerNumberModel(0, 0, 5, 1), Color.white, Color.black, () -> getOtherLevelSpells(),
				(i) -> updateOtherLevelSpells(i));
	}

	public void updateClass(CharacterClass cls)
	{
		this.cls = cls;
	}

	public void updateValues()
	{
		lvl1.updateValue();
		otherLevels.updateValue();
	}

	private int getLevel1Spells()
	{
		return cls.getClassSpells().getSpellsForLevel(1);
	}

	private void updateLevel1Spells(int count)
	{
		cls.getClassSpells().setSpellsForLevel(1, count);
	}

	private int getOtherLevelSpells()
	{
		return cls.getClassSpells().getSpellsForLevel(2);
	}

	private void updateOtherLevelSpells(int count)
	{
		for (int lvl = 2; lvl <= 20; lvl++)
		{
			cls.getClassSpells().setSpellsForLevel(lvl, count);
		}
	}
}
