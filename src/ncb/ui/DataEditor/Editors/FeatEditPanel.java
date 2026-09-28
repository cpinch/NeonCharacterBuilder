package ncb.ui.DataEditor.Editors;

import java.awt.Color;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Feat;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class FeatEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Feat feat;

	@Override
	protected String getItemName()
	{
		return feat.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		feat.setName(s);
	}

	private SelectablePrereqsEditPanel spep = new SelectablePrereqsEditPanel();
	private SelectableFeaturesEditPanel sfep = new SelectableFeaturesEditPanel();

	public FeatEditPanel()
	{
		super();

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Feat Type: ", c, Color.white, Color.black,
				() -> feat.getType(), (s) -> feat.setType(s)));
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
		feat = (Feat) sel;

		linkedProperties.forEach(p -> p.updateValue());
		spep.updatePrereqs(feat);
		sfep.updateFeatures(feat);

		setVisible(true);
	}
}
