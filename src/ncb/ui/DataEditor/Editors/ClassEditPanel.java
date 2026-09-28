package ncb.ui.DataEditor.Editors;

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
import javax.swing.SpinnerNumberModel;

import ncb.data.ClassEquipment;
import ncb.data.ClassSpells;
import ncb.data.Feature;
import ncb.data.enums.Ability;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.CharacterClass;
import ncb.ui.CollapsablePanel;
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

		CollapsablePanel spellsPanel = new CollapsablePanel(true);
		spellsPanel.bodyPanel.setLayout(new GridBagLayout());
		UILib.addLabel(spellsPanel.headerPanel, "Spellcasting Settings", Color.black);
		add(spellsPanel, c);
		c.gridy++;

		UILib.addLabel(this, "Features:", c, Color.black);
		c.gridy++;

		featuresPanel.setLayout(new GridBagLayout());
		add(featuresPanel, c);
		c.gridy++;

		addFeature.addActionListener(this);
		add(addFeature, c);
		c.gridy++;

		c.gridy = 0;
		linkedProperties.add(UILib.addLabeledLinkedDropdown(spellsPanel.bodyPanel,
				List.of("", Ability.Int.toString(), Ability.Wis.toString(), Ability.Cha.toString(),
						Ability.Primary.toString()),
				"Spellcasting Ability: ", c, Color.white, Color.black, () -> getSpellcastingAbility(),
				(s) -> updateSpellcastingAbility(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(spellsPanel.bodyPanel, "Known Spells/Lvl: ", c,
				Color.white, Color.black, () -> getKnownSpells(), (s) -> updateKnownSpells(s)));
		c.gridy++;

		linkedProperties.add(UILib.addLabeledLinkedTextField(spellsPanel.bodyPanel, "Spells Slots/Lvl: ", c,
				Color.white, Color.black, () -> getSpellSlots(), (s) -> updateSpellSlots(s)));

		setVisible(false);
	}

	@Override
	public void setSelected(Customizable sel)
	{
		cls = (CharacterClass) sel;

		linkedProperties.forEach(p -> p.updateValue());

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

	private String getSpellcastingAbility()
	{
		return cls.getSpellcastingAbility() == null ? "" : cls.getSpellcastingAbility().toString();
	}

	private void updateSpellcastingAbility(String abl)
	{
		cls.setSpellcastingAbility(abl.isBlank() ? null : Ability.valueOf(abl));
	}

	private String getSpellSlots()
	{
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
		return String.join(", ", slotStrings);
	}

	private void updateSpellSlots(String s)
	{
		String[] slotLevels = s.split(",");
		Map<Integer, Map<Integer, Integer>> slotsByLevel = new HashMap<>();
		boolean fullStyle = s.contains("[");
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

	private String getKnownSpells()
	{
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
		return String.join(", ", knownStrings);
	}

	private void updateKnownSpells(String s)
	{
		String[] knownLevels = s.split(",");
		Map<Integer, Integer> cantripsByLvl = new HashMap<>();
		Map<Integer, Integer> spellsByLvl = new HashMap<>();
		boolean flatStyle = s.contains("-");
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
}
