package ncb.ui.subpanels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;

import ncb.data.loadables.Spell;
import ncb.ui.CollapsablePanel;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class SpellPanel extends CollapsablePanel
{
	private static final long serialVersionUID = -2739120980015488390L;

	public SpellPanel(Spell s)
	{
		super(true);

		setBorder(BorderFactory.createEtchedBorder());
		setOpaque(false);

		GridBagConstraints c = UILib.getStandardGBC();
		c.anchor = GridBagConstraints.WEST;
		bodyPanel.setLayout(new GridBagLayout());

		UILib.addLabel(headerPanel, getNameLabel(s), VaporwaveColors.HOT_PINK);

		UILib.addLabel(bodyPanel, "<html><b>Casting Time</b>: " + getCastTimeString(s.getCastTime()) + "</html>", c,
				VaporwaveColors.HOT_PINK);
		c.gridy++;

		if (!s.getTrigger().isBlank())
		{
			UILib.addLabel(bodyPanel, "Trigger: " + s.getTrigger(), c, VaporwaveColors.HOT_PINK)
					.setFont(UILib.italicFont);
			c.gridy++;
		}

		UILib.addLabel(bodyPanel, "<html><b>Range</b>: " + s.getRange() + "</html>", c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		UILib.addLabel(bodyPanel, "<html><b>Components</b>: " + s.getComponents() + "</html>", c,
				VaporwaveColors.HOT_PINK);
		c.gridy++;

		if (!s.getMaterials().isBlank())
		{
			UILib.addLabel(bodyPanel, "Materials: " + s.getMaterials(), c, VaporwaveColors.HOT_PINK)
					.setFont(UILib.italicFont);
			c.gridy++;
		}

		UILib.addLabel(bodyPanel, "<html><b>Duration</b>: " + s.getDuration() + "</html>", c, VaporwaveColors.HOT_PINK);
		c.gridy++;

		c.weighty = 1;
		UILib.addTextDisplay(bodyPanel, s.getText(), c, VaporwaveColors.HOT_PINK);
		c.gridy++;
		c.weighty = 0;

		if (!s.getNotes().isBlank())
		{
			UILib.addLabel(bodyPanel, "Notes: " + s.getNotes(), c, VaporwaveColors.HOT_PINK).setFont(UILib.italicFont);
		}
	}

	private String getNameLabel(Spell s)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("<html><b>");
		sb.append(s.getName());
		sb.append("</b> <i>(");
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
}
