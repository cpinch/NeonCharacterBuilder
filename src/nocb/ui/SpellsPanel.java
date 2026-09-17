package nocb.ui;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.data.Spell;
import nocb.data.SpellChoice;
import nocb.main.CharacterSheet;

public class SpellsPanel extends JPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JPanel grantedSpellsPanel = new JPanel();
	private final JPanel spellSelectionPanel = new JPanel();

	public SpellsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BorderLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);

		grantedSpellsPanel.setLayout(new BoxLayout(grantedSpellsPanel, BoxLayout.Y_AXIS));
		spellSelectionPanel.setLayout(new BoxLayout(spellSelectionPanel, BoxLayout.Y_AXIS));

		JPanel content = new JPanel();
		content.setBackground(VaporwaveColors.DARK_PURPLE);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		UILib.addLabel(content, "Granted Spells:");
		grantedSpellsPanel.setBackground(VaporwaveColors.DARK_PURPLE);
		content.add(grantedSpellsPanel);

		UILib.addLabel(content, "Spell Selections:");
		spellSelectionPanel.setBackground(VaporwaveColors.DARK_PURPLE);
		content.add(spellSelectionPanel);

		// Dump the extra space at the bottom
		content.add(Box.createVerticalGlue());

		JScrollPane scroll = new JScrollPane(content);
		add(scroll, BorderLayout.CENTER);
	}

	public void updateDetails()
	{
		grantedSpellsPanel.removeAll();
		for (Spell s : sheet.getAllGrantedSpells())
		{
			grantedSpellsPanel.add(new SpellPanel(s));
		}

		spellSelectionPanel.removeAll();
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			List<SpellChoice> spellsByLevel = sheet.getCharClass().getSpellChoicesByLevel(sheet.getLevel(), lvl);

			if (spellsByLevel.isEmpty())
			{
				continue;
			}

			UILib.addLabel(spellSelectionPanel, (lvl == 0 ? "Cantrips" : "Level " + lvl)).setFont(UILib.boldFont);
			final int spellLvl = lvl;
			spellsByLevel.forEach(sc ->
			{
				spellSelectionPanel.add(new SpellSelectionPanel(sheet, sc, spellLvl));
			});
		}

		revalidate();
	}
}
