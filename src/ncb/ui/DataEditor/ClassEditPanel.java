package ncb.ui.DataEditor;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ncb.data.ClassEquipment;
import ncb.data.ClassSpells;
import ncb.data.Feature;
import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.Skill;
import ncb.data.loadables.CharacterClass;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;

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
	private final JTextArea knownSpells = new JTextArea(2, 20);
	private final JTextArea spellSlots = new JTextArea(2, 20);

	private final JButton addFeature = new JButton("Add Class Feature");
	private final JPanel featuresPanel = new NoHorizontalScrollPanel();

	public ClassEditPanel()
	{
		super();

		c.weighty = 1;
		desc.setLineWrap(true);
		desc.setWrapStyleWord(true);
		desc.addFocusListener(UILib.createFocusListener(() -> updateDesc()));
		UILib.addLabeledComponent(this, "Desc: ", desc, c).setForeground(Color.black);
		c.gridy++;
		c.weighty = 0;

		primary.addFocusListener(UILib.createFocusListener(() -> updatePrimary()));
		UILib.addLabeledComponent(this, "Primary Ability: ", primary, c).setForeground(Color.black);
		c.gridy++;

		hd.addChangeListener(this);
		((JSpinner.DefaultEditor) hd.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "HD: ", hd, c).setForeground(Color.black);
		c.gridy++;

		saves.addFocusListener(UILib.createFocusListener(() -> updateSaves()));
		UILib.addLabeledComponent(this, "Save Profs: ", saves, c).setForeground(Color.black);
		c.gridy++;

		skills.addFocusListener(UILib.createFocusListener(() -> updateSkillOpts()));
		UILib.addLabeledComponent(this, "Skill Options: ", skills, c).setForeground(Color.black);
		c.gridy++;

		skillCount.addChangeListener(this);
		((JSpinner.DefaultEditor) skillCount.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		UILib.addLabeledComponent(this, "Skill Count: ", skillCount, c).setForeground(Color.black);
		c.gridy++;

		weps.addFocusListener(UILib.createFocusListener(() -> updateWeapons()));
		UILib.addLabeledComponent(this, "Weapons Profs: ", weps, c).setForeground(Color.black);
		c.gridy++;

		tools.addFocusListener(UILib.createFocusListener(() -> updateTools()));
		UILib.addLabeledComponent(this, "Tool Profs: ", tools, c).setForeground(Color.black);
		c.gridy++;

		armor.addFocusListener(UILib.createFocusListener(() -> updateArmor()));
		UILib.addLabeledComponent(this, "Armor Trainings: ", armor, c).setForeground(Color.black);
		c.gridy++;

		c.weighty = 1;
		equip.setWrapStyleWord(true);
		equip.setLineWrap(true);
		equip.addFocusListener(UILib.createFocusListener(() -> updateEquipment()));
		UILib.addLabeledComponent(this, "Equipment: ", equip, c).setForeground(Color.black);
		c.gridy++;

		knownSpells.setWrapStyleWord(true);
		knownSpells.setLineWrap(true);
		knownSpells.addFocusListener(UILib.createFocusListener(() -> updateKnownSpells()));
		UILib.addLabeledComponent(this, "Known Spells/Lvl: ", knownSpells, c).setForeground(Color.black);
		c.gridy++;

		spellSlots.setWrapStyleWord(true);
		spellSlots.setLineWrap(true);
		spellSlots.addFocusListener(UILib.createFocusListener(() -> updateSpellSlots()));
		UILib.addLabeledComponent(this, "Spell Slots/Lvl: ", spellSlots, c).setForeground(Color.black);
		c.gridy++;

		addFeature.addActionListener(this);
		add(addFeature, c);
		c.gridy++;
		featuresPanel.setLayout(new GridBagLayout());
		add(featuresPanel, c);
		c.gridy++;

		setVisible(false);
	}

	@Override
	protected void updateSelection()
	{
		cls = CharacterClass.getById(id);

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
		List<String> knownStrings = new ArrayList<>();
		if (cls.getClassSpells() != null)
		{
			if (cls.getClassSpells().getKnownStyle() == ClassSpells.FLAT)
			{
				Map<Integer, Integer> cantrips = cls.getClassSpells().getAllFlatCantrips();
				Map<Integer, Integer> knowns = cls.getClassSpells().getAllFlatKnown();
				for (int i = 1; i <= 20; i++)
				{
					int cCount = cantrips.containsKey(i) ? cantrips.get(i) : 0;
					int sCount = knowns.containsKey(i) ? knowns.get(i) : 0;

					if (cCount > 0 || sCount > 0)
					{
						knownStrings.add(i + "-" + cCount + "-" + sCount);
					}
				}
			}
			else
			{
				Map<Integer, Integer> cantrips = cls.getClassSpells().getAllGainCantrips();
				Map<Integer, Integer> knowns = cls.getClassSpells().getAllGainKnown();
				for (int i = 1; i <= 20; i++)
				{
					int cCount = cantrips.containsKey(i) ? cantrips.get(i) : 0;
					int sCount = knowns.containsKey(i) ? knowns.get(i) : 0;

					if (cCount > 0 || sCount > 0)
					{
						knownStrings.add(i + "+" + cCount + "+" + sCount);
					}
				}
			}
		}
		knownSpells.setText(String.join(", ", knownStrings));
		List<String> slotStrings = new ArrayList<>();
		if (cls.getClassSpells() != null)
		{
			if (cls.getClassSpells().getSlotsStyle() == ClassSpells.FLAT)
			{
				// If we use entryset we get random order, want these ordered for easy reading
				Map<Integer, Map<Integer, Integer>> fslots = cls.getClassSpells().getAllFlatSlots();
				List<Integer> orderedSlotClsLvls = new ArrayList<>(fslots.keySet());
				orderedSlotClsLvls.sort(Integer::compareTo);
				for (int charLvl : orderedSlotClsLvls)
				{
					int spellLvl = 0;
					int count = 0;
					for (Map.Entry<Integer, Integer> spellLevelToCount : fslots.get(charLvl).entrySet())
					{
						spellLvl = spellLevelToCount.getKey();
						count = spellLevelToCount.getValue();
					}
					slotStrings.add(charLvl + "-" + spellLvl + "-" + count);
				}
			}
			else
			{
				// If we use entryset we get random order, want these ordered for easy reading
				Map<Integer, Map<Integer, Integer>> fslots = cls.getClassSpells().getAllFlatSlots();
				List<Integer> orderedSlotClsLvls = new ArrayList<>(fslots.keySet());
				orderedSlotClsLvls.sort(Integer::compareTo);
				for (int charLvl : orderedSlotClsLvls)
				{
					List<String> slots = new ArrayList<>();
					for (Map.Entry<Integer, Integer> slotsBySpellLvl : fslots.get(charLvl).entrySet())
					{
						slots.add(slotsBySpellLvl.getKey() + "-" + slotsBySpellLvl.getValue());
					}
					slotStrings.add(charLvl + " [" + String.join(" | ", slots) + "]");
				}
			}
		}
		spellSlots.setText(String.join(", ", slotStrings));

		updateFeaturePanels();

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
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(hd))
		{
			cls.setHd((int) hd.getValue());
		}
		else if (e.getSource().equals(skillCount))
		{
			cls.setSkillSelectionCount((int) skillCount.getValue());
		}
	}

	@Override
	protected void clearSelectedCustom()
	{
		if (cls != null)
		{
			cls.setCustom(false);
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
			ClassFeatureEditPanel cfep = new ClassFeatureEditPanel(cf);
			cfep.updateSelection();
			featuresPanel.add(cfep, c);
			c.gridy++;
		}
		featuresPanel.revalidate();
	}

	@Override
	protected void updateName()
	{
		cls.setName(nameField.getText());
	}

	private void updateDesc()
	{
		cls.setDesc(desc.getText());
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
			cls.setPrimaryAbilities(abilities);
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
		showErrorMessage(invalid, "skills");
	}

	private void updateWeapons()
	{
		cls.setWeaponProfs(Arrays.asList(weps.getText().split(",")).stream().map(s -> s.trim()).toList());
	}

	private void updateTools()
	{
		cls.setToolProfs(Arrays.asList(tools.getText().split(",")).stream().map(s -> s.trim()).toList());
	}

	private void updateArmor()
	{
		String[] armorNames = armor.getText().split(",");
		List<ArmorTraining> armor = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String an : armorNames)
		{
			if (an.isBlank())
			{
				continue;
			}
			try
			{
				armor.add(ArmorTraining.valueOf(an.trim()));
			}
			catch (Exception e)
			{
				invalid.add(an);
			}
		}
		cls.setArmorProfs(armor);
		showErrorMessage(invalid, "armor trainings");
	}

	private void updateEquipment()
	{
		String[] equipmentOptions = equip.getText().split("\\|");
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

	private void updateSpellSlots()
	{
		String[] slotLevels = spellSlots.getText().split(",");
		Map<Integer, Map<Integer, Integer>> slotsByLevel = new HashMap<>();
		boolean fullStyle = spellSlots.getText().contains("[");
		List<String> invalid = new ArrayList<>();
		for (String sl : slotLevels)
		{
			if (sl.isBlank())
			{
				continue;
			}
			try
			{
				if (fullStyle)
				{
					if (!sl.contains("["))
					{
						invalid.add(sl);
						continue;
					}
					int clsLvl = Integer.parseInt(sl.substring(0, sl.indexOf('[')).trim());

					Map<Integer, Integer> slots = new HashMap<>();
					String slotStr = sl.substring(sl.indexOf('[') + 1, sl.indexOf(']'));
					String[] splitSlots = slotStr.split("\\|");

					for (String slot : splitSlots)
					{
						String[] splitSlot = slot.split("-");
						if (splitSlot.length != 2)
						{
							invalid.add(sl);
							continue;
						}
						slots.put(Integer.parseInt(splitSlot[0].trim()), Integer.parseInt(splitSlot[1].trim()));
					}

					slotsByLevel.put(clsLvl, slots);
				}
				else
				{
					String[] split = sl.split("-");
					if (split.length != 3)
					{
						invalid.add(sl);
						continue;
					}
					int charLvl = Integer.parseInt(split[0].trim());
					int spLvl = Integer.parseInt(split[1].trim());
					int count = Integer.parseInt(split[2].trim());
					Map<Integer, Integer> spLvlToCount = new HashMap<>();
					spLvlToCount.put(spLvl, count);
					slotsByLevel.put(charLvl, spLvlToCount);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				invalid.add(sl);
			}
		}
		if (fullStyle)
		{
			cls.getClassSpells().setAllFullSlots(slotsByLevel);
		}
		else
		{
			cls.getClassSpells().setAllFlatSlots(slotsByLevel);
		}
		showErrorMessage(invalid, "spell slots");
	}

	private void updateKnownSpells()
	{
		String[] knownLevels = knownSpells.getText().split(",");
		Map<Integer, Integer> cantripsByLvl = new HashMap<>();
		Map<Integer, Integer> spellsByLvl = new HashMap<>();
		boolean flatStyle = knownSpells.getText().contains("-");
		List<String> invalid = new ArrayList<>();
		for (String kl : knownLevels)
		{
			if (kl.isBlank())
			{
				continue;
			}
			try
			{
				String[] split = kl.split(flatStyle ? "-" : "\\+");
				if (split.length != 3)
				{
					invalid.add(kl);
					continue;
				}
				int lvl = Integer.parseInt(split[0].trim());
				int cCount = Integer.parseInt(split[1].trim());
				int sCount = Integer.parseInt(split[2].trim());
				if (cCount > 0)
				{
					cantripsByLvl.put(lvl, cCount);
				}
				if (sCount > 0)
				{
					spellsByLvl.put(lvl, sCount);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				invalid.add(kl);
			}
		}
		if (flatStyle)
		{
			cls.getClassSpells().setAllFlatCantrips(cantripsByLvl);
			cls.getClassSpells().setAllFlatKnown(spellsByLvl);
		}
		else
		{
			cls.getClassSpells().setAllGainCantrips(cantripsByLvl);
			cls.getClassSpells().setAllGainKnown(spellsByLvl);
		}
		showErrorMessage(invalid, "known spells");
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
