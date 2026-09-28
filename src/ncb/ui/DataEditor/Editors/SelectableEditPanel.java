package ncb.ui.DataEditor.Editors;

import java.awt.Color;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Selectable;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class SelectableEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable select;

	@Override
	protected String getItemName()
	{
		return select.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		select.setName(s);
	}

	private SelectablePrereqsEditPanel spep = new SelectablePrereqsEditPanel();
	private SelectableFeaturesEditPanel sfep = new SelectableFeaturesEditPanel();

	public SelectableEditPanel()
	{
		super();

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Selectable Type: ", c, Color.white, Color.black,
				() -> select.getType(), (s) -> select.setType(s)));
		c.gridy++;

		c.weighty = 1;
		add(spep, c);
		c.gridy++;
		add(sfep, c);

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		select = (Selectable) sel;

		linkedProperties.forEach(p -> p.updateValue());
		spep.updatePrereqs(select);
		sfep.updateFeatures(select);

		setVisible(true);
	}
}
