package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.ui.FlatBorder;

import ncb.data.enums.ArmorTraining;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.HasAssociatedClass;
import ncb.ui.ListensForChanges;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

public class MulticlassTraitsPanel extends JPanel implements ListensForChanges, HasAssociatedClass
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;
	private final int classIndex;

	private final PrimaryAbilityPanel primaryAbilityPanel;
	private final JLabel HD;
	private final MulticlassSkillSelector skills;
	private final JPanel weaponsPanel = new NoHorizontalScrollPanel();
	private final JLabel weapons;
	private final JPanel toolsPanel = new NoHorizontalScrollPanel();
	private final JLabel tools;
	private final JPanel armorPanel = new NoHorizontalScrollPanel();
	private final JLabel armor;

	public MulticlassTraitsPanel(CharacterSheet sheet, int multiclassIndex)
	{
		this.sheet = sheet;
		this.classIndex = multiclassIndex;

		setBorder(new FlatBorder());
		setLayout(new GridBagLayout());
		setOpaque(false);
		GridBagConstraints c = UILib.getStandardGBC();
		c.insets = new Insets(5, 5, 5, 5);
		PropertyListener.listenForChanges(PropertyListener.CLASS, this);

		primaryAbilityPanel = new PrimaryAbilityPanel(sheet, classIndex);
		add(primaryAbilityPanel, c);
		c.gridy++;

		HD = UILib.getLabel("");
		UILib.addLabeledComponent(this, "Hit Point Die: ", HD, c);
		c.gridy++;

		skills = new MulticlassSkillSelector(sheet, classIndex);
		add(skills, c);
		c.gridy++;

		weaponsPanel.setLayout(new GridLayout(1, 1));
		weapons = UILib.getLabel("");
		UILib.addLabeledComponent(weaponsPanel, "Weapon Proficiencies: ", weapons);
		add(weaponsPanel, c);
		c.gridy++;

		toolsPanel.setLayout(new GridLayout(1, 1));
		tools = UILib.getLabel("");
		UILib.addLabeledComponent(toolsPanel, "Tool Proficiencies: ", tools);
		add(toolsPanel, c);
		c.gridy++;

		armorPanel.setLayout(new GridLayout(1, 1));
		armor = UILib.getLabel("");
		UILib.addLabeledComponent(armorPanel, "Armor Training: ", armor);
		add(armorPanel, c);
		c.gridy++;

		c.weighty = 1;
		add(new JPanel(), c);

		primaryAbilityPanel.updateProperty(PropertyListener.CLASS);
		skills.updateProperty(PropertyListener.CLASS);
	}

	@Override
	public void updateProperty(String prop)
	{
		HD.setText("D" + getAssociatedClass().getHd());
		List<String> weaponProfs = getAssociatedClass().getMulticlassFeature().getWeaponProfs();
		if (weaponProfs.isEmpty())
		{
			weaponsPanel.setVisible(false);
		}
		else
		{
			weaponsPanel.setVisible(true);
			weapons.setText(String.join(" and ", weaponProfs));
		}
		List<String> toolProfs = getAssociatedClass().getToolProfs();
		if (toolProfs.isEmpty())
		{
			toolsPanel.setVisible(false);
		}
		else
		{
			toolsPanel.setVisible(true);
			tools.setText(String.join(" and ", toolProfs));
		}
		List<ArmorTraining> armorProfs = getAssociatedClass().getArmorProfs();
		if (armorProfs.isEmpty())
		{
			armorPanel.setVisible(false);
		}
		else
		{
			armorPanel.setVisible(true);
			armor.setText(String.join(" and ", armorProfs.stream().map(ap -> ap.name()).toList()));
		}
	}

	@Override
	public int getClassIndex()
	{
		return classIndex;
	}

	@Override
	public CharacterSheet getSheet()
	{
		return sheet;
	}
}
