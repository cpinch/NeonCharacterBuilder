package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nocb.data.CharacterClass;
import nocb.main.CharacterSheet;

public class ClassPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<String> classNameSelector;
	private final ClassTraitsPanel traitsPanel;
	private final ClassFeaturesPanel featuresPanel;

	public ClassPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		classNameSelector = new JComboBox<>(
				CharacterClass.getAllClasses().stream().map(cls -> cls.getName()).toList().toArray(new String[0]));
		classNameSelector.addActionListener(this);
		classNameSelector.setBackground(VaporwaveColors.DEEP_VIOLET);
		classNameSelector.setForeground(VaporwaveColors.LASER_YELLOW);
		UILib.addLabeledComponent(this, "Class: ", classNameSelector, c);
		c.gridy++;
		c.weighty = 1;

		JPanel splitPane = new JPanel(new GridLayout(1, 2));
		traitsPanel = new ClassTraitsPanel(sheet, () -> skillsUpdated());
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

			traitsPanel.updateDetails();
			featuresPanel.updateDetails();
		}
	}

	private void skillsUpdated()
	{
		if (featuresPanel != null)
		{
			featuresPanel.skillsUpdated();
		}
	}

	public void updateDetails()
	{
		classNameSelector.setSelectedItem(sheet.getCharClass().getName());
		traitsPanel.updateDetails();
		featuresPanel.updateDetails();
	}
}
