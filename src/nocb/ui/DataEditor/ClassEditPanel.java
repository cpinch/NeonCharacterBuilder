package nocb.ui.DataEditor;

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
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import nocb.data.Ability;
import nocb.data.ArmorProf;
import nocb.data.CharacterClass;
import nocb.data.ClassEquipment;
import nocb.data.ClassFeature;
import nocb.data.Selectable;
import nocb.data.Skill;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class ClassEditPanel extends EditPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private CharacterClass cls;

	private final JTextArea desc = new JTextArea(2, 20);
	private final JTextField primary = new JTextField(20);
	private final JSpinner hd = new JSpinner(new SpinnerNumberModel(8, 4, 12, 2));
	private final JTextField saves = new JTextField(20);
	private final JTextField skills = new JTextField(20);
	private final JSpinner skillCount = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
	private final JTextField weps = new JTextField(20);
	private final JTextField tools = new JTextField(20);
	private final JTextField armor = new JTextField(20);
	private final JTextArea equip = new JTextArea(3, 20);

	private final JButton addFeature = new JButton("Add Class Feature");
	private final JPanel featuresPanel = new NoHorizontalScrollPanel();

	private final JButton addSelectable = new JButton("Add Selectable");
	private final JPanel selectablesPanel = new NoHorizontalScrollPanel();

	public ClassEditPanel()
	{
		super();

		c.weighty = 1;
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.addFocusListener(UILib.createFocusListener(() -> updateDesc()));
		UILib.addLabeledComponent(this, "Desc: ", desc, c);
		c.gridy++;
		c.weighty = 0;

		primary.addFocusListener(UILib.createFocusListener(() -> updatePrimary()));
		UILib.addLabeledComponent(this, "Primary Ability: ", primary, c);
		c.gridy++;

		hd.addChangeListener(this);
		((JSpinner.DefaultEditor) hd.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "HD: ", hd, c);
		c.gridy++;

		saves.addFocusListener(UILib.createFocusListener(() -> updateSaves()));
		UILib.addLabeledComponent(this, "Save Profs: ", saves, c);
		c.gridy++;

		skills.addFocusListener(UILib.createFocusListener(() -> updateSkillOpts()));
		UILib.addLabeledComponent(this, "Skill Options: ", skills, c);
		c.gridy++;

		skillCount.addChangeListener(this);
		((JSpinner.DefaultEditor) skillCount.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "Skill Count: ", skillCount, c);
		c.gridy++;

		weps.addFocusListener(UILib.createFocusListener(() -> updateWeapons()));
		UILib.addLabeledComponent(this, "Weapons Profs: ", weps, c);
		c.gridy++;

		tools.addFocusListener(UILib.createFocusListener(() -> updateTools()));
		UILib.addLabeledComponent(this, "Tool Profs: ", tools, c);
		c.gridy++;

		armor.addFocusListener(UILib.createFocusListener(() -> updateArmor()));
		UILib.addLabeledComponent(this, "Armor Trainings: ", armor, c);
		c.gridy++;

		c.weighty = 1;
		equip.setWrapStyleWord(true);
		equip.setLineWrap(true);
		equip.addFocusListener(UILib.createFocusListener(() -> updateEquipment()));
		UILib.addLabeledComponent(this, "Equipment: ", equip, c);
		c.gridy++;

		addFeature.addActionListener(this);
		add(addFeature, c);
		c.gridy++;
		featuresPanel.setLayout(new GridBagLayout());
		add(featuresPanel, c);
		c.gridy++;

		addSelectable.addActionListener(this);
		add(addSelectable, c);
		c.gridy++;
		selectablesPanel.setLayout(new GridBagLayout());
		add(selectablesPanel, c);

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		cls = CharacterClass.getById(id);

		System.out.println("Got class " + cls);

		nameField.setText(cls.getName());
		desc.setText(cls.getDesc());
		primary.setText(cls.getPrimaryAbilities().isEmpty()
				? String.join(" or ", cls.getPrimaryAbilityOptions().stream().map(a -> a.toString()).toList())
				: String.join(" and ", cls.getPrimaryAbilities().stream().map(a -> a.toString()).toList()));
		hd.setValue(cls.getHd());
		saves.setText(String.join(", ", cls.getSaveProfs().stream().map(a -> a.toString()).toList()));
		skills.setText(String.join(", ", cls.getSkillSelectionOptions().stream().map(s -> s.toString()).toList()));
		skillCount.setValue(cls.getSkillSelectionCount());
		weps.setText(String.join(", ", cls.getWeaponProfs()));
		tools.setText(String.join(", ", cls.getToolProfs()));
		armor.setText(String.join(", ", cls.getArmorProfs().stream().map(a -> a.toString()).toList()));
		List<String> equipStrings = new ArrayList<>();
		for (ClassEquipment ce : cls.getEquipmentOptions())
		{
			if (!ce.getItems().isBlank())
			{
				equipStrings.add(ce.getItems() + " / Notes: " + ce.getNotes());
			}
			else
			{
				equipStrings.add("Notes: " + ce.getNotes());
			}
		}
		equip.setText(String.join(" | ", equipStrings));

		updateFeaturePanels();
		updateSelectablePanels();

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
		else if (e.getSource().equals(addSelectable))
		{
			String type = JOptionPane.showInputDialog(null, "Selectable Type Name?:", "New Class Selectable",
					JOptionPane.QUESTION_MESSAGE);
			String name = JOptionPane.showInputDialog(null, "This Option Name?:", "New Class Selectable",
					JOptionPane.QUESTION_MESSAGE);

			if (!name.isBlank() && !type.isBlank())
			{
				Selectable.addNewSelectable(name, type);
				updateSelectablePanels();
			}
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(hd))
		{
			cls.setHD((int) hd.getValue());
		}
		else if (e.getSource().equals(skillCount))
		{
			cls.setSkillSelectionCount((int) skillCount.getValue());
		}
	}

	private void updateFeaturePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		featuresPanel.removeAll();
		for (ClassFeature cf : cls.getClassFeatures())
		{
			ClassFeatureEditPanel cfep = new ClassFeatureEditPanel(cf);
			cfep.updateSelection();
			featuresPanel.add(cfep, c);
			c.gridy++;
		}
		featuresPanel.revalidate();
	}

	private void updateSelectablePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		c.weighty = 1;
		c.ipady = 20;
		selectablesPanel.removeAll();
		List<String> selectableNames = new ArrayList<>();
		for (ClassFeature cf : cls.getClassFeatures())
		{
			if (!cf.getSelectableName().isBlank())
			{
				selectableNames.add(cf.getSelectableName());
			}
		}
		for (String selectableName : selectableNames)
		{
			for (Selectable s : Selectable.getSelectablesByType(selectableName))
			{
				SelectableEditPanel sfep = new SelectableEditPanel();
				sfep.setSelectedId(s.getId());
				sfep.updateSelection();
				selectablesPanel.add(sfep, c);
				c.gridy++;
			}
		}
		selectablesPanel.revalidate();
	}

	@Override
	protected void updateName()
	{
		System.out.println("Setting class name to " + nameField.getText());
		cls.setName(nameField.getText());
		cls.setCustom(true);
	}

	private void updateDesc()
	{
		cls.setDesc(desc.getText());
		cls.setCustom(true);
	}

	private void updatePrimary()
	{
		String primaryText = primary.getText();
		List<Ability> abilities = new ArrayList<>();
		List<String> invalid = new ArrayList<>();

		if (primaryText.toLowerCase().contains("or"))
		{
			String[] ablOs = primaryText.split("or");
			for (String an : ablOs)
			{
				if (an.isBlank())
				{
					continue;
				}
				try
				{
					abilities.add(Ability.valueOf(an.trim()));
				}
				catch (Exception e)
				{
					invalid.add(an);
				}
			}
			cls.setPrimaryAbilityOptions(abilities);
		}
		else
		{
			String[] abls = primaryText.split("and");
			for (String an : abls)
			{
				if (an.isBlank())
				{
					continue;
				}
				try
				{
					abilities.add(Ability.valueOf(an.trim()));
				}
				catch (Exception e)
				{
					invalid.add(an);
				}
			}
			cls.setSaveProfs(abilities);
			cls.setCustom(true);
		}
		showErrorMessage(invalid, "abilities");
	}

	private void updateSaves()
	{
		String[] abilityNames = saves.getText().split(",");
		List<Ability> abilities = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String an : abilityNames)
		{
			if (an.isBlank())
			{
				continue;
			}
			try
			{
				abilities.add(Ability.valueOf(an.trim()));
			}
			catch (Exception e)
			{
				invalid.add(an);
			}
		}
		cls.setSaveProfs(abilities);
		cls.setCustom(true);
		showErrorMessage(invalid, "abilities");
	}

	private void updateSkillOpts()
	{
		String[] skillNames = skills.getText().split(",");
		List<Skill> skills = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String sn : skillNames)
		{
			if (sn.isBlank())
			{
				continue;
			}
			try
			{
				skills.add(Skill.skillByName(sn.trim()));
			}
			catch (Exception e)
			{
				invalid.add(sn);
			}
		}
		cls.setSkillSelectionOptions(skills);
		cls.setCustom(true);
		showErrorMessage(invalid, "skills");
	}

	private void updateWeapons()
	{
		cls.setWeaponProfs(Arrays.asList(weps.getText().split(",")).stream().map(s -> s.trim()).toList());
		cls.setCustom(true);
	}

	private void updateTools()
	{
		cls.setToolProfs(Arrays.asList(tools.getText().split(",")).stream().map(s -> s.trim()).toList());
		cls.setCustom(true);
	}

	private void updateArmor()
	{
		String[] armorNames = armor.getText().split(",");
		List<ArmorProf> armor = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String an : armorNames)
		{
			if (an.isBlank())
			{
				continue;
			}
			try
			{
				armor.add(ArmorProf.valueOf(an.trim()));
			}
			catch (Exception e)
			{
				invalid.add(an);
			}
		}
		cls.setArmorProfs(armor);
		cls.setCustom(true);
		showErrorMessage(invalid, "armor trainings");
	}

	private void updateEquipment()
	{
		String[] equipmentOptions = armor.getText().split("|");
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
					notes = Integer.parseInt(notesString.replace("Notes", "").trim());
				}
				else
				{
					notes = Integer.parseInt(eo.replace("Notes", "").trim());
				}
				ClassEquipment ce = new ClassEquipment(items, notes);
				equips.add(ce);
			}
			catch (Exception e)
			{
				invalid.add(eo);
			}
		}
		cls.setEquipmentOptions(equips);
		cls.setCustom(true);
		showErrorMessage(invalid, "equipment options");
	}

	private void showErrorMessage(List<String> invalid, String type)
	{
		if (!invalid.isEmpty())
		{
			JOptionPane.showMessageDialog(this, String.join(", ", invalid) + " were not valid " + type + ".",
					"Invalid Entries", JOptionPane.ERROR_MESSAGE);
		}
	}
}
