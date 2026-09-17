package nocb.ui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import nocb.data.Species;
import nocb.main.CharacterSheet;

public class SpeciesTraitsPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JTextPane desc = UILib.getTextDisplay();
	private final JLabel type = UILib.getLabel("");
	private final JComboBox<String> sizeSelector = new JComboBox<>();
	private final JLabel sizeLabel = UILib.getLabel("");
	private final JLabel speed = UILib.getLabel("");

	public SpeciesTraitsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();
		c.anchor = GridBagConstraints.WEST;

		JLabel l = UILib.addLabeledComponent(this, "Type:", type, c);
		l.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l.setFont(UILib.boldFont);
		type.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		type.setFont(UILib.boldFont);
		c.gridy++;

		JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		sizePanel.setBackground(VaporwaveColors.DARK_PURPLE);
		sizePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel l2 = UILib.getLabel("Size:");
		l2.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
		l2.setFont(UILib.boldFont);
		sizePanel.add(l2);
		sizeSelector.addActionListener(this);
		sizeSelector.setBackground(VaporwaveColors.DEEP_VIOLET);
		sizeSelector.setForeground(VaporwaveColors.LASER_YELLOW);
		sizeSelector.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		sizeSelector.setFont(UILib.boldFont);
		sizePanel.add(sizeSelector);
		sizeLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		sizeLabel.setFont(UILib.boldFont);
		sizePanel.add(sizeLabel);
		add(sizePanel, c);
		c.gridy++;

		JLabel l3 = UILib.addLabeledComponent(this, "Speed: ", speed, c);
		l3.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l3.setFont(UILib.boldFont);
		speed.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		speed.setFont(UILib.boldFont);
		c.gridy++;

		c.weighty = 1;
		desc.setBorder(BorderFactory.createEmptyBorder(15, 5, 0, 5));
		add(desc, c);
		c.gridy++;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(sizeSelector))
		{
			String size = (String) sizeSelector.getSelectedItem();
			if (size != null)
			{
				sheet.getSpecies().setSize(size);
			}
		}
	}

	public void updateDetails()
	{
		Species species = sheet.getSpecies();
		desc.setText("<i>" + species.getDesc() + "</i>");
		type.setText(species.getType());
		speed.setText(species.getSpeedMod() + "ft");

		if (species.getSpeciesSize().length() > 1)
		{
			sizeSelector.setVisible(true);
			sizeLabel.setVisible(false);
			sizeSelector.removeAllItems();
			species.getSpeciesSize().chars().forEach(s -> sizeSelector.addItem("" + ((char) s)));
			sizeSelector.setSelectedItem(species.getSize());
		}
		else
		{
			sizeLabel.setVisible(true);
			sizeSelector.setVisible(false);
			sizeLabel.setText(species.getSize());
		}
	}
}
