package ncb.ui.DataEditor.Editors;

import javax.swing.SpinnerNumberModel;

import ncb.data.interfaces.Customizable;
import ncb.data.loadables.Spell;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class SpellEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private Spell spell;

	@Override
	protected String getItemName()
	{
		return spell.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		spell.setName(s);
	}

	public SpellEditPanel()
	{
		super();

		linkedProperties.add(UILib.addLabeledLinkedSpinner(this, "Spell Level: ", c, new SpinnerNumberModel(0, 0, 9, 1),
				() -> spell.getLevel(), (i) -> spell.setLevel(i)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "School: ", c, () -> spell.getSchool(),
				(s) -> spell.setSchool(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Cast Time: ", c, () -> spell.getCastTime(),
				(s) -> spell.setCastTime(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Trigger (optional): ", c, () -> spell.getTrigger(),
				(s) -> spell.setTrigger(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Components: ", c, () -> spell.getComponents(),
				(s) -> spell.setComponents(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Materials (optional): ", c,
				() -> spell.getMaterials(), (s) -> spell.setMaterials(s)));
		c.gridy++;

		linkedProperties.add(
				UILib.addLabeledLinkedTextField(this, "Range: ", c, () -> spell.getRange(), (s) -> spell.setRange(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Duration: ", c, () -> spell.getDuration(),
				(s) -> spell.setDuration(s)));
		c.gridy++;

		linkedProperties.add(
				UILib.addLabeledLinkedCheckbox(this, "Ritual: ", c, () -> spell.isRitual(), (b) -> spell.setRitual(b)));
		c.gridy++;

		UILib.addLabel(this, "Text:", c);
		c.gridy++;
		c.weighty = 1;
		linkedProperties
				.add(UILib.addLabeledLinkedTextArea(this, 10, "", c, () -> spell.getText(), (s) -> spell.setText(s)));
		c.gridy++;

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		spell = (Spell) sel;

		linkedProperties.forEach(l -> l.updateValue());

		setVisible(true);
	}
}
