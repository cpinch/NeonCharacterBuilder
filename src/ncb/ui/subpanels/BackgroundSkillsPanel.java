package ncb.ui.subpanels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ncb.data.enums.Skill;
import ncb.data.loadables.Background;
import ncb.main.CharacterSheet;
import ncb.main.PropertyListener;
import ncb.ui.ListensForChanges;
import ncb.ui.UILib;
import ncb.ui.VaporwaveColors;

public class BackgroundSkillsPanel extends JPanel implements ActionListener, ListensForChanges
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Skill> skill1, skill2, skill3;
	private final JLabel label;

	public BackgroundSkillsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		setOpaque(false);
		PropertyListener.listenForChanges(PropertyListener.BACKGROUND, this);
		PropertyListener.listenForChanges(PropertyListener.SKILLPROFS, this);

		label = UILib.addLabel(this, "Skill Proficiencies: ", VaporwaveColors.HOT_PINK);
		label.setFont(UILib.boldFont);

		// Custom backgrounds have 2 of any skill + 1 of Computers/Technology
		// TODO Background loading
		skill1 = UILib.getComboBox(Skill.realValues(), this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(skill1);
		skill2 = UILib.getComboBox(Skill.realValues(), this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(skill2);
		skill3 = UILib.getComboBox(new Skill[]
		{ Skill.Computers, Skill.Technology }, this, VaporwaveColors.DEEP_VIOLET, VaporwaveColors.LASER_YELLOW);
		add(skill3);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (e.getSource().equals(skill1) || e.getSource().equals(skill2) || e.getSource().equals(skill3))
			{
				bg.setSkillsSelected(List.of((Skill) skill1.getSelectedItem(), (Skill) skill2.getSelectedItem(),
						(Skill) skill3.getSelectedItem()));

			}
		}
	}

	@Override
	public void updateProperty(String prop)
	{
		Background bg = sheet.getBackground();
		if (bg != null)
		{
			if (bg.getName().equals("Custom"))
			{
				List<Skill> bgSkills = new ArrayList<>(bg.getSkillsSelected());
				skill1.setVisible(true);
				skill1.setSelectedItem(bgSkills.get(0));
				skill2.setVisible(true);
				skill2.setSelectedItem(bgSkills.get(1));
				skill3.setVisible(true);
				skill3.setSelectedItem(bgSkills.get(2));
				label.setText("Skill Proficiencies: ");
			}
			else
			{
				skill1.setVisible(false);
				skill2.setVisible(false);
				skill3.setVisible(false);
				label.setText("Skill Proficiencies: "
						+ String.join(", ", bg.getSkillsGranted().stream().map(s -> s.toString()).toList()));
			}
		}
	}
}
