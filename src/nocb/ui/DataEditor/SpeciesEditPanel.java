package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nocb.data.Homeworld;
import nocb.data.Species;
import nocb.data.SpeciesTrait;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class SpeciesEditPanel extends EditPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Species species;

	private final JTextArea desc = new JTextArea(5, 20);
	private final JTextField type = new JTextField(20);
	private final JComboBox<String> size = new JComboBox<>(new String[]
	{ "M", "M or S", "S" });
	private final JSpinner speed = new JSpinner(new SpinnerNumberModel(30, 0, 50, 5));
	private final JTextField homeworld = new JTextField(20);

	private final JButton addTrait = new JButton("Add Trait");
	private final JPanel traitsPanel = new NoHorizontalScrollPanel();

	public SpeciesEditPanel()
	{
		super();

		c.weighty = 1;
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.addFocusListener(UILib.createFocusListener(() -> updateDesc()));
		UILib.addLabeledComponent(this, "Desc: ", desc, c);
		c.gridy++;
		c.weighty = 0;

		type.addFocusListener(UILib.createFocusListener(() -> updateType()));
		UILib.addLabeledComponent(this, "Type: ", type, c);
		c.gridy++;

		size.addActionListener(this);
		UILib.addLabeledComponent(this, "Size: ", size, c);
		c.gridy++;

		speed.addChangeListener(this);
		((JSpinner.DefaultEditor) speed.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "Speed: ", speed, c);
		c.gridy++;

		homeworld.addFocusListener(UILib.createFocusListener(() -> updateHomeworld()));
		UILib.addLabeledComponent(this, "Homeworld: ", homeworld, c);
		c.gridy++;

		addTrait.addActionListener(this);
		add(addTrait, c);
		c.gridy++;
		traitsPanel.setLayout(new GridBagLayout());
		add(traitsPanel, c);
		c.gridy++;

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		species = Species.getById(id);

		nameField.setText(species.getText());
		desc.setText(species.getDesc());
		type.setText(species.getType());
		size.setSelectedItem(species.getSpeciesSize().length() == 1 ? species.getSpeciesSize() : "M or S");
		speed.setValue(species.getSpeedMod());
		homeworld.setText(species.getHomeworld() == null ? "" : species.getHomeworld().getName());

		updateTraitPanels();

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(size))
		{
			String sz = (String) size.getSelectedItem();
			if (sz.equals("M or S"))
			{
				species.setSpeciesSize("MS");
				species.setSize("");
			}
			else
			{
				species.setSpeciesSize(sz);
				species.setSize("");
			}
		}
		else if (e.getSource().equals(addTrait))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New Species Trait",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				species.addNewTrait(name);
				updateTraitPanels();
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(speed))
		{
			species.setSpeedMod((int) speed.getValue());
		}
	}

	private void updateTraitPanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		traitsPanel.removeAll();
		for (SpeciesTrait st : species.getTraits())
		{
			SpeciesTraitEditPanel step = new SpeciesTraitEditPanel(st);
			step.updateSelection();
			traitsPanel.add(step, c);
			c.gridy++;
		}
		traitsPanel.revalidate();
	}

	private void updateDesc()
	{
		species.setDesc(desc.getText());
	}

	private void updateType()
	{
		species.setType(type.getText());
	}

	private void updateHomeworld()
	{
		try
		{
			Homeworld hw = Homeworld.getByName(homeworld.getText());
			species.setHomeworld(hw);
		}
		catch (Exception e)
		{
		}
	}

	@Override
	protected void updateName()
	{
		species.setName(nameField.getText());
	}
}
