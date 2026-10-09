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

public class FullSpellSlotsEditPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -1490061632608447226L;

	private final List<LabeledSpinnersPanel> slotPanels = new ArrayList<>();

	private CharacterClass cls;

	public FullSpellSlotsEditPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		JPanel fullLabelPanel = new NoHorizontalScrollPanel();
		fullLabelPanel.setLayout(new GridLayout(1, 10));
		fullLabelPanel.add(new JLabel());
		for (int i = 1; i <= 9; i++)
		{
			UILib.addLabel(fullLabelPanel, "Lvl " + i, Color.black);
		}
		add(fullLabelPanel);
		for (int lvl = 1; lvl <= 20; lvl++)
		{
			final int level = lvl;
			SpinnerNumberModel[] models = new SpinnerNumberModel[]
			{ new SpinnerNumberModel(0, 0, 4, 1), new SpinnerNumberModel(0, 0, 4, 1),
					new SpinnerNumberModel(0, 0, 4, 1), new SpinnerNumberModel(0, 0, 4, 1),
					new SpinnerNumberModel(0, 0, 4, 1), new SpinnerNumberModel(0, 0, 4, 1),
					new SpinnerNumberModel(0, 0, 4, 1), new SpinnerNumberModel(0, 0, 4, 1),
					new SpinnerNumberModel(0, 0, 4, 1) };
			LabeledSpinnersPanel full = new LabeledSpinnersPanel("Level " + (lvl < 10 ? " " : "") + lvl + ": ", models,
					(Integer index, Integer val) -> updateSlot(level, index + 1, val));
			slotPanels.add(full);
			add(full);
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
		slotPanels.get(classLvl - 1).updateValues(slots.getSlots());
	}

	private void updateSlot(int classLvl, int spLvl, int newVal)
	{
		cls.getSpellSlotsForLevel(classLvl).setSlot(spLvl, newVal);

		checkNext(classLvl, spLvl, newVal);
	}

	private void checkNext(int classLvl, int spLvl, int updatedCount)
	{
		if (classLvl == 20)
		{
			return;
		}

		SpellSlots nextSlots = cls.getSpellSlotsForLevel(classLvl + 1);
		if (nextSlots.getCount(spLvl) < updatedCount)
		{
			updateSlot(classLvl + 1, spLvl, updatedCount);
			updateValue(classLvl + 1);
		}
	}
}
