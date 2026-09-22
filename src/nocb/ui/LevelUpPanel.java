package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.main.CharacterSheet;

public class LevelUpPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private CharacterSheet sheet;

	private final JButton lvlUp = new JButton("Level Up");
	private final JPanel changesPanel = new NoHorizontalScrollPanel();

	private Runnable showSubclassTab;

	public LevelUpPanel(CharacterSheet sheet, Runnable showSubclassTab)
	{
		this.sheet = sheet;
		this.showSubclassTab = showSubclassTab;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		UILib.addTextDisplay(this,
				"<html>The button at the bottom of this page will allow you to level up your character.<br>This tab will display a summary of class and species changes.<br>You should visit any tabs with changes after level up to review the changes and make any selections.</html>",
				c);
		c.gridy++;

		c.weighty = 1;
		changesPanel.setLayout(new BoxLayout(changesPanel, BoxLayout.Y_AXIS));
		JScrollPane scroll = new JScrollPane(changesPanel);
		scroll.getViewport().setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, c);
		c.gridy++;
		c.weighty = 0;

		lvlUp.addActionListener(this);
		add(lvlUp, c);
	}

	public void updateDetails()
	{
		int charLevel = sheet.getLevel();
		changesPanel.removeAll();
		for (int i = 2; i <= charLevel; i++)
		{
			changesPanel.add(new LvlChangePanel(i, sheet, i != charLevel, showSubclassTab));
		}
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(lvlUp))
		{
			// TODO - multiclassing will change this
			sheet.getCharClass().setLevel(sheet.getLevel() + 1);
			updateDetails();
		}
	}
}
