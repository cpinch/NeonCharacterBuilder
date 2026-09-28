package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.util.Arrays;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Homeworld;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class HomeworldEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Homeworld homeworld;

	@Override
	protected String getItemName()
	{
		return homeworld.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		homeworld.setName(s);
	}

	public HomeworldEditPanel()
	{
		super();

		c.weighty = 0.2;
		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Traits: ", c, Color.white, Color.black,
				() -> getTraits(), (s) -> updateTraits(s)));
		c.gridy++;
		c.weighty = 0.8;
		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Desc: ", c, Color.white, Color.black,
				() -> homeworld.getDesc(), (s) -> homeworld.setDesc(s)));

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		homeworld = (Homeworld) sel;

		linkedProperties.forEach(p -> p.updateValue());

		setVisible(true);
	}

	private String getTraits()
	{
		return String.join(", ", homeworld.getTraits());
	}

	private void updateTraits(String s)
	{
		homeworld.setTraits(Arrays.asList(s.split(",")).stream().map(t -> t.trim()).toList());
	}
}
