package ncb.ui.tabs;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ncb.data.SpellChoice;
import ncb.data.loadables.Spell;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.SpellPanel;
import ncb.ui.subpanels.SpellSelectionPanel;

public class SpellsPanel extends UIPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JPanel grantedSpellsPanel = new JPanel();
	private final JPanel spellSelectionPanel = new JPanel();

	public SpellsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setLayout(new BorderLayout());
		setAlignmentX(JComponent.LEFT_ALIGNMENT);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);
		PropertyListener.listenForChanges(PropertyListener.SELECTED, this);
		PropertyListener.listenForChanges(PropertyListener.SPELLS, this);

		grantedSpellsPanel.setLayout(new BoxLayout(grantedSpellsPanel, BoxLayout.Y_AXIS));
		grantedSpellsPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		grantedSpellsPanel.setOpaque(false);
		spellSelectionPanel.setLayout(new BoxLayout(spellSelectionPanel, BoxLayout.Y_AXIS));
		spellSelectionPanel.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		spellSelectionPanel.setOpaque(false);

		JPanel content = new NoHorizontalScrollPanel();
		content.setOpaque(false);
		content.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		UILib.addLabel(content, "Granted Spells:", VaporwaveColors.HOT_PINK).setFont(UILib.boldFont);
		content.add(grantedSpellsPanel);

		UILib.addLabel(content, "Spell Selections:", VaporwaveColors.HOT_PINK).setFont(UILib.boldFont);
		content.add(spellSelectionPanel);

		// Dump any extra space at the bottom
		content.add(Box.createVerticalGlue());

		JScrollPane scroll = UILib.getScrollPaneFor(content);
		scroll.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		add(scroll, BorderLayout.CENTER);

		updateProperty(PropertyListener.CLASS);
	}

	@Override
	public void updateProperty(String prop)
	{
		grantedSpellsPanel.removeAll();
		for (Spell s : sheet.getAllGrantedSpells())
		{
			grantedSpellsPanel.add(new SpellPanel(s));
		}

		spellSelectionPanel.removeAll();
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			List<SpellChoice> spellsByLevel = sheet.getCharClass().getSpellChoicesByLevel(lvl);

			if (spellsByLevel.isEmpty())
			{
				continue;
			}

			UILib.addLabel(spellSelectionPanel, (lvl == 0 ? "Cantrips" : "Level " + lvl), VaporwaveColors.HOT_PINK)
					.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
			final int spellLvl = lvl;
			spellsByLevel.forEach(sc ->
			{
				spellSelectionPanel.add(new SpellSelectionPanel(sheet, sc, spellLvl));
			});
		}

		revalidate();
	}
}
