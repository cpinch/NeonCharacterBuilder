package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class EquipmentSelectionPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -1513890001811947077L;

	private char[] letters =
	{ 'A', 'B', 'C' };

	private final CharacterSheet sheet;

	private final JComboBox<Character> selector;
	private final JTextPane label;

	public EquipmentSelectionPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setOpaque(false);
		setLayout(new GridBagLayout());
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(0, 0, 0, 0);

		selector = UILib.getComboBox(new Character[0], this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Equipment: ", selector, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.weighty = 1;
		label = UILib.addTextDisplay(this, "", c, VaporwaveColors.HOT_PINK);
	}

	@Override
	public void updateProperty(String prop)
	{
		if (sheet.getCharClass() != null)
		{
			selector.removeActionListener(this);
			selector.removeAllItems();
			for (int i = 0; i < sheet.getCharClass().getEquipmentOptions().size(); i++)
			{
				selector.addItem(letters[i]);
			}
			selector.addActionListener(this);
			if (selector.getItemCount() > 0)
			{
				selector.setSelectedIndex(sheet.getCharClass().getSelectedEquipmentIndex());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(selector))
		{
			int index = selector.getSelectedIndex();
			sheet.getCharClass().setSelectedEquipmentIndex(index);
			String labelText = sheet.getCharClass().getEquipmentItems();
			if (!labelText.isBlank())
			{
				labelText += " and ";
			}
			labelText += sheet.getCharClass().getEquipmentNotes() + " Notes";
			label.setText(labelText);
			revalidate();
			repaint();
		}
	}
}
