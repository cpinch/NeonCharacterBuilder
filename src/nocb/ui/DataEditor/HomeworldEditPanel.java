package nocb.ui.DataEditor;

import java.util.Arrays;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

import nocb.data.Homeworld;
import nocb.ui.UILib;

public class HomeworldEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Homeworld homeworld;

	private final JTextArea traits = new JTextArea(2, 20), desc = new JTextArea(5, 20);

	public HomeworldEditPanel()
	{
		super();

		c.weighty = 0.2;
		traits.addFocusListener(UILib.createFocusListener(() -> updateTraits()));
		traits.setBorder(BorderFactory.createEtchedBorder());
		traits.setLineWrap(true);
		traits.setWrapStyleWord(true);
		UILib.addLabeledComponent(this, "Traits: ", traits, c);
		c.gridy++;
		c.weighty = 0.8;
		desc.setBorder(BorderFactory.createEtchedBorder());
		desc.addFocusListener(UILib.createFocusListener(() -> updateDesc()));
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		UILib.addLabeledComponent(this, "Desc:  ", desc, c);

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		homeworld = Homeworld.getById(id);

		nameField.setText(homeworld.getName());
		traits.setText(String.join(", ", homeworld.getTraits()));
		desc.setText(homeworld.getDesc());

		setVisible(true);
	}

	@Override
	protected void updateName()
	{
		homeworld.setName(nameField.getText());
	}

	private void updateTraits()
	{
		homeworld.setTraits(Arrays.asList(traits.getText().split(",")).stream().map(t -> t.trim()).toList());
		homeworld.setCustom(true);
	}

	private void updateDesc()
	{
		homeworld.setDesc(desc.getText());
		homeworld.setCustom(true);
	}
}
