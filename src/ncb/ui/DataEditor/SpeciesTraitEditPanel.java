package ncb.ui.DataEditor;

import java.awt.Color;

import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import ncb.data.Feature;
import ncb.ui.UILib;

public class SpeciesTraitEditPanel extends FeatureEditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private final JSpinner lvl = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

	private Feature st;

	public SpeciesTraitEditPanel(Feature st)
	{
		super(st);

		this.st = st;

		lvl.addChangeListener(this);
		((JSpinner.DefaultEditor) lvl.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(lvlSlot, "Level: ", lvl).setForeground(Color.black);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		super.stateChanged(e);
		if (e.getSource().equals(lvl))
		{
			st.setLevel((int) lvl.getValue());
		}
	}

	@Override
	protected void updateSelection()
	{
		super.updateSelection();
		lvl.setValue(st.getLevel());
	}
}
