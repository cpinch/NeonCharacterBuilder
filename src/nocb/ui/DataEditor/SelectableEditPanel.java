package nocb.ui.DataEditor;

import javax.swing.JTextField;

import nocb.data.Selectable;
import nocb.ui.UILib;

public class SelectableEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Selectable select;

	private JTextField type = new JTextField(20);
	private SelectablePrereqsEditPanel spep = new SelectablePrereqsEditPanel();
	private SelectableFeaturesEditPanel sfep = new SelectableFeaturesEditPanel();

	public SelectableEditPanel()
	{
		super();

		type.addFocusListener(UILib.createFocusListener(() -> updateType()));
		UILib.addLabeledComponent(this, "Selectable Type: ", type, c);
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
		select = Selectable.getSelectableById(id);

		nameField.setText(select.getName());
		type.setText(select.getType());
		spep.updatePrereqs(select);
		sfep.updateFeatures(select);

		setVisible(true);
	}

	@Override
	protected void updateName()
	{
		select.setName(nameField.getText());
	}

	private void updateType()
	{
		select.setType(type.getText());
	}
}
