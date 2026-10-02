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
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;

import org.apache.commons.lang3.function.BooleanConsumer;

import ncb.data.AbilityIncrease;
import ncb.data.Feature;
import ncb.data.SpellChoice;
import ncb.data.enums.Ability;
import ncb.data.enums.Skill;
import ncb.data.interfaces.Customizable;
import ncb.data.loadables.SpellList;
import ncb.ui.NoHorizontalScrollPanel;
import ncb.ui.UILib;
import ncb.ui.DataEditor.EditPanel;

public class FeatureEditPanel extends EditPanel implements ActionListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private static final String OPT_TEXT = "Text", OPT_G_SKILLS = "Gives Skill Profs",
			OPT_C_SKILLS = "Choose Skill Profs", OPT_G_SAVES = "Gives Save Profs", OPT_G_SPELLS = "Gives Spells",
			OPT_C_SPELLS = "Choose Spells", OPT_C_S_SPELLS = "Choose Specific Spells",
			OPT_G_ARMOR = "Gives Armor Train", OPT_G_LANGS = "Gives Languages", OPT_C_LANGS = "Choose Languages",
			OPT_G_RES = "Gives Resistances", OPT_R_BY_H = "Gives Resistances Based on Homeworld Traits",
			OPT_C_RES = "Choose Resistances", OPT_C_EXP = "Choose Skill Expertise", OPT_G_TOOLS = "Gives Tool Profs",
			OPT_G_WEPS = "Gives Weapon Profs", OPT_ABL_AC = "Sets AC Abilities",
			OPT_ABL_SKILLS = "Adds Extra Ability to Skills", OPT_SPD = "Increase Speed", OPT_HP_1 = "+HP Lvl 1 Only",
			OPT_HP_LVL = "+HP/Level", OPT_FEAT = "Choose Feat", OPT_SEL = "Choose Selectable",
			OPT_NOTES = "Puts Notes on Sheet", OPT_HALFPROF_ALL = "Gives Half Proficiency to all non-Proficient Skills",
			OPT_PROF_INIT = "Gives Proficiency to Initiative", OPT_ABL_INIT = "Use other Abilities for Initiative",
			OPT_C_SKILL_ABL_TO = "Choose Skill Prof and get extra Ability to it.",
			OPT_G_SKILL_EXP = "Gives Skills or Expertise", OPT_G_ABL = "Gives Ability Score Increases",
			OPT_C_ABL = "Choose Ability Score Increases";

	private static final List<String> options = List.of(OPT_TEXT, OPT_G_SKILLS, OPT_C_SKILLS, OPT_G_SAVES, OPT_G_SPELLS,
			OPT_C_SPELLS, OPT_C_S_SPELLS, OPT_G_ARMOR, OPT_G_LANGS, OPT_C_LANGS, OPT_G_RES, OPT_R_BY_H, OPT_C_RES,
			OPT_C_EXP, OPT_G_TOOLS, OPT_G_WEPS, OPT_ABL_AC, OPT_ABL_SKILLS, OPT_SPD, OPT_HP_1, OPT_HP_LVL,
			OPT_HALFPROF_ALL, OPT_PROF_INIT, OPT_ABL_INIT, OPT_C_SKILL_ABL_TO, OPT_G_SKILL_EXP, OPT_FEAT, OPT_SEL,
			OPT_G_ABL, OPT_C_ABL, OPT_NOTES);

	private final JComboBox<String> optionSel = new JComboBox<>(options.toArray(new String[0]));
	private final JButton add = new JButton("Add Feature");

	private final Feature f;

	@Override
	protected String getItemName()
	{
		return f.getName();
	}

	@Override
	protected void setItemName(String s)
	{
		f.setName(s);
	}

	private final Map<String, JPanel> optionPanels = new HashMap<>();
	private final JButton addUpgradeButton = new JButton("Add Upgrade"),
			removeUpgradeButton = new JButton("Remove Last Upgrade");
	private final JPanel upgradesPanel = new NoHorizontalScrollPanel();

	public FeatureEditPanel(Feature f, boolean startCollapsed, ActionListener delListener)
	{
		super(startCollapsed);

		this.f = f;

		linkedProperties
				.add(UILib.addLabeledLinkedSpinner(collapse.headerPanel, "Level: ", new SpinnerNumberModel(1, 1, 20, 1),
						Color.white, Color.black, () -> f.getLevel(), (i) -> f.setLevel(i)));

		if (delListener != null)
		{
			JButton delBtn = new JButton("Delete");
			delBtn.addActionListener(delListener);
			delBtn.setActionCommand("Delete " + f.getId());
			collapse.headerPanel.add(delBtn);
		}

		c.gridwidth = 2;

		collapse.bodyPanel.add(getOptionSelectPanel(), c);
		c.gridy++;

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_SKILLS, c, Color.white, Color.black, () -> getSkillProfs(),
				(s) -> updateSkillProfs(s));

		addLabeledLinkedTextFieldAndSpinner(collapse.bodyPanel, OPT_C_SKILLS, "Count: ", c,
				new SpinnerNumberModel(0, 0, 5, 1), Color.white, Color.black, () -> getSkillOptions(),
				(s) -> updateSkillOptions(s), () -> getSkillOptionsCount(), (i) -> updateSkillOptionsCount(i));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_SAVES, c, Color.white, Color.black, () -> getSaveProfs(),
				(s) -> updateSaveProfs(s));

		addLabeledLinkedTextArea(collapse.bodyPanel, OPT_G_SPELLS, 2, c, Color.white, Color.black,
				() -> getSpellsGranted(), (s) -> updateSpellsGranted(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_C_SPELLS, c, Color.white, Color.black,
				() -> getSpellOptions(), (s) -> updateSpellOptions(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_C_S_SPELLS, c, Color.white, Color.black,
				() -> getSpecificSpells(), (s) -> updateSpecificSpells(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_ARMOR, c, Color.white, Color.black, () -> getArmor(),
				(s) -> updateArmor(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_LANGS, c, Color.white, Color.black, () -> getLangs(),
				(s) -> updateLangs(s));

		addLabeledLinkedTextFieldAndSpinner(collapse.bodyPanel, OPT_C_LANGS, "Count: ", c,
				new SpinnerNumberModel(0, 0, 5, 1), Color.white, Color.black, () -> getLangOptions(),
				(s) -> updateLangOptions(s), () -> getLangOptionsCount(), (i) -> updateLangOptionsCount(i));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_RES, c, Color.white, Color.black,
				() -> getResistancesGranted(), (s) -> updateResistancesGranted(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_R_BY_H, c, Color.white, Color.black, () -> getResByHome(),
				(s) -> updateResByHome(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_C_RES, c, Color.white, Color.black, () -> getResOptions(),
				(s) -> updateResOptions(s));

		addLabeledLinkedTextFieldAndSpinner(collapse.bodyPanel, OPT_C_EXP, "Count: ", c,
				new SpinnerNumberModel(0, 0, 5, 1), Color.white, Color.black, () -> getSkillExpertOptions(),
				(s) -> updateSkillExpertOptions(s), () -> getSkillExpertCount(), (i) -> updateSkillExpertCount(i));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_TOOLS, c, Color.white, Color.black, () -> getToolProfs(),
				(s) -> updateToolProfs(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_WEPS, c, Color.white, Color.black, () -> getWeaponProfs(),
				(s) -> updateWeaponProfs(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_ABL_AC, c, Color.white, Color.black, () -> getAblToAC(),
				(s) -> updateAblToAC(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_ABL_SKILLS, c, Color.white, Color.black,
				() -> getAblToSkills(), (s) -> updateAblToSkills(s));

		addLabeledLinkedSpinner(collapse.bodyPanel, OPT_SPD, new SpinnerNumberModel(0, 0, 50, 5), c, Color.white,
				Color.black, () -> getSpeed(), (s) -> updateSpeed(s));

		addLabeledLinkedSpinner(collapse.bodyPanel, OPT_HP_1, new SpinnerNumberModel(0, 0, 5, 1), c, Color.white,
				Color.black, () -> getLvl1Hp(), (s) -> updateLvl1Hp(s));

		addLabeledLinkedSpinner(collapse.bodyPanel, OPT_HP_LVL, new SpinnerNumberModel(0, 0, 5, 1), c, Color.white,
				Color.black, () -> getAllLvlHp(), (s) -> updateAllLvlHp(s));

		addLabeledLinkedCheckbox(collapse.bodyPanel, OPT_HALFPROF_ALL, Color.black, c, () -> getHalfProf(),
				(b) -> updateHalfProf(b));

		addLabeledLinkedCheckbox(collapse.bodyPanel, OPT_PROF_INIT, Color.black, c, () -> getProfInit(),
				(b) -> updateProfInit(b));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_ABL_INIT, c, Color.white, Color.black, () -> getAblsToInit(),
				(s) -> updateAblsToInit(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_FEAT, c, Color.white, Color.black, () -> getFeatTrait(),
				(s) -> updateFeatTrait(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_SEL, c, Color.white, Color.black, () -> getSelectableType(),
				(s) -> updateSelectableType(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_C_SKILL_ABL_TO, c, Color.white, Color.black,
				() -> getSkillChoiceWithAbilityAdd(), (s) -> updateSkillChoiceWithAbilityAdd(s));

		addLabeledLinkedTextField(collapse.bodyPanel, OPT_G_SKILL_EXP, c, Color.white, Color.black,
				() -> getSkillProfExp(), (s) -> updateSkillProfExp(s));

		addLabeledLinkedTextFieldAndSpinner(collapse.bodyPanel, OPT_G_ABL, "Max: ", c,
				new SpinnerNumberModel(20, 10, 30, 1), Color.white, Color.black, () -> getGrantedAblIncreases(),
				(s) -> updateGrantedAblIncreases(s), () -> getGrantedAblMax(), (i) -> updateGrantedAblMax(i));

		addLabeledLinkedTextFieldAndSpinner(collapse.bodyPanel, OPT_C_ABL, "Max: ", c,
				new SpinnerNumberModel(20, 10, 30, 1), Color.white, Color.black, () -> getChooseAblIncreases(),
				(s) -> updateChooseAblIncreases(s), () -> getChooseAblMax(), (i) -> updateChooseAblMax(i));

		c.weighty = 1;
		addLabeledLinkedTextArea(collapse.bodyPanel, OPT_TEXT, 3, c, Color.white, Color.black, () -> getText(),
				(s) -> updateText(s));

		addLabeledLinkedTextArea(collapse.bodyPanel, OPT_NOTES, 4, c, Color.white, Color.black, () -> getSheetNotes(),
				(s) -> updateSheetNotes(s));

		if (!f.isCopy())
		{
			int oldy = c.gridy;
			c.gridy = 100;
			c.gridwidth = 1;
			addUpgradeButton.addActionListener(this);
			collapse.bodyPanel.add(addUpgradeButton, c);
			c.gridx++;
			removeUpgradeButton.addActionListener(this);
			collapse.bodyPanel.add(removeUpgradeButton, c);
			c.gridx = 0;
			c.gridwidth = 2;
			c.gridy++;

			upgradesPanel.setLayout(new GridBagLayout());
			collapse.bodyPanel.add(upgradesPanel, c);
			updateUpgradePanels();
			c.gridy = oldy;
		}
		c.weighty = 0;

		linkedProperties.forEach(p -> p.updateValue());
	}

	private JPanel getOptionSelectPanel()
	{
		JPanel optionsPanel = new NoHorizontalScrollPanel();
		optionsPanel.setLayout(new GridBagLayout());
		GridBagConstraints c2 = UILib.getStandardGBC();
		optionsPanel.add(optionSel, c2);
		c2.weightx = 0;
		c2.gridx++;
		add.addActionListener(this);
		optionsPanel.add(add, c2);
		return optionsPanel;
	}

	private void updateUpgradePanels()
	{
		GridBagConstraints c = UILib.getStandardGBC();
		upgradesPanel.removeAll();
		for (Feature upgrade : f.getUpgrades())
		{
			upgradesPanel.add(new FeatureEditPanel(upgrade, true, null), c);
			c.gridy++;
		}
		upgradesPanel.revalidate();
	}

	private static final GridBagConstraints STANDARDC = UILib.getStandardGBC();

	private void addLabeledLinkedTextField(JComponent parent, String label, GridBagConstraints c, Color bg, Color fg,
			Supplier<String> getter, Consumer<String> setter)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		if (!getter.get().isBlank())
		{
			parent.add(panel, c);
			c.gridy++;
		}

		linkedProperties.add(UILib.addLabeledLinkedTextField(panel, label + ": ", c, bg, fg, getter, setter));
	}

	private void addLabeledLinkedTextArea(JComponent parent, String label, int rows, GridBagConstraints c, Color bg,
			Color fg, Supplier<String> getter, Consumer<String> setter)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		if (!getter.get().isBlank())
		{
			parent.add(panel, c);
			c.gridy++;
		}

		linkedProperties.add(UILib.addLabeledLinkedTextArea(panel, rows, label + ": ", c, bg, fg, getter, setter));
	}

	private void addLabeledLinkedTextFieldAndSpinner(JComponent parent, String txtLabel, String spLabel,
			GridBagConstraints c, SpinnerNumberModel model, Color bg, Color fg, Supplier<String> txtGetter,
			Consumer<String> txtSetter, IntSupplier spGetter, IntConsumer spSetter)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(txtLabel, panel);
		if (!txtGetter.get().isBlank())
		{
			parent.add(panel, c);
			c.gridy++;
		}

		linkedProperties
				.add(UILib.addLabeledLinkedTextField(panel, txtLabel + ": ", STANDARDC, bg, fg, txtGetter, txtSetter));
		STANDARDC.gridx = 1;
		linkedProperties.add(
				UILib.addLabeledLinkedSpinner(panel, spLabel + ": ", STANDARDC, model, bg, fg, spGetter, spSetter));
		STANDARDC.gridx = 0;
	}

	private void addLabeledLinkedSpinner(JComponent parent, String label, SpinnerNumberModel model,
			GridBagConstraints c, Color bg, Color fg, IntSupplier getter, IntConsumer setter)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		if (getter.getAsInt() > 0)
		{
			parent.add(panel, c);
			c.gridy++;
		}

		linkedProperties.add(UILib.addLabeledLinkedSpinner(panel, label + ": ", c, model, bg, fg, getter, setter));
	}

	private void addLabeledLinkedCheckbox(JComponent parent, String label, Color fg, GridBagConstraints c,
			BooleanSupplier getter, BooleanConsumer setter)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		if (getter.getAsBoolean())
		{
			parent.add(panel, c);
			c.gridy++;
		}

		linkedProperties.add(UILib.addLabeledLinkedCheckbox(panel, label + ": ", c, fg, getter, setter));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(add))
		{
			collapse.bodyPanel.add(optionPanels.get(optionSel.getSelectedItem()), c);
			c.gridy++;
			revalidate();
		}
		else if (e.getSource().equals(addUpgradeButton))
		{
			f.addUpgrade();
			updateUpgradePanels();
		}
		else if (e.getSource().equals(removeUpgradeButton))
		{
			f.removeUpgrade();
			updateUpgradePanels();
		}
	}

	@Override
	public void setSelected(Customizable sel)
	{
		throw new UnsupportedOperationException("Cannot change feature edit panel selection.");
	}

	private String getSkillProfs()
	{
		return String.join(", ", f.getSkillsGranted().stream().map(s -> s.toString()).toList());
	}

	private void updateSkillProfs(String s)
	{
		f.setSkillsGranted(parseSkills(s.split(",")));
	}

	private String getSkillOptions()
	{
		return String.join(", ", f.getSkillSelectionOptions().stream().map(s -> s.toString()).toList());
	}

	private void updateSkillOptions(String s)
	{
		f.setSkillSelectionOptions(parseSkills(s.split(",")));
	}

	private int getSkillOptionsCount()
	{
		return f.getSkillSelectionCount();
	}

	private void updateSkillOptionsCount(int i)
	{
		f.setSkillSelectionCount(i);
	}

	private String getSaveProfs()
	{
		return String.join(", ", f.getSaveProfs().stream().map(s -> s.toString()).toList());
	}

	private void updateSaveProfs(String s)
	{
		f.setSaveProfs(parseAbilities(s.split(",")));
	}

	private String getSpellsGranted()
	{
		return String.join(" | ", f.getSpellsGranted().stream().map(sp -> spellToText(sp)).toList());
	}

	private void updateSpellsGranted(String s)
	{
		f.setSpellsGranted(parseSpells(s.split("\\|")));
	}

	private String getSpecificSpells()
	{
		return String.join(" | ", f.getSpecificSpellChoices().stream().map(sp -> spellToText(sp)).toList());
	}

	private void updateSpecificSpells(String s)
	{
		f.setSpecificSpellChoices(parseSpells(s.split("\\|")));
	}

	private String getSpellOptions()
	{
		List<String> spellChoices = new ArrayList<>();
		for (Map.Entry<Integer, List<SpellChoice>> sc : f.getSpellChoices().entrySet())
		{
			spellChoices.add(
					sc.getKey() + "-" + sc.getValue().size() + "-" + sc.getValue().get(0).getSpellList().toString());
		}
		return String.join(", ", spellChoices);
	}

	private void updateSpellOptions(String s)
	{
		String[] spellOptions = s.split(",");
		Map<Integer, List<SpellChoice>> spellChoices = new HashMap<>();
		List<String> invalid = new ArrayList<>();
		for (String so : spellOptions)
		{
			if (so.isBlank())
			{
				continue;
			}
			try
			{
				String[] split = so.split("-");
				if (split.length != 3)
				{
					invalid.add(so);
					continue;
				}
				int lvl = Integer.parseInt(split[0].trim());
				if (!spellChoices.containsKey(lvl))
				{
					spellChoices.put(lvl, new ArrayList<>());
				}

				int count = Integer.parseInt(split[1].trim());
				for (int i = 0; i < count; i++)
				{
					SpellChoice sc = new SpellChoice(SpellList.getForClass(split[2].trim()), lvl);
					spellChoices.get(lvl).add(sc);
				}
			}
			catch (Exception e)
			{
				invalid.add(so);
			}
		}
		f.setSpellChoices(spellChoices);
		showErrorMessage(invalid, "spell options");
	}

	private String getArmor()
	{
		return String.join(", ", f.getArmorProfs().stream().map(a -> a.toString()).toList());
	}

	private void updateArmor(String s)
	{
		f.setArmorProfs(parseArmor(s.split(",")));
	}

	private String getLangs()
	{
		return String.join(", ", f.getLanguagesGranted().stream().map(s -> s.toString()).toList());
	}

	private void updateLangs(String s)
	{
		f.setLanguagesGranted(parseLanguages(s.split(",")));
	}

	private String getLangOptions()
	{
		return String.join(", ", f.getLanguageSelectionOptions().stream().map(s -> s.toString()).toList());
	}

	private void updateLangOptions(String s)
	{
		if (s.contains("Any"))
		{
			f.setLanguageSelectionOptions(List.of("Any"));
		}
		else
		{
			f.setLanguageSelectionOptions(parseLanguages(s.split(",")).stream().map(l -> l.toString()).toList());
		}
	}

	private int getLangOptionsCount()
	{
		return f.getLanguageSelectionCount();
	}

	private void updateLangOptionsCount(int i)
	{
		f.setLanguageSelectionCount(i);
	}

	private String getResistancesGranted()
	{
		return String.join(",", f.getResistancesGranted());
	}

	private void updateResistancesGranted(String s)
	{
		List<String> res = new ArrayList<>();
		for (String r : s.split(","))
		{
			if (!r.trim().isBlank())
			{
				res.add(r.trim());
			}
		}
		f.setResistancesGranted(res);
	}

	private String getResByHome()
	{
		List<String> resByH = new ArrayList<>();
		for (Map.Entry<String, String> rH : f.getResistancesByHomeworld().entrySet())
		{
			resByH.add(rH.getKey() + "-" + rH.getValue());
		}
		return String.join(", ", resByH);
	}

	private void updateResByHome(String s)
	{
		String[] resOptions = s.split(",");
		Map<String, String> resByHome = new HashMap<>();
		List<String> invalid = new ArrayList<>();
		for (String ro : resOptions)
		{
			String[] split = ro.split("-");
			if (split.length != 2)
			{
				invalid.add(ro);
				continue;
			}
			resByHome.put(split[0], split[1]);
		}
		f.setResistancesByHomeworld(resByHome);
		showErrorMessage(invalid, "homeworld trait-resistance mapping");
	}

	private String getSkillExpertOptions()
	{
		return String.join(", ", f.getSkillExpertOptions().stream().map(s -> s.toString()).toList());
	}

	private void updateSkillExpertOptions(String s)
	{
		f.setSkillExpertOptions(parseSkills(s.split(",")));
	}

	private int getSkillExpertCount()
	{
		return f.getSkillExpertCount();
	}

	private void updateSkillExpertCount(int i)
	{
		f.setSkillExpertCount(i);
	}

	private String getResOptions()
	{
		return String.join(", ", f.getResistanceOptions().stream().map(s -> s.toString()).toList());
	}

	private void updateResOptions(String s)
	{
		List<String> res = new ArrayList<>();
		for (String r : s.split(","))
		{
			if (!r.trim().isBlank())
			{
				res.add(r.trim());
			}
		}
		f.setResistanceOptions(res);
	}

	private String getToolProfs()
	{
		return String.join(", ", f.getToolProfs().stream().map(s -> s.toString()).toList());
	}

	private void updateToolProfs(String s)
	{
		f.setToolProfs(Arrays.asList(s.split(",")).stream().map(s2 -> s2.trim()).toList());
	}

	private String getWeaponProfs()
	{
		return String.join(", ", f.getWeaponProfs().stream().map(s -> s.toString()).toList());
	}

	private void updateWeaponProfs(String s)
	{
		f.setWeaponProfs(Arrays.asList(s.split(",")).stream().map(s2 -> s2.trim()).toList());
	}

	private String getAblToAC()
	{
		return String.join(", ", f.getAbilitiesToAC().stream().map(a -> a.toString()).toList());
	}

	private void updateAblToAC(String s)
	{
		f.setAbilitiesToAC(parseAbilities(s.split(",")));
	}

	private String getAblToSkills()
	{
		List<String> ats = new ArrayList<>();
		for (Map.Entry<Skill, Ability> sc : f.getAbilitiesAddToSkills().entrySet())
		{
			ats.add(sc.getKey().toString() + "-" + sc.getValue().toString());
		}
		return String.join(", ", ats);
	}

	private void updateAblToSkills(String s)
	{
		String[] ablSOptions = s.split(",");
		Map<Skill, Ability> aForS = new HashMap<>();
		List<String> invalid = new ArrayList<>();
		for (String as : ablSOptions)
		{
			if (as.isBlank())
			{
				continue;
			}
			try
			{
				String[] split = as.split("-");
				if (split.length != 2)
				{
					invalid.add(as);
					continue;
				}
				aForS.put(Skill.skillByName(split[0].trim()), Ability.valueOf(split[1].trim()));
			}
			catch (Exception e)
			{
				invalid.add(as);
			}
		}
		f.setAbilitiesAddToSkills(aForS);
		showErrorMessage(invalid, "skill ability");
	}

	private int getSpeed()
	{
		return f.getSpeedMod();
	}

	private void updateSpeed(int i)
	{
		f.setSpeedMod(i);
	}

	private int getLvl1Hp()
	{
		return f.getExtraHPLvl1();
	}

	private void updateLvl1Hp(int i)
	{
		f.setExtraHPLvl1(i);
	}

	private int getAllLvlHp()
	{
		return f.getExtraHPPerLevel();
	}

	private void updateAllLvlHp(int i)
	{
		f.setExtraHPPerLevel(i);
	}

	private boolean getHalfProf()
	{
		return f.givesHalfProfAll();
	}

	private void updateHalfProf(boolean b)
	{
		f.setHalfProfAll(b);
	}

	private boolean getProfInit()
	{
		return f.givesProfToInit();
	}

	private void updateProfInit(boolean b)
	{
		f.setProfToInit(b);
	}

	private String getAblsToInit()
	{
		return String.join(", ", f.getInitAbilities().stream().map(a -> a.toString()).toList());
	}

	private void updateAblsToInit(String s)
	{
		f.setInitAbilities(parseAbilities(s.split(",")));
	}

	private String getFeatTrait()
	{
		return f.getFeatTraitName() + (f.featIgnoresPrereqs() ? " (ignores prereqs)" : "");
	}

	private void updateFeatTrait(String s)
	{
		if (s.contains("(ignore"))
		{
			f.setFeatIgnoresPrereqs(true);
			f.setFeatTraitName(s.substring(0, s.indexOf("(ignore")).trim());
		}
		else
		{
			f.setFeatTraitName(s.trim());
		}
	}

	private String getSelectableType()
	{
		return f.getSelectableName();
	}

	private void updateSelectableType(String s)
	{
		f.setSelectableName(s);
	}

	private String getSkillChoiceWithAbilityAdd()
	{
		List<Skill> skills = f.getSkillsWAblSkills();
		Ability abl = f.getSkillsWAblAbility();

		if (!skills.isEmpty() && abl != null)
		{
			return String.join(", ", skills.stream().map(s -> s.toString()).toList()) + " | " + abl.toString();
		}
		return "";
	}

	private void updateSkillChoiceWithAbilityAdd(String s)
	{
		String[] split = s.split("\\|");
		if (split.length != 2)
		{
			showErrorMessage(List.of(s), "skill choices | ability");
		}
		else
		{
			List<Skill> skills = parseSkills(split[0].split(","));
			List<Ability> ability = parseAbilities(new String[]
			{ split[1] });

			if (!skills.isEmpty() && ability.size() == 1)
			{
				f.setSkillsWAbl(skills, ability.get(0));
			}
		}
	}

	private String getSkillProfExp()
	{
		return String.join(", ", f.getSkillProfOrExpertise().stream().map(s -> s.toString()).toList());
	}

	private void updateSkillProfExp(String s)
	{
		f.setSkillProfOrExpertise(parseSkills(s.split(", ")));
	}

	private String getText()
	{
		return f.getText();
	}

	private void updateText(String s)
	{
		f.setText(s);
	}

	private String getSheetNotes()
	{
		return f.getSheetNotes();
	}

	private void updateSheetNotes(String s)
	{
		f.setSheetNotes(s);
	}

	private String getGrantedAblIncreases()
	{
		return String.join(", ", f.getIncreasedAbilities().stream().map(ai -> ai.getAbility().toString()).toList());
	}

	private void updateGrantedAblIncreases(String s)
	{
		List<Ability> abilities = parseAbilities(s.split(","));
		int max = getGrantedAblMax();
		f.setIncreasedAbilities(abilities.stream().map(a -> new AbilityIncrease(a, 1, max)).toList());
	}

	private int getGrantedAblMax()
	{
		return f.getIncreasedAbilities().isEmpty() ? 20 : f.getIncreasedAbilities().get(0).getMax();
	}

	private void updateGrantedAblMax(int i)
	{
		List<Ability> abilities = f.getIncreasedAbilities().stream().map(ai -> ai.getAbility()).toList();
		f.setIncreasedAbilities(abilities.stream().map(a -> new AbilityIncrease(a, 1, i)).toList());
	}

	private String getChooseAblIncreases()
	{
		return String.join(", ", f.getAbilityIncreaseOptions().stream().map(ai -> ai.getAbility().toString()).toList());
	}

	private void updateChooseAblIncreases(String s)
	{
		List<Ability> abilities = parseAbilities(s.split(","));
		int max = getGrantedAblMax();
		f.setAbilityIncreaseOptions(abilities.stream().map(a -> new AbilityIncrease(a, 1, max)).toList());
	}

	private int getChooseAblMax()
	{
		return f.getAbilityIncreaseOptions().isEmpty() ? 20 : f.getAbilityIncreaseOptions().get(0).getMax();
	}

	private void updateChooseAblMax(int i)
	{
		List<Ability> abilities = f.getAbilityIncreaseOptions().stream().map(ai -> ai.getAbility()).toList();
		f.setAbilityIncreaseOptions(abilities.stream().map(a -> new AbilityIncrease(a, 1, i)).toList());
	}
}
