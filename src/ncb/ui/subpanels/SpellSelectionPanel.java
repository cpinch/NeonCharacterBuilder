package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import com.formdev.flatlaf.FlatClientProperties;

import ncb.data.SpellChoice;
import ncb.data.loadables.Spell;
import ncb.data.loadables.SpellList;
import ncb.ui.CollapsablePanel;
import ncb.ui.UILib;

public class SpellSelectionPanel extends CollapsablePanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final SpellChoice sc;

	private final JComboBox<Spell> spellSelector;
	private final JLabel basicInfo;
	private final JLabel castTime;
	private final JLabel trigger;
	private final JLabel range;
	private final JLabel components;
	private final JLabel materials;
	private final JLabel duration;
	private final JTextPane text;

	public SpellSelectionPanel(SpellChoice sc, int spellLvl, List<SpellList> spellLists)
	{
		this(sc, spellLvl, spellLists, false);
	}

	public SpellSelectionPanel(SpellChoice sc, int spellLvl, List<SpellList> spellLists, boolean sortAlpha)
	{
		super(true);

		this.sc = sc;

		setBorder(BorderFactory.createEtchedBorder());
		setOpaque(false);
		setAlignmentX(JComponent.LEFT_ALIGNMENT);

		GridBagConstraints c = UILib.getStandardGBC();
		bodyPanel.setLayout(new GridBagLayout());

		List<Spell> spellOpts = new ArrayList<>();
		if (spellLvl > 0)
		{
			// Allow selecting spells from lower levels but not cantrips
			for (int spLvl = spellLvl; spLvl > 0; spLvl--)
			{
				final int lvl = spLvl;
				spellLists.forEach(sl -> sl.getSpellsForLevel(lvl).forEach(s ->
				{
					if (!spellOpts.contains(s))
					{
						spellOpts.add(s);
					}
				}));
			}
		}
		else
		{
			// Cantrps
			spellLists.forEach(sl -> sl.getSpellsForLevel(spellLvl).forEach(s ->
			{
				if (!spellOpts.contains(s))
				{
					spellOpts.add(s);
				}
			}));
		}

		if (sortAlpha)
		{
			spellOpts.sort(Comparator.comparing(Spell::getName));
		}

		spellSelector = UILib.getComboBox(spellOpts.toArray(new Spell[0]), this);
		headerPanel.add(spellSelector);

		basicInfo = UILib.addLabel(headerPanel, "");

		castTime = UILib.getLabel("");
		UILib.addLabeledComponent(bodyPanel, "Casting Time: ", castTime, c);
		c.gridy++;

		trigger = UILib.addLabel(bodyPanel, "Trigger: ", c);
		trigger.putClientProperty(FlatClientProperties.STYLE, "font: italic");
		c.gridy++;

		range = UILib.getLabel("");
		UILib.addLabeledComponent(bodyPanel, "Range: ", range, c);
		c.gridy++;

		components = UILib.getLabel("");
		UILib.addLabeledComponent(bodyPanel, "Components: ", components, c);
		c.gridy++;

		materials = UILib.addLabel(bodyPanel, "Materials: ", c);
		materials.putClientProperty(FlatClientProperties.STYLE, "font: italic");
		c.gridy++;

		duration = UILib.getLabel("");
		UILib.addLabeledComponent(bodyPanel, "Duration: ", duration, c);
		c.gridy++;

		c.weighty = 1;
		text = UILib.addTextDisplay(bodyPanel, "", c);
		c.gridy++;
		c.weighty = 0;

		if (sc.getSpell() != null)
		{
			spellSelector.setSelectedItem(sc.getSpell());
		}
		else
		{
			spellSelector.setSelectedIndex(0);
		}
	}

	private void updateSpell(Spell s)
	{
		sc.setSpell(s);

		basicInfo.setText(getBasicInfo(s));

		castTime.setText(getCastTimeString(s.getCastTime()));
		if (s.getTrigger().isBlank())
		{
			trigger.setVisible(false);
		}
		else
		{
			trigger.setVisible(true);
			trigger.setText("Trigger: " + s.getTrigger());
		}
		range.setText(s.getRange());
		components.setText(s.getComponents());
		if (s.getMaterials().isBlank())
		{
			materials.setVisible(false);
		}
		else
		{
			materials.setVisible(true);
			materials.setText("Materials: " + s.getMaterials());
		}
		duration.setText(s.getDuration());
		text.setText("<html>" + s.getText() + "</html>");
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
