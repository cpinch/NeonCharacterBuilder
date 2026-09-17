package nocb.ui.DataEditor;

import javax.swing.JTextField;

import nocb.data.Feat;
import nocb.ui.UILib;

public class FeatEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Feat feat;

	private JTextField featType = new JTextField(20);
	private SelectablePrereqsEditPanel spep = new SelectablePrereqsEditPanel();
	private SelectableFeaturesEditPanel sfep = new SelectableFeaturesEditPanel();

	public FeatEditPanel()
	{
		super();

		featType.addFocusListener(UILib.createFocusListener(() -> updateFeatType()));
		UILib.addLabeledComponent(this, "Feat Type: ", featType, c);
		c.gridy++;

		c.weighty = 1;
		add(spep, c);
		c.gridy++;
		add(sfep, c);

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		feat = Feat.getById(id);

		nameField.setText(feat.getName());
		featType.setText(feat.getFeatType());
		spep.updatePrereqs(feat);
		sfep.updateFeatures(feat);

		setVisible(true);
	}

	@Override
	protected void updateName()
	{
		feat.setName(nameField.getText());
	}

	private void updateFeatType()
	{
		feat.setFeatType(featType.getText());
		feat.setCustom(true);
	}
}
