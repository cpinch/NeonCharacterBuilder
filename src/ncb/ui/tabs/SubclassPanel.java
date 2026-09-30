package ncb.ui.tabs;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import ncb.data.loadables.Subclass;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.UIPanel;
import ncb.ui.VaporwaveColors;
import ncb.ui.subpanels.SubclassFeaturesPanel;

public class SubclassPanel extends UIPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> subclassNameSelector;
	private final JLabel subclassDesc;
	private final SubclassFeaturesPanel featuresPanel;

	public SubclassPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);
		PropertyListener.listenForChanges(PropertyListener.SUBCLASS, this);

		GridBagConstraints c = UILib.getStandardGBC();

		subclassNameSelector = UILib
				.getComboBox(
						Subclass.getForClassName(sheet.getCharClass().getName()).stream().map(sc -> sc.getName())
								.toList().toArray(new String[0]),
						this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Subclass: ", subclassNameSelector, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		subclassDesc = UILib.addLabel(this, "", c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.weighty = 1;
		featuresPanel = new SubclassFeaturesPanel(sheet);
		UILib.addScrollPaneFor(this, featuresPanel, c);
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
			}
		}
	}

	@Override
	public void updateProperty(String prop)
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
	}
}
