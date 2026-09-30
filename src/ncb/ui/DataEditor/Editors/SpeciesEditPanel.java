package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import ncb.data.Feature;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Homeworld;
import ncb.data.loadables.Species;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class SpeciesEditPanel extends EditPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Species species;

	@Override
	protected String getItemName()
	{
		return species.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		species.setName(s);
	}

	private final JButton addTrait = new JButton("Add Trait");
	private final JPanel traitsPanel = new NoHorizontalScrollPanel();

	public SpeciesEditPanel()
	{
		super();

		c.weighty = 1;
		linkedProperties.add(UILib.addLabeledLinkedTextArea(this, 5, "Desc: ", c, Color.white, Color.black,
				() -> species.getDesc(), (s) -> species.setDesc(s)));
		c.gridy++;
		c.weighty = 0;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Type: ", c, Color.white, Color.black,
				() -> species.getType(), (s) -> species.setType(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedDropdown(this, List.of("M", "M or S", "S"), "Size: ", c, Color.white,
				Color.black, () -> getSpeciesSize(), (s) -> updateSpeciesSize(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedSpinner(this, "Speed: ", c, new SpinnerNumberModel(30, 0, 50, 5),
				Color.white, Color.black, () -> species.getSpeedMod(), (i) -> species.setSpeedMod(i)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Homeworld: ", c, Color.white, Color.black,
				() -> getHomeworld(), (s) -> updateHomeworld(s)));
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
	public void setSelected(Customizable sel)
	{
		species = (Species) sel;

		linkedProperties.forEach(p -> p.updateValue());

		updateTraitPanels();

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(addTrait))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New Species Trait",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				species.addNewTrait(name);
				updateTraitPanels();
			}
		}
		else if (e.getActionCommand().startsWith("Delete"))
		{
			int fId = Integer.parseInt(e.getActionCommand().split(" ")[1].trim());
			species.removeSpeciesTrait(fId);
			updateTraitPanels();
		}
	}

	private void updateTraitPanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		traitsPanel.removeAll();
		for (Feature st : species.getAllTraits())
		{
			FeatureEditPanel step = new FeatureEditPanel(st, true, this);
			traitsPanel.add(step, c);
			c.gridy++;
		}
		traitsPanel.revalidate();
	}

	private String getSpeciesSize()
	{
		return species.getSize().equals("MS") ? "M or S" : species.getSize();
	}

	private void updateSpeciesSize(String s)
	{
		if (s.equals("M or S"))
		{
			species.setSpeciesSize("MS");
		}
		else
		{
			species.setSpeciesSize(s);
		}
	}

	private String getHomeworld()
	{
		return species.getHomeworld() == null ? "" : species.getHomeworld().toString();
	}

	private void updateHomeworld(String s)
	{
		try
		{
			species.setHomeworld(Homeworld.getByName(s));
		}
		catch (Exception e)
		{
			showErrorMessage(List.of(s), "homeworld");
		}
	}
}
