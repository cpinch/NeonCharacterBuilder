package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.util.Arrays;

import javax.swing.SpinnerNumberModel;

import ncb.data.Prereq;
import ncb.data.enums.ArmorTraining;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.CharacterClass;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;
import ncb.ui.DataEditor.PropertyLinkedComboBox;
import ncb.ui.DataEditor.PropertyLinkedSpinner;
import ncb.ui.DataEditor.PropertyLinkedTextField;

public class SelectablePrereqEditPanel extends EditPanel
{
	private static final long serialVersionUID = -4113129376172120186L;

	private final Prereq sp;

	private final PropertyLinkedComboBox typeSel, clsName, armor;
	private final PropertyLinkedTextField hwTraits, abls, fName;
	private final PropertyLinkedSpinner val;

	public SelectablePrereqEditPanel(Prereq sp)
	{
		this.sp = sp;
		// We don't use name
		removeAll();
		linkedProperties.clear();
		c.weightx = 0;

		typeSel = UILib.getLinkedDropdown(Prereq.prereqTypes, Color.white, Color.black, () -> sp.getType(),
				(s) -> updateType(s));
		add(typeSel, c);
		c.gridx++;
		c.weightx = 1;

		clsName = UILib.getLinkedDropdown(CharacterClass.getAllClasses().stream().map(cls -> cls.getName()).toList(),
				Color.white, Color.black, () -> sp.getReqClassName(), (s) -> sp.setReqClassName(s));
		add(clsName, c);
		c.gridx++;

		armor = UILib.getLinkedDropdown(
				Arrays.asList(ArmorTraining.values()).stream().map(at -> at.toString()).toList(), Color.white,
				Color.black,
				() -> sp.getReqArmor() == null ? ArmorTraining.Light.toString() : sp.getReqArmor().toString(),
				(s) -> sp.setReqArmor(ArmorTraining.valueOf(s)));
		add(armor, c);
		c.gridx++;

		hwTraits = UILib.getLinkedTextField(Color.white, Color.black, () -> String.join(", ", sp.getReqHwTraits()),
				(s) -> Arrays.asList(s.split(",")).stream().map(s2 -> s2.trim()).toList());
		add(hwTraits, c);
		c.gridx++;

		abls = UILib.getLinkedTextField(Color.white, Color.black,
				() -> String.join(", ", sp.getReqAbilities().stream().map(a -> a.toString()).toList()),
				(s) -> sp.setReqAbilities(parseAbilities(s.split(","))));
		add(abls, c);
		c.gridx++;

		fName = UILib.getLinkedTextField(Color.white, Color.black, () -> sp.getReqFeatureName(),
				(s) -> sp.setReqFeatureName(s));
		add(fName, c);
		c.gridx++;

		val = UILib.getLinkedSpinner(new SpinnerNumberModel(0, 0, 30, 1), Color.white, Color.black,
				() -> sp.getReqVal(), (i) -> sp.setReqVal(i));
		add(val, c);
		c.gridx++;

		typeSel.updateValue();
	}

	private void updateType(String type)
	{
		sp.setType(type);

		clsName.setVisible(false);
		armor.setVisible(false);
		hwTraits.setVisible(false);
		abls.setVisible(false);
		fName.setVisible(false);
		val.setVisible(false);

		switch (type)
		{
			case Prereq.ablScore:
				abls.updateValue();
				abls.setVisible(true);
				val.updateValue();
				val.setVisible(true);
			break;
			case Prereq.armor:
				armor.updateValue();
				armor.setVisible(true);
			break;
			case Prereq.chrLvl:
				val.updateValue();
				val.setVisible(true);
			break;
			case Prereq.clsLvl:
				clsName.updateValue();
				clsName.setVisible(true);
				val.updateValue();
				val.setVisible(true);
			break;
			case Prereq.hTrait:
				hwTraits.updateValue();
				hwTraits.setVisible(true);
			break;
			case Prereq.pSel:
				fName.updateValue();
				fName.setVisible(true);
			break;
			case Prereq.spellcast: // No settings
			break;
		}
	}

	@Override
	public void setSelected(Customizable sel)
	{
	}

	@Override
	protected String getItemName()
	{
		return "";
	}

	@Override
	protected void setItemName(String s)
	{
	}
}
