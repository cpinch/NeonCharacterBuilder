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

public class FeaturePanel extends CollapsablePanel implements ActionListener
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
	private final JComboBox<Feat> featOptions = new JComboBox<>();
	private SelectablePanel selectablePanel, featPanel;

	private String priorHomeworldName = "", featTrait = "";
	private JLabel resLabel;

	public FeaturePanel(CharacterSheet sheet, Feature feature, boolean startCollapsed)
	{
		super(startCollapsed);

		this.sheet = sheet;
		this.feature = feature;
		if (sheet.getBackground() != null)
		{
			this.priorHomeworldName = sheet.getBackground().getHomeworld().getName();
		}

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
		// abilityToAC always has accompanying text, so don't show it
		if (feature.getSpeedMod() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Speed + " + feature.getSpeedMod() + "</html>", c);
			c.gridy++;
		}
		if (feature.getExtraHPPerLevel() > 0)
		{
			UILib.addLabel(bodyPanel, "<html>Extra HP per Level " + feature.getExtraHPPerLevel() + "</html>", c);
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
		if (!feature.getSelectableName().isBlank())
		{
			selectablePanel = new SelectablePanel(sheet);
			selectableOptions.setFont(UILib.standardFont);
			selectableOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			selectableOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			String selName = feature.getSelectableName();
			List<Selectable> selOpts = Selectable.getAllValidSelectablesOfType(sheet, selName);
			selOpts.forEach(s -> selectableOptions.addItem(s));
			selectableOptions.addActionListener(this);
			UILib.addLabeledComponent(bodyPanel, selName + ": ", selectableOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
			c.weighty = 1;
			bodyPanel.add(selectablePanel, c);
			c.gridy++;
			if (feature.getSelected() == null)
			{
				selectableOptions.setSelectedIndex(0);
			}
			else
			{
				selectableOptions.setSelectedItem(feature.getSelected());
			}
		}
		if (!feature.getFeatTraitName().isBlank())
		{
			featPanel = new SelectablePanel(sheet);
			featOptions.setFont(UILib.standardFont);
			featOptions.setBackground(VaporwaveColors.DEEP_VIOLET);
			featOptions.setForeground(VaporwaveColors.LASER_YELLOW);
			featTrait = feature.getFeatTraitName();
			List<Feat> featOpts = Feat.getAllValidFeatsOfType(sheet, featTrait);
			featOpts.forEach(s -> featOptions.addItem(s));
			featOptions.addActionListener(this);
			UILib.addLabeledComponent(bodyPanel, "Feat: ", featOptions, c)
					.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			c.gridy++;
			c.weighty = 1;
			bodyPanel.add(featPanel, c);
			c.gridy++;
			if (feature.getFeat() == null)
			{
				featOptions.setSelectedIndex(0);
			}
			else
			{
				featOptions.setSelectedItem(feature.getFeat());
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
				feature.setSelection(sel);
				selectablePanel.setSelected(sel);
				revalidate();
			}
		}
		else if (e.getSource().equals(featOptions))
		{
			Feat f = (Feat) featOptions.getSelectedItem();
			if (f != null)
			{
				feature.setFeat(f);
				featPanel.setSelected(f);
				revalidate();
			}
		}
	}

	/**
	 * Needed for cases where another panel on the same tab updates the character's
	 * skill proficiencies
	 */
	public void updateSkillExpertiseLists()
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
			skillE.removeAllItems();
			validSkills.forEach(s -> skillE.addItem(s));
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

	/**
	 * Needed for cases where we have homeworld-dependent fields and the homeworld
	 * got updated
	 */
	public void updateHomeworld()
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

		if (featTrait != null && featTrait.equals("Origin")
				&& !sheet.getBackground().getHomeworld().getName().equals(priorHomeworldName))
		{
			// If homeworld changed and our feat selection was based on that, need to update
			// the feat options
			List<Feat> featOpts = Feat.getAllValidFeatsOfType(sheet, featTrait);
			featOptions.removeAllItems();
			featOpts.forEach(s -> featOptions.addItem(s));
		}
	}
}
