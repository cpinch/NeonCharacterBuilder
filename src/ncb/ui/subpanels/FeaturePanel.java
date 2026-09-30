package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.Feature;
import ncb.data.SpellChoice;
import ncb.data.enums.Ability;
import ncb.data.enums.ArmorTraining;
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
import ncb.ui.VaporwaveColors;

public class FeaturePanel extends CollapsablePanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private static final String skillExpertAC = "Skill Expert";

	private final CharacterSheet sheet;
	private final Feature feature;

	private JComboBox<Skill> skillOptions;
	private JComboBox<String> resistanceOptions;
	private final List<JComboBox<Skill>> skillExpertOptions = new ArrayList<>();
	private JComboBox<Language> langOptions;
	private JComboBox<Selectable> selectableOptions;
	private SelectablePanel selectablePanel;
	private JComboBox<Spell> specificSpellOptions;
	private JLabel resLabel;
	private JComboBox<Skill> skillWAbilityOptions;

	public FeaturePanel(CharacterSheet sheet, Feature feature, boolean startCollapsed)
	{
		super(startCollapsed);

		this.sheet = sheet;
		this.feature = feature;

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		UILib.addLabel(headerPanel, feature.getName(), c, VaporwaveColors.HOT_PINK).setFont(UILib.boldFont);
		c.gridy++;

		// Basic
		if (!feature.getText().isBlank())
		{
			c.weighty = 1;
			// We don't want this to scroll
			bodyPanel.add(UILib.getTextDisplay(feature.getText(), VaporwaveColors.HOT_PINK), c);
			c.gridy++;
			c.weighty = 0;
		}
		if (feature.getSpeedMod() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Speed + " + feature.getSpeedMod() + "</html>", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (feature.getExtraHPLvl1() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Extra HP " + feature.getExtraHPLvl1() + "</html>", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (feature.getExtraHPPerLevel() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Extra HP per Level " + feature.getExtraHPPerLevel() + "</html>", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (feature.givesHalfProfAll())
		{
			UILib.addLabel(bodyPanel, "<html>Gives Half Proficiency to all non-proficient skills.</html>", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (feature.givesProfToInit())
		{
			UILib.addLabel(bodyPanel, "<html>Gives Proficincy Bonus to Initiative</html>.", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}

		// List
		List<Ability> saves = feature.getSaveProfs();
		if (!saves.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Save Proficiencies: <i>"
							+ String.join(", ", saves.stream().map(a -> a.toString()).toList()) + "</i></html>",
					c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<String> weps = feature.getWeaponProfs();
		if (!weps.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Weapon Proficiencies: <i>" + String.join(", ", weps) + "</i></html>", c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<String> tools = feature.getToolProfs();
		if (!tools.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Tool Proficiencies: <i>" + String.join(", ", tools) + "</i></html>",
					c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<ArmorTraining> armor = feature.getArmorProfs();
		if (!armor.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Armor Trainings: <i>"
							+ String.join(", ", armor.stream().map(a -> a.toString()).toList()) + "</i></html>",
					c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<Skill> skills = feature.getSkillsGranted();
		if (!skills.isEmpty())
		{
			UILib.addLabel(
					bodyPanel, "<html>Grants Skills: <i>"
							+ String.join(", ", skills.stream().map(s -> s.toString()).toList()) + "</i></html>",
					c, VaporwaveColors.HOT_PINK);
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
					+ "</html>", c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<Language> freeLangs = feature.getLanguagesGranted();
		if (!freeLangs.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"Languages: " + String.join(", ", freeLangs.stream().map(la -> la.toString()).toList()), c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		List<String> res = feature.getResistancesGranted();
		if (sheet.getBackground() != null && !sheet.getBackground().getHomeworld().hasTrait("Any"))
		{
			String r = feature.getResistanceByHomeworld(sheet.getBackground().getHomeworld());
			if (!r.isBlank())
			{
				res.add(r);
			}
		}
		if (!res.isEmpty())
		{
			PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);
			resLabel = UILib.addLabel(bodyPanel,
					"<html>Grants Resistance: <i>" + String.join(", ", res) + "</i></html>", c,
					VaporwaveColors.HOT_PINK);
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
			UILib.addLabel(bodyPanel, "Grants extra known spells: " + String.join(", ", levelCount), c,
					VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (!feature.getAbilitiesAddToSkills().isEmpty())
		{
			List<String> adds = new ArrayList<>();
			for (Map.Entry<Skill, Ability> abilitiesToSkills : feature.getAbilitiesAddToSkills().entrySet())
			{
				adds.add("Adds " + abilitiesToSkills.getValue() + " to " + abilitiesToSkills.getKey());
			}
			UILib.addLabel(bodyPanel, String.join(", ", adds), c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (!feature.getInitAbilities().isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html><i>May use "
							+ String.join(", ", feature.getInitAbilities().stream().map(a -> a.toString()).toList())
							+ " for initiative.</i></html>",
					c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (!feature.getSkillProfOrExpertise().isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html><i>Gives proficiency in "
							+ String.join(", ",
									feature.getSkillProfOrExpertise().stream().map(s -> s.toString()).toList())
							+ " or expertise if you already have proficiency.",
					c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		// abilitiesToAC always has accompanying text, so we don't show it

		// Selections
		List<String> resOpts = feature.getResistanceOptions();
		if (!feature.getResistancesByHomeworld().isEmpty() && sheet.getBackground() != null
				&& sheet.getBackground().getHomeworld().hasTrait("Any"))
		{
			resOpts.addAll(feature.getResistancesByHomeworld().values());
		}
		if (!resOpts.isEmpty())
		{
			resistanceOptions = UILib.getComboBox(resOpts.toArray(new String[0]), this, VaporwaveColors.DEEP_VIOLET,
					VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Resistance: ", resistanceOptions, c, VaporwaveColors.HOT_PINK);
			c.gridy++;
			if (feature.getResistancesSelected().isEmpty() || feature.getResistancesSelected().get(0).isBlank())
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
			skillOptions = UILib.getComboBox(skillOpts.toArray(new Skill[0]), this, VaporwaveColors.DEEP_VIOLET,
					VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Skill Proficiency: ", skillOptions, c, VaporwaveColors.HOT_PINK);
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
				JComboBox<Skill> skillE = UILib.getComboBox(new Skill[0], this, VaporwaveColors.DEEP_VIOLET,
						VaporwaveColors.LASER_YELLOW);
				skillEPanel.add(skillE);
				skillE.setActionCommand(skillExpertAC);
				skillEPanel.add(skillE);
				skillExpertOptions.add(skillE);
			}
			updateSkillExpertiseLists();
			UILib.addLabeledComponent(bodyPanel, "Select Expertise:", skillEPanel, c, VaporwaveColors.HOT_PINK);
			c.gridy++;
		}
		if (feature.getLanguageSelectionCount() > 0) // TODO future - handle multiple or remove option to set multiple
														// if no feature uses it
		{
			langOptions = UILib.getComboBox(feature.getLanguageSelectionOptions().toArray(new Language[0]), this,
					VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Select Language:", langOptions, c, VaporwaveColors.HOT_PINK);
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
			selectableOptions = UILib.getComboBox(new Selectable[0], this, VaporwaveColors.DEEP_VIOLET,
					VaporwaveColors.LASER_YELLOW);
			String label = (feature.getSelectableName().isBlank() ? feature.getFeatTraitName()
					: feature.getSelectableName()) + ": ";
			updateSelectables();
			UILib.addLabeledComponent(bodyPanel, label, selectableOptions, c, VaporwaveColors.HOT_PINK)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
			c.weighty = 1;
			bodyPanel.add(selectablePanel, c);
			c.gridy++;
		}
		if (!feature.getSpecificSpellChoices().isEmpty())
		{
			specificSpellOptions = UILib.getComboBox(feature.getSpecificSpellChoices().toArray(new Spell[0]), this,
					VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Select Spell:", specificSpellOptions, c, VaporwaveColors.HOT_PINK);
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
			skillWAbilityOptions = UILib.getComboBox(skillOpts.toArray(new Skill[0]), this, VaporwaveColors.DEEP_VIOLET,
					VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Skill Prof + Add " + abl.toString() + " mod: ", skillWAbilityOptions,
					c, VaporwaveColors.HOT_PINK);
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
			feature.setResistancesSelected(List.of((String) resistanceOptions.getSelectedItem()));
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
			List<String> res = feature.getResistancesGranted();
			if (sheet.getBackground() != null && !sheet.getBackground().getHomeworld().hasTrait("Any"))
			{
				res.add(feature.getResistanceByHomeworld(sheet.getBackground().getHomeworld()));
			}
			if (res.isEmpty())
			{
				resLabel.setVisible(false);
			}
			{
				resLabel.setVisible(true);
				resLabel.setText("<html>Grants Resistance: <i>" + String.join(", ", res) + "</i></html>");
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
				this.updateSelectables();
			break;
		}
	}
}
