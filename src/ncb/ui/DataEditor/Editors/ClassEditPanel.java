package ncb.ui.DataEditor.Editors;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import ncb.data.ClassEquipment;
import ncb.data.Feature;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.CharacterClass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class ClassEditPanel extends EditPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private CharacterClass cls;

	@Override
	protected String getItemName()
	{
		return cls.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		cls.setName(s);
	}

	private final JButton addFeature = new JButton("Add Class Feature");
	private final ClassSpellcastingEditPanel spellcastingPanel = new ClassSpellcastingEditPanel();
	private final JPanel featuresPanel = new NoHorizontalScrollPanel();

	public ClassEditPanel()
	{
		super();

		c.weighty = 1;
		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Desc: ", c, Color.white, Color.black,
				() -> cls.getDesc(), (s) -> cls.setDesc(s)));
		c.gridy++;
		c.weighty = 0;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Primary Ability: ", c, Color.white, Color.black,
				() -> getPrimary(), (s) -> updatePrimary(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedSpinner(this, "HD: ", c, new SpinnerNumberModel(8, 4, 12, 2),
				Color.white, Color.black, () -> cls.getHd(), (i) -> cls.setHd(i)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Save Profs: ", c, Color.white, Color.black,
				() -> getSaves(), (s) -> updateSaves(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Skill Options: ", c, Color.white, Color.black,
				() -> getSkillOpts(), (s) -> updateSkillOpts(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedSpinner(this, "Skill Count: ", c, new SpinnerNumberModel(2, 1, 4, 1),
				Color.white, Color.black, () -> cls.getSkillSelectionCount(), (i) -> cls.setSkillSelectionCount(i)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Weapon Profs: ", c, Color.white, Color.black,
				() -> getWeapons(), (s) -> updateWeapons(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Tool Profs: ", c, Color.white, Color.black,
				() -> getTools(), (s) -> updateTools(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(this, "Armor Training: ", c, Color.white, Color.black,
				() -> getArmor(), (s) -> updateArmor(s)));
		c.gridy++;

		c.weighty = 1;
		linkedProperties.add(UILib.addLabeledLinkedTextArea(this, 2, "Equipment: ", c, Color.white, Color.black,
				() -> getEquipment(), (s) -> updateEquipment(s)));
		c.gridy++;

		add(spellcastingPanel, c);
		c.gridy++;

		UILib.addLabel(this, "Features:", c, Color.black);
		c.gridy++;

		featuresPanel.setLayout(new GridBagLayout());
		add(featuresPanel, c);
		c.gridy++;

		addFeature.addActionListener(this);
		add(addFeature, c);
		c.gridy++;

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		cls = (CharacterClass) sel;

		linkedProperties.forEach(p -> p.updateValue());

		updateFeaturePanels();
		spellcastingPanel.updatePanel(cls);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(addFeature))
		{
			String name = JOptionPane.showInputDialog(null, "Name?:", "New Class Feature",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank())
			{
				cls.addClassFeature(name);
				updateFeaturePanels();
			}
		}
		else if (e.getActionCommand().startsWith("Delete"))
		{
			int fId = Integer.parseInt(e.getActionCommand().split(" ")[1].trim());
			cls.removeClassFeature(fId);
			updateFeaturePanels();
		}
	}

	private void updateFeaturePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		featuresPanel.removeAll();
		for (Feature cf : cls.getAllClassFeatures())
		{
			FeatureEditPanel cfep = new FeatureEditPanel(cf, true, this);
			featuresPanel.add(cfep, c);
			c.gridy++;
		}
		featuresPanel.revalidate();
	}

	private String getPrimary()
	{
		return cls.getPrimaryAbilities().isEmpty()
				? String.join(" or ", cls.getPrimaryAbilityOptions().stream().map(a -> a.toString()).toList())
				: String.join(" and ", cls.getPrimaryAbilities().stream().map(a -> a.toString()).toList());
	}

	private void updatePrimary(String primaryText)
	{
		if (primaryText.toLowerCase().contains("or"))
		{
			cls.setPrimaryAbilityOptions(parseAbilities(primaryText.split("or")));
		}
		else
		{
			cls.setPrimaryAbilities(parseAbilities(primaryText.split("and")));
		}
	}

	private String getSaves()
	{
		return String.join(", ", cls.getSaveProfs().stream().map(a -> a.toString()).toList());
	}

	private void updateSaves(String s)
	{
		cls.setSaveProfs(parseAbilities(s.split(",")));
	}

	private String getSkillOpts()
	{
		return String.join(", ", cls.getSkillSelectionOptions().stream().map(s -> s.toString()).toList());
	}

	private void updateSkillOpts(String s)
	{
		cls.setSkillSelectionOptions(parseSkills(s.split(",")));
	}

	private String getWeapons()
	{
		return String.join(", ", cls.getWeaponProfs());
	}

	private void updateWeapons(String s)
	{
		cls.setWeaponProfs(Arrays.asList(s.split(",")).stream().map(s2 -> s2.trim()).toList());
	}

	private String getTools()
	{
		return String.join(", ", cls.getToolProfs());
	}

	private void updateTools(String s)
	{
		cls.setToolProfs(Arrays.asList(s.split(",")).stream().map(s2 -> s2.trim()).toList());
	}

	private String getArmor()
	{
		return String.join(", ", cls.getArmorProfs().stream().map(a -> a.toString()).toList());
	}

	private void updateArmor(String s)
	{
		cls.setArmorProfs(parseArmor(s.split(",")));
	}

	private String getEquipment()
	{
		List<String> equipStrings = new ArrayList<>();
		for (ClassEquipment ce : cls.getEquipmentOptions())
		{
			if (ce.getItems().isBlank())
			{
				equipStrings.add("Notes: " + ce.getNotes());
			}
			else
			{
				equipStrings.add(ce.getItems() + " / Notes: " + ce.getNotes());
			}
		}
		return String.join(" | ", equipStrings);
	}

	private void updateEquipment(String s)
	{
		String[] equipmentOptions = s.split("\\|");
		List<ClassEquipment> equips = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String eo : equipmentOptions)
		{
			if (eo.isBlank())
			{
				continue;
			}
			try
			{
				if (!eo.contains("Notes"))
				{
					invalid.add(eo);
					continue;
				}
				String items = "";
				int notes = 0;
				if (eo.contains("/"))
				{
					String[] split = eo.split("/");
					if (split.length != 2)
					{
						invalid.add(eo);
						continue;
					}
					items = split[0].trim();
					String notesString = split[1];
					if (!notesString.contains("Notes"))
					{
						invalid.add(eo);
						continue;
					}
					notes = Integer.parseInt(notesString.replace("Notes:", "").trim());
				}
				else
				{
					notes = Integer.parseInt(eo.replace("Notes:", "").trim());
				}
				ClassEquipment ce = new ClassEquipment();
				ce.setItems(items);
				ce.setNotes(notes);
				equips.add(ce);
			}
			catch (Exception e)
			{
				e.printStackTrace();
				invalid.add(eo);
			}
		}
		cls.setEquipmentOptions(equips);
		showErrorMessage(invalid, "equipment options");
	}
}
