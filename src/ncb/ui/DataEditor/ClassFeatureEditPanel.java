package ncb.ui.DataEditor;

import java.awt.Color;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;

import ncb.data.Feature;
import ncb.data.enums.Ability;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public class ClassFeatureEditPanel extends FeatureEditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private final JComboBox<String> spellcastingAbility = new JComboBox<>();
	private final JPanel sap = new NoHorizontalScrollPanel();
	private final JSpinner lvl = new JSpinner(new SpinnerNumberModel(1, 1, 20, 1));

	private Feature cf;

	public ClassFeatureEditPanel(Feature cf)
	{
		super(cf);

		this.cf = cf;

		lvl.addChangeListener(this);
		((JSpinner.DefaultEditor) lvl.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(lvlSlot, "Level: ", lvl).setForeground(Color.black);

		spellcastingAbility.addItem("");
		spellcastingAbility.addItem(Ability.Int.toString());
		spellcastingAbility.addItem(Ability.Wis.toString());
		spellcastingAbility.addItem(Ability.Cha.toString());
		spellcastingAbility.addItem(Ability.Primary.toString());
		spellcastingAbility.addActionListener(this);
		sap.setOpaque(false);
		sap.setVisible(false);
		UILib.addLabeledComponent(sap, "Spellcasting Ability: ", spellcastingAbility).setForeground(Color.black);
		add(sap, c);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		super.stateChanged(e);
		if (e.getSource().equals(lvl))
		{
			cf.setLevel((int) lvl.getValue());
		}
	}

	@Override
	protected void updateSelection()
	{
		super.updateSelection();
		updateSpellcastingAbilityVisibility();
		lvl.setValue(cf.getLevel());
	}

	@Override
	protected void updateName()
	{
		super.updateName();
		updateSpellcastingAbilityVisibility();
	}

	private void updateSpellcastingAbilityVisibility()
	{
		if (cf.getName().contains("Magic") || cf.getName().contains("Spellcasting"))
		{
			sap.setVisible(true);
		}
		else
		{
			sap.setVisible(false);
		}
	}
}
