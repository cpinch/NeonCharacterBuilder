package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import ncb.data.loadables.CharacterClass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.LabeledSpinnersPanel;

public class CantripsEditPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -1490061632608447226L;

	private final LabeledSpinnersPanel spinners1, spinners2;

	private CharacterClass cls;

	public CantripsEditPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel labelPanel1 = new NoHorizontalScrollPanel();
		labelPanel1.setLayout(new GridLayout(1, 10));
		labelPanel1.add(new JLabel());
		for (int i = 1; i <= 10; i++)
		{
			UILib.addLabel(labelPanel1, "Level " + i, Color.black);
		}
		add(labelPanel1);

		SpinnerNumberModel[] models1 = new SpinnerNumberModel[10];
		for (int lvl = 1; lvl <= 10; lvl++)
		{
			models1[lvl - 1] = new SpinnerNumberModel(0, 0, 25, 1);
		}
		spinners1 = new LabeledSpinnersPanel("", models1,
				(Integer index, Integer val) -> updateCantrips(index + 1, val));
		add(spinners1);

		JPanel labelPanel2 = new NoHorizontalScrollPanel();
		labelPanel2.setLayout(new GridLayout(1, 10));
		labelPanel2.add(new JLabel());
		for (int i = 11; i <= 20; i++)
		{
			UILib.addLabel(labelPanel2, "Level " + i, Color.black);
		}
		add(labelPanel2);

		SpinnerNumberModel[] models2 = new SpinnerNumberModel[10];
		for (int lvl = 11; lvl <= 20; lvl++)
		{
			models2[lvl - 11] = new SpinnerNumberModel(0, 0, 25, 1);
		}
		spinners2 = new LabeledSpinnersPanel("", models2,
				(Integer index, Integer val) -> updateCantrips(index + 11, val));
		add(spinners2);
	}

	public void updateClass(CharacterClass cls)
	{
		this.cls = cls;

		updateValues();
	}

	public void updateValues()
	{
		Integer[] cantripsPerLevel1 = new Integer[10];
		for (int lvl = 1; lvl <= 10; lvl++)
		{
			Integer val = cls.getClassSpells().getCantripsForLevel(lvl);
			cantripsPerLevel1[lvl - 1] = val == null ? 0 : val;
		}
		spinners1.updateValues(cantripsPerLevel1);

		Integer[] cantripsPerLevel2 = new Integer[10];
		for (int lvl = 11; lvl <= 20; lvl++)
		{
			Integer val = cls.getClassSpells().getCantripsForLevel(lvl);
			cantripsPerLevel2[lvl - 11] = val == null ? 0 : val;
		}
		spinners2.updateValues(cantripsPerLevel2);
	}

	private void updateCantrips(int classLvl, int count)
	{
		cls.getClassSpells().setCantripsForLevel(classLvl, count);

		checkNext(classLvl, count);
	}

	private void checkNext(int classLvl, int updatedCount)
	{
		if (classLvl == 20)
		{
			return;
		}

		Integer nextCount = cls.getClassSpells().getCantripsForLevel(classLvl + 1);
		if (nextCount < updatedCount)
		{
			updateCantrips(classLvl + 1, updatedCount);
			updateValues();
		}
	}
}
