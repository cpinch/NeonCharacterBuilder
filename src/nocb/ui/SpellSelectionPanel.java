package nocb.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import nocb.data.Spell;
import nocb.data.SpellChoice;
import nocb.data.SpellList;
import nocb.main.CharacterSheet;

public class SpellSelectionPanel extends CollapsablePanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final SpellChoice sc;

	private final JComboBox<Spell> spellSelector = new JComboBox<>();
	private final JLabel basicInfo = UILib.getLabel("");
	private final JLabel castTime = UILib.getLabel("");
	private final JPanel triggerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JLabel trigger = UILib.getLabel("");
	private final JLabel range = UILib.getLabel("");
	private final JLabel components = UILib.getLabel("");
	private final JPanel materialsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
	private final JLabel materials = UILib.getLabel("");
	private final JLabel duration = UILib.getLabel("");
	private final JTextPane text = UILib.getTextDisplay();

	public SpellSelectionPanel(CharacterSheet sheet, SpellChoice sc, int spellLvl)
	{
		super(true);

		this.sc = sc;

		setBorder(BorderFactory.createEtchedBorder());
		setOpaque(false);

		GridBagConstraints c = UILib.getStandardGBC();
		c.anchor = GridBagConstraints.WEST;
		bodyPanel.setLayout(new GridBagLayout());

		// TODO post1.0 - when level up also pull from lower level lists above 0
		SpellList list = sc.getSpellList();
		list.getSpellsForLevel(spellLvl).forEach(s -> spellSelector.addItem(s));
		spellSelector.addActionListener(this);
		spellSelector.setBackground(VaporwaveColors.DEEP_VIOLET);
		spellSelector.setForeground(VaporwaveColors.LASER_YELLOW);
		if (sc.getSpell() != null)
		{
			spellSelector.setSelectedItem(sc.getSpell());
		}
		else
		{
			spellSelector.setSelectedIndex(0);
		}
		headerPanel.add(spellSelector);
		headerPanel.add(basicInfo);

		UILib.addLabeledComponent(bodyPanel, "Casting Time: ", castTime, c).setFont(UILib.boldFont);
		c.gridy++;

		trigger.setFont(UILib.italicFont);
		UILib.addLabeledComponent(triggerPanel, "Trigger: ", trigger).setFont(UILib.italicFont);
		bodyPanel.add(triggerPanel, c);
		triggerPanel.setOpaque(false);
		c.gridy++;

		UILib.addLabeledComponent(bodyPanel, "Range: ", range, c).setFont(UILib.boldFont);
		c.gridy++;

		UILib.addLabeledComponent(bodyPanel, "Components: ", components, c).setFont(UILib.boldFont);
		c.gridy++;

		materials.setFont(UILib.italicFont);
		UILib.addLabeledComponent(materialsPanel, "Materials: ", materials, c).setFont(UILib.italicFont);
		bodyPanel.add(materialsPanel, c);
		materialsPanel.setOpaque(false);
		c.gridy++;

		UILib.addLabeledComponent(bodyPanel, "Duration: ", duration, c).setFont(UILib.boldFont);
		c.gridy++;

		c.weighty = 1;
		JScrollPane scroll = new JScrollPane(text);
		scroll.setPreferredSize(new Dimension(300, 100));
		scroll.setOpaque(false);
		scroll.getViewport().setOpaque(false);
		bodyPanel.add(scroll, c);
		c.gridy++;
		c.weighty = 0;
	}

	private void updateSpell(Spell s)
	{
		sc.setSpell(s);

		basicInfo.setText(getBasicInfo(s));

		castTime.setText(getCastTimeString(s.getCastTime()));
		if (s.getTrigger().isBlank())
		{
			triggerPanel.setVisible(false);
		}
		else
		{
			triggerPanel.setVisible(true);
			trigger.setText(s.getTrigger());
		}
		range.setText(s.getRange());
		components.setText(s.getComponents());
		if (s.getMaterials().isBlank())
		{
			materialsPanel.setVisible(false);
		}
		else
		{
			materialsPanel.setVisible(true);
			materials.setText(s.getMaterials());
		}
		duration.setText(s.getDuration());
		text.setText(s.getText());
	}

	private String getBasicInfo(Spell s)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html><i>(");
		if (s.getLevel() == 0) // Cantrip
		{
			sb.append(s.getSchool());
			sb.append(" ");
			sb.append("cantrip");
		}
		else
		{
			sb.append("Level ");
			sb.append(s.getLevel());
			sb.append(" ");
			sb.append(s.getSchool());
		}
		sb.append(") - Cast: ");
		sb.append(s.getCastTime());
		sb.append(" / Components: ");
		sb.append(s.getComponents());
		if (s.isConcentration())
		{
			sb.append(" (concentration)");
		}
		if (s.isRitual())
		{
			sb.append(" (ritual)");
		}
		sb.append("</i></html>");

		return sb.toString();
	}

	private String getCastTimeString(String castTime)
	{
		switch (castTime)
		{
			case "A":
				return "Action";
			case "B":
				return "Bonus Action";
			case "R":
				return "Reaction";
			default:
				return castTime;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(spellSelector))
		{
			updateSpell((Spell) spellSelector.getSelectedItem());
		}
	}
}
