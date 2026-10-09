package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import ncb.data.SpellSlots;
import ncb.data.loadables.CharacterClass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.LabeledSpinnersPanel;

public class SingleSpellSlotsEditPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -1490061632608447226L;

	private final List<LabeledSpinnersPanel> slotPanels = new ArrayList<>();

	private CharacterClass cls;

	public SingleSpellSlotsEditPanel()
	{

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel singleLabelPanel = new NoHorizontalScrollPanel();
		singleLabelPanel.setLayout(new GridLayout(1, 3));
		singleLabelPanel.add(new JLabel());
		UILib.addLabel(singleLabelPanel, "Spell Slots", Color.black);
		UILib.addLabel(singleLabelPanel, "Slots Level", Color.black);
		add(singleLabelPanel);
		for (int lvl = 1; lvl <= 20; lvl++)
		{
			final int level = lvl;
			SpinnerNumberModel[] models = new SpinnerNumberModel[]
			{ new SpinnerNumberModel(0, 0, 9, 1), new SpinnerNumberModel(1, 1, 9, 1) };
			LabeledSpinnersPanel single = new LabeledSpinnersPanel("Level " + (lvl < 10 ? " " : "") + lvl + ": ",
					models, (Integer index, Integer val) -> updateSlot(level, index, val));
			slotPanels.add(single);
			add(single);
		}
	}

	public void updateClass(CharacterClass cls)
	{
		this.cls = cls;

		updateValues();
	}

	public void updateValues()
	{
		for (int lvl = 1; lvl <= 20; lvl++)
		{
			updateValue(lvl);
		}
	}

	private void updateValue(int classLvl)
	{
		SpellSlots slots = cls.getSpellSlotsForLevel(classLvl);
		int spLvl = slots.getFirstNonZeroSlotLevel();
		int count = slots.getCount(spLvl);
		slotPanels.get(classLvl - 1).updateValues(new Integer[]
		{ count, spLvl });
	}

	private void updateSlot(int classLvl, int index, int newVal)
	{
		SpellSlots slots = cls.getSpellSlotsForLevel(classLvl);
		int curSpLvl = slots.getFirstNonZeroSlotLevel();
		int curSpCount = slots.getCount(curSpLvl);

		if (index == 1)
		{
			// Changing the spell level
			slots.setSlot(curSpLvl, 0);
			slots.setSlot(newVal, curSpCount);
			checkNext(classLvl, newVal, curSpCount);
		}
		else
		{
			// Changing the count
			slots.setSlot(curSpLvl, newVal);
			checkNext(classLvl, curSpLvl, newVal);
		}

	}

	private void checkNext(int classLvl, int baseSpLvl, int baseCount)
	{
		if (classLvl == 20)
		{
			return;
		}

		SpellSlots nextSlots = cls.getSpellSlotsForLevel(classLvl + 1);
		int nextSpLvl = nextSlots.getFirstNonZeroSlotLevel();
		int nextCount = nextSlots.getCount(nextSpLvl);

		if (nextSpLvl < baseSpLvl)
		{
			updateSlot(classLvl + 1, 1, baseSpLvl);
			updateValue(classLvl + 1);
		}
		if (nextCount < baseCount)
		{
			updateSlot(classLvl + 1, 0, baseCount);
			updateValue(classLvl + 1);
		}
	}
}
