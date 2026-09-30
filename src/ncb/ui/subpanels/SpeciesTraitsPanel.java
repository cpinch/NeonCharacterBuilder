package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import ncb.data.loadables.Species;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class SpeciesTraitsPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextPane desc;
	private final JLabel type;
	private final JComboBox<String> sizeSelector;
	private final JLabel sizeLabel;
	private final JLabel speed;

	public SpeciesTraitsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();
		PropertyListener.listenForChanges(PropertyListener.SPECIES, this);
		PropertyListener.listenForChanges(PropertyListener.SIZE, this);

		c.gridwidth = 2;
		type = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Type: ", type, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.gridwidth = 1;
		sizeLabel = UILib.addLabel(this, "Size: ", c, VaporwaveColors.HOT_PINK);
		sizeLabel.setFont(UILib.boldFont);
		c.gridx++;
		sizeSelector = UILib.getComboBox(new String[0], this, VaporwaveColors.DEEP_VIOLET,
				VaporwaveColors.LASER_YELLOW);
		add(sizeSelector, c);
		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 2;

		speed = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Speed: ", speed, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.weighty = 1;
		desc = UILib.addTextDisplay(this, "", c, VaporwaveColors.HOT_PINK);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(sizeSelector))
		{
			String size = (String) sizeSelector.getSelectedItem();
			if (size != null)
			{
				sheet.getSpecies().setSelectedSize(size);
			}
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		Species species = sheet.getSpecies();
		if (prop.equals(PropertyListener.SPECIES))
		{
			desc.setText("<i>" + species.getDesc() + "</i>");
			type.setText(species.getType());
			speed.setText(species.getSpeedMod() + "ft");
		}

		if (species.getSpeciesSize().length() > 1)
		{
			sizeSelector.setVisible(true);
			sizeLabel.setText("Size: ");
			sizeSelector.removeActionListener(this);
			sizeSelector.removeAllItems();
			species.getSpeciesSize().chars().forEach(s -> sizeSelector.addItem("" + ((char) s)));
			sizeSelector.addActionListener(this);
			sizeSelector.setSelectedItem(species.getSize());
		}
		else
		{
			sizeSelector.setVisible(false);
			sizeLabel.setText("Size: " + species.getSize());
		}
	}
}
