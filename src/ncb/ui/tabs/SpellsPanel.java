package ncb.ui.tabs;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.Feature;
import ncb.data.SpellChoice;
import ncb.data.loadables.CharacterClass;
import ncb.data.loadables.Species;
import ncb.data.loadables.Spell;
import ncb.data.loadables.SpellList;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.CustomSpellsDocDialog;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.subpanels.SpellPanel;
import ncb.ui.subpanels.SpellSelectionPanel;

public class SpellsPanel extends UIPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JPanel grantedSpellsPanel = new JPanel();
	private final JPanel spellSelectionPanel = new JPanel();

	private final JButton customSpellDocBtn;

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

		UILib.addLabel(content, "Granted Spells:").putClientProperty(FlatClientProperties.STYLE, "font: bold");
		content.add(grantedSpellsPanel);

		UILib.addLabel(content, "Spell Selections:").putClientProperty(FlatClientProperties.STYLE, "font: bold");
		content.add(spellSelectionPanel);

		// Dump any extra space at the bottom
		content.add(Box.createVerticalGlue());

		JScrollPane scroll = UILib.getScrollPaneFor(content);
		scroll.setAlignmentX(JComponent.LEFT_ALIGNMENT);
		add(scroll, BorderLayout.CENTER);

		updateProperty(PropertyListener.CLASS);

		customSpellDocBtn = new JButton("Create Custom Spell List");
		customSpellDocBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				new CustomSpellsDocDialog();
			}
		});
		add(customSpellDocBtn, BorderLayout.SOUTH);
	}

	@Override
	public void updateProperty(String prop)
	{
		grantedSpellsPanel.removeAll();
		for (Spell s : sheet.getAllGrantedSpells())
		{
			grantedSpellsPanel.add(new SpellPanel(s));
		}

		// Map is spell level -> each spell choice at that level with its spell list and
		// selection
		List<Map<Integer, List<SpellChoice>>> miscSpellChoices = sheet.getEachMap(Feature::getSpellChoices);

		List<SpellList> classListOptions = sheet.getCharClass().getSpellLists();
		spellSelectionPanel.removeAll();
		for (int lvl = 0; lvl <= 9; lvl++)
		{
			// Class spells
			List<SpellChoice> classSpellsForLvl = sheet.getCharClass().getSpellChoicesByLevel(lvl);
			List<SpellSelectionPanel> panelsToAdd = new ArrayList<>();

			final int spellLvl = lvl;
			classSpellsForLvl.forEach(sc ->
			{
				panelsToAdd.add(new SpellSelectionPanel(sc, spellLvl, classListOptions));
			});

			// Other feature spells
			for (Map<Integer, List<SpellChoice>> map : miscSpellChoices)
			{
				if (map != null && map.containsKey(spellLvl))
				{
					map.get(spellLvl).forEach(sc ->
					{
						panelsToAdd.add(new SpellSelectionPanel(sc, spellLvl, List.of(sc.getSpellList())));
					});
				}
			}

			if (panelsToAdd.isEmpty())
			{
				continue;
			}

			UILib.addLabel(spellSelectionPanel, (lvl == 0 ? "Cantrips" : "Level " + lvl))
					.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 0));
			for (SpellSelectionPanel panel : panelsToAdd)
			{
				spellSelectionPanel.add(panel);
			}
		}

		revalidate();

		if (!sheet.getName().isBlank()
				|| sheet.getCharClass().getName() != CharacterClass.getAllClasses().get(0).getName()
				|| sheet.getSpecies().getName() != Species.getAllSpecies().get(0).getName())
		{
			customSpellDocBtn.setVisible(false);
		}
	}
}
