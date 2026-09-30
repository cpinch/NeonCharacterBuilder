package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.loadables.Background;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Language;
import ncb.data.loadables.Species;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundLangsPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Language> lang1, lang2, lang3;
	private final JLabel suggestions;
	private final JLabel label;

	public BackgroundLangsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		setLayout(new GridBagLayout());
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);
		PropertyListener.listenForChanges(PropertyListener.LANGUAGES, this);
		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(0, 0, 0, 0);

		label = UILib.addLabel(this, "Languages: ", c, VaporwaveColors.HOT_PINK);
		label.setFont(UILib.boldFont);
		c.gridx++;

		// Custom backgrounds have Common and 2 of any languages
		// TODO Background loading
		lang1 = UILib.getComboBox(new Language[]
		{ Language.getByName("Common") }, this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(lang1, c);
		c.gridx++;
		lang2 = UILib.getComboBox(Language.getAllLanguages().toArray(new Language[0]), this,
				VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(lang2, c);
		c.gridx++;
		lang3 = UILib.getComboBox(Language.getAllLanguages().toArray(new Language[0]), this,
				VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(lang3, c);
		c.gridx = 1;
		c.gridy++;

		c.gridwidth = 3;
		suggestions = UILib.addLabel(this, "", c, VaporwaveColors.ELECTRIC_TEAL);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (e.getSource().equals(lang1) || e.getSource().equals(lang2) || e.getSource().equals(lang3))
			{
				bg.setLanguagesSelected(List.of((Language) lang1.getSelectedItem(), (Language) lang2.getSelectedItem(),
						(Language) lang3.getSelectedItem()));

			}
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				List<Language> bgLangs = new ArrayList<>(bg.getLanguagesSelected());
				lang1.setVisible(true);
				lang1.setSelectedItem(bgLangs.get(0));
				lang2.setVisible(true);
				lang2.setSelectedItem(bgLangs.get(1));
				lang3.setVisible(true);
				lang3.setSelectedItem(bgLangs.get(2));
				label.setText("Languages: ");

				suggestions.setVisible(true);
				suggestions.setText("<html><i>Suggested Languages: "
						+ String.join(", ", getLanguageSuggestions(sheet.getSpecies(), bg.getHomeworld())));
			}
			else
			{
				lang1.setVisible(false);
				lang2.setVisible(false);
				lang3.setVisible(false);
				suggestions.setVisible(false);
				label.setText("Languages: "
						+ String.join(", ", bg.getLanguagesSelected().stream().map(s -> s.getName()).toList()));
			}
		}
	}

	private static List<String> getLanguageSuggestions(Species s, Homeworld h)
	{
		List<String> langs = new ArrayList<>();

		String speciesHomeworldName = s.getHomeworld() == null ? "" : s.getHomeworld().getName();
		String charHomeworldName = h == null ? "" : h.getName();

		List<Language> allLanguages = Language.getAllLanguages();

		for (Language l : allLanguages)
		{
			if (l.getName().equals("Common"))
			{
				// This is forced, so ignore
				continue;
			}

			List<String> spokenAt = l.getSpokenAt();
			if (spokenAt.contains("Galaxy-wide") || spokenAt.contains(speciesHomeworldName)
					|| spokenAt.contains(charHomeworldName))
			{
				langs.add(l.getName());
			}
		}

		return langs;
	}
}
