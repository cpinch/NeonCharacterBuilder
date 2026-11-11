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
import ncb.ui.HasAssociatedClass;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;

public class EquipmentSelectionPanel extends JPanel implements ActionListener, ListensForChanges, HasAssociatedClass
{
	private static final long serialVersionUID = -1513890001811947077L;

	private char[] letters =
	{ 'A', 'B', 'C' };

	private final CharacterSheet sheet;
	private final int classIndex;

	private final JComboBox<Character> selector;
	private final JTextPane label;

	public EquipmentSelectionPanel(CharacterSheet sheet, int multiclassIndex)
	{
		this.sheet = sheet;
		this.classIndex = multiclassIndex;
		setOpaque(false);
		setLayout(new GridBagLayout());
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.EQUIPMENT, this);
		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(0, 0, 0, 0);

		selector = UILib.getComboBox(new Character[0], this);
		UILib.addLabeledComponent(this, "Equipment: ", selector, c);
		c.gridy++;

		c.weighty = 1;
		label = UILib.addTextDisplay(this, "", c);
	}

	@Override
	public void updateProperty(String prop)
	{
		if (getAssociatedClass() != null)
		{
			selector.removeActionListener(this);
			selector.removeAllItems();
			for (int i = 0; i < getAssociatedClass().getEquipmentOptions().size(); i++)
			{
				selector.addItem(letters[i]);
			}
			selector.addActionListener(this);
			if (selector.getItemCount() > 0)
			{
				selector.setSelectedIndex(getAssociatedClass().getSelectedEquipmentIndex());
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(selector))
		{
			int index = selector.getSelectedIndex();
			getAssociatedClass().setSelectedEquipmentIndex(index);
			String labelText = getAssociatedClass().getEquipmentItems();
			if (!labelText.isBlank())
			{
				labelText += " and ";
			}
			labelText += getAssociatedClass().getEquipmentNotes() + " Notes";
			label.setText(labelText);
		}
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public CharacterSheet getSheet()
	{
		return sheet;
	}
}
