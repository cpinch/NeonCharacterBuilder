package nocb.ui.DataEditor;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import nocb.data.Feature;
import nocb.data.Language;
import nocb.data.Skill;
import nocb.data.Spell;
import nocb.data.SpellChoice;
import nocb.data.SpellList;
import nocb.ui.NoHorizontalScrollPanel;
import nocb.ui.UILib;

public class FeatureEditPanel extends EditPanel implements ActionListener, ChangeListener
{
	private static final long serialVersionUID = -4113129376172120186L;

	private static final String OPT_TEXT = "Text", OPT_G_SKILLS = "Gives Skill Profs",
			OPT_C_SKILLS = "Choose Skill Profs", OPT_G_SAVES = "Gives Save Profs", OPT_G_SPELLS = "Gives Spells",
			OPT_C_SPELLS = "Choose Spells", OPT_G_ARMOR = "Gives Armor Train", OPT_G_LANGS = "Gives Languages",
			OPT_C_LANGS = "Choose Languages", OPT_G_RES = "Gives Resistances",
			OPT_R_BY_H = "Gives Resistances Based on Homeworld Traits", OPT_C_RES = "Choose Resistances",
			OPT_C_EXP = "Choose Skill Expertise", OPT_G_TOOLS = "Gives Tool Profs", OPT_G_WEPS = "Gives Weapon Profs",
			OPT_ABL_AC = "Add Ability to AC", OPT_ABL_SKILLS = "Adds Extra Ability to Skills",
			OPT_SPD = "Increase Speed", OPT_HP = "+HP/Level", OPT_FEAT = "Choose Feat", OPT_SEL = "Choose Selectable",
			OPT_NOTES = "Puts Notes on Sheet";

	private static final List<String> options = List.of(OPT_TEXT, OPT_G_SKILLS, OPT_C_SKILLS, OPT_G_SAVES, OPT_G_SPELLS,
			OPT_C_SPELLS, OPT_G_ARMOR, OPT_G_LANGS, OPT_C_LANGS, OPT_G_RES, OPT_R_BY_H, OPT_C_RES, OPT_C_EXP,
			OPT_G_TOOLS, OPT_G_WEPS, OPT_ABL_AC, OPT_ABL_SKILLS, OPT_SPD, OPT_HP, OPT_FEAT, OPT_SEL, OPT_NOTES);

	private final Feature f;

	private final JComboBox<String> optionSel = new JComboBox<>(options.toArray(new String[0]));
	private final JButton add = new JButton("Add Feature");

	private final JTextField skillProfs = new JTextField(20);
	private final JTextField chooseSkills = new JTextField(20);
	private final JSpinner chooseSkillCount = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
	private final JTextField saveProfs = new JTextField(20);
	private final JTextField spells = new JTextField(20);
	private final JTextField chooseSpells = new JTextField(20);
	private final JTextField armor = new JTextField(20);
	private final JTextField langs = new JTextField(20);
	private final JTextField chooseLangs = new JTextField(20);
	private final JSpinner chooseLangsCount = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
	private final JTextField resists = new JTextField(20);
	private final JTextField resByHome = new JTextField(20);
	private final JTextField chooseRes = new JTextField(20);
	private final JTextField chooseExp = new JTextField(20);
	private final JSpinner chooseExpCount = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
	private final JTextField tools = new JTextField(20);
	private final JTextField weapons = new JTextField(20);
	private final JComboBox<Ability> ablAddAc = new JComboBox<>(Ability.realValues());
	private final JTextField ablToSkills = new JTextField(20);
	private final JSpinner speed = new JSpinner(new SpinnerNumberModel(0, 0, 50, 1));
	private final JSpinner hp = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
	private final JTextField feat = new JTextField(20);
	private final JTextField selectable = new JTextField(20);
	private final JTextArea text = new JTextArea(3, 20);
	private final JTextArea sheetNotes = new JTextArea(4, 20);

	private final Map<String, JPanel> optionPanels = new HashMap<>();

	public FeatureEditPanel(Feature f)
	{
		super();

		this.f = f;

		add(getOptionSelectPanel(), c);
		c.gridy++;

		skillProfs.addFocusListener(UILib.createFocusListener(() -> updateSkillProfs()));
		addLabeledComp(OPT_G_SKILLS, skillProfs, c);

		chooseSkills.addFocusListener(UILib.createFocusListener(() -> updateSkillOptions()));
		chooseSkillCount.addChangeListener(this);
		((JSpinner.DefaultEditor) chooseSkillCount.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		addLabeledSelComps(OPT_C_SKILLS, chooseSkills, " Count", chooseSkillCount, c);

		saveProfs.addFocusListener(UILib.createFocusListener(() -> updateSaveProfs()));
		addLabeledComp(OPT_G_SAVES, saveProfs, c);

		spells.addFocusListener(UILib.createFocusListener(() -> updateSpells()));
		addLabeledComp(OPT_G_SPELLS, spells, c);

		chooseSpells.addFocusListener(UILib.createFocusListener(() -> updateSpellOptions()));
		addLabeledComp(OPT_C_SPELLS, chooseSpells, c);

		armor.addFocusListener(UILib.createFocusListener(() -> updateArmor()));
		addLabeledComp(OPT_G_ARMOR, armor, c);

		langs.addFocusListener(UILib.createFocusListener(() -> updateLangs()));
		addLabeledComp(OPT_G_LANGS, langs, c);

		chooseLangs.addFocusListener(UILib.createFocusListener(() -> updateLangOptions()));
		chooseLangsCount.addChangeListener(this);
		((JSpinner.DefaultEditor) chooseLangsCount.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		addLabeledSelComps(OPT_C_LANGS, chooseLangs, " Count", chooseLangsCount, c);

		resists.addFocusListener(UILib.createFocusListener(() -> updateResists()));
		addLabeledComp(OPT_G_RES, resists, c);

		resByHome.addFocusListener(UILib.createFocusListener(() -> updateResByHome()));
		addLabeledComp(OPT_R_BY_H, resByHome, c);

		chooseRes.addFocusListener(UILib.createFocusListener(() -> updateResOptions()));
		addLabeledComp(OPT_C_RES, chooseRes, c);

		chooseExp.addFocusListener(UILib.createFocusListener(() -> updateExpOptions()));
		chooseExpCount.addChangeListener(this);
		((JSpinner.DefaultEditor) chooseExpCount.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		addLabeledSelComps(OPT_C_EXP, chooseExp, " Count", chooseExpCount, c);

		tools.addFocusListener(UILib.createFocusListener(() -> updateTools()));
		addLabeledComp(OPT_G_TOOLS, tools, c);

		weapons.addFocusListener(UILib.createFocusListener(() -> updateWeapons()));
		addLabeledComp(OPT_G_WEPS, weapons, c);

		ablAddAc.addActionListener(this);
		addLabeledComp(OPT_ABL_AC, ablAddAc, c);

		ablToSkills.addFocusListener(UILib.createFocusListener(() -> updateAblToSkills()));
		addLabeledComp(OPT_ABL_SKILLS, ablToSkills, c);

		speed.addChangeListener(this);
		((JSpinner.DefaultEditor) speed.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		addLabeledComp(OPT_SPD, speed, c);

		hp.addChangeListener(this);
		((JSpinner.DefaultEditor) hp.getEditor()).getTextField().setHorizontalAlignment(JTextField.LEFT);
		addLabeledComp(OPT_HP, hp, c);

		feat.addFocusListener(UILib.createFocusListener(() -> updateFeatTrait()));
		addLabeledComp(OPT_FEAT, feat, c);

		selectable.addFocusListener(UILib.createFocusListener(() -> updateSelectableType()));
		addLabeledComp(OPT_SEL, selectable, c);

		c.weighty = 1;
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		text.addFocusListener(UILib.createFocusListener(() -> updateText()));
		addLabeledComp(OPT_TEXT, text, c);

		sheetNotes.setLineWrap(true);
		sheetNotes.setWrapStyleWord(true);
		sheetNotes.addFocusListener(UILib.createFocusListener(() -> updateSheetNotes()));
		addLabeledComp(OPT_NOTES, sheetNotes, c);
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

	private static final GridBagConstraints c2 = UILib.getStandardGBC();

	private void addLabeledComp(String label, JComponent component, GridBagConstraints c)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		UILib.addLabeledComponent(panel, label + ": ", component, c2);
		add(panel, c);
		c.gridy++;
		panel.setVisible(false);
	}

	private void addLabeledSelComps(String label, JComponent sel, String countLabel, JComponent count,
			GridBagConstraints c)
	{
		JPanel panel = new NoHorizontalScrollPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEtchedBorder());
		optionPanels.put(label, panel);
		UILib.addLabeledComponent(panel, label + ": ", sel, c2);
		c2.gridx = 1;
		UILib.addLabeledComponent(panel, countLabel + ": ", count, c2);
		c2.gridx = 0;
		add(panel, c);
		c.gridy++;
		panel.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(add))
		{
			optionPanels.get(optionSel.getSelectedItem()).setVisible(true);
		}
		else if (e.getSource().equals(ablAddAc))
		{
			f.setAbilityAddToAC((Ability) ablAddAc.getSelectedItem());
		}
	}

	@Override
	public void stateChanged(ChangeEvent e)
	{
		if (e.getSource().equals(chooseSkillCount))
		{
			f.setSkillSelectionCount((int) chooseSkillCount.getValue());
		}
		else if (e.getSource().equals(chooseLangsCount))
		{
			f.setLanguageSelectionCount((int) chooseLangsCount.getValue());
		}
		else if (e.getSource().equals(chooseExpCount))
		{
			f.setSkillExpertCount((int) chooseExpCount.getValue());
		}
		else if (e.getSource().equals(speed))
		{
			f.setSpeedMod((int) speed.getValue());
		}
		else if (e.getSource().equals(hp))
		{
			f.setExtraHPPerLevel((int) hp.getValue());
		}
	}

	@Override
	protected void updateSelection()
	{
		nameField.setText(f.getName());
		if (!f.getSkillsGranted().isEmpty())
		{
			skillProfs.setText(String.join(", ", f.getSkillsGranted().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_SKILLS).setVisible(true);
		}
		if (!f.getSkillSelectionOptions().isEmpty())
		{
			chooseSkills
					.setText(String.join(", ", f.getSkillSelectionOptions().stream().map(s -> s.toString()).toList()));
			chooseSkillCount.setValue(f.getSkillSelectionCount());
			optionPanels.get(OPT_C_SKILLS).setVisible(true);
		}
		if (!f.getSaveProfs().isEmpty())
		{
			saveProfs.setText(String.join(", ", f.getSaveProfs().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_SAVES).setVisible(true);
		}
		if (!f.getSpellsGranted().isEmpty())
		{
			final List<String> sns = new ArrayList<>();
			f.getSpellsGranted().forEach(s ->
			{
				if (s.getNotes().isBlank())
				{
					sns.add(s.getName());
				}
				else
				{
					sns.add(s.getName() + " (" + s.getNotes() + ")");
				}
			});
			spells.setText(String.join(" | ", sns));
			optionPanels.get(OPT_G_SPELLS).setVisible(true);
		}
		if (!f.getSpellChoices().isEmpty())
		{
			List<String> spellChoices = new ArrayList<>();
			for (Map.Entry<Integer, List<SpellChoice>> sc : f.getSpellChoices().entrySet())
			{
				spellChoices.add(sc.getKey() + " " + sc.getValue().size() + " "
						+ sc.getValue().get(0).getSpellList().toString());
			}
			chooseSpells.setText(String.join(", ", spellChoices));
			optionPanels.get(OPT_C_SPELLS).setVisible(true);
		}
		if (!f.getArmorProfs().isEmpty())
		{
			armor.setText(String.join(", ", f.getArmorProfs().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_ARMOR).setVisible(true);
		}
		if (!f.getLanguagesGranted().isEmpty())
		{
			langs.setText(String.join(", ", f.getLanguagesGranted().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_LANGS).setVisible(true);
		}
		if (!f.getLanguageSelectionOptions().isEmpty())
		{
			chooseLangs.setText(
					String.join(", ", f.getLanguageSelectionOptions().stream().map(s -> s.toString()).toList()));
			chooseLangsCount.setValue(f.getLanguageSelectionCount());
			optionPanels.get(OPT_C_LANGS).setVisible(true);
		}
		if (!f.getResistancesGranted().isEmpty())
		{
			resists.setText(String.join(", ", f.getResistancesGranted().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_RES).setVisible(true);
		}
		if (!f.getAllResistancesByHomeworld().isEmpty())
		{
			List<String> resByH = new ArrayList<>();
			for (Map.Entry<String, String> rH : f.getAllResistancesByHomeworld().entrySet())
			{
				resByH.add(rH.getKey() + "-" + rH.getValue());
			}
			resByHome.setText(String.join(", ", resByH));
			optionPanels.get(OPT_R_BY_H).setVisible(true);
		}
		if (!f.getResistanceOptions().isEmpty())
		{
			chooseRes.setText(String.join(", ", f.getResistanceOptions().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_C_RES).setVisible(true);
		}
		if (!f.getSkillExpertOptions().isEmpty())
		{
			chooseExp.setText(String.join(", ", f.getSkillExpertOptions().stream().map(s -> s.toString()).toList()));
			chooseExpCount.setValue(f.getSkillExpertCount());
			optionPanels.get(OPT_C_EXP).setVisible(true);
		}
		if (!f.getToolProfs().isEmpty())
		{
			tools.setText(String.join(", ", f.getToolProfs().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_TOOLS).setVisible(true);
		}
		if (!f.getWeaponProfs().isEmpty())
		{
			resists.setText(String.join(", ", f.getWeaponProfs().stream().map(s -> s.toString()).toList()));
			optionPanels.get(OPT_G_WEPS).setVisible(true);
		}
		if (f.getAbilityAddToAC() != null)
		{
			ablAddAc.setSelectedItem(f.getAbilityAddToAC());
			optionPanels.get(OPT_ABL_AC).setVisible(true);
		}
		if (!f.getAbilitiesAddToSkills().isEmpty())
		{
			List<String> ats = new ArrayList<>();
			for (Map.Entry<Skill, Ability> sc : f.getAbilitiesAddToSkills().entrySet())
			{
				ats.add(sc.getKey().toString() + "-" + sc.getValue().toString());
			}
			ablToSkills.setText(String.join(", ", ats));
			optionPanels.get(OPT_ABL_SKILLS).setVisible(true);
		}
		if (f.getSpeedMod() > 0)
		{
			speed.setValue(f.getSpeedMod());
			optionPanels.get(OPT_SPD).setVisible(true);
		}
		if (f.getExtraHPPerLevel() > 0)
		{
			hp.setValue(f.getExtraHPPerLevel());
			optionPanels.get(OPT_HP).setVisible(true);
		}
		if (!f.getFeatTraitName().isBlank())
		{
			feat.setText(f.getFeatTraitName());
			optionPanels.get(OPT_FEAT).setVisible(true);
		}
		if (!f.getSelectableName().isBlank())
		{
			selectable.setText(f.getSelectableName());
			optionPanels.get(OPT_SEL).setVisible(true);
		}
		if (!f.getText().isBlank())
		{
			text.setText(f.getText());
			optionPanels.get(OPT_TEXT).setVisible(true);
		}
		if (!f.getSheetNotes().isBlank())
		{
			sheetNotes.setText(f.getSheetNotes());
			optionPanels.get(OPT_NOTES).setVisible(true);
		}
	}

	private void showErrorMessage(List<String> invalid, String type)
	{
		if (!invalid.isEmpty())
		{
			JOptionPane.showMessageDialog(this, String.join(", ", invalid) + " were not valid " + type + ".",
					"Invalid Entries", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void updateName()
	{
		f.setName(nameField.getText());
		f.setCustom(true);
	}

	private void updateSkillProfs()
	{
		String[] skillNames = skillProfs.getText().split(",");
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
		f.setSkillsGranted(skills);
		f.setCustom(true);
		showErrorMessage(invalid, "skills");
	}

	private void updateSkillOptions()
	{
		String[] skillNames = chooseSkills.getText().split(",");
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
		f.setSkillSelectionOptions(skills);
		f.setCustom(true);
		showErrorMessage(invalid, "skills");
	}

	private void updateSaveProfs()
	{
		String[] abilityNames = saveProfs.getText().split(",");
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
		f.setSaveProfs(abilities);
		f.setCustom(true);
		showErrorMessage(invalid, "abilities");
	}

	private void updateSpells()
	{
		String[] spellNames = spells.getText().split("\\|");
		System.out.println("Split into " + spellNames);
		List<Spell> spells = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String sn : spellNames)
		{
			if (sn.isBlank())
			{
				continue;
			}
			try
			{
				if (sn.contains("("))
				{
					spells.add(Spell.getCopyByName(sn.substring(0, sn.indexOf('(')).trim(),
							sn.substring(sn.indexOf('('), sn.indexOf(')')).trim()));
				}
				else
				{
					spells.add(Spell.getByName(sn.trim()));
				}

			}
			catch (Exception e)
			{
				invalid.add(sn);
			}
		}
		System.out.println("Setting granted spells to " + spells + " for feature " + f.getName());
		f.setSpellsGranted(spells);
		f.setCustom(true);
		showErrorMessage(invalid, "spells");
	}

	private void updateSpellOptions()
	{
		String[] spellOptions = spells.getText().split(",");
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
				String[] split = so.split(" ");
				if (split.length != 3)
				{
					invalid.add(so);
					continue;
				}
				int lvl = Integer.parseInt(split[1]);
				if (!spellChoices.containsKey(lvl))
				{
					spellChoices.put(lvl, new ArrayList<>());
				}

				int count = Integer.parseInt(split[0]);
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
		f.setCustom(true);
		showErrorMessage(invalid, "spell options");
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
		f.setArmorProfs(armor);
		f.setCustom(true);
		showErrorMessage(invalid, "armor trainings");
	}

	private void updateLangs()
	{
		String[] langNames = langs.getText().split(",");
		List<Language> langs = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String ln : langNames)
		{
			if (ln.isBlank())
			{
				continue;
			}
			try
			{
				langs.add(Language.getByName(ln));
			}
			catch (Exception e)
			{
				invalid.add(ln);
			}
		}
		f.setLanguagesGranted(langs);
		f.setCustom(true);
		showErrorMessage(invalid, "language");
	}

	private void updateLangOptions()
	{
		String[] langNames = chooseLangs.getText().split(",");
		f.setLanguageOptionNames(Arrays.asList(langNames));
		f.setCustom(true);
	}

	private void updateResists()
	{
		String[] resNames = resists.getText().split(",");
		f.setResistancesGranted(Arrays.asList(resNames));
		f.setCustom(true);
	}

	private void updateResByHome()
	{
		String[] resOptions = resByHome.getText().split(",");
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
		f.setCustom(true);
		showErrorMessage(invalid, "homeworld trait-resistance mapping");
	}

	private void updateExpOptions()
	{
		String[] skillNames = chooseExp.getText().split(",");
		List<Skill> skills = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		for (String sn : skillNames)
		{
			try
			{
				skills.add(Skill.skillByName(sn.trim()));
			}
			catch (Exception e)
			{
				invalid.add(sn);
			}
		}
		f.setSkillExpertOptions(skills);
		f.setCustom(true);
		showErrorMessage(invalid, "skills");
	}

	private void updateResOptions()
	{
		String[] resNames = chooseRes.getText().split(",");
		f.setResistanceOptions(Arrays.asList(resNames));
		f.setCustom(true);
	}

	private void updateTools()
	{
		String[] t = tools.getText().split(",");
		f.setToolProfs(Arrays.asList(t));
		f.setCustom(true);
	}

	private void updateWeapons()
	{
		String[] w = weapons.getText().split(",");
		f.setWeaponProfs(Arrays.asList(w));
		f.setCustom(true);
	}

	private void updateAblToSkills()
	{
		String[] ablSOptions = ablToSkills.getText().split(",");
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
		f.setCustom(true);
		showErrorMessage(invalid, "skill ability");
	}

	private void updateFeatTrait()
	{
		f.setFeatTraitName(feat.getText());
		f.setCustom(true);
	}

	private void updateSelectableType()
	{
		f.setSelectableName(selectable.getText());
		f.setCustom(true);
	}

	private void updateText()
	{
		f.setText(text.getText());
		f.setCustom(true);
	}

	private void updateSheetNotes()
	{
		f.setSheetNotes(sheetNotes.getText());
		f.setCustom(true);
	}
}
