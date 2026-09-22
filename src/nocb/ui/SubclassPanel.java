package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.data.Subclass;
import nocb.main.CharacterSheet;
import nocb.main.PropertyListener;

public class SubclassPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> subclassNameSelector = new JComboBox<>();
	private final JLabel subclassDesc = UILib.getLabel("");
	private final SubclassFeaturesPanel featuresPanel;

	public SubclassPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		subclassNameSelector.addActionListener(this);
		subclassNameSelector.setBackground(VaporwaveColors.DEEP_VIOLET);
		subclassNameSelector.setForeground(VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Subclass: ", subclassNameSelector, c);
		c.gridy++;

		add(subclassDesc, c);
		c.gridy++;

		c.weighty = 1;
		featuresPanel = new SubclassFeaturesPanel(sheet);
		JScrollPane scroll = new JScrollPane(featuresPanel);
		scroll.setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.getViewport().setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, c);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(subclassNameSelector) && subclassNameSelector.getSelectedItem() != null)
		{
			Subclass selectedSubclass = Subclass.getByName((String) subclassNameSelector.getSelectedItem());
			if (selectedSubclass != null)
			{
				sheet.getCharClass().setSubclass(selectedSubclass);

				subclassDesc.setText("<html>" + selectedSubclass.getDesc() + "</html>");
				featuresPanel.updateDetails();
			}
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		if ((prop.equals(PropertyListener.CLASS) || prop.equals(PropertyListener.CLASSLEVEL)))
		{
			subclassNameSelector.removeActionListener(this);
			subclassNameSelector.removeAllItems();
			Subclass.getForClassName(sheet.getCharClass().getName())
					.forEach(sc -> subclassNameSelector.addItem(sc.getName()));
			subclassNameSelector.addActionListener(this);
			if (sheet.getCharClass() != null && sheet.getCharClass().getSubclass() != null)
			{
				subclassNameSelector.setSelectedItem(sheet.getCharClass().getSubclass().getName());
			}
			else if (subclassNameSelector.getItemCount() > 0)
			{
				subclassNameSelector.setSelectedIndex(0);
			}
			featuresPanel.updateDetails();
		}
	}
}
