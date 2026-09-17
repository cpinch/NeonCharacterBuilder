package nocb.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.ArmorProf;
import nocb.main.CharacterSheet;

public class ClassTraitsPanel extends JPanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final PrimaryAbilityPanel primaryAbilityPanel;
	private final JLabel HD = UILib.getLabel("");
	private final JLabel saves = UILib.getLabel("");
	private final ClassSkillSelector skills;
	private final JLabel weapons = UILib.getLabel("");
	private final JPanel toolsPanel = new JPanel();
	private final JLabel tools = UILib.getLabel("");
	private final JPanel armorPanel = new JPanel();
	private final JLabel armor = UILib.getLabel("");
	private final EquipmentSelectionPanel equipmentSelectionPanel;

	public ClassTraitsPanel(CharacterSheet sheet, Runnable skillsUpdatedCallback)
	{
		this.sheet = sheet;

		setBorder(BorderFactory.createEtchedBorder());
		setLayout(new GridBagLayout());
		setBackground(VaporwaveColors.DARK_PURPLE);
		GridBagConstraints c = UILib.getStandardGBC();
		c.anchor = GridBagConstraints.WEST;

		primaryAbilityPanel = new PrimaryAbilityPanel(sheet);
		add(primaryAbilityPanel, c);
		c.gridy++;

		JLabel l = UILib.addLabeledComponent(this, "Hit Point Die:", HD, c);
		l.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l.setFont(UILib.boldFont);
		HD.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		HD.setFont(UILib.boldFont);
		c.gridy++;

		JLabel l2 = UILib.addLabeledComponent(this, "Save Proficiencies:", saves, c);
		l2.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l2.setFont(UILib.boldFont);
		saves.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		saves.setFont(UILib.boldFont);
		c.gridy++;

		skills = new ClassSkillSelector(sheet, skillsUpdatedCallback);
		add(skills, c);
		c.gridy++;

		JLabel l3 = UILib.addLabeledComponent(this, "Weapon Proficiencies: ", weapons, c);
		l3.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l3.setFont(UILib.boldFont);
		weapons.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		weapons.setFont(UILib.boldFont);
		c.gridy++;

		GridBagConstraints c2 = UILib.getStandardGBC();

		toolsPanel.setLayout(new BoxLayout(toolsPanel, BoxLayout.X_AXIS));
		toolsPanel.setOpaque(false);
		JLabel l4 = UILib.addLabeledComponent(toolsPanel, "Tool Proficiencies: ", tools, c2);
		l4.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l4.setFont(UILib.boldFont);
		tools.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		tools.setFont(UILib.boldFont);
		add(toolsPanel, c);
		c.gridy++;

		armorPanel.setLayout(new BoxLayout(armorPanel, BoxLayout.X_AXIS));
		armorPanel.setOpaque(false);
		JLabel l5 = UILib.addLabeledComponent(armorPanel, "Armor Training: ", armor, c2);
		l5.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		l5.setFont(UILib.boldFont);
		armor.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		armor.setFont(UILib.boldFont);
		add(armorPanel, c);
		c.gridy++;

		c.weighty = 1;
		equipmentSelectionPanel = new EquipmentSelectionPanel(sheet);
		add(equipmentSelectionPanel, c);
	}

	public void updateDetails()
	{
		primaryAbilityPanel.updateDetails();
		HD.setText("D" + sheet.getCharClass().getHd());
		saves.setText(String.join(" and ", sheet.getCharClass().getSaveProfs().stream().map(a -> a.name()).toList()));
		skills.updateDetails();
		weapons.setText(String.join(" and ", sheet.getCharClass().getWeaponProfs()));
		List<String> toolProfs = sheet.getCharClass().getToolProfs();
		if (toolProfs.isEmpty())
		{
			toolsPanel.setVisible(false);
		}
		else
		{
			toolsPanel.setVisible(true);
			tools.setText(String.join(" and ", toolProfs));
		}
		List<ArmorProf> armorProfs = sheet.getCharClass().getArmorProfs();
		if (armorProfs.isEmpty())
		{
			armorPanel.setVisible(false);
		}
		else
		{
			armorPanel.setVisible(true);
			armor.setText(String.join(" and ", armorProfs.stream().map(ap -> ap.name()).toList()));
		}
		equipmentSelectionPanel.updateDetails();
	}
}
