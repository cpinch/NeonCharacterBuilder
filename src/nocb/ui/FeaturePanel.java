package nocb.ui;

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
import javax.swing.JTextPane;

import nocb.data.Ability;
import nocb.data.ArmorProf;
import nocb.data.Feat;
import nocb.data.Feature;
import nocb.data.Language;
import nocb.data.Selectable;
import nocb.data.Skill;
import nocb.data.Spell;
import nocb.data.SpellChoice;
import nocb.main.CharacterSheet;
import nocb.main.PropertyListener;

public class FeaturePanel extends CollapsablePanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private static final String skillExpertAC = "Skill Expert";

	private final CharacterSheet sheet;
	private final Feature feature;

	private final JComboBox<Skill> skillOptions = new JComboBox<>();
	private final JComboBox<String> resistanceOptions = new JComboBox<>();
	private final List<JComboBox<Skill>> skillExpertOptions = new ArrayList<>();
	private final JComboBox<Language> langOptions = new JComboBox<>();
	private final JComboBox<Selectable> selectableOptions = new JComboBox<>();
	private SelectablePanel selectablePanel;
	private final JComboBox<Spell> specificSpellOptions = new JComboBox<>();

	private JLabel resLabel;

	public FeaturePanel(CharacterSheet sheet, Feature feature, boolean startCollapsed)
	{
		super(startCollapsed);

		this.sheet = sheet;
		this.feature = feature;

		bodyPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = UILib.getStandardGBC();

		JLabel l = UILib.addLabel(headerPanel, feature.getName(), c);
		l.setFont(UILib.boldFont);
		c.gridy++;

		// Basic
		if (!feature.getText().isBlank())
		{
			c.weighty = 1;
			JTextPane textPane = UILib.getTextDisplay();
			textPane.setText(feature.getText());
			bodyPanel.add(textPane, c);
			c.gridy++;
			c.weighty = 0;
		}
		if (feature.getSpeedMod() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Speed + " + feature.getSpeedMod() + "</html>", c);
			c.gridy++;
		}
		if (feature.getExtraHPLvl1() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Extra HP " + feature.getExtraHPLvl1() + "</html>", c);
			c.gridy++;
		}
		if (feature.getExtraHPPerLevel() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Extra HP per Level " + feature.getExtraHPPerLevel() + "</html>", c);
			c.gridy++;
		}
		if (feature.givesHalfProfAll())
		{
			UILib.addLabel(bodyPanel, "<html>Gives Half Proficiency to all non-proficient skills.</html>", c);
			c.gridy++;
		}
		if (feature.givesProfToInit())
		{
			UILib.addLabel(bodyPanel, "<html>Gives Proficincy Bonus to Initiative</html>.", c);
			c.gridy++;
		}

		// List
		List<Ability> saves = feature.getSaveProfs();
		if (!saves.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Save Proficiencies: <i>"
							+ String.join(", ", saves.stream().map(a -> a.toString()).toList()) + "</i></html>",
					c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<String> weps = feature.getWeaponProfs();
		if (!weps.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Weapon Proficiencies: <i>" + String.join(", ", weps) + "</i></html>", c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<String> tools = feature.getToolProfs();
		if (!tools.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Tool Proficiencies: <i>" + String.join(", ", tools) + "</i></html>",
					c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<ArmorProf> armor = feature.getArmorProfs();
		if (!armor.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"<html>Grants Armor Trainings: <i>"
							+ String.join(", ", armor.stream().map(a -> a.toString()).toList()) + "</i></html>",
					c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<Skill> skills = feature.getSkillsGranted();
		if (!skills.isEmpty())
		{
			UILib.addLabel(bodyPanel, "<html>Grants Skills: <i>"
					+ String.join(", ", skills.stream().map(s -> s.toString()).toList()) + "</i></html>", c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
					+ "</html>", c).setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<Language> freeLangs = feature.getLanguagesGranted();
		if (!freeLangs.isEmpty())
		{
			UILib.addLabel(bodyPanel,
					"Languages: " + String.join(", ", freeLangs.stream().map(la -> la.toString()).toList()), c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		List<String> res = feature
				.getAllResistancesGranted(sheet.getBackground() != null ? sheet.getBackground().getHomeworld() : null);
		if (!res.isEmpty())
		{
			PropertyListener.listenForChanges(PropertyListener.HOMEWORLD, this);
			resLabel = UILib.addLabel(bodyPanel,
					"<html>Grants Resistance: <i>" + String.join(", ", res) + "</i></html>", c);
			resLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
		// abilitiesToAC always has accompanying text, so don't show it

		// Selections
		if (!feature.getResistanceOptions().isEmpty())
		{
			resistanceOptions.setFont(UILib.standardFont);
			feature.getResistanceOptions().forEach(s -> resistanceOptions.addItem(s));
			UILib.addLabeledComponent(bodyPanel, "Resistance: ", resistanceOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			resistanceOptions.addActionListener(this);
			c.gridy++;
			if (feature.getResistanceSelected().isBlank())
			{
				resistanceOptions.setSelectedIndex(0);
			}
			else
			{
				resistanceOptions.setSelectedItem(feature.getResistanceSelected());
			}
		}
		if (feature.getSkillSelectionCount() > 0)
		{
			skillOptions.setFont(UILib.standardFont);
			skillOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			skillOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			List<Skill> skillOpts = feature.getSkillSelectionOptions();
			if (skillOpts.contains(Skill.Any))
			{
				Arrays.asList(Skill.realValues()).forEach(s -> skillOptions.addItem(s));
			}
			else
			{
				skillOpts.forEach(s -> skillOptions.addItem(s));
			}
			skillOptions.addActionListener(this);
			UILib.addLabeledComponent(bodyPanel, "Skill Proficiency: ", skillOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
			PropertyListener.listenForChanges(PropertyListener.SKILLS, this);
			JPanel skillEPanel = new JPanel();
			skillEPanel.setOpaque(false);
			for (int i = 0; i < feature.getSkillExpertCount(); i++)
			{
				JComboBox<Skill> skillE = new JComboBox<>();
				skillE.setBackground(VaporwaveColors.DEEP_VIOLET);
				skillE.setForeground(VaporwaveColors.LASER_YELLOW);
				skillE.addActionListener(this);
				skillE.setActionCommand(skillExpertAC);
				skillEPanel.add(skillE);
				skillExpertOptions.add(skillE);
			}
			updateSkillExpertiseLists();
			UILib.addLabeledComponent(bodyPanel, "Select Expertise:", skillEPanel, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
		}
		if (feature.getLanguageSelectionCount() > 0)
		{
			// TODO post1.0 - for now this only supports picking 1 language. If a feature
			// exists that lets you select more than 1 will need to revisit
			feature.getLanguageSelectionOptions().forEach(la -> langOptions.addItem(la));
			langOptions.addActionListener(this);
			langOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			langOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Select Language:", langOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
			selectableOptions.setFont(UILib.standardFont);
			selectableOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			selectableOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			selectableOptions.addActionListener(this);
			String label = (feature.getSelectableName().isBlank() ? feature.getFeatTraitName()
					: feature.getSelectableName()) + ": ";
			updateSelectables();
			UILib.addLabeledComponent(bodyPanel, label, selectableOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
			c.weighty = 1;
			bodyPanel.add(selectablePanel, c);
			c.gridy++;
		}
		if (!feature.getSpecificSpellChoices().isEmpty())
		{
			// TODO post1.0 - for now this only supports picking 1 specific spell. If a
			// feature exists that lets you select more than 1 will need to revisit
			feature.getSpecificSpellChoices().forEach(sp -> specificSpellOptions.addItem(sp));
			specificSpellOptions.addActionListener(this);
			specificSpellOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			specificSpellOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			UILib.addLabeledComponent(bodyPanel, "Select Spell:", specificSpellOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
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
			feature.setSkillsExpert(expertsSelected);
		}
		else if (e.getSource().equals(langOptions))
		{
			feature.setLanguagesSelected(List.of((Language) langOptions.getSelectedItem()));
		}
		else if (e.getSource().equals(resistanceOptions))
		{
			feature.setResistanceSelected((String) resistanceOptions.getSelectedItem());
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
					feature.setSelection(sel);
				}
				selectablePanel.setSelected(sel);
				revalidate();
			}
		}
		else if (e.getSource().equals(specificSpellOptions))
		{
			feature.chooseSpecificSpell((Spell) specificSpellOptions.getSelectedItem());
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
			if (feature.getSkillsExpert().size() <= i)
			{
				skillE.setSelectedIndex(0);
			}
			else
			{
				skillE.setSelectedItem(feature.getSkillsExpert().get(i));
			}
		}
	}

	private void updateHomeworldResists()
	{
		if (resLabel != null)
		{
			List<String> res = feature.getAllResistancesGranted(
					sheet.getBackground() != null ? sheet.getBackground().getHomeworld() : null);
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
			case PropertyListener.SKILLS:
				updateSkillExpertiseLists();
			break;
			case PropertyListener.SELECTED:
				this.updateSelectables();
			break;
		}
	}
}
