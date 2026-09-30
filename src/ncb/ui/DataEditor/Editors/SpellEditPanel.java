package ncb.ui.DataEditor.Editors;

import java.awt.Color;

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
				Color.white, Color.black, () -> spell.getLevel(), (i) -> spell.setLevel(i)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "School: ", c, Color.white, Color.black,
				() -> spell.getSchool(), (s) -> spell.setSchool(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Cast Time: ", c, Color.white, Color.black,
				() -> spell.getCastTime(), (s) -> spell.setCastTime(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Trigger (optional): ", c, Color.white, Color.black,
				() -> spell.getTrigger(), (s) -> spell.setTrigger(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Components: ", c, Color.white, Color.black,
				() -> spell.getComponents(), (s) -> spell.setComponents(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Materials (optional): ", c, Color.white,
				Color.black, () -> spell.getMaterials(), (s) -> spell.setMaterials(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Range: ", c, Color.white, Color.black,
				() -> spell.getRange(), (s) -> spell.setRange(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Duration: ", c, Color.white, Color.black,
				() -> spell.getDuration(), (s) -> spell.setDuration(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedCheckbox(this, "School: ", c, Color.black, () -> spell.isRitual(),
				(b) -> spell.setRitual(b)));
		c.gridy++;

		UILib.addLabel(this, "Text:", c, Color.black).setFont(UILib.boldFont);
		c.gridy++;
		c.weighty = 1;
		linkedProperties.add(UILib.addLabeledLinkedTextArea(this, 10, "", c, Color.white, Color.black,
				() -> spell.getText(), (s) -> spell.setText(s)));
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
