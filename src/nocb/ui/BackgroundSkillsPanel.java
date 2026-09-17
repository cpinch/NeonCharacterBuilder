package nocb.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nocb.data.Background;
import nocb.data.Skill;
import nocb.main.CharacterSheet;

public class BackgroundSkillsPanel extends JPanel implements ActionListener
{
	private static final long serialVersionUID = -2739120980015488390L;

	private final CharacterSheet sheet;

	private final JComboBox<Skill> skill1, skill2, skill3;
	private final JLabel label = new JLabel();

	public BackgroundSkillsPanel(CharacterSheet sheet)
	{
		this.sheet = sheet;

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
		setOpaque(false);

		UILib.addLabel(this, "Skill Proficiencies: ");

		add(label);

		// Custom backgrounds have 2 of any skill + 1 of Computers/Technology
		skill1 = new JComboBox<>(Skill.realValues());
		skill1.addActionListener(this);
		skill1.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		skill1.setBackground(VaporwaveColors.DEEP_VIOLET);
		skill1.setForeground(VaporwaveColors.LASER_YELLOW);
		add(skill1);
		skill2 = new JComboBox<>(Skill.realValues());
		skill2.addActionListener(this);
		skill2.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		skill2.setBackground(VaporwaveColors.DEEP_VIOLET);
		skill2.setForeground(VaporwaveColors.LASER_YELLOW);
		add(skill2);
		skill3 = new JComboBox<>(new Skill[]
		{ Skill.Computers, Skill.Technology });
		skill3.addActionListener(this);
		skill3.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
		skill3.setBackground(VaporwaveColors.DEEP_VIOLET);
		skill3.setForeground(VaporwaveColors.LASER_YELLOW);
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

	public void updateDetails()
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
				label.setVisible(false);
			}
			else
			{
				skill1.setVisible(false);
				skill2.setVisible(false);
				skill3.setVisible(false);
				label.setVisible(true);
				label.setText(String.join(", ", bg.getSkillsGranted().stream().map(s -> s.toString()).toList()));
			}
		}
	}
}
