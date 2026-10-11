package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.AbilityIncrease;
import ncb.data.Feature;
import ncb.data.SpellChoice;
import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
import ncb.data.enums.DamageType;
import ncb.data.enums.Skill;
import ncb.data.loadables.Feat;
import ncb.data.loadables.Language;
import ncb.data.loadables.Selectable;
import ncb.data.loadables.Spell;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.CollapsablePanel;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;

public class FeaturePanel extends CollapsablePanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private static final String skillExpertAC = "Skill Expert";

	private final CharacterSheet sheet;
	private final Feature feature;

	private JComboBox<Skill> skillOptions;
	private JComboBox<DamageType> resistanceOptions;
	private final List<JComboBox<Skill>> skillExpertOptions = new ArrayList<>();
	private JComboBox<Language> langOptions;
	private JComboBox<Selectable> selectableOptions;
	private SelectablePanel selectablePanel;
	private JComboBox<Spell> specificSpellOptions;
	private JLabel resLabel;
	private JComboBox<Skill> skillWAbilityOptions;
	private JComboBox<AbilityIncrease> ablIncOptions;

	public FeaturePanel(CharacterSheet sheet, Feature feature, boolean startCollapsed)
	{
		super(startCollapsed);

		this.sheet = sheet;
		this.feature = feature;

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		UILib.addLabel(headerPanel, feature.getName(), c).putClientProperty(FlatClientProperties.STYLE, "font: bold");
		;
		c.gridy++;

		// Basic
		if (!feature.getText().isBlank())
		{
			c.weighty = 1;
			// We don't want this to scroll
			bodyPanel.add(UILib.getTextDisplay(feature.getText()), c);
			c.gridy++;
			c.weighty = 0;
		}
		if (feature.getSpeedMod() > 0)
		{
			UILib.addLabel(bodyPanel, "Speed + " + feature.getSpeedMod(), c);
			c.gridy++;
		}
		if (feature.getExtraHPLvl1() > 0)
		{
			UILib.addLabel(bodyPanel, "Extra HP " + feature.getExtraHPLvl1(), c);
			c.gridy++;
		}
		if (feature.getExtraHPPerLevel() > 0)
		{
			UILib.addLabel(bodyPanel, "Extra HP per Level " + feature.getExtraHPPerLevel(), c);
			c.gridy++;
		}
		if (feature.givesHalfProfAll())
		{
			UILib.addLabel(bodyPanel, "Gives Half Proficiency to all non-proficient skills.", c);
			c.gridy++;
		}
		if (feature.givesProfToInit())
		{
			UILib.addLabel(bodyPanel, "Gives Proficincy Bonus to Initiative.", c);
			c.gridy++;
		}

		// List
		List<Ability> saves = feature.getSaveProfs();
		if (!saves.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Save Proficiencies: <i>"
					+ String.join(", ", saves.stream().map(a -> a.toString()).toList()) + "</i></html>", c);
			c.gridy++;
		}
		List<String> weps = feature.getWeaponProfs();
		if (!weps.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Weapon Proficiencies: <i>" + String.join(", ", weps) + "</i></html>", c);
			c.gridy++;
		}
		List<String> tools = feature.getToolProfs();
		if (!tools.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Tool Proficiencies: <i>" + String.join(", ", tools) + "</i></html>",
					c);
			c.gridy++;
		}
		List<ArmorTraining> armor = feature.getArmorProfs();
		if (!armor.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Armor Trainings: <i>"
					+ String.join(", ", armor.stream().map(a -> a.toString()).toList()) + "</i></html>", c);
			c.gridy++;
		}
		List<Skill> skills = feature.getSkillsGranted();
		if (!skills.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Skills: <i>"
					+ String.join(", ", skills.stream().map(s -> s.toString()).toList()) + "</i></html>", c);
			c.gridy++;
		}
		List<Skill> skillExps = feature.getSkillExpsGranted();
		if (!skillExps.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Skill Expertise (if proficient): <i>"
					+ String.join(", ", skillExps.stream().map(s -> s.toString()).toList()) + "</i></html>", c);
			c.gridy++;
		}
		List<Spell> spells = feature.getSpellsGranted();
		if (!spells.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Spells: "
					+ String.join(", ",
							spells.stream()
									.map(s -> "<b>" + s.getName() + "</b>"
											+ (s.getNotes().isBlank() ? "" : " <i>(" + s.getNotes() + ")<i>"))
									.toList())
					+ "</html>", c);
			c.gridy++;
		}
		List<Language> freeLangs = feature.getLanguagesGranted();
		if (!freeLangs.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"Languages: " + String.join(", ", freeLangs.stream().map(la -> la.toString()).toList()), c);
			c.gridy++;
		}
		List<String> resStr = new ArrayList<>();
		resStr.addAll(feature.getResistancesGranted().stream().map(r -> r.toString()).toList());
		if (!feature.getResistancesByHomeworld().isEmpty() && sheet.getBackground() != null)
		{
			String r = CharacterSheet.getResistanceForHomeworld(sheet.getBackground().getHomeworld(),
					feature.getResistancesByHomeworld());
			if (!r.isBlank())
			{
				resStr.add(r);
			}
		}
		if (!resStr.isEmpty())
		{
			PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);
			resLabel = UILib.addLabel(bodyPanel,
					"<html>Grants Resistance: <i>" + String.join(", ", resStr) + "</i></html>", c);
			c.gridy++;
		}
		if (!feature.getSpellChoices().isEmpty())
		{
			List<String> levelCount = new ArrayList<>();
			for (Map.Entry<Integer, List<SpellChoice>> lvlChoices : feature.getSpellChoices().entrySet())
			{
				levelCount.add(lvlChoices.getValue().size() + " " + lvlChoices.getValue().get(0).getSpellList()
						+ (lvlChoices.getKey() == 0 ? " cantrip" + (lvlChoices.getValue().size() > 1 ? "s" : "")
								: " level " + lvlChoices.getKey()));
			}
			UILib.addLabel(bodyPanel, "Grants extra known spells: " + String.join(", ", levelCount), c);
			c.gridy++;
		}
		if (!feature.getAbilitiesAddToSkills().isEmpty())
		{
			List<String> adds = new ArrayList<>();
			for (Map.Entry<Skill, Ability> abilitiesToSkills : feature.getAbilitiesAddToSkills().entrySet())
			{
				adds.add("Adds " + abilitiesToSkills.getValue() + " to " + abilitiesToSkills.getKey());
			}
			UILib.addLabel(bodyPanel, String.join(", ", adds), c);
			c.gridy++;
		}
		if (!feature.getInitAbilities().isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html><i>May use "
							+ String.join(", ", feature.getInitAbilities().stream().map(a -> a.toString()).toList())
							+ " for initiative.</i></html>",
					c);
			c.gridy++;
		}
		if (!feature.getSkillProfOrExpertise().isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html><i>Gives proficiency in "
							+ String.join(", ",
									feature.getSkillProfOrExpertise().stream().map(s -> s.toString()).toList())
							+ " or expertise if you already have proficiency.</html>",
					c);
			c.gridy++;
		}
		if (!feature.getIncreasedAbilities().isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html><i>Increases "
							+ String.join(", ",
									feature.getIncreasedAbilities().stream().map(s -> s.toString()).toList())
							+ ".</html>",
					c);
			c.gridy++;
		}
		// abilitiesToAC always has accompanying text, so we don't show it

		// Selections
		List<DamageType> resOpts = feature.getResistanceOptions();
		if (!resOpts.isEmpty())
		{
			resistanceOptions = UILib.getComboBox(resOpts.toArray(new DamageType[0]), this);
			UILib.addLabeledComponent(bodyPanel, "Resistance: ", resistanceOptions, c);
			c.gridy++;
			if (feature.getResistancesSelected().isEmpty() || feature.getResistancesSelected().get(0) != null)
			{
				resistanceOptions.setSelectedIndex(0);
			}
			else
			{
				resistanceOptions.setSelectedItem(feature.getResistancesSelected().get(0));
			}
		}
		if (feature.getSkillSelectionCount() > 0) // TODO future - handle multiple or remove option to set multiple if
													// no feature uses it
		{
			PropertyListener.listenForChanges(PropertyListener.SKILLPROFS, this);
			List<Skill> skillOpts = feature.getSkillSelectionOptions();
			if (skillOpts.contains(Skill.Any))
			{
				skillOpts.clear();
				skillOpts.addAll(Arrays.asList(Skill.realValues()));
			}
			skillOptions = UILib.getComboBox(skillOpts.toArray(new Skill[0]), this);
			UILib.addLabeledComponent(bodyPanel, "Skill Proficiency: ", skillOptions, c);
			c.gridy++;
			if (feature.getSkillsSelected().isEmpty())
			{
				skillOptions.setSelectedIndex(0);
			}
			else
			{
				skillOptions.setSelectedItem(feature.getSkillsSelected().get(0));
			}
		}
		if (feature.getSkillExpertCount() > 0)
		{
			PropertyListener.listenForChanges(PropertyListener.SKILLPROFS, this);
			PropertyListener.listenForChanges(PropertyListener.SKILLEXPS, this);
			JPanel skillEPanel = new JPanel();
			skillEPanel.setOpaque(false);
			for (int i = 0; i < feature.getSkillExpertCount(); i++)
			{
				JComboBox<Skill> skillE = UILib.getComboBox(new Skill[0], this);
				skillEPanel.add(skillE);
				skillE.setActionCommand(skillExpertAC);
				skillEPanel.add(skillE);
				skillExpertOptions.add(skillE);
			}
			updateSkillExpertiseLists();
			UILib.addLabeledComponent(bodyPanel, "Select Expertise:", skillEPanel, c);
			c.gridy++;
		}
		if (feature.getLanguageSelectionCount() > 0) // TODO future - handle multiple or remove option to set multiple
														// if no feature uses it
		{
			langOptions = UILib.getComboBox(feature.getLanguageSelectionOptions().toArray(new Language[0]), this);
			UILib.addLabeledComponent(bodyPanel, "Select Language:", langOptions, c);
			c.gridy++;
			if (feature.getLanguagesSelected().isEmpty())
			{
				langOptions.setSelectedIndex(0);
			}
			else
			{
				langOptions.setSelectedItem(feature.getLanguagesSelected().get(0));
			}
		}
		if (!feature.getFeatTraitName().isBlank() || !feature.getSelectableName().isBlank())
		{
			PropertyListener.listenForChanges(PropertyListener.SELECTED, this);
			selectablePanel = new SelectablePanel(sheet);
			selectableOptions = UILib.getComboBox(new Selectable[0], this);
			String label = (feature.getSelectableName().isBlank() ? feature.getFeatTraitName()
					: feature.getSelectableName()) + ": ";
			updateSelectables();
			UILib.addLabeledComponent(bodyPanel, label, selectableOptions, c);
			c.gridy++;
			c.weighty = 1;
			bodyPanel.add(selectablePanel, c);
			c.gridy++;
		}
		if (!feature.getSpecificSpellChoices().isEmpty())
		{
			specificSpellOptions = UILib.getComboBox(feature.getSpecificSpellChoices().toArray(new Spell[0]), this);
			UILib.addLabeledComponent(bodyPanel, "Select Spell:", specificSpellOptions, c);
			c.gridy++;
			if (feature.getLanguagesSelected().isEmpty())
			{
				specificSpellOptions.setSelectedIndex(0);
			}
			else
			{
				specificSpellOptions.setSelectedItem(feature.getSpecificSpellChoices().get(0));
			}
		}
		if (!feature.getSkillsWAblSkills().isEmpty())
		{
			List<Skill> skillOpts = feature.getSkillsWAblSkills();
			Ability abl = feature.getSkillsWAblAbility();
			if (skillOpts.contains(Skill.Any))
			{
				skillOpts.clear();
				skillOpts.addAll(Arrays.asList(Skill.realValues()));
			}
			skillWAbilityOptions = UILib.getComboBox(skillOpts.toArray(new Skill[0]), this);
			UILib.addLabeledComponent(bodyPanel, "Skill Prof + Add " + abl.toString() + " mod: ", skillWAbilityOptions,
					c);
			c.gridy++;
			if (feature.getSkillWAbilitySelected() == null)
			{
				skillWAbilityOptions.setSelectedIndex(0);
			}
			else
			{
				skillWAbilityOptions.setSelectedItem(feature.getSkillWAbilitySelected());
			}
		}
		if (!feature.getAbilityIncreaseOptions().isEmpty())
		{
			PropertyListener.listenForChanges(PropertyListener.ABILITYSCOREINC, this);
			ablIncOptions = UILib.getComboBox(feature.getAbilityIncreaseOptions().toArray(new AbilityIncrease[0]),
					this);
			UILib.addLabeledComponent(bodyPanel, "Increase Ability: ", ablIncOptions, c);
			c.gridy++;
			if (feature.getAbilityIncreasesSelected().isEmpty())
			{
				ablIncOptions.setSelectedIndex(0);
			}
			else
			{
				ablIncOptions.setSelectedItem(feature.getAbilityIncreasesSelected().get(0));
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(skillOptions))
		{
			Skill skil = (Skill) skillOptions.getSelectedItem();
			if (skil != null)
			{
				feature.setSkillsSelected(List.of(skil));
			}
		}
		else if (e.getActionCommand().equals(skillExpertAC))
		{
			List<Skill> expertsSelected = new ArrayList<>();
			for (JComboBox<Skill> skillE : skillExpertOptions)
			{
				if (skillE.getSelectedItem() == null)
				{
					return; // Invalid state, bail
				}
				expertsSelected.add((Skill) skillE.getSelectedItem());
			}
			feature.setSkillExpertsSelected(expertsSelected);
		}
		else if (e.getSource().equals(langOptions))
		{
			feature.setLanguagesSelected(List.of((Language) langOptions.getSelectedItem()));
		}
		else if (e.getSource().equals(resistanceOptions))
		{
			feature.setResistancesSelected(List.of((DamageType) resistanceOptions.getSelectedItem()));
		}
		else if (e.getSource().equals(selectableOptions))
		{
			Selectable sel = (Selectable) selectableOptions.getSelectedItem();
			if (sel != null)
			{
				if (sel.getClass().equals(Feat.class))
				{
					feature.setFeat((Feat) sel);
				}
				else
				{
					feature.setSelected(sel);
				}
				selectablePanel.setSelected(sel);
				revalidate();
			}
		}
		else if (e.getSource().equals(specificSpellOptions))
		{
			feature.setChosenSpell((Spell) specificSpellOptions.getSelectedItem());
		}
		else if (e.getSource().equals(skillWAbilityOptions))
		{
			Skill skil = (Skill) skillWAbilityOptions.getSelectedItem();
			if (skil != null)
			{
				feature.setSkillWAbilitySelected(skil);
			}
		}
		else if (e.getSource().equals(ablIncOptions))
		{
			feature.setAbilityIncreasesSelected(List.of((AbilityIncrease) ablIncOptions.getSelectedItem()));
		}
	}

	private void updateSelectables()
	{
		String selName = feature.getSelectableName();
		String featTrait = feature.getFeatTraitName();
		List<Selectable> selOpts = Selectable.getAllValidSelectablesOfType(sheet, selName);
		if (feature.featIgnoresPrereqs())
		{
			selOpts.addAll(Feat.getAllFeatsOfType(sheet, featTrait));
		}
		else
		{
			selOpts.addAll(Feat.getAllValidFeatsOfType(sheet, featTrait));
		}
		selectableOptions.removeActionListener(this);
		selectableOptions.removeAllItems();
		selOpts.forEach(s -> selectableOptions.addItem(s));
		selectableOptions.addActionListener(this);
		if (feature.getSelected() != null && selOpts.contains(feature.getSelected()))
		{
			selectableOptions.setSelectedItem(feature.getSelected());
		}
		else if (feature.getFeat() != null && selOpts.contains(feature.getFeat()))
		{
			selectableOptions.setSelectedItem(feature.getFeat());
		}
		else if (selectableOptions.getItemCount() > 0)
		{
			selectableOptions.setSelectedIndex(0);
		}
	}

	private void updateSkillProfSelect()
	{
		List<Skill> sp = feature.getSkillsSelected();

		if (!sp.isEmpty())
		{
			skillOptions.setSelectedItem(sp.get(0));
		}
	}

	private void updateSkillExpertiseLists()
	{
		List<Skill> seo = feature.getSkillExpertOptions();
		List<Skill> validSkills = new ArrayList<>();
		for (Skill s : sheet.getAllSkillProfs())
		{
			if (seo.contains(s) || seo.contains(Skill.Any))
			{
				validSkills.add(s);
			}
		}
		for (int i = 0; i < skillExpertOptions.size(); i++)
		{
			JComboBox<Skill> skillE = skillExpertOptions.get(i);
			skillE.removeActionListener(this); // Don't want to "hear" this bit
			skillE.removeAllItems();
			validSkills.forEach(s -> skillE.addItem(s));
			skillE.addActionListener(this);
			if (feature.getSkillExpertsSelected().size() <= i)
			{
				skillE.setSelectedIndex(0);
			}
			else
			{
				skillE.setSelectedItem(feature.getSkillExpertsSelected().get(i));
			}
		}
	}

	private void updateHomeworldResists()
	{
		if (resLabel != null)
		{
			List<String> resStr = new ArrayList<>();
			resStr.addAll(feature.getResistancesGranted().stream().map(r -> r.toString()).toList());
			if (!feature.getResistancesByHomeworld().isEmpty() && sheet.getBackground() != null)
			{
				String r = CharacterSheet.getResistanceForHomeworld(sheet.getBackground().getHomeworld(),
						feature.getResistancesByHomeworld());
				if (!r.isBlank())
				{
					resStr.add(r);
				}
			}
			if (resStr.isEmpty())
			{
				resLabel.setVisible(false);
			}
			else
			{
				resLabel.setVisible(true);
				resLabel.setText("<html>Grants Resistance: <i>" + String.join(", ", resStr) + "</i></html>");
			}
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		switch (prop)
		{
			case PropertyListener.HOMEWORLD:
				updateHomeworldResists();
			break;
			case PropertyListener.SKILLPROFS:
				updateSkillProfSelect();
				// Deliberate "fall-through"
			case PropertyListener.SKILLEXPS:
				updateSkillExpertiseLists();
			break;
			case PropertyListener.SELECTED:
				updateSelectables();
			break;
			case PropertyListener.ABILITYSCOREINC:
				ablIncOptions.setSelectedItem(feature.getAbilityIncreasesSelected().get(0));
			break;
		}
	}
}
