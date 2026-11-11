package ncb.ui.tabs;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ncb.data.loadables.CharacterClass;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.subpanels.LvlChangePanel;

public class LevelUpPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private CharacterSheet sheet;

	private final JButton lvlUp = new JButton("Level Up");
	private final JButton multiclass = new JButton("Multiclass");
	private final JPanel changesPanel = new NoHorizontalScrollPanel();

	public LevelUpPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setLayout(new BorderLayout());
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);
		PropertyListener.listenForChanges(PropertyListener.SUBCLASS, this);
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);

		add(UILib.getTextDisplay(
				"<html>The button at the bottom of this page will allow you to level up your character.<br>This tab will display a summary of class and species changes.<br>You should visit any tabs with changes after level up to review the changes and make any selections.</html>"),
				BorderLayout.PAGE_START);

		changesPanel.setLayout(new BoxLayout(changesPanel, BoxLayout.Y_AXIS));
		changesPanel.setOpaque(false);
		add(UILib.getScrollPaneFor(changesPanel), BorderLayout.CENTER);

		JPanel buttonPanel = new NoHorizontalScrollPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		lvlUp.addActionListener(this);
		buttonPanel.add(lvlUp);
		multiclass.addActionListener(this);
		buttonPanel.add(multiclass);
		add(buttonPanel, BorderLayout.PAGE_END);
	}

	@Override
	public void updateProperty(String prop)
	{
		List<Integer> lvlUpOrder = sheet.getLevelUpOrder();
		Map<Integer, Integer> clsIndexLevels = new HashMap<>();
		changesPanel.removeAll();
		clsIndexLevels.put(lvlUpOrder.get(0), 1);
		for (int i = 1; i < lvlUpOrder.size(); i++) // Skip level 1
		{
			int clsIndex = lvlUpOrder.get(i);
			if (!clsIndexLevels.containsKey(clsIndex))
			{
				clsIndexLevels.put(clsIndex, 0);
			}

			clsIndexLevels.put(clsIndex, clsIndexLevels.get(clsIndex) + 1);
			int clsLvl = clsIndexLevels.get(clsIndex);
			int charLvl = i + 1; // The 0th element of the level up order is level 1
			changesPanel.add(new LvlChangePanel(sheet, clsIndex, clsLvl, charLvl, i < lvlUpOrder.size() - 1));
		}
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(lvlUp))
		{
			if (sheet.getMulticlasses().isEmpty())
			{
				// Single class character, easy
				sheet.levelUp(CharacterSheet.baseClassIndex);
			}
			else
			{
				// Ask which class to level up
				List<CharacterClass> options = sheet.getAllCharClasses();
				CharacterClass cls = (CharacterClass) JOptionPane.showInputDialog(null, "Level up which class?",
						"Select Class", JOptionPane.QUESTION_MESSAGE, null, options.toArray(new CharacterClass[0]),
						options.get(0));

				if (cls != null)
				{
					int index = options.indexOf(cls) - 1; // The 0th element of the list is the base class
					sheet.levelUp(index < 0 ? CharacterSheet.baseClassIndex : index);
				}
			}
		}
		else if (e.getSource().equals(multiclass))
		{
			List<CharacterClass> options = new ArrayList<>();

			if (sheet.canMulticlassOutOfCurrentClasses())
			{
				for (CharacterClass cls : CharacterClass.getAllClasses())
				{
					if (sheet.getAllCharClasses().stream().anyMatch(c -> c.getName().equals(cls.getName())))
					{
						// We already have this class as base or another multiclass, skip
						continue;
					}
					if (cls.multiclassPrereqsMet(sheet.getTotalAbilityScores()))
					{
						options.add(cls);
					}
				}
			}

			if (options.isEmpty())
			{
				JOptionPane.showMessageDialog(null,
						"You do not qualify to multiclass to any classes. You must meet the multiclass requirements of your current class(es) and the multiclass requirement of the class you want to multiclass to.",
						"No Valid Multiclass", JOptionPane.WARNING_MESSAGE, null);
			}
			else
			{
				CharacterClass cls = (CharacterClass) JOptionPane.showInputDialog(null, "Multiclass into?",
						"Select Class", JOptionPane.QUESTION_MESSAGE, null, options.toArray(new CharacterClass[0]),
						options.get(0));

				if (cls != null)
				{
					sheet.addMulticlass(cls);
				}
			}
		}
	}
}
