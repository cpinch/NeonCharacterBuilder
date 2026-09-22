package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.data.CharacterClass;
import nocb.main.CharacterSheet;
import nocb.main.PropertyListener;

public class ClassPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> classNameSelector;
	private final JLabel classLevelLabel = UILib.getLabel("");
	private final ClassTraitsPanel traitsPanel;
	private final ClassFeaturesPanel featuresPanel;

	public ClassPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;
		PropertyListener.listenForChanges(PropertyListener.CLASSLEVEL, this);

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();

		JPanel classHeaderPanel = new JPanel(new GridBagLayout());
		classHeaderPanel.setOpaque(false);
		classNameSelector = new JComboBox<>(
				CharacterClass.getAllClasses().stream().map(cls -> cls.getName()).toList().toArray(new String[0]));
		classNameSelector.addActionListener(this);
		classNameSelector.setBackground(VaporwaveColors.DEEP_VIOLET);
		classNameSelector.setForeground(VaporwaveColors.LASER_YELLOW);
		classHeaderPanel.add(classNameSelector, c);
		c.gridx++;
		c.weightx = 0;
		classLevelLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
		classHeaderPanel.add(classLevelLabel, c);
		c.gridx = 0;
		c.weightx = 1;
		UILib.addLabeledComponent(this, "Class: ", classHeaderPanel, c);
		c.gridy++;
		c.weighty = 1;

		JPanel splitPane = new JPanel(new GridLayout(1, 2));
		traitsPanel = new ClassTraitsPanel(sheet);
		splitPane.add(traitsPanel);

		featuresPanel = new ClassFeaturesPanel(sheet);
		JScrollPane scroll = new JScrollPane(featuresPanel);
		scroll.setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.getViewport().setBackground(VaporwaveColors.DARK_PURPLE);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		splitPane.add(scroll);

		add(splitPane, c);

		classNameSelector.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(classNameSelector))
		{
			CharacterClass selectedClass = CharacterClass.getByName((String) classNameSelector.getSelectedItem());
			sheet.setCharClass(selectedClass);
			classLevelLabel.setText("Level " + selectedClass.getLevel());

			traitsPanel.updateDetails();
			featuresPanel.updateDetails();
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		if (prop.equals(PropertyListener.CLASSLEVEL))
		{
			classLevelLabel.setText("Level " + sheet.getCharClass().getLevel());
			traitsPanel.updateDetails();
			featuresPanel.updateDetails();
		}
	}
}
