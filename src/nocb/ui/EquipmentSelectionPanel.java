package nocb.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import nocb.main.CharacterSheet;

public class EquipmentSelectionPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -1513890001811947077L;

	private char[] letters =
	{ 'A', 'B', 'C' };

	private final CharacterSheet sheet;

	private final JComboBox<Character> selector = new JComboBox<>();
	private final JTextPane label;

	public EquipmentSelectionPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setBackground(VaporwaveColors.DARK_PURPLE);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		selector.addActionListener(this);
		selector.setBackground(VaporwaveColors.DEEP_VIOLET);
		selector.setForeground(VaporwaveColors.LASER_YELLOW);
		JLabel l = UILib.addLabeledComponent(this, "Equipment:", selector);
		l.setFont(UILib.boldFont);
		selector.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

		label = UILib.getTextDisplay();
		JScrollPane scroll = new JScrollPane(label);
		scroll.getViewport().setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
		scroll.setPreferredSize(new Dimension(400, 300));

		add(scroll);
	}

	public void updateDetails()
	{
		if (sheet.getCharClass() != null)
		{
			int charEquipIndex = sheet.getCharClass().getSelectedEquipmentIndex();
			selector.removeAllItems();
			for (int i = 0; i < sheet.getCharClass().getEquipmentOptionsCount(); i++)
			{
				selector.addItem(letters[i]);
			}
			if (selector.getItemCount() > 0)
			{
				selector.setSelectedIndex(charEquipIndex);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(selector))
		{
			int index = selector.getSelectedIndex();
			sheet.getCharClass().setSelectedEquipment(index);
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
