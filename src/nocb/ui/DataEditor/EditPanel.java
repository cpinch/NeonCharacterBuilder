package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JTextField;

import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public abstract class EditPanel extends NoHorizontalScrollPanel
{
	private static final long serialVersionUID = -3410949620714799628L;

	protected int id;

	protected final JTextField nameField = new JTextField(20);

	protected final GridBagConstraints c = UILib.getStandardGBC();

	public EditPanel()
	{
		setLayout(new GridBagLayout());

		nameField.addFocusListener(UILib.createFocusListener(() -> updateName()));
		UILib.addLabeledComponent(this, "Name: ", nameField, c);
		c.gridy++;
	}

	public void setSelectedId(int id)
	{
		this.id = id;
		updateSelection();
	}

	protected abstract void updateName();

	protected abstract void updateSelection();
}
