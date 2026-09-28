package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.enums.ArmorTraining;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class ClassTraitsPanel extends JPanel implements ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final PrimaryAbilityPanel primaryAbilityPanel;
	private final JLabel HD;
	private final JLabel saves;
	private final ClassSkillSelector skills;
	private final JLabel weapons;
	private final JLabel tools;
	private final JLabel armor;
	private final EquipmentSelectionPanel equipmentSelectionPanel;

	public ClassTraitsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);

		primaryAbilityPanel = new PrimaryAbilityPanel(sheet);
		add(primaryAbilityPanel, c);
		c.gridy++;

		HD = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Hit Point Die: ", HD, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		saves = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Save Proficiencies: ", saves, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		skills = new ClassSkillSelector(sheet);
		add(skills, c);
		c.gridy++;

		weapons = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Weapon Proficiencies: ", weapons, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		tools = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Tool Proficiencies: ", tools, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		armor = UILib.getLabel("", VaporwaveColors.HOT_PINK);
		UILib.addLabeledComponent(this, "Armor Training: ", armor, c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.weighty = 1;
		equipmentSelectionPanel = new EquipmentSelectionPanel(sheet);
		add(equipmentSelectionPanel, c);
	}

	@Override
	public void updateProperty(String prop)
	{
		HD.setText("D" + sheet.getCharClass().getHd());
		saves.setText(String.join(" and ", sheet.getCharClass().getSaveProfs().stream().map(a -> a.name()).toList()));
		weapons.setText(String.join(" and ", sheet.getCharClass().getWeaponProfs()));
		List<String> toolProfs = sheet.getCharClass().getToolProfs();
		if (toolProfs.isEmpty())
		{
			tools.setVisible(false);
		}
		else
		{
			tools.setVisible(true);
			tools.setText(String.join(" and ", toolProfs));
		}
		List<ArmorTraining> armorProfs = sheet.getCharClass().getArmorProfs();
		if (armorProfs.isEmpty())
		{
			armor.setVisible(false);
		}
		else
		{
			armor.setVisible(true);
			armor.setText(String.join(" and ", armorProfs.stream().map(ap -> ap.name()).toList()));
		}
	}
}
