package nocb.ui.DataEditor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nocb.data.Spell;
import nocb.ui.UILib;

public class SpellEditPanel extends EditPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Spell spell;

	private final JSpinner lvl = new JSpinner(new SpinnerNumberModel(0, 0, 9, 1));
	private final JTextField sch = new JTextField(20), time = new JTextField(20), trigger = new JTextField(20),
			comps = new JTextField(20), mats = new JTextField(20), range = new JTextField(20), dur = new JTextField(20);
	private final JTextArea text = new JTextArea(10, 20);
	private final JCheckBox rit = new JCheckBox();

	public SpellEditPanel()
	{
		super();

		lvl.addChangeListener(this);
		((JSpinner.DefaultEditor) lvl.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "Spell Level: ", lvl, c);
		c.gridy++;
		sch.addFocusListener(UILib.createFocusListener(() -> updateSchool()));
		UILib.addLabeledComponent(this, "School: ", sch, c);
		c.gridy++;
		time.addFocusListener(UILib.createFocusListener(() -> updateCastTime()));
		UILib.addLabeledComponent(this, "Cast Time: ", time, c);
		c.gridy++;
		trigger.addFocusListener(UILib.createFocusListener(() -> updateTrigger()));
		UILib.addLabeledComponent(this, "Trigger (Opt): ", trigger, c);
		c.gridy++;
		comps.addFocusListener(UILib.createFocusListener(() -> updateComponents()));
		UILib.addLabeledComponent(this, "Components: ", comps, c);
		c.gridy++;
		mats.addFocusListener(UILib.createFocusListener(() -> updateMaterials()));
		UILib.addLabeledComponent(this, "Materials (Opt): ", mats, c);
		c.gridy++;
		range.addFocusListener(UILib.createFocusListener(() -> updateRange()));
		UILib.addLabeledComponent(this, "Range: ", range, c);
		c.gridy++;
		dur.addFocusListener(UILib.createFocusListener(() -> updateDuration()));
		UILib.addLabeledComponent(this, "Duration: ", dur, c);
		c.gridy++;
		rit.addActionListener(this);
		UILib.addLabeledComponent(this, "Ritual?:", rit, c);
		c.gridy++;
		text.addFocusListener(UILib.createFocusListener(() -> updateText()));
		UILib.addLabel(this, "Text:", c);
		c.gridy++;
		c.weighty = 1;
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		add(text, c);
		c.gridy++;

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		spell = Spell.getById(id);

		nameField.setText(spell.getName());
		lvl.setValue(spell.getLevel());
		sch.setText(spell.getSchool());
		time.setText(spell.getCastTime());
		trigger.setText(spell.getTrigger());
		comps.setText(spell.getComponents());
		mats.setText(spell.getMaterials());
		range.setText(spell.getRange());
		dur.setText(spell.getDuration());
		rit.setSelected(spell.isRitual());
		text.setText(spell.getText());

		setVisible(true);
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(lvl))
		{
			spell.setLevel((int) lvl.getValue());
			spell.setCustom(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(rit))
		{
			spell.setRitual(rit.isSelected());
			spell.setCustom(true);
		}
	}

	private void updateSchool()
	{
		spell.setSchool(sch.getText());
		spell.setCustom(true);
	}

	private void updateCastTime()
	{
		spell.setCastTime(time.getText());
		spell.setCustom(true);
	}

	private void updateTrigger()
	{
		spell.setTrigger(trigger.getText());
		spell.setCustom(true);
	}

	private void updateComponents()
	{
		spell.setComponents(comps.getText());
		spell.setCustom(true);
	}

	private void updateMaterials()
	{
		spell.setMaterials(mats.getText());
		spell.setCustom(true);
	}

	private void updateRange()
	{
		spell.setRange(range.getText());
		spell.setCustom(true);
	}

	private void updateDuration()
	{
		spell.setDuration(dur.getText());
		spell.setCustom(true);
	}

	private void updateText()
	{
		spell.setText(text.getText());
		spell.setCustom(true);
	}

	@Override
	protected void updateName()
	{
		spell.setName(nameField.getText());
		spell.setCustom(true);
	}
}
