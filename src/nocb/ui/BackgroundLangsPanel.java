package nocb.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import nocb.data.Background;
import nocb.data.Homeworld;
import nocb.data.Language;
import nocb.data.Species;
import nocb.main.CharacterSheet;

public class BackgroundLangsPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Language> lang1, lang2, lang3;
	private final JLabel suggestions = UILib.getLabel("");
	private final JLabel label = UILib.getLabel("");

	public BackgroundLangsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		JPanel selectPanel = new JPanel();
		selectPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		selectPanel.setOpaque(false);
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));

		UILib.addLabel(selectPanel, "Languages: ");

		selectPanel.add(label);

		// Custom backgrounds have Common and 2 of any languages
		lang1 = new JComboBox<>(new Language[]
		{ Language.getByName("Common") });
		lang1.addActionListener(this);
		lang1.setBackground(VaporwaveColors.DEEP_VIOLET);
		lang1.setForeground(VaporwaveColors.LASER_YELLOW);
		selectPanel.add(lang1);
		lang2 = new JComboBox<>(Language.getAllLanguages().toArray(new Language[0]));
		lang2.addActionListener(this);
		lang2.setBackground(VaporwaveColors.DEEP_VIOLET);
		lang2.setForeground(VaporwaveColors.LASER_YELLOW);
		selectPanel.add(lang2);
		lang3 = new JComboBox<>(Language.getAllLanguages().toArray(new Language[0]));
		lang3.addActionListener(this);
		lang3.setBackground(VaporwaveColors.DEEP_VIOLET);
		lang3.setForeground(VaporwaveColors.LASER_YELLOW);
		selectPanel.add(lang3);

		add(selectPanel);
		suggestions.setAlignmentX(Component.CENTER_ALIGNMENT);
		suggestions.setHorizontalAlignment(SwingConstants.CENTER);
		add(suggestions);
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

	public void updateDetails()
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
				label.setVisible(false);

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
				label.setVisible(true);
				label.setText(String.join(", ", bg.getLanguagesSelected().stream().map(s -> s.getName()).toList()));
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
