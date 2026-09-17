package nocb.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import nocb.data.Background;
import nocb.data.Homeworld;
import nocb.data.Species;
import nocb.main.CharacterSheet;

public class BackgroundHomeworldPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Homeworld> home;
	private final JLabel suggestions = UILib.getLabel("");
	private final JLabel label = UILib.getLabel("");
	private final JTextPane desc;

	public BackgroundHomeworldPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		JPanel selectPanel = new JPanel();
		selectPanel.setOpaque(false);
		selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.X_AXIS));

		UILib.addLabel(selectPanel, "Homeworld: ");
		selectPanel.add(label);

		home = new JComboBox<>(Homeworld.getAllHomeworlds().toArray(new Homeworld[0]));
		home.addActionListener(this);
		home.setBackground(VaporwaveColors.DEEP_VIOLET);
		home.setForeground(VaporwaveColors.LASER_YELLOW);
		selectPanel.add(home);

		add(selectPanel);
		suggestions.setFont(UILib.standardFont);
		suggestions.setForeground(VaporwaveColors.ELECTRIC_TEAL);
		add(suggestions);
		desc = UILib.getTextDisplay();
		add(desc);
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
				updateCallback.run();
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
				home.setVisible(true);
				home.setSelectedItem(bg.getHomeworld());
				label.setVisible(false);

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
				label.setVisible(true);
				Homeworld h = bg.getHomeworld();
				label.setText(h.getName());
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

	private Runnable updateCallback;

	public void setUpdateCallback(Runnable updateCallback)
	{
		this.updateCallback = updateCallback;
	}
}
