package ncb.ui.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.LvlChangePanel;

public class LevelUpPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private CharacterSheet sheet;

	private final JButton lvlUp = new JButton("Level Up");
	private final JPanel changesPanel = new NoHorizontalScrollPanel();

	public LevelUpPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setLayout(new BorderLayout());
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);

		add(UILib.getTextDisplay(
				"<html>The button at the bottom of this page will allow you to level up your character.<br>This tab will display a summary of class and species changes.<br>You should visit any tabs with changes after level up to review the changes and make any selections.</html>",
				VaporwaveColors.HOT_PINK), BorderLayout.PAGE_START);

		changesPanel.setLayout(new BoxLayout(changesPanel, BoxLayout.Y_AXIS));
		changesPanel.setOpaque(false);
		add(UILib.getScrollPaneFor(changesPanel), BorderLayout.CENTER);

		lvlUp.addActionListener(this);
		lvlUp.setBackground(VaporwaveColors.NEON_BLUE);
		add(lvlUp, BorderLayout.PAGE_END);
	}

	@Override
	public void updateProperty(String prop)
	{
		int charLevel = sheet.getLevel();
		changesPanel.removeAll();
		for (int i = 2; i <= charLevel; i++)
		{
			changesPanel.add(new LvlChangePanel(i, sheet, i != charLevel));
		}
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(lvlUp))
		{
			// TODO Multiclass
			sheet.getCharClass().setLevel(sheet.getLevel() + 1);
		}
	}
}
