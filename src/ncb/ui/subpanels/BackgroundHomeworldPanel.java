package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import ncb.data.loadables.Background;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Species;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundHomeworldPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Homeworld> home;
	private final JLabel suggestions;
	private final JLabel label;
	private final JTextPane desc;

	public BackgroundHomeworldPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);
		PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);

		label = UILib.addLabel(this, "Homeworld: ", c, VaporwaveColors.HOT_PINK);
		c.gridx++;
		home = UILib.getComboBox(Homeworld.getAllHomeworlds().toArray(new Homeworld[0]), this,
				VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(home, c);
		c.gridx = 0;
		c.gridy++;

		suggestions = UILib.addLabel(this, "", c, VaporwaveColors.ELECTRIC_TEAL);
		c.gridy++;

		c.weighty = 1;
		c.gridwidth = 2;
		desc = UILib.addTextDisplay(this, "", c, VaporwaveColors.HOT_PINK);

		updateProperty(PropertyListener.BACKGROUND);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (e.getSource().equals(home))
			{
				Homeworld h = (Homeworld) home.getSelectedItem();
				bg.setHomeworld(h);
				updateHomeworldDesc(h);
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
				home.setVisible(true);
				home.setSelectedItem(bg.getHomeworld());
				label.setText("Homeworld: ");

				List<String> homeworldSug = getHomeworldSuggestions(sheet.getSpecies());
				if (homeworldSug.isEmpty())
				{
					suggestions.setVisible(false);
				}
				else
				{
					suggestions.setVisible(true);
					suggestions.setText("<html><i>Suggested Homeworlds: " + String.join(", ", homeworldSug));
				}
			}
			else
			{
				home.setVisible(false);
				suggestions.setVisible(false);
				Homeworld h = bg.getHomeworld();
				label.setText("Homeworld: " + h.getName());
				updateHomeworldDesc(h);
			}
		}
	}

	private void updateHomeworldDesc(Homeworld h)
	{
		if (h != null)
		{
			desc.setText(h.getDesc());
		}
	}

	private static List<String> getHomeworldSuggestions(Species s)
	{
		List<String> homes = new ArrayList<>();

		if (s.getHomeworld() != null)
		{
			homes.add(s.getHomeworld().getName());
		}

		return homes;
	}
}
